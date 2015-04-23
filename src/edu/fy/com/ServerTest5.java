package edu.fy.com;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

import sun.awt.geom.AreaOp.IntOp;

public class ServerTest5 {
	
	public final static int PORT = 8877;

	public static void main(String[] args) {

		int port = PORT;
		System.out.println("listern prot " + port);
		
		ServerSocketChannel serverChannel;
		Selector selector;
		
		try{
			serverChannel = ServerSocketChannel.open();
			ServerSocket server = serverChannel.socket();
			InetSocketAddress address = new InetSocketAddress(port);
			server.bind(address);
			serverChannel.configureBlocking(false);
			selector = Selector.open();
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			
		}catch (Exception e){
			e.printStackTrace();
			return ;
		}
		
		
		while(true){
			try{
				selector.select();
			}catch (Exception e){
				e.printStackTrace();
			}
			
			Set<SelectionKey> readykeys = selector.selectedKeys();
			Iterator<SelectionKey> iterators = readykeys.iterator();
			while(iterators.hasNext()){
				SelectionKey key = iterators.next();
				iterators.remove();
				try{
					
					if(key.isAcceptable()){
						// 处理连接请求
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel client = server.accept();
						System.out.println("accept connent from " + client.toString());
						client.configureBlocking(false);
						
						SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
						ByteBuffer buffer = ByteBuffer.allocate(8192);
					
						clientKey.attach(buffer);
					}
					
					if(key.isReadable()){
						// 读取来自客户端的发送数据
						SocketChannel clientr = (SocketChannel) key.channel();
						ByteBuffer bufferr = (ByteBuffer) key.attachment();
						
						
						clientr.read(bufferr);
						
						/**
						 * 	       
			while (true) {
	            int read = 0;
	            if (inputStream != null) {
	                read = inputStream.read(buf);
	            }
	            len += read;
	            if (read == -1) {
	                break;
	            }
	            //下面进度条本为图形界面的prograssBar做的，这里如果是打文件，可能会重复打印出一些相同的百分比
	            fileOut.write(buf, 0, read);
	        }
	        System.out.println("图片大小为：" + len + "\n");
	        System.out.println("接收完成！\n");
						 */
					}
					
					if(key.isWritable()){
						// 向客户端发送数据
						SocketChannel clientw = (SocketChannel) key.channel();
						ByteBuffer output = (ByteBuffer) key.attachment();
						output.flip();
						clientw.write(output);
						output.compact();
					}
					
				}catch (IOException e){
					key.cancel();
					try {
						key.channel().close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
				
			}
			
			
		}
	}

}
