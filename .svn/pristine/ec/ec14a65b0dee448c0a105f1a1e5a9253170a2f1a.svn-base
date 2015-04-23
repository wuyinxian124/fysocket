package test.java.threadpool;

import java.util.*;
import java.util.concurrent.*;

public class ThreadPoolMain {

	public static void main(String[] args)  {
		try {
			new ThreadPoolMain().test3();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	private void test4() throws InterruptedException{

		// 测试 pool 发起的子线程是否会受 pool 规则限制 即时执行的demon后台线程
		ExecutorService service = Executors.newFixedThreadPool(2);
		service.submit(new sTrLThread1());
		service.submit(new sTrLThread1());
		service.submit(new sTrLThread1());
		service.submit(new LongTask());
		service.submit(new sTrLThread1());
System.out.println("两秒后关闭线程池");
		TimeUnit.SECONDS.sleep(2);
		List<Runnable> runnables = service.shutdownNow();
		System.out.println(runnables.size());

		while (!service.awaitTermination(1, TimeUnit.MILLISECONDS)) {
			System.out.println("线程池没有关闭");
		}
		System.out.println("线程池已经关闭");
	
		
	}
	
	private void test3() throws InterruptedException{
		// 测试 pool 发起的子线程是否会受 pool 规则限制
		ExecutorService service = Executors.newFixedThreadPool(2);
		service.submit(new sTrLThread());
		service.submit(new sTrLThread());
		service.submit(new sTrLThread());
		service.submit(new LongTask());
		service.submit(new sTrLThread());
System.out.println("两秒后关闭线程池");
		TimeUnit.SECONDS.sleep(2);
		List<Runnable> runnables = service.shutdownNow();
		System.out.println(runnables.size());

		while (!service.awaitTermination(1, TimeUnit.MILLISECONDS)) {
			System.out.println("线程池没有关闭");
		}
		System.out.println("线程池已经关闭");
	}
	
	/**
	 * 线程本身执行时间不长，但是其会创建一个子线程，子线程执行时间长
	 * @author Administrator
	 *
	 */
	class sTrLThread implements Runnable{

		public sTrLThread(){
			System.out.println("我是sTrLThread Thread");
		}
		@Override
		public void run() {
			System.out.println("启动验证线程，验证成功，开始启动读写 线程");
			new Thread(new LongThread()).start();;
		}
		
		class LongThread implements Runnable{

			@Override
			public void run() {
		 		try {
					TimeUnit.SECONDS.sleep(10);
					System.out.println("读写10秒完成");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		/**
		 * 长时间运行的 后台进程
		 * @author Administrator
		 *
		 */
		class LongThread1 extends Thread{
			
			public LongThread1(){
				setDaemon(true);
				
			}
			
			@Override
			public void run() {
		 		try {
					TimeUnit.SECONDS.sleep(10);
					System.out.println("读写10秒完成");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	
	/**
	 * 线程本身执行时间不长，但是其会创建一个子线程，子线程执行时间长
	 * @author Administrator
	 *
	 */
	class sTrLThread1 extends Thread{

		public sTrLThread1(){
			setDaemon(true);
			System.out.println("我是sTrLThread1 Thread");
			
		}
		@Override
		public void run() {
			System.out.println("启动验证线程，验证成功，开始启动读写 线程");
			new Thread(new LongThread()).start();;
		}
		
		class LongThread implements Runnable{

			@Override
			public void run() {
		 		try {
					TimeUnit.SECONDS.sleep(10);
					System.out.println("读写10秒完成");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		/**
		 * 长时间运行的 后台进程
		 * @author Administrator
		 *
		 */
		class LongThread1 extends Thread{
			
			public LongThread1(){
				setDaemon(true);
				
			}
			
			@Override
			public void run() {
		 		try {
					TimeUnit.SECONDS.sleep(10);
					System.out.println("读写10秒完成");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		}
	}
	/**
	 *  线程本身执行时间不长，其会创建一个子线程，子线程执行时间也不长
	 * @author Administrator
	 *
	 */
	class sTrRThread implements Runnable{

		@Override
		public void run() {
			
		}
		
	}
	

	
	private void test2() throws InterruptedException{
		ExecutorService service = Executors.newFixedThreadPool(3);
		service.submit(new LongTask());
		service.submit(new LongTask());
		service.submit(new LongTask());
		service.submit(new LongTask());
		service.submit(new LongTask());
		
		List<Runnable> runnables = service.shutdownNow();
		System.out.println(runnables.size());

		while (!service.awaitTermination(1, TimeUnit.MILLISECONDS)) {
			System.out.println("线程池没有关闭");
		}
		System.out.println("线程池已经关闭");
		
	}
	
	private  void test1() throws InterruptedException{
		
		ExecutorService service = Executors.newFixedThreadPool(4);
		service.submit(new Task1());
		service.submit(new Task1());
		service.submit(new LongTask());
		service.submit(new Task1());

		service.shutdown();

		while (!service.awaitTermination(1, TimeUnit.SECONDS)) {
			System.out.println("线程池没有关闭");
		}
		System.out.println("线程池已经关闭");
	}

}
/**
 * 随机生成10个字符的字符串
 * @author dream-victor
 *
 */
 class Task1 implements Callable<String> {

	@Override
	public String call() throws Exception {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

}
 
 
 
 /**
  * 长时间任务
  * 
  * @author dream-victor
  * 
  */
 class LongTask implements Callable<String> {

 	@Override
 	public String call() throws Exception {
 		System.out.println("呆在线程池中执行十秒");
 		TimeUnit.SECONDS.sleep(10);
 		return "success";
 	}

 }