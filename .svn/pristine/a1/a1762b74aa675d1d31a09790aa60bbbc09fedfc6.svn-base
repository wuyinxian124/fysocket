package edu.fy.com.websocketServer.simpleDemo;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;



/**
 * 
 * @功能 WebServerSocket DEMO
 * @作者 蛋蛋
 */
public class WebServerSocket
{	
	private static final String TAG = "\r\n";
	public WebServerSocket(ServerSocket serverSocket)
	{
		this.serverSocket = serverSocket;
	}
	ServerSocket serverSocket = null;
	/**
	 *  接收并握手,如果失败,则返回NULL,视为无效的WebSocket,
	 * @return
	 * @throws IOException 
	 */
	public Socket accept() throws IOException
	{
		Socket socket = serverSocket.accept();
		return startHandshake(socket) ? socket : null;
	}
	/**
	 * 握手
	 * @param socket
	 * @return
	 * @throws IOException
	 */
	private boolean startHandshake(Socket socket) throws IOException
	{
		socket.setSoTimeout(10000);
		InputStream in = socket.getInputStream();
		
		ByteArrayOutputStream requestStream = new ByteArrayOutputStream(){
			// 原方法内容 return Arrays.copyOf(buf, count); 下面需要根据\r\n\r\n内容,进行跳出,避免重复创建对象
			@Override
			public synchronized byte[] toByteArray()
			{
			    return buf;
			}
		};
		
		while (Boolean.TRUE)
		{
			requestStream.write((byte)in.read());
			byte[] data = requestStream.toByteArray();
			//如果当前数据以 \r\n\r\n结尾,则跳出
			if (data[requestStream.size() -  1] == 0x0A && data[requestStream.size() - 2] == 0x0D && data[requestStream.size() - 3] == 0x0A && data[requestStream.size() - 4] == 0x0D)
			{
				break;
			}
		}
		
		byte[] coda = new byte[8];
/*		//报文后跟的8个字节 稍后需要他计算MD5 
		byte[] coda = new byte[8];
		if (in.read(coda) != 8)
		{
			return false;
		}*/
		
		byte[] header = Arrays.copyOf(requestStream.toByteArray(), requestStream.size());
		System.out.println("============================请求报文============================");
		System.out.println(new String(header));
		System.out.println("结尾的8个字节:" +BinaryUtil.formatBytes(coda));
		System.out.println("=======================R=====分隔线=============================");
		//解析请求报文
		Map<String, String> headerMap = parseRequestHeader(header);
		
		
		//响应请求消息
		System.out.println("============================响应消息=============================");
		ResponseFrame frame = buildResponseMsg(headerMap,coda);
		
		//拼装的 报文头
		System.out.println(frame.getHeader());
		System.out.println("MD5(String)===" + new String(frame.getMd5()));
		System.out.println("MD5(bytes)===" + BinaryUtil.formatBytes(frame.getMd5()));
		
		//响应握手
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(frame.getHeader().getBytes());
		baos.write(frame.getMd5());
		socket.getOutputStream().write(baos.toByteArray());
		socket.setSoTimeout(0);
		return true;
	}
	
