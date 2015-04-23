package test.java.niotutorial;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class EchoWorker implements Runnable {
	private List queue = new LinkedList();
	
	
	// 处理读写数据
	public void processData(NioServer server, SocketChannel socket, byte[] data, int count) {
		
		String lala = "我收到了消息，现在通知你，我收到了\r\n\r\n";
		byte[] dataCopy = new byte[lala.getBytes().length];
		System.out.println("收到消息体："+new String (data));
		System.arraycopy(lala.getBytes(), 0, dataCopy, 0, lala.getBytes().length );
		synchronized(queue) {
			queue.add(new ServerDataEvent(server, socket, dataCopy));
			queue.notify();
		}
	}
	
	public void run() {
		ServerDataEvent dataEvent;
		
		while(true) {
			// 等待任何服务器待处理事件
			synchronized(queue) {
				while(queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				dataEvent = (ServerDataEvent) queue.remove(0);
			}
			
			// 读完客户端数据，触发该事件，然后回复客户端
			dataEvent.server.send(dataEvent.socket, dataEvent.data);
		}
	}
}
