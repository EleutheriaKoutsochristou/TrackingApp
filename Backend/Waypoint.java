package App;

import java.io.Serializable;

public class Waypoint implements Serializable {
	
	
	private double lon;
	private double lat;
	private double elev;
	private String time;
	
	public double getLon() {return this.lon;}
	public void setLon(double lon) {this.lon = lon;}
	public double getLat() {return this.lat;}
	public void setLat(double lat) {this.lat = lat;}
	public double getElev() {return this.elev;}
	public void setElev(double elev) {this.elev = elev;}
	public String getTime() {return this.time;}
	public void setTime(String time) {this.time = time;}

}