	/**
	 * 构建响应消息报文实体.
	 * @param header 请求消息的报文键值对
	 * @param coda 请求消息的最后8个字节
	 * @return frame
	 * @throws IOException
	 */
	public static ResponseFrame buildResponseMsg(Map<String,String> header, byte[] coda) throws IOException
	{	
		ResponseFrame frame = new ResponseFrame(); 
		/*
		 * MD5 部分
		 * 将 Sec-WebSocket-Key1 和 Sec-WebSocket-Key2 
		 * 以及 请求报文后面的正文的8个字节 转成byte[]
		 * 再进行MD5签名
		 */
		ByteArrayOutputStream md5Bytes = new ByteArrayOutputStream();
		//协议是 Sec-WebSocket-Key1 和 Sec-WebSocket-Key2 .  我想可能会扩展,就循环了 ^_^!
		for (int i = 1;;i++)
		{
			String key = header.get("Sec-WebSocket-Key" + i);
			if(key == null){break;}
			md5Bytes.write(MD5(key));
		}
		md5Bytes.write(coda);
		byte[] md5 = md5Bytes.toByteArray();
        try
        {
        	//MD5 签名
	        md5 = MessageDigest.getInstance("MD5").digest(md5);
        }
        catch (NoSuchAlgorithmException e)
        {
	        e.printStackTrace();
        }
        frame.setMd5(md5);
        
		/*
		 * web socket 响应消息头 部分 ,类似HTTP协议,
		 * 
		 */
		StringBuilder wsHead = new StringBuilder();
			wsHead.append("HTTP/1.1 101 Web Socket Protocol Handshake").append(TAG)
			.append("Upgrade: ").append("WebSocket").append(TAG)
			.append("Connection: ").append("Upgrade").append(TAG)
			.append("Sec-WebSocket-Origin: ").append(header.get("Origin")).append(TAG)
			.append("Sec-WebSocket-Location: ").append("ws://localhost/").append(TAG)
			//.append("Server: ").append("Kaazing Gateway").append(TAG)
			//.append("Date: ").append(new Date()).append(TAG)
			//.append("Access-Control-Allow-Origin: ").append(header.get("Origin")).append(TAG)
			//.append("Access-Control-Allow-Credentials: ").append("true").append(TAG)
		.append(TAG);
		frame.setHeader(wsHead.toString());
		return frame;
	}
	
	/**
	 * 解析请求消息报文
	 * @param header Websocket协议的消息头. 最后
	 * @return map 
	 * @throws IOException
	 */
	public static Map<String, String> parseRequestHeader(byte[] header) throws IOException
	{
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new StringReader(new String(header)));
		//走读 第一行. 即 : GET HTTP1.1 ...  他不在解析范围
		reader.readLine();
		//开始解析键值对: 如 Upgrade: WebSocket 则  Key=Upgrade  Value=WebSocket
		while (reader.ready())
		{
			String[] value = reader.readLine().split(": ");
			if(value.length != 2)
			{
				break;
			}
			map.put(value[0], value[1]);
		}
		return map;
	}
	
	/**
	 * 根据WebSocket 协议
	 * 如:
	 * Sec-WebSocket-Key1: 1P:(~7 25Xq46t6ed9T2\ $0
	 * 将值 "1P:(~7 25Xq46t6ed9T2\ $0" 只保留数字在 除以空格数.
	 * 最后在转成 byte[] (高字节序,高位在前)
	 * @param key
	 * @return
	 */
	public static byte[] MD5(String key)
	{
		long number = Long.valueOf(key.replaceAll("\\D", ""));
		int spaceSum= key.replaceAll("\\S", "").length();
		return spaceSum == 0 ? null : BinaryUtil.formatInteage((int)(number / spaceSum));
	}
	
	
	public static void main(String[] args) throws Exception 
    {
		WebServerSocket server = new WebServerSocket(new ServerSocket(8877));
		final Socket socket = server.accept();
		if(socket != null)
		{
			System.out.println(socket);
			System.out.println("\n====开始接收 & 回发消息====");
			new Thread(new Runnable(){
				@Override
	            public void run()
	            {
					try
	                {
		                InputStream in = socket.getInputStream();
		                byte[] data = new byte[2048];
		                int len = -1;
		                
		                while (-1 != (len = in.read(data)))
		                {
		                	byte[] bytes = Arrays.copyOf(data, len);
		                	//消息都是以0x00 打头, 0xFF结尾 收发消息 都是这种格式
		                	System.out.println(BinaryUtil.formatBytes(bytes));
		                	//去取 0x00 打头, 以及0xFF结尾
		                	String clientMsg = new String(bytes,1,bytes.length - 2,"UTF-8");
		                	System.out.println("收到WEB 客户端的消息:" + clientMsg);
		                	
		                	ByteArrayOutputStream msg = new ByteArrayOutputStream();
		                	msg.write(0x00);//0x00打头
		                		msg.write("服务端给你发消息了!!..".getBytes("UTF-8"));
		                	msg.write((byte)0xFF);//0xFF结尾
		                	
		                	socket.getOutputStream().write(msg.toByteArray());
		                }
	                }
	                catch (IOException e)
	                {
		                e.printStackTrace();
	                }
	            }
			}).start();
		}
    }
}
