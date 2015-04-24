package test.java.nioClient;

import java.net.InetAddress;

public class ClientStart {

	public static void main(String[] args) {

		for (int i = 0; i < 100; i++) {
			new Thread(new ClientService()).start();
		}

	}

}
