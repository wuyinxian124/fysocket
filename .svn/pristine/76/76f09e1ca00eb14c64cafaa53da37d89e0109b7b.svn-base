package edu.fy.com;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class ClientTest6 {

	public static void main(String[] args){
		System.out.println("0");
	}
	
	private void start() throws IOException, InterruptedException{

        int port = 8877;
        SocketChannel channel = SocketChannel.open();
 
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress("localhost", port));
 
        while (!channel.finishConnect()) {
            // System.out.println("still connecting");
        }
        int j = 0;
        while (true) {
        	
            ByteBuffer bufferA = ByteBuffer.allocate(20);
            int count = 0;
            String message = "begin";
            
            TimeUnit.SECONDS.sleep(2);
            if (message.length() > 0) {
                CharBuffer buffer = CharBuffer.wrap("Hello Server =" + j++);
                while (buffer.hasRemaining()) {
                    channel.write(Charset.defaultCharset().encode(buffer));
                }
                message = "";
                
            }
            // see if any message has been received

            while ((count = channel.read(bufferA)) > 0) {
                // flip the buffer to start reading
                bufferA.flip();
                message += Charset.defaultCharset().decode(bufferA);
                System.out.println("client read =="+message);
            }
        }
	}
}
