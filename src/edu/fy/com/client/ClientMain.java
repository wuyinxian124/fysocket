package edu.fy.com.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class ClientMain  implements ClientCallback {


	
	public static void main(String[] args) {
		new ClientMain().start();
	}
	
	private int mi = 0;
	private ClientInter client;
    
	private String createMsg(){
		String msg = "messageing msg" + mi++;
		return msg;
	}
	
	private void start() {

		client = new ClientTest1(this);
		// 启动连接
		if(client.connect()){
			// 每秒钟发送一条消息
			send0();
		}
		try {
			while(true)
				client.recive();
		} catch (IOException e) {
			e.printStackTrace();
		}

/*		String n;
		Scanner input = new Scanner(System.in);
	    System.out.println("Input an integer"); 
	 
	    while ((n = input.nextLine()) != "") {
	      System.out.println("You entered " + n);
	      System.out.println("Input an integer");
	    }
	 
	    System.out.println("Out of loop");
	  */
		
	}
	
	private void send0(){
		new Thread(
				
				new Runnable() {
			
			@Override
			public void run() {
				while(true){
					try {
						TimeUnit.SECONDS.sleep(1);
						client.send(createMsg());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			}
		}).start();
		 
	}

	@Override
	public String recive0(String msg) {
		System.out.println("recive msg from server =" + msg);
		return null;
	}


	
}


