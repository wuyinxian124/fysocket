package edu.fy.com.parttern.dispatch;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.net.*;
import java.util.*;

public class ServerMain1 {

	public static void main(String[] args) {
		try {
			new Thread(new Reactor(8877)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

class Reactor implements Runnable {
	final Selector selector;
	final ServerSocketChannel serverSocket;

	Reactor(int port) throws IOException  { // Reactor初始化
		selector = Selector.open();
		serverSocket = ServerSocketChannel.open();
		serverSocket.socket().bind(new InetSocketAddress(port));
		serverSocket.configureBlocking(false); // 非阻塞
		SelectionKey sk = serverSocket.register(selector,
				SelectionKey.OP_ACCEPT); // 分步处理,第一步,接收accept事件
		sk.attach(new Acceptor()); // attach callback object, Acceptor
		/*
		 * Alternatively, use explicit SPI provider: SelectorProvider p =
		 * SelectorProvider.provider(); selector = p.openSelector();
		 * serverSocket = p.openServerSocketChannel();
		 */
	}

	public void run() {
		try {
			while (!Thread.interrupted()) {
				selector.select();
				Set selected = selector.selectedKeys();
				Iterator it = selected.iterator();
				while (it.hasNext())
					dispatch((SelectionKey) (it.next())); // Reactor负责dispatch收到的事件
				selected.clear();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			
			/* ... */
		} 
	}

	void dispatch(SelectionKey k) {
		Runnable r = (Runnable) (k.attachment()); // 调用之前注册的callback对象
		if (r != null)
			r.run();
	}

	// accptor 只负责一个事情那就是 接收连接
	class Acceptor implements Runnable { // inner
		public void run() {
			try {
				SocketChannel c = serverSocket.accept();
				System.out.println("接收用户请求");
				if (c != null)
					new Handler(selector, c);
			} catch (IOException ex) {
				ex.printStackTrace();
				/* ... */
			}
		}
	}

}



final class Handler implements Runnable {
	
	int MAXIN = 1024;
	int MAXOUT = 1024;
	final SocketChannel socket;
	final SelectionKey sk;
	ByteBuffer input = ByteBuffer.allocate(MAXIN);
	ByteBuffer output = ByteBuffer.allocate(MAXOUT);
	static final int READING = 0, SENDING = 1;
	int state = READING;

	Handler(Selector sel, SocketChannel c) throws IOException {
		socket = c;
		c.configureBlocking(false);
		// Optionally try first read now
		sk = socket.register(sel, 0);
		sk.attach(this); // 将Handler作为callback对象
		sk.interestOps(SelectionKey.OP_READ); // 第二步,接收Read事件
		sel.wakeup();
		
	}

	boolean inputIsComplete() throws IOException {

			input.flip(); // make buffer ready for read

			while (input.hasRemaining()) {
				System.out.print((char) input.get()); // read 1 byte at a time
			}

			input.clear(); // make buffer ready for writing

		return true;
	}

	boolean outputIsComplete() {
		/* ... */
		return true;
	}

	void process() {
		System.out.println("处理……");
		output.clear();
		output.put("ok server got it".getBytes());
		output.flip();
		/* ... */
	}

	public void run() {
		
		try {
			
			if (state == READING&&sk.isReadable())
				read();
			else if (state == SENDING&&sk.isWritable())
				send();
		} catch (IOException ex) { 
			ex.printStackTrace();
			sk.cancel();
			/* ... */
		}
	}

	void read() throws IOException {
		System.out.println("读……");
		int bytesRead = socket.read(input); // read into buffer.
		//int bytesRead = socket.read(input);// inChannel.read(buf); //read into buffer.socket.read(input);
		if (inputIsComplete()) {
			process();
			state = SENDING;
			// Normally also do first write now
			sk.interestOps(SelectionKey.OP_WRITE); // 第三步,接收write事件
		}
	}

	void send() throws IOException {
		System.out.println("写……");
		socket.write(output);
		if (outputIsComplete()){
			state = READING;
			sk.interestOps(SelectionKey.OP_READ); // 第三步,接收write事件
			//sk.cancel(); // write完就结束了, 关闭select key
			System.out.println("一次收 收确认结束");
		}
			
	}
}