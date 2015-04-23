package edu.fy.com.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface ClientInter {

	final static int PORT = 8877;
	final static String ADDRESS = "localhost";
	
	public String send(String message)throws IOException ;
	public String recive() throws IOException;
	public boolean connect();
	public void stop();
	
	final static String SEND_NO_ERROR = "200";
	final static String SEND_ERROR = "202";
	
	final static String RECIVE_NO_ERROR = "300";
	final static String RECIVE_ERROR = "302";
	
	final static ByteBuffer RBUFFER = ByteBuffer.allocate(128);
	final static ByteBuffer WBUFFER = ByteBuffer.allocate(128);
}
