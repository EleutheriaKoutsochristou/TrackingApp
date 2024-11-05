package App;

import java.io.Serializable;
import java.util.ArrayList;

public class MasterData implements Serializable {

	private int connections;
	private ArrayList<User> users;
	private double totalUserDistance;
	private double totalUserTime;
	private double totalUserAscend;
	private double averageUserDistance;
	private double averageUserTime;
	private double averageUserAscend;
	
	public MasterData() {
		this.users = new ArrayList<User>();
		this.averageUserAscend = 0;
		this.averageUserDistance = 0;
		this.averageUserTime = 0;
	}
	
	
	public int getConnections() {return this.connections;}
	public void setConnections(int connections) {this.connections = connections;}
	public ArrayList<User> getUsers(){return this.users;} 
	public double getAverageUserDistance() {return this.averageUserDistance;}
	public void setAverageUserDistance(double distance) {this.averageUserDistance = distance;}
	public double getAvetageUserTime() {return this.averageUserTime;}
	public void setAverageUserTime(double time) {this.averageUserTime = time;}
	public double getAverageUserAscend() {return this.averageUserAscend;}
	public void setAverageUserAscend(double ascend) {this.averageUserAscend = ascend;}
	public double getTotalUserDistance() {return this.totalUserDistance;}
	public void setTotalUserDistance(double distance) {this.totalUserDistance = distance;}
	public double getTotalUserTime() {return this.totalUserTime;}
	public void setTotalUserTime(double time) {this.totalUserTime = time;}
	public double getTotalUserAscend() {return this.totalUserAscend;}
	public void setTotalUserAscend(double ascend) {this.totalUserAscend = ascend;}
}
	
