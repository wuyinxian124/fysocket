package edu.fy.com.channal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
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
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress("localhost", 8877));
			
			new SendThread(socketChannel).start();
			new ReceiveTread(socketChannel).start();

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	class SendThread extends Thread {

		SocketChannel out;

		public SendThread(SocketChannel out) {
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
						String filePath = callback.sendMsg1();
						RandomAccessFile aFile = new RandomAccessFile(filePath, "r");
						FileChannel inChannel = aFile.getChannel();

						//create buffer with capacity of 1024 bytes
						ByteBuffer buf = ByteBuffer.allocate(1024);

						int bytesRead = inChannel.read(buf); //read into buffer.
						while (bytesRead != -1) {

							buf.flip(); // make buffer ready for read
							while (buf.hasRemaining()) {
								out.write(buf);
							}
							buf.clear(); // make buffer ready for writing
							bytesRead = inChannel.read(buf);
						}
						aFile.close();
					}

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	class ReceiveTread extends Thread {
		SocketChannel socketChannelR;

		public ReceiveTread(SocketChannel socketChannel) {

			this.socketChannelR = socketChannel;
		}

		@Override
		public void run() {
			try {
				ByteBuffer readBuffer = ByteBuffer.allocate(1000);
				int length;
				String message = "";
				while (true) {
					//TimeUnit.SECONDS.sleep(1);
					//SocketChannel channel = (SocketChannel) key.channel();
					readBuffer.clear(); // 清空 缓存
					try{
					length = socketChannelR.read(readBuffer);
					} catch (IOException e){
						System.out.println("Reading problem, closing connection");
						socketChannelR.close();
						return;
					}
					if (length > 0){
					}else{
						readBuffer.flip(); // 调转 由可写，变为可读。
/*						byte[] buff = new byte[1024];
						readBuffer.get(buff, 0, length);*/
						System.out.println("Server said: "+Charset.defaultCharset().decode(readBuffer));
						callback.sendclallback0(Charset.defaultCharset().decode(readBuffer)+"");
					}
					
/*		            while ((length = socketChannelR.read(readBuffer)) > 0) {
		                // flip the buffer to start reading
		            	readBuffer.flip();
		                message += Charset.defaultCharset().decode(readBuffer);
		                System.out.println("client read =="+message);
		            }*/

				}

			} catch (  IOException e) {
				e.printStackTrace();
			}
		}
	}

}


