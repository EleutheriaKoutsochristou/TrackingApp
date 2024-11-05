package App;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class Master {
	
	ServerSocket server;
	Socket providerSocket;
	MasterData data;
	int port_listen;
	int worker_amount;
	int chunk_size;
	ArrayList<String> workerips;
	ArrayList<Integer> workerports;
	
	public Master(int worker_amount, int chunk_size, String[] args) {
		this.worker_amount = worker_amount;
		this.chunk_size = chunk_size;
		this.workerips = new ArrayList<String>();
		this.workerports = new ArrayList<Integer>();
		int port = 0;
		for(int i = 3; i < args.length; i++) {
			if(port == 0) {
				this.workerips.add(args[i]);
				port++;
			}else if(port == 1) {
				this.workerports.add(Integer.parseInt(args[i]));
				port--;
			}
		}
	}
	
	void openMaster(int port_listen) {
		
		this.port_listen = port_listen;
		
		try {
			
			server = new ServerSocket(this.port_listen);
			data = new MasterData();
			
			while(true) {
				providerSocket = server.accept();
				
				Thread t = new MasterActions(providerSocket, data, this.workerips, this.workerports, this.worker_amount, this.chunk_size);
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
		Master master = new Master(Integer.parseInt(args[1]), Integer.parseInt(args[2]), args);
		
		master.openMaster(Integer.parseInt(args[0]));
	}

}
