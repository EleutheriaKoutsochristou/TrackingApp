package App;

import java.io.*;
import java.net.*;

public class Worker {	
	
	ServerSocket server;
	Socket providerSocket;
	int port;
	
	
	
	public void openWorker(int port) {
		
		this.port = port;
		
		try {
			
			server = new ServerSocket(this.port);
			
			while(true) {
				providerSocket = server.accept();
				
				Thread t = new WorkerActions(providerSocket);
                t.start();
			}
			
		} catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
        	try {
        		providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
		
	}
	
	
	
	public static void main(String[] args) {
		
		new Worker().openWorker(Integer.parseInt(args[0]));

	}

}
