package edu.fy.com.ansyDemoPic;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
			Socket client = new Socket(InetAddress.getLocalHost(), 8877);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream());

			new SendThread(new DataOutputStream(client.getOutputStream())).start();
			new ReceiveTread(client, in, out).start();

		} catch (IOException e) {
			e.printStackTrace();
		}

	
	}
	
	class SendThread extends Thread {

		PrintWriter out;
		DataOutputStream out1;
		public SendThread(PrintWriter out) {
			this.out = out;

		}
		
		public SendThread(DataOutputStream out) {
			this.out1 = out;

		}

		@Override
		public void run() {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int jj = 0;
			try {
				while (true) {

					if(callback.sendACK()){
						TimeUnit.SECONDS.sleep(1);
						String filePath = callback.sendMsg1();
						DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
						File fi = new File(filePath);
			            long len = fi.length();
			            int read = 0;
			            System.out.println("文件名称:" + fi.getName());
			            System.out.println("文件大小:" + len);
			            out1.writeLong(len);
			            out1.flush();
			            int bufferSize = 8192;
			            byte[] buf = new byte[bufferSize];
/*			            while (true) {
			                int read = 0;
			                if (fis != null) {
			                    read = fis.read(buf);
			                }
			                if (read == -1) {
			                    break;
			                }
			                System.out.println("文件传输中...");
			                out1.write(buf, 0, read);
			            }*/
			            
		                while ((read = fis.read(buf)) != -1) {  
		                    out1.write(buf, 0, read);  
		                }  
		                out1.flush();
			            System.out.println("文件传输完成");
			            callback.setACK(false);
					}

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
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


