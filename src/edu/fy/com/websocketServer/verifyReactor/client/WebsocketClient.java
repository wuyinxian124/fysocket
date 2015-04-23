package edu.fy.com.websocketServer.verifyReactor.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import edu.fy.com.websocketServer.verifyReactor.util.ClientException;

public class WebsocketClient {

	private IClientCallinface clientIm;
	
	private final static int PORT = 8866;
	private final static String HOST = "localhost";
	public WebsocketClient(){
		
	}
	public WebsocketClient(IClientCallinface clientF){
		this.clientIm = clientF;
	}
	

	
	public IWebsocketClientObject createConnection(){
		return new ClientImpl();
	}
	
	final class ClientImpl implements IWebsocketClientObject{

		private Socket clientS ;
		private final static String USERNAME = "client";
		private final static String VERCODE = "20141227";
		
		private DataOutputStream dout;
		private DataInputStream din;
		private ClientImpl(){
			try {
				init();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private final void init() throws UnknownHostException, IOException{
			// 初始化连接
			// 发送连接请求  验证用户
			if(clientS == null)
				clientS  = new Socket(HOST,PORT);
			if(verifyUser()){
				// 验证用户
				System.out.println("用户验证通过！！");
				new Thread(new ReciveThread4()).start();
			}
		}
		private boolean pass = false;
		
		private boolean verifyUser(){
			
			try {
				dout = new DataOutputStream(clientS.getOutputStream());
				din  = new DataInputStream(clientS.getInputStream());
				String verfUser = USERNAME+ ":"+VERCODE;
				dout.writeInt(verfUser.length());
				dout.flush();
				dout.write(verfUser.getBytes());
				dout.flush();
				
				int readLen = din.readInt();
				String ack = din.readUTF();
				if("ackPass".equals(ack)){
					pass = true;
				}
			} catch (IOException e) {
				e.printStackTrace();
				clientIm.onError(e.toString());
			}
			return pass;
			
		}
		

		@Override
		public void onClose() {
			
			try {
				pass = false;
				clientIm.onMessage("server 关闭");
				clientS.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void sendMsgText(String message) {
			if(pass)
				sendMessageText(message);
		}
		
		private void sendMessageText(String message){
			try {
				dout.writeInt(message.length());
				dout.flush();
				dout.writeUTF(message);
				dout.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		private void onMessageText(String message){
			 clientIm.onMessage(message);
		}
		
		class ReciveThread4 implements Runnable{

			
			public ReciveThread4(){
				
			}
			
			@Override
			public void run() {
				try{
					int readLen = 0;
					//ByteBuffer buf = ByteBuffer.allocate(1024);
					while(true){

						if((readLen = din.readInt()) != -1){
							String msg = din.readUTF();
							onMessageText(msg);
						}

					}
				}catch(  IOException e){
					e.printStackTrace();
				}

			}
			
		}
	}
	
}


