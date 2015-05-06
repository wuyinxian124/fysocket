package com.fy.msgsys.client.api;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import com.fy.msgsys.client.api.exception.IOSocketException;
import com.fy.msgsys.client.api.exception.AuthException;
import com.fy.msgsys.client.api.util.ExceptionConstants;
import com.fy.msgsys.servernetty.util.SocketConstant;

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
		private final static String USERNAME = "webband";
		private final static String VERCODE = "20141227";
		
		private DataOutputStream dout;
		private DataInputStream din;
		
		private ClientImpl(){
			
			try {
				init();
			} catch (IOException e) {
				System.out.println("初始化 socket 连接失败 （client）");
			}  
		}
		private final void init() throws  IOException{
			// 初始化连接
			// 发送连接请求  验证用户
			if(clientS == null)
				clientS  = new Socket(HOST,PORT);
			if(verifyUser()){
				// 验证用户
				System.out.println("吾托帮web用户身份被通过！！");
				new Thread(new ReciveThread4()).start();
			}
		}
		private boolean pass = false;
		
		/**
		 * java API 接收验证用户
		 * @return
		 */
		private boolean verifyUser(){
			
			try {
				dout = new DataOutputStream(clientS.getOutputStream());
				din  = new DataInputStream(clientS.getInputStream());
				String verfUser = USERNAME+ SocketConstant.splitNormalUV.getRssURL()+VERCODE;
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
				System.out.println("验证java API 调用者身份过程中 发生异常");
				clientIm.onError(e.toString());
			}
			return pass;
			
		}
		


		@Override
		public void sendMsgText(String message) throws  AuthException {
			if(pass)
				try {
					sendMessageText(message);
				} catch (IOException e) {
					abortError("写（主）线程 突发异常");
				}
			else 
				throw new  AuthException(ExceptionConstants.SendError.getRss(),"标志（pass）未没有开启，不能发送文本消息");//clientIm.onError("标志（pass）未没有开启，不能发送文本消息");;
		}
		
		@Override
		public void close() throws IOException  {
			
				pass = false;
				clientIm.onMessage("server 关闭");
				clientS.close();
				clientIm.onClosed("清理连接资源");
		}
		
		public void closed() {
			try {
				pass = false;
				clientIm.onMessage("server 关闭");
				clientIm.onError("IO 异常，socket 已经关闭");
				clientS.close();
			} catch (IOException e) {
				System.out.println("socket client  关闭发送异常");
			}
		}
		/**
		 * 通过socket连接，发送文本消息
		 * @param message
		 * @throws IOException
		 */
		private void sendMessageText(String message) throws IOException{

				dout.writeInt(message.length());
				dout.flush();
				dout.writeUTF(message);
				dout.flush();

		}
		
		/**
		 * 接收消息
		 * <br> 回调onMessage方法
		 * @param message
		 * @throws IOException
		 */
		private void onMessageText(String message) throws IOException{
			 clientIm.onMessage(message);
		}
		
		private void abortError(String msg){
			clientIm.onError(msg);
			closed();
		}
		
		/**
		 * java socket api 接收socket server 回复
		 * @author Administrator
		 *
		 */
		final class ReciveThread4 implements Runnable{

			
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
					System.out.println("接收消息过程中，socket 发生异常");
					abortError("读取子线程，突然发生异常");
					//throw new IOSocketException(ExceptionConstants.OutputError.getRss(), "读取子线程，突然发送异常");
				}
				System.out.println("结束读子线程，然后关闭写（主）线程");
				
			}
			
		}


	}
	
}


