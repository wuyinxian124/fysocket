package edu.fy.com.parttern.dispatch;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Client {
	
	private SocketCallBack callback;
	public Client(SocketCallBack callback ){
		this.callback = callback;
	}
	public void start(){

		try {
			Socket client = new Socket("localhost", 8877);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream());

			new SendThread(out).start();
			//new ReceiveTread(client, in, out).start();
			new ReciveTread(new DataInputStream(client.getInputStream())).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	
	}
	
	class SendThread extends Thread {

		PrintWriter out;

		public SendThread(PrintWriter out) {
			this.out = out;

		}

		@Override
		public void run() {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int jj = 0;
			try {
				while (true) {

					if(callback.sendACK()){
						TimeUnit.SECONDS.sleep(2);
						out.print(callback.sendMsg0(jj++ +""));
						out.flush();
						callback.setACK(false);
					}

/*					out.println("client  " + sf.format(new Date()) + "\n\t"
								+ "hello server" + jj++);
					
					out.flush();*/
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	class ReciveTread extends Thread {
		BufferedReader in;
		PrintWriter out;
		Socket client;
		private DataInputStream ins;

		public ReciveTread(Socket client, BufferedReader in, PrintWriter out) {
			this.in = in;
			this.client = client;
			this.out = out;

		}
		
		public ReciveTread(DataInputStream ins){
			this.ins = ins;
		}

		@Override
		public void run() {
			int len;
			byte[] buffer = new byte[1024];
			while(true){
			    len = -1;  
			    try {
					while ((len = ins.read(buffer)) > 0) {  
						//System.out.write(buffer, 0, len);
						String info = new String(buffer, 0, len);
						System.out.println(info);
						callback.sendclallback0(info);
						buffer = new byte[1024];
					}
				} catch (IOException e) {
					e.printStackTrace();			if(ins != null)
						try {
							ins.close();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					
					break;
				}
			}
			System.out.println("over");

			
			/*
			try {
				while (true) {
					String info = in.readLine();
					while (info != null) {
						System.out.println(info);
						// 回调
						callback.sendclallback0(info);
						info = in.readLine();
					}
					if (in.readLine().equals("end")) {
						break;
					}
				}
				in.close();
				out.close();
				if (client != null) {
					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		*/
			
		}
		
		
	}

}


