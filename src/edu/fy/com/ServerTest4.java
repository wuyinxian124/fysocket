package edu.fy.com;

import java.util.concurrent.*;
import java.io.IOException;
import java.net.*;
import java.nio.*;

public class ServerTest4 {

	public final static int PORT = 8877;

	public static void main(String[] args) {

		new ServerTest4().start();
	}
	
	private void start(){
		
		ExecutorService pool = Executors.newFixedThreadPool(50);
		try  {
			ServerSocket server = new ServerSocket(PORT);
			while (true) {
				Socket connection = server.accept();
				pool.execute(new ConnectTask(connection));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class ConnectTask extends Thread {

		private Socket connection;
		
		public ConnectTask(Socket connection ){
			setDaemon(true);
			this.connection = connection;
		}
		
		public void run() {
			while(true){
				
				
			}
		}

	}

}
