package edu.fy.com.channal;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) {

        try {
			new Server().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
    
    private void start() throws IOException{
		ServerSocket server = new ServerSocket(8877);
    	while(true){
    		try {
				new Thread(new ServerThread1(server.accept())).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    }
    
    class ServerThread1 implements Runnable{

    	private Socket serverfork;
    	
    	public ServerThread1(Socket serverfork){
    		this.serverfork = serverfork;
    	}
    	
		@Override
		public void run() {
			try {
	            BufferedReader in = new BufferedReader(new InputStreamReader(serverfork.getInputStream()));
	            PrintWriter out=new PrintWriter(serverfork.getOutputStream());
	            
	            new ReceiveTread1(in,out,serverfork).start();
	            new SendThread1(out).start();
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
    	
    }

}


class SendThread1 extends Thread {

	PrintWriter out;

	public SendThread1(PrintWriter out) {
		this.out = out;
	}

	@Override
	public void run() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int jj = 0;
		try {
			while (true) {

				TimeUnit.SECONDS.sleep(7);
				out.println("Server  " + sf.format(new Date()) + "\n\t"
						+ "hello client" + jj++);

				out.flush();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}

class ReceiveTread1 extends Thread {
	BufferedReader in;
	PrintWriter out;
	Socket client;

	public ReceiveTread1( BufferedReader in,
			PrintWriter out,  Socket client) {
		this.in = in;
		this.client = client;
		this.out = out;
	}

	@Override
	public void run() {
		try {
			while (true) {
				String info = in.readLine();
				while (info != null) {
					System.out.println(info);
					info = in.readLine();
					out.println("ok recive");
					out.flush();
				}

				if (in.readLine().equals("end")) {
					break;
				}
			}
			in.close();
			out.close();
			if (client != null) {
				client.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}