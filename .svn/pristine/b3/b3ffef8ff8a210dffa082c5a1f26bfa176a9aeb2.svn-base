package edu.fy.com.ansyDemo;

import java.io.BufferedReader;
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
			new ReceiveTread(client, in, out).start();

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
						TimeUnit.SECONDS.sleep(1);
						synchronized (out) {
							out.println(callback.sendMsg0(jj++ +""));
							out.flush();
						}

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

	class ReceiveTread extends Thread {
		BufferedReader in;
		PrintWriter out;
		Socket client;

		public ReceiveTread(Socket client, BufferedReader in, PrintWriter out) {
			this.in = in;
			this.client = client;
			this.out = out;

		}

		@Override
		public void run() {
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
		}
	}

}


