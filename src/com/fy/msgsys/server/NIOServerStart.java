package com.fy.msgsys.server;

import java.io.IOException;

import com.fy.msgsys.server.service.EchoWorker;
import com.fy.msgsys.server.service.NioServer;

public class NIOServerStart {

	public static void main(String[] args) {
		try {
			EchoWorker worker = new EchoWorker();
			new Thread(worker).start();
			new Thread(new NioServer(null, 9090, worker)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
