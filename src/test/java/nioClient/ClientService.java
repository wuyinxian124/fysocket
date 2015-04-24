package test.java.nioClient;

import java.net.InetAddress;

public class ClientService implements Runnable {

	@Override
	public void run() {
		

	    try {
		      NioClient client = new NioClient(InetAddress.getByName("222.201.139.162"), 9090);
		      Thread t = new Thread(client);
		      t.setDaemon(true);
		      t.start();
		      RspHandler handler = new RspHandler();
		      for(int i=0;i<3;i++){
			      client.send("Hello World".getBytes(), handler);
			      handler.waitForResponse();
		      }

		    } catch (Exception e) {
		      e.printStackTrace();
		    }

	}

}
