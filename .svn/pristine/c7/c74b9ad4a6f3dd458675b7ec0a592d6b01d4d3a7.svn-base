package edu.fy.com.asyncChannal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server implements Runnable {

	public final static String ADDRESS = "localhost";
	public final static int PORT = 8877;
	public final static long TIMEOUT = 10000;
	
	private ServerSocketChannel serverChannel;
	private Selector selector;
	/**
	 * This hashmap is important. It keeps track of the data
	 */
	private Map<SocketChannel,byte[]> dataTracking = new HashMap<SocketChannel, byte[]>();

	public Server(){
		init();
	}

	private void init(){
		System.out.println("initializing server");
		// 不需要初始化两次
		if (selector != null) return;
		if (serverChannel != null) return;

		try {
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(ADDRESS, PORT));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println("Now accepting connections...");
		try{
			// 服务器线程是不中断的。
			while (!Thread.currentThread().isInterrupted()){
				/**
				 * selector.select(TIMEOUT) is waiting for an OPERATION to be ready and is a blocking call.
				 * For example, if a client connects right this second, then it will break from the select()
				 * call and run the code below it. The TIMEOUT is not needed, but its just so it doesn't 
				 * block undefinitely.
				 */
				selector.select(TIMEOUT);

				// 代码到这里就是说明要么等待超时，要么有连接操作了，通过一个迭代看看到底是等到了什么操作。
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

				while (keys.hasNext()){
					SelectionKey key = keys.next();
					// 移除已经处理过的操作
					keys.remove();

					// 防止，客户端发送请求之后，异常关闭。
					if (!key.isValid()){
						continue;
					}

					/**
					 * 通过注册 OP_ACCEPT 到selector中，就可以监听用户发送的连接请求，
					 * 如果 key 是可用的，那么我们肯定准备好接受client连接请求了，然后再做一些其他的事情。
					 * now 可以去读 accept方法了
					 */
					if (key.isAcceptable()){
						System.out.println("Accepting connection");
						accept(key);
					}
					/**
					 * 如果你看懂了 accept方法
					 * 那么下面这个你就比较好理解了
					 */
					if (key.isReadable()){
						System.out.println("Reading connection");
						read(key);
					}
					if (key.isWritable()){
						System.out.println("Writing...");
						write(key);
					}
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		} finally{
			closeConnection();
		}

	}

	private void write(SelectionKey key) throws IOException{
		SocketChannel channel = (SocketChannel) key.channel();
		byte[] data = dataTracking.get(channel);
		dataTracking.remove(channel);
		
		channel.write(ByteBuffer.wrap(data));
		
		key.interestOps(SelectionKey.OP_READ);
		
	}
	// Nothing special, just closing our selector and socket.
	private void closeConnection(){
		System.out.println("Closing server down");
		if (selector != null){
			try {
				selector.close();
				serverChannel.socket().close();
				serverChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 一旦接受连接，就可以通过 key.chanel 初始化一个serverSocketChannel.
	 * 我们通过调用serverSocketChannel.accept 方法获取 socketChannel 对象，该对象类似socket的 I/O
	 * 我们将该socketChannel注册到selector对象中，监听写操作。
	 * 这里这么写的原因是 服务器会发送一个 hello 给每个连接的客户端。
	 * @param key
	 * @throws IOException
	 */
	private void accept(SelectionKey key) throws IOException{
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		
		socketChannel.register(selector, SelectionKey.OP_WRITE);
		byte[] hello = new String("Hello from server").getBytes();
		dataTracking.put(socketChannel, hello);
	}

	private void read(SelectionKey key) throws IOException{
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer readBuffer = ByteBuffer.allocate(1024);
		readBuffer.clear();
		int read;
		try {
			read = channel.read(readBuffer);
		} catch (IOException e) {
			System.out.println("Reading problem, closing connection");
			key.cancel();
			channel.close();
			return;
		}
		if (read == -1){
			System.out.println("Nothing was there to be read, closing connection");
			channel.close();
			key.cancel();
			return;
		}
		readBuffer.flip();
		byte[] data = new byte[1000];
		readBuffer.get(data, 0, read);
		System.out.println("Received: "+new String(data));

		echo(key,data);
	}

	private void echo(SelectionKey key, byte[] data){
		SocketChannel socketChannel = (SocketChannel) key.channel();
		dataTracking.put(socketChannel, data);
		key.interestOps(SelectionKey.OP_WRITE);
	}

}
