package edu.fy.com.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ClientTest1 implements ClientInter {

	private SocketChannel channel = null;

	private ClientCallback call;
	public ClientTest1(ClientCallback call){
		this.call = call;
	}
	
	@Override
	public String send(String message) throws IOException {
		if (channel == null|| (!channel.isConnected()))
			return SEND_ERROR;

		// 清除原来数据，将数据存入buffer，转为读模式
		WBUFFER.clear();
		WBUFFER.put(message.getBytes());
		WBUFFER.flip();

		while(WBUFFER.hasRemaining()) {
		    channel.write(WBUFFER);
		}
		return SEND_NO_ERROR;
	}

	@Override
	public String recive() throws IOException {
		if (channel == null)
			return RECIVE_ERROR;
		
		String message = "";
		// 清除buffer ，将buffer转为写模式
		RBUFFER.clear();
		int bytesRead = channel.read(RBUFFER);
		
		// 转为读模式
		RBUFFER.flip(); 

		// 将buffer数据变为字符串
		message += Charset.defaultCharset().decode(RBUFFER);
        System.out.println("client read =="+message);
        call.recive0(message);
		return message;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean connect() {

		boolean pass = false;
		try {
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(ADDRESS, PORT));

			while (!channel.finishConnect()) {
				// System.out.println("still connecting");
			}
			pass = true;
			System.out.println("客户端连接成功");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pass;
	}


}
