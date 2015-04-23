package edu.fy.com.testThread;

import java.util.concurrent.TimeUnit;

public class ThreadMain {

	public static void main(String[] args) {
		new ThreadMain().start();
	}

	private void start(){
		send0();
	}

	private void send0(){

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int j= 0;
				while(true){
					try {
						System.out.println("hello world"+ j++);
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	
	}
}
