package com.fy.msgsys.server1;

import java.io.IOException;

import com.fy.msgsys.server1.service.EchoWorker;
import com.fy.msgsys.server1.service.NioServer;

public class MainStart {

	public static void main(String[] args) {

		try {
			EchoWorker worker = new EchoWorker();
			new Thread(worker).start();
			new Thread(new NioServer(null, 9090, worker)).start();
			new Thread(new NormalforJavaServer()).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
