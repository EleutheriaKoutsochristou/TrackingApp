package App;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MasterActions extends Thread {

	ObjectInputStream client_in;
	ObjectOutputStream client_out;
	MasterData data;
	int worker_amount;
	int chunk_size;
	int number_of_chunks = 0;
	ArrayList<String> workerips;
	ArrayList<Integer> workerports;
	ArrayList<ObjectInputStream> workerinputs;
	MasterData sendData;
	User sendUser;
	
	public MasterActions(Socket connection, MasterData data, ArrayList<String> workerips, ArrayList<Integer> workerports, int worker_amount, int chunk_size) {
		try {
		client_out = new ObjectOutputStream(connection.getOutputStream());
		client_in = new ObjectInputStream(connection.getInputStream());
		this.data = data;
		this.worker_amount = worker_amount;
		this.chunk_size = chunk_size;
		this.workerips = workerips;
		this.workerports = workerports;
		this.workerinputs = new ArrayList<ObjectInputStream>();
		this.sendData = new MasterData();
		this.sendUser = new User();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void run() {
		
		try {
			
			byte[] gpx = (byte[]) client_in.readObject();
			System.out.println("Object read");
			//parse gpx
			Route new_route = gpxParse(gpx);
			//map route
			Map(new_route);
			//reduce route
			Reduce(new_route);
			

			//ready relevant data to send back to the user
			/*MasterData send_data = new MasterData();
			User send_user = new User();
			
			synchronized(data){
				//search all users
				for(int i = 0; i < data.getUsers().size(); i++) {
					//if current user's username matches route username
					if(data.getUsers().get(i).getUsername().equals(new_route.getRouteUser())) {
						send_user.setUsername(data.getUsers().get(i).getUsername());
						send_user.setAverageDistance(data.getUsers().get(i).getAverageDistance());
						send_user.setAverageElevation(data.getUsers().get(i).getAverageElevation());
						send_user.setAverageExerciseTime(data.getUsers().get(i).getAverageExerciseTime());
						send_data.setAverageUserAscend(data.getAverageUserAscend());
						send_data.setAverageUserDistance(data.getAverageUserDistance());
						send_data.setAverageUserTime(data.getAvetageUserTime());
						break;
					}
				}
			}*/
			
			client_out.writeObject(new_route);
			client_out.flush();
			
			client_out.writeObject(this.sendUser);
			client_out.flush();
			
			client_out.writeObject(this.sendData);
			client_out.flush();
			
			System.out.println("Data gathered for user: " + new_route.getRouteUser());
			System.out.println("Route data for user: " + new_route.getRouteUser());
			System.out.println("Route distance: " + (double)Math.round(new_route.getTotalDistance() * 1000) / 1000 + " kilometers, Time:" + (double)Math.round((new_route.getTotalTime() * 60) *100) / 100 + " minutes,  Ascend:" + (double)Math.round(new_route.getTotalAscend() * 100) / 100+ " meters Speed: " + (double)Math.round(new_route.getAverageSpeed() * 100) / 100 + " km/h");                
			System.out.println("User data for user " + sendUser.getUsername());
			System.out.println("User average distance: " + (double)Math.round(sendUser.getAverageDistance() * 1000) / 1000 + " kilometers, Average Exercise Time:" + (double)Math.round((sendUser.getAverageExerciseTime() * 60) * 100) / 100 + " minutes, Average Ascend:" + (double)Math.round(sendUser.getAverageElevation() * 100) / 100 + " meters");
			System.out.println("General statistics for all users");
			System.out.println("Average distance among all users: " + (double)Math.round(data.getAverageUserDistance() * 1000) / 1000 + " kilometers, Average Time:" + (double)Math.round((data.getAvetageUserTime() * 60) * 100) / 100 + " minutes,  Average Ascend:" + (double)Math.round(data.getAverageUserAscend() * 100) / 100 + " meters");
			System.out.println(" ");
	        System.out.println(" ");
			
			
		}catch (IOException e) {
            e.printStackTrace();
        }catch(ClassNotFoundException e){
        	throw new RuntimeException(e);
        }finally {
        	try {
        		client_in.close();
        		client_out.close();
        	} catch (IOException ioException) {
        		ioException.printStackTrace();
        	}
        }
		
	}
	
	//Maps route
	//each waypoint gets placed in a chunk 
	//if the chunk reaches the chunk size the master connects with relevant worker (round robin) and sends the chunk
	public void Map(Route route) {
		
		int chunk_counter = 0; //keeps track of the chunk size
		Route chunk = new Route(); //creates chunk
		int worker_counter = 0; //keeps track of workers
		for(int i = 0; i < route.routeSize(); i++) {
	
			if(chunk_counter == this.chunk_size) {
				this.number_of_chunks++;
				chunk.setChunkId(this.number_of_chunks);
				try {
					
					Socket requestSocket = new Socket(this.workerips.get(worker_counter), this.workerports.get(worker_counter));
					ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
					this.workerinputs.add(in);
					out.writeObject(chunk);
					out.flush();
				}catch (IOException e) {
		            e.printStackTrace();
		        }
				chunk_counter = 0;
				worker_counter++;
			}
			if(chunk_counter == 0) {chunk = new Route(); chunk.setRouteUser(route.getRouteUser());}
			if(worker_counter == this.worker_amount) {worker_counter = 0;}
			chunk.addWaypoint(route.getRoutewaypoints().get(i));
			chunk_counter++;
		}
		
		//sends remaining waypoints that don't make a whole chunk
		if(chunk.routeSize() > 0) {
			this.number_of_chunks++;
			chunk.setChunkId(this.number_of_chunks);
			try {
			
				Socket requestSocket = new Socket(this.workerips.get(worker_counter), this.workerports.get(worker_counter));
				ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
				this.workerinputs.add(in);
				out.writeObject(chunk);
			}catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		
	}
	
	//Collects all chunks and reconstructs the route 
	//Makes all relevant calculations
	public void Reduce(Route route) {
		
		ArrayList<Route> reconstructed_route = new ArrayList<Route>();
		//collects chunks
		for(int i = 0; i< this.workerinputs.size(); i++) {
			
			try {
				Route chunk = (Route) this.workerinputs.get(i).readObject();
				reconstructed_route.add(chunk);
			}catch (IOException e) {
	            e.printStackTrace();
	        }catch(ClassNotFoundException e){
	        	throw new RuntimeException(e);
	        }
		}
	
		
		
		for(int i = 0; i < reconstructed_route.size(); i++) {
			
			route.setTotalDistance(route.getTotalDistance() + reconstructed_route.get(i).getTotalDistance());
			route.setTotalTime(route.getTotalTime() + reconstructed_route.get(i).getTotalTime());
			route.setTotalAscend(route.getTotalAscend() + reconstructed_route.get(i).getTotalAscend());	
		}
		
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		
		for(int i = 0; i < reconstructed_route.size(); i++) {
			for(int j = 0; j < reconstructed_route.size(); j++ ) {
				//calculates data in the places where the route broke into chunks
				if(reconstructed_route.get(j).getChunkId() == reconstructed_route.get(i).getChunkId()+1) {
					double distance = Math.acos((Math.sin(reconstructed_route.get(i).getRoutewaypoints().get(this.chunk_size - 1).getLat() * Math.PI/180)  * Math.sin(reconstructed_route.get(j).getRoutewaypoints().get(0).getLat() * Math.PI/180)) + (Math.cos(reconstructed_route.get(i).getRoutewaypoints().get(this.chunk_size - 1).getLat() * Math.PI/180) * Math.cos(reconstructed_route.get(j).getRoutewaypoints().get(0).getLat() * Math.PI/180) * Math.cos((reconstructed_route.get(j).getRoutewaypoints().get(0).getLon() * Math.PI/180) - (reconstructed_route.get(i).getRoutewaypoints().get(this.chunk_size - 1).getLon()* Math.PI/180)))) * 6371;       
					route.setTotalDistance(route.getTotalDistance() + distance);
				
					try {
						Date date1 = format.parse(reconstructed_route.get(i).getRoutewaypoints().get(this.chunk_size - 1).getTime());	
						Date date2 = format.parse(reconstructed_route.get(j).getRoutewaypoints().get(0).getTime());
					
						double time =  date2.getTime() - date1.getTime();
						time = time / (60 * 60 * 1000) % 24;
						
					route.setTotalTime(route.getTotalTime() + time);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				 
					if(reconstructed_route.get(i).getRoutewaypoints().get(this.chunk_size - 1).getElev() < reconstructed_route.get(j).getRoutewaypoints().get(0).getElev()) {
						double ascend = reconstructed_route.get(j).getRoutewaypoints().get(0).getElev() - reconstructed_route.get(i).getRoutewaypoints().get(this.chunk_size - 1).getElev(); 
						route.setTotalAscend(route.getTotalAscend() + ascend);
					}
					break;

				}else {continue;}
			}		
		}
		
		route.setAverageSpeed(route.getTotalDistance() / route.getTotalTime());
		
		//check if route username already exists
		//if it exists put route in user
		//if if does not exist create new user
		
		synchronized(data) {
			
			int found = 0;
			for(int i = 0; i < data.getUsers().size(); i++) {
				if(data.getUsers().get(i).getUsername().equals(route.getRouteUser())) {
					data.getUsers().get(i).addRoute(route);
					found++;
					break;
				}
			}
			
			if(found == 0) {
				User user = new User();
				user.addRoute(route);
				user.setUsername(route.getRouteUser());
				data.getUsers().add(user);
				
			}
			
			
			for(int i = 0; i < data.getUsers().size(); i++) {
				if(data.getUsers().get(i).getUsername().equals(route.getRouteUser())) {
					User user = data.getUsers().get(i);
					//calculates average data for the user and user's routes
					user.setTotalDistance(user.getTotalDistance() + route.getTotalDistance());
					user.setAverageDistance(user.getTotalDistance() / user.getRoutes().size());
					
					user.setTotalExersciseTime(user.getTotalExersciseTime() + route.getTotalTime());
					user.setAverageExerciseTime(user.getTotalExersciseTime() / user.getRoutes().size());
					
					user.setTotalElavation(user.getTotalElevation() + route.getTotalAscend());
					user.setAverageElevation(user.getTotalElevation() / user.getRoutes().size());
					//calculates average data for all users 
					data.setTotalUserAscend(0);
					data.setTotalUserDistance(0);
					data.setTotalUserTime(0);
					
					for(int j = 0; j < data.getUsers().size(); j++) {
						data.setTotalUserDistance(data.getTotalUserDistance() + data.getUsers().get(j).getAverageDistance());
						//data.setAverageUserDistance(data.getTotalUserDistance() / data.getUsers().size());
						
						data.setTotalUserTime(data.getTotalUserTime() + data.getUsers().get(j).getAverageExerciseTime());
						//data.setAverageUserTime(data.getTotalUserTime() / data.getUsers().size());
						
						data.setTotalUserAscend(data.getTotalUserAscend() + data.getUsers().get(j).getAverageElevation());
						//data.setAverageUserAscend(data.getTotalUserAscend() / data.getUsers().size());
					}
					data.setAverageUserDistance(data.getTotalUserDistance() / data.getUsers().size());
					data.setAverageUserTime(data.getTotalUserTime() / data.getUsers().size());
					data.setAverageUserAscend(data.getTotalUserAscend() / data.getUsers().size());
					
					this.sendUser.setUsername(data.getUsers().get(i).getUsername());
					this.sendUser.setAverageDistance(data.getUsers().get(i).getAverageDistance());
					this.sendUser.setAverageElevation(data.getUsers().get(i).getAverageElevation());
					this.sendUser.setAverageExerciseTime(data.getUsers().get(i).getAverageExerciseTime());
					this.sendUser.setTotalDistance(data.getUsers().get(i).getTotalDistance());
					this.sendUser.setTotalElavation(data.getUsers().get(i).getTotalElevation());
					this.sendUser.setTotalExersciseTime(data.getUsers().get(i).getTotalExersciseTime());
					this.sendData.setAverageUserAscend(data.getAverageUserAscend());
					this.sendData.setAverageUserDistance(data.getAverageUserDistance());
					this.sendData.setAverageUserTime(data.getAvetageUserTime());					
					break;
				}
			}
			
		}
		
	}
	
	
	public Route gpxParse(byte[] gpx) {
		Route new_route = new Route();
		int username = 0;
		int y = 0;
		String word = ""; int save = 0; 
		
		for(int i=0; i < gpx.length; i++) {
			
			if(save == 0) {
				if((char)gpx[i] == ' ') {word = ""; continue;}
				if(username == 0) {
					if((char)gpx[i] == '"' && word.equals("creator=")) {
						save = 1;
						word = "";
						continue;
					}
				}else {
					if(gpx[i] == '"' && (word.equals("lat=") || word.equals("lon="))) {
					save = 1;
					word = "";
					continue;
					}else if(gpx[i]=='>' && word.equals("<ele")) {
						save = 1;
						word = "";
						continue;
					}else if(gpx[i] == 'T') {
						save = 1;
						word = "";
						continue;
					}
		    			
				}
			}else {
				if(y == 0 || y == 1 || y == 2) { 
					if((char)gpx[i] == '"') {
						if(y==0) {
							new_route.setRouteUser(word);
							word = "";
							y++;
							save = 0;
							username = 1;
						}else if(y == 1) {
							Waypoint waypoint = new Waypoint();
							waypoint.setLat(Double.parseDouble(word));
							new_route.addWaypoint(waypoint);
							word = "";
							y++;
							save = 0;
						}else if(y == 2) {
							new_route.getRoutewaypoints().get(new_route.routeSize()-1).setLon(Double.parseDouble(word));
							word = "";
							y++;
							save = 0;
						}
					}
				}else if(y == 3) {
			    	if((char)gpx[i] == '<') {
			    		new_route.getRoutewaypoints().get(new_route.routeSize()-1).setElev(Double.parseDouble(word));
			    		word = "";
			    		y++;
			    		save = 0;
			    	}
			    }else if(y == 4) {
			    	if((char)gpx[i] == 'Z') {
			    		new_route.getRoutewaypoints().get(new_route.routeSize()-1).setTime(word);
			    		word = "";
			    		y = 1;
			    		save = 0;
			    	}
			    }
				
				
			}
			word += String.valueOf((char)gpx[i]);  
		}   	
			
		return new_route;
		
	}
}
