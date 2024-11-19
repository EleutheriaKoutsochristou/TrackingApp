import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkerActions extends Thread {

	Route chunk; // A portion of the route (chunk) to process.
	ObjectInputStream in; // Input stream to receive data from the master.
	ObjectOutputStream out; // Output stream to send results back to the master.

	// Constructor initializes the input and output streams for communication.
	public WorkerActions(Socket connection) {
		try {
			out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to process the chunk and calculate statistics.
	public void run() {
		try {
			// Read the chunk of the route sent by the master.
			this.chunk = (Route) in.readObject();

			// Retrieve waypoints from the chunk for processing.
			ArrayList<Waypoint> waypoints = chunk.getRoutewaypoints();
			double distance = 0; // Total distance for the chunk.
			double time = 0; // Total time for the chunk.
			double speed = 0; // Average speed for the chunk.
			double elevation = 0; // Total elevation gain for the chunk.
			int speed_count = 0; // Counter for speed calculations.

			// Date format for parsing timestamps.
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

			// Process waypoints to calculate distance, time, and elevation.
			for (int i = 1; i < chunk.getRoutewaypoints().size(); i++) {
				// Calculate the distance between consecutive waypoints using the Haversine formula.
				double dist = Math.acos(
						(Math.sin(waypoints.get(i - 1).getLat() * Math.PI / 180) * Math.sin(waypoints.get(i).getLat() * Math.PI / 180)) +
								(Math.cos(waypoints.get(i - 1).getLat() * Math.PI / 180) * Math.cos(waypoints.get(i).getLat() * Math.PI / 180) *
										Math.cos((waypoints.get(i).getLon() * Math.PI / 180) - (waypoints.get(i - 1).getLon() * Math.PI / 180)))
				) * 6371; // Radius of Earth in kilometers.

				distance += dist;

				// Calculate time difference between waypoints.
				try {
					Date date1 = format.parse(waypoints.get(i - 1).getTime());
					Date date2 = format.parse(waypoints.get(i).getTime());
					double diff = (date2.getTime() - date1.getTime()) / (60.0 * 60.0 * 1000.0); // Convert milliseconds to hours.
					time += diff;

					// Calculate speed for this segment if time > 0.
					if (diff > 0) {
						speed += (dist / diff);
						speed_count++;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

				// Calculate elevation gain.
				if (waypoints.get(i - 1).getElev() < waypoints.get(i).getElev()) {
					elevation += (waypoints.get(i).getElev() - waypoints.get(i - 1).getElev());
				}
			}

			// Calculate the average speed for the chunk.
			if (speed_count > 0) {
				speed /= speed_count;
			}

			// Update the chunk's statistics.
			chunk.setTotalDistance(distance);
			chunk.setTotalTime(time);
			chunk.setAverageSpeed(speed);
			chunk.setTotalAscend(elevation);

			// Send the processed chunk back to the master.
			out.writeObject(chunk);
			out.flush();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				// Close streams to release resources.
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
