package App;

import java.util.ArrayList;
import java.io.Serializable;

public class User implements Serializable {
	
	private String username;
	private ArrayList<Route> user_routes;
	private double totalExersciseTime;
	private double totalDistance;
	private double totalElevation;
	private double averageExersciseTime;
	private double averageDistance;
	private double averageElevation;
	
	
	public User() {
		this.username = "";
		this.user_routes = new ArrayList<Route>();
		this.totalExersciseTime = 0;
		this.totalElevation = 0;
		this.averageDistance = 0;
		this.averageDistance = 0;
		this.averageElevation = 0;
		this.averageExersciseTime = 0;
	}
	
	public String getUsername() {return this.username;}
	public void setUsername(String username) {this.username = username;}
	public ArrayList<Route> getRoutes(){return this.user_routes;}
	public void addRoute(Route route) {
		this.user_routes.add(route);
	}
	public double getAverageExerciseTime() {return this.averageExersciseTime;}
	public void setAverageExerciseTime(double time) {this.averageExersciseTime = time;}
	public double getAverageDistance() {return this.averageDistance;}
	public void setAverageDistance(double distance) {this.averageDistance = distance;}
	public double getAverageElevation() {return this.averageElevation;}
	public void setAverageElevation(double elevation) {this.averageElevation = elevation;}
	public double getTotalExersciseTime() {return this.totalExersciseTime;}
	public void setTotalExersciseTime(double time) {this.totalExersciseTime = time;}
	public double getTotalDistance() {return this.totalDistance;}
	public void setTotalDistance(double distance) {this.totalDistance = distance;}
	public double getTotalElevation() {return this.totalElevation;}
	public void setTotalElavation(double elevation) {this.totalElevation = elevation;}
}
