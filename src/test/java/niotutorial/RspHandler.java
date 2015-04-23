package test.java.niotutorial;

/**
 * 处理回复
 * <br> 有两个同步方法
 * @author wurunzhou
 *
 */
public class RspHandler {
	
	private byte[] rsp = null;
	
	public synchronized boolean handleResponse(byte[] rsp) {
		this.rsp = rsp;
		this.notify();
		return true;
	}
	
	// 通过同步该办法
	public synchronized void waitForResponse() {
		while(this.rsp == null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			}
		}
		
		System.out.println("收到返回消息："+new String(this.rsp));
	}
}
