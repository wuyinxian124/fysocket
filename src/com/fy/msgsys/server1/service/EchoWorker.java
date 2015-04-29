package com.fy.msgsys.server1.service;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fy.msgsys.server1.util.logger.LoggerUtil;

public class EchoWorker implements Runnable {
	
	private final Logger logger= LoggerUtil.getLogger(this.getClass().getName());
	
	private List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
	
	public void processData(NioServer server, SocketChannel socket, byte[] data, int count) {
		byte[] dataCopy = new byte[count];
		
		System.arraycopy(data, 0, dataCopy, 0, count);
		logger.log(Level.INFO, "收到 client 消息：" + new String(dataCopy));
		dealOPS(new String(dataCopy));
		synchronized(queue) {
			queue.add(new ServerDataEvent(server, socket, dataCopy));
			queue.notify();
		}
	}
	

	/**
	 * 根据消息格式，处理消息
	 * @param msg
	 */
	private void dealOPS(String msg){
		 
	}
	
	public void run() {
		
		ServerDataEvent dataEvent;
		
		while(true) {
			// Wait for data to become available
			synchronized(queue) {
				while(queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				dataEvent = queue.remove(0);
			}
			
			// Return to sender
			dataEvent.server.send(dataEvent.socket, dataEvent.data);
		}
	}
}