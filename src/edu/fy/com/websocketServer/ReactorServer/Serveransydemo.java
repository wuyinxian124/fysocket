package edu.fy.com.websocketServer.ReactorServer;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.fy.com.websocketServer.ReactorServer.Serveransydemo.socketCallbackInterface;
import edu.fy.com.websocketServer.simpleDemo.BinaryUtil;
import edu.fy.com.websocketServer.simpleDemo.ResponseFrame;

public class Serveransydemo {

    public static void main(String[] args) {

        try {
			new Serveransydemo().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
	private static final String TAG = "\r\n";
    
    private void start() throws IOException{
		ServerSocket server = new ServerSocket(8877);
		while (true) {
			try {
				Socket ss = server.accept();
				if (handlerShake0(ss))
					new Thread(new ServerThread1(ss)).start();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
    	
    }
    
    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream socketOut = socket.getOutputStream();
        return new PrintWriter(socketOut, true);
    }
    
    public boolean handlerShake0(Socket serverfork) throws NoSuchAlgorithmException, IOException{
    	boolean hasHandshake = false;
    	 InputStream in = serverfork.getInputStream();
         
         PrintWriter pw = getWriter(serverfork);
         //读入缓存
         byte[] buf = new byte[1024];
         //读到字节
         int len = in.read(buf, 0, 1024);
         //读到字节数组
         byte[] res = new byte[len];
         System.arraycopy(buf, 0, res, 0, len);
         String key = new String(res);
         if(key.indexOf("Key") > 0){
             //握手
             key = key.substring(0, key.indexOf("==") + 2);
             key = key.substring(key.indexOf("Key") + 4, key.length()).trim();
             key+= "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
             MessageDigest md = MessageDigest.getInstance("SHA-1");  
             md.update(key.getBytes("utf-8"), 0, key.length());
             byte[] sha1Hash = md.digest();  
                 sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();  
             key = encoder.encode(sha1Hash);  
             pw.println("HTTP/1.1 101 Switching Protocols");
             pw.println("Upgrade: websocket");
             pw.println("Connection: Upgrade");
             pw.println("Sec-WebSocket-Accept: " + key);
             pw.println();
             pw.flush();
             hasHandshake = true;
         }
    	return hasHandshake;
    }
    
    class ServerThread1 implements Runnable ,socketCallbackInterface{

    	private Socket serverfork;
    	
    	public ServerThread1(Socket serverfork){
    		this.serverfork = serverfork;
    		System.out.println("接受一个连接……");
    	}
    	
		@Override
		public void run() {
			try {
	           // BufferedReader in = new BufferedReader(new InputStreamReader(serverfork.getInputStream()));
	           // PrintWriter out=new PrintWriter(serverfork.getOutputStream());
	            
	            DataInputStream ins = new DataInputStream(serverfork.getInputStream());
	            DataOutputStream outs = new DataOutputStream(serverfork.getOutputStream());
	            
	            new Thread(new ReciveThread2(ins,this,outs)).start();
	            new Thread(new SendThread2(outs,this)).start();
	            //new ReciveTread2(in,out).start();
	            //new SendThread1(out).start();
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void closeSocket() {

			try {
				if (serverfork != null)
					serverfork.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
    	
    }
    
    interface socketCallbackInterface{
    	public void closeSocket();
    }

}


class SendThread2 implements  Runnable {

	private DataOutputStream outss;
	private Charset charset = Charset.forName("UTF-8");
	private socketCallbackInterface callback;
	public SendThread2(DataOutputStream out,socketCallbackInterface callback){
		this.outss = out;
		this.callback = callback;
	}
	
	@Override
	public void run() {

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int jj = 0;
		try {
			while (true) {

				TimeUnit.SECONDS.sleep(4);

				ByteBuffer buf = ByteBuffer.allocate(1024);
				buf.put(("Server  " + sf.format(new Date()) + "\n\t"
						+ "hello client" + jj++ + " ;").getBytes());
				responseClient(buf,true);
				printRes(buf.array());

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			callback.closeSocket();
		}

	
		
	}
	
	private void responseClient(ByteBuffer byteBuf, boolean finalFragment)
			throws IOException {
		//OutputStream out = socket.getOutputStream();
		int first = 0x00;
		// 是否是输出最后的WebSocket响应片段
		if (finalFragment) {
			first = first + 0x80;
			first = first + 0x1;
		}
		outss.write(first);

		if (byteBuf.limit() < 126) {
			outss.write(byteBuf.limit());
		} else if (byteBuf.limit() < 65536) {
			outss.write(126);
			outss.write(byteBuf.limit() >>> 8);
			outss.write(byteBuf.limit() & 0xFF);
		} else {
			// Will never be more than 2^31-1
			outss.write(127);
			outss.write(0);
			outss.write(0);
			outss.write(0);
			outss.write(0);
			outss.write(byteBuf.limit() >>> 24);
			outss.write(byteBuf.limit() >>> 16);
			outss.write(byteBuf.limit() >>> 8);
			outss.write(byteBuf.limit() & 0xFF);

		}

		// Write the content
		outss.write(byteBuf.array(), 0, byteBuf.limit());
		outss.flush();
	}
	
	private void printRes(byte[] array) {
		ByteArrayInputStream byteIn = new ByteArrayInputStream(array);
		InputStreamReader reader = new InputStreamReader(byteIn,
				charset.newDecoder());
		int b = 0;
		String res = "";
		try {
			while ((b = reader.read()) > 0) {
				res += (char) b;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(res);
	}

}

class ReciveThread2 implements Runnable {

	private DataInputStream ins;
	private socketCallbackInterface socketOptions;
	private OutputStream out;
	private Charset charset = Charset.forName("UTF-8");

	public ReciveThread2(DataInputStream ins,
			socketCallbackInterface socketOptions,OutputStream out) {
		this.ins = ins;
		this.socketOptions = socketOptions;
		this.out = out;
	}

	@Override
	public void run() {

		try {

			// 接收数据
			byte[] first = new byte[1];
			// 这里会阻塞
			int read = ins.read(first, 0, 1);
			while (read > 0) {
				int b = first[0] & 0xFF;
				// 1为字符数据，8为关闭socket
				byte opCode = (byte) (b & 0x0F);

				if (opCode == 8) {
					// socket.getOutputStream().close();
					socketOptions.closeSocket();
					break;
				}
				b = ins.read();
				int payloadLength = b & 0x7F;
				if (payloadLength == 126) {
					byte[] extended = new byte[2];
					ins.read(extended, 0, 2);
					int shift = 0;
					payloadLength = 0;
					for (int i = extended.length - 1; i >= 0; i--) {
						payloadLength = payloadLength
								+ ((extended[i] & 0xFF) << shift);
						shift += 8;
					}

				} else if (payloadLength == 127) {
					byte[] extended = new byte[8];
					ins.read(extended, 0, 8);
					int shift = 0;
					payloadLength = 0;
					for (int i = extended.length - 1; i >= 0; i--) {
						payloadLength = payloadLength
								+ ((extended[i] & 0xFF) << shift);
						shift += 8;
					}
				}

				// 掩码
				byte[] mask = new byte[4];
				ins.read(mask, 0, 4);
				int readThisFragment = 1;
				ByteBuffer byteBuf = ByteBuffer.allocate(payloadLength + 10);
				byteBuf.put("echo: ".getBytes("UTF-8"));
				while (payloadLength > 0) {
					int masked = ins.read();
					masked = masked
							^ (mask[(int) ((readThisFragment - 1) % 4)] & 0xFF);
					byteBuf.put((byte) masked);
					payloadLength--;
					readThisFragment++;
				}
				byteBuf.flip();
				responseClient(byteBuf, true);
				printRes(byteBuf.array());
				ins.read(first, 0, 1);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void responseClient(ByteBuffer byteBuf, boolean finalFragment)
			throws IOException {
		//OutputStream out = socket.getOutputStream();
		int first = 0x00;
		// 是否是输出最后的WebSocket响应片段
		if (finalFragment) {
			first = first + 0x80;
			first = first + 0x1;
		}
		out.write(first);

		if (byteBuf.limit() < 126) {
			out.write(byteBuf.limit());
		} else if (byteBuf.limit() < 65536) {
			out.write(126);
			out.write(byteBuf.limit() >>> 8);
			out.write(byteBuf.limit() & 0xFF);
		} else {
			// Will never be more than 2^31-1
			out.write(127);
			out.write(0);
			out.write(0);
			out.write(0);
			out.write(0);
			out.write(byteBuf.limit() >>> 24);
			out.write(byteBuf.limit() >>> 16);
			out.write(byteBuf.limit() >>> 8);
			out.write(byteBuf.limit() & 0xFF);

		}

		// Write the content
		out.write(byteBuf.array(), 0, byteBuf.limit());
		out.flush();
	}

	private void printRes(byte[] array) {
		ByteArrayInputStream byteIn = new ByteArrayInputStream(array);
		InputStreamReader reader = new InputStreamReader(byteIn,
				charset.newDecoder());
		int b = 0;
		String res = "";
		try {
			while ((b = reader.read()) > 0) {
				res += (char) b;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(res);
	}

}

class ReciveThread1 extends Thread {
	BufferedReader in;
	PrintWriter out;
/*	Socket client;

	public ReciveTread1( BufferedReader in,
			PrintWriter out,  Socket client) {
		this.in = in;
		this.client = client;
		this.out = out;
	}*/
	
	public ReciveThread1(BufferedReader in,PrintWriter out){
		this.in = in;
		this.out = out;
	}

	@Override
	public void run() {
		try {
			String info = "";
			while (true) {

				while ((info = in.readLine()) != null) {
					System.out.println(info);
					synchronized (out) {
						out.print("ok recive: "+ " ** ");
						out.flush();
					}
					//info = in.readLine();
				}

				if (in.readLine().equals("end")) {
					break;
				}
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}


class SendThread1 extends Thread {

	PrintWriter out;

	public SendThread1(PrintWriter out) {
		this.out = out;
	}

	@Override
	public void run() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int jj = 0;
		try {
			while (true) {

				TimeUnit.SECONDS.sleep(7);
				synchronized (out) {
					out.print("Server  " + sf.format(new Date()) + "\n\t"
							+ "hello client" + jj++ +" ;");
					out.flush();
				}

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}