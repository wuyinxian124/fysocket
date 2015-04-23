package edu.fy.com.ansyDemoPic;

import java.util.concurrent.TimeUnit;

public class ClientMain implements SocketCallBack{

	public static void main(String[] args) {

		new ClientMain().start();

	}
	
	private boolean send = false;
	private String content = "";
	private String filePath = "";
	// 连接
	public void start(){
		// 初始化连接
		new Client(this).start();
		// 定时发送图片
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int j=0;
				while(true){
					j++;
					try {
						TimeUnit.SECONDS.sleep(2);
						if(j%3 == 0){
							setSend(true);
							//content = "hello wusir " + j + "  ";
							filePath = "D:\\123.bmp"; //进行传输的文件
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
	@Override
	public String sendCallback() {
		return null;
	}

	// 接收触发
	@Override
	public void sendclallback0(String ack) {
		System.out.println("客户端收到结果回调 ：" + ack);
	}

	// 发送
	@Override
	public void sendMsg(String msg) {
		
	}


	@Override
	public String sendMsg0(String lal) {
		return content + lal;
	}


	@Override
	public boolean sendACK() {
		return send;
	}


	public boolean isSend() {
		return send;
	}


	public synchronized void  setSend(boolean send) {
		this.send = send;
	}


	@Override
	public String sendMsg1() {
		return filePath;
	}


	@Override
	public void setACK(boolean ack) {
		setSend(ack);
		
	}

 
}
