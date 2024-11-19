import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Master {
	// ServerSocket for listening to incoming connections
	ServerSocket server;
	Socket providerSocket; // Client connection socket
	MasterData data; // Shared data structure containing statistics
	int port_listen; // Port to listen on
	int worker_amount; // Number of worker nodes
	int chunk_size; // Number of data points per chunk
	ArrayList<String> workerips; // List of worker IP addresses
	ArrayList<Integer> workerports; // List of worker ports

	// Constructor initializes master with worker details and chunk size
	public Master(int worker_amount, int chunk_size, String[] args) {
		this.worker_amount = worker_amount;
		this.chunk_size = chunk_size;
		this.workerips = new ArrayList<>();
		this.workerports = new ArrayList<>();

		int port = 0;
		for (int i = 3; i < args.length; i++) { // Read worker IP and port pairs
			if (port == 0) {
				this.workerips.add(args[i]);
				port++;
			} else if (port == 1) {
				this.workerports.add(Integer.parseInt(args[i]));
				port--;
			}
		}
	}

	// Method to start the server and handle incoming client connections
	void openMaster(int port_listen) {
		this.port_listen = port_listen;

		try {
			server = new ServerSocket(this.port_listen); // Open server socket
			data = new MasterData(); // Initialize shared data structure

			while (true) {
				providerSocket = server.accept(); // Wait for a client connection
				// Handle each client in a separate thread
				Thread t = new MasterActions(providerSocket, data, this.workerips, this.workerports, this.worker_amount, this.chunk_size);
				t.start();
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				providerSocket.close(); // Ensure socket is closed on exit
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	// Main method to start the Master node
	public static void main(String[] args) {
		// Initialize Master with command-line arguments
		Master master = new Master(Integer.parseInt(args[1]), Integer.parseInt(args[2]), args);
		master.openMaster(Integer.parseInt(args[0]));
	}
}
