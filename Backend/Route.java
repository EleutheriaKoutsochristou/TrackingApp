package App;

import java.util.ArrayList;
import java.io.Serializable;

public class Route implements Serializable  {

	private String route_user;
	private ArrayList<Waypoint> route_waypoints;
	private double total_distance;
	private double average_speed;
	private double total_ascend;
	private double total_time;
	private int chunkid = 0;
	
	
	public Route() {
		this.route_waypoints = new ArrayList<Waypoint>();
		this.total_distance = 0;
		this.average_speed = 0;
		this.total_ascend = 0;
		this.total_time = 0;
	}
	
	
	public ArrayList<Waypoint> getRoutewaypoints() {return this.route_waypoints;}
	
	public void addWaypoint(Waypoint waypoint) {
		this.route_waypoints.add(waypoint);
	}
	
	public int routeSize() {return this.route_waypoints.size();}
	public double getTotalDistance() {return this.total_distance;}
	public void setTotalDistance(double distance) {this.total_distance = distance;}
	public double getAverageSpeed() {return this.average_speed;}
	public void setAverageSpeed(double speed) {this.average_speed = speed;}
	public double getTotalAscend() {return this.total_ascend;}
	public void setTotalAscend(double ascend) {this.total_ascend = ascend;}
	public double getTotalTime() {return this.total_time;}
	public void setTotalTime(double time) {this.total_time = time;}
	public String getRouteUser() {return this.route_user;}
	public void setRouteUser(String username) {this.route_user = username;}
	public int getChunkId() {return this.chunkid;}
	public void setChunkId(int id) {this.chunkid = id;}
}
