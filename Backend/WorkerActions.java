package App;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkerActions extends Thread{
			
	Route chunk;
	ObjectInputStream in;
	ObjectOutputStream out;
	
	public WorkerActions(Socket connection) {
		try {	
			out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
		try {
			 
			this.chunk = (Route) in.readObject();
			
			
			ArrayList<Waypoint> waypoints = chunk.getRoutewaypoints();
			double distance = 0;
			double time = 0;
			double speed = 0;
			double elevation = 0;
			int speed_count = 0;
			     
			
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			
			for(int i = 1; i < chunk.getRoutewaypoints().size(); i++) {
				
				double dist = Math.acos((Math.sin(waypoints.get(i-1).getLat() * Math.PI/180)  * Math.sin(waypoints.get(i).getLat() * Math.PI/180)) + (Math.cos(waypoints.get(i-1).getLat() * Math.PI/180) * Math.cos(waypoints.get(i).getLat() * Math.PI/180) * Math.cos((waypoints.get(i).getLon() * Math.PI/180) - (waypoints.get(i-1).getLon()* Math.PI/180)))) * 6371;       
				distance += dist;
				
				Date date1 = format.parse(waypoints.get(i-1).getTime());
				Date date2 = format.parse(waypoints.get(i).getTime());
				double tim =  date2.getTime() - date1.getTime();
				tim = tim / (60 * 60 * 1000) % 24;
				time += tim; 
				speed += dist / tim;
				speed_count++;
				
				if(waypoints.get(i).getElev()> waypoints.get(i-1).getElev()) {
					elevation += waypoints.get(i).getElev() - waypoints.get(i-1).getElev();
				}
				
			}
			
			speed = speed / speed_count;
			
			chunk.setTotalDistance(distance);
			chunk.setTotalTime(time);
			chunk.setAverageSpeed(speed);
			chunk.setTotalAscend(elevation);
			
			
			out.writeObject(chunk);
			out.flush();
		
		
		}catch (IOException e) {
            e.printStackTrace();
        }catch(ClassNotFoundException e){
        	throw new RuntimeException(e);
        } catch (ParseException e) {
			e.printStackTrace();
		}finally {
        	try {
        		in.close();
        		out.close();
        	} catch (IOException ioException) {
        		ioException.printStackTrace();
        	}
        }
	}
	
}
