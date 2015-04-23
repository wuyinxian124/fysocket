package com.fy.msgsys.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fy.msgsys.server.util.ConstantUtil;
import com.fy.msgsys.server.util.SignChatroomUtil;
import com.fy.msgsys.server.util.SocketConstant;
import com.fy.msgsys.server.util.UserUtil;
import com.fy.msgsys.server.util.logger.LoggerUtil;

public class NormalforJavaServer  extends Thread{

	// 获取日志对象 打日志
	private final Logger logger = LoggerUtil.getLogger(this.getClass().getName());
	
	//private final static int PROT = 8866;
	//private final static String SocketConstant.SYSTEMOUT.getRssURL() = "************";
	//private final static String SocketConstant.ERROROUT.getRssURL() = "-------------";
	private boolean wtbweblogin = false;
	
    @Override
	public void run() {
    	try {
			start1();
		} catch (IOException e) {
			logger.log(Level.INFO,SocketConstant.ERROROUT.getRssURL() + "socket 模块启动异常");
		}
	}

    /**
     * socket 处理程序启动
     * @throws IOException
     * @throws InterruptedException 
     */
	public void start1() throws IOException {
		ServerSocket server = new ServerSocket(ConstantUtil.NORMALPORT);
		logger.log(Level.INFO,SocketConstant.SYSTEMOUT.getRssURL() +"socket 处理程序 监听 ：" + ConstantUtil.NORMALPORT );
		int ErrorTimes = 0;
		boolean  overRun = false;

		
//		ExecutorService  pool =
//			    Executors.newFixedThreadPool(2);
    	while(!overRun){
    		try {
    			final Socket ss = server.accept();
    			//pool.execute((new ServerThread1(ss)));
    			// wurunzhou edit at 20150313 for 吾托帮web 重连的问题
    			new Thread((new ServerThread1(ss))).start();
			} catch (IOException e) {
				logger.log(Level.INFO,SocketConstant.ERROROUT.getRssURL() + "socket 接收连接出现异常，但是不能终止socket主线程，继续接收新的连接");
				ErrorTimes ++;
				if(ErrorTimes>=5){
					logger.log(Level.INFO,SocketConstant.ERROROUT.getRssURL() +"已经出现五次以上连接异常，关闭socket主程序");
					overRun = true;
					//pool.
				}
			}
    	}
//        // 关闭启动线程
		//List<Runnable> runnables = pool.shutdownNow();
		//logger.log(Level.INFO,SocketConstant.SYSTEMOUT.getRssURL() + "还有 "+runnables.size()+" 个连接，在等待验证");
    	logger.log(Level.INFO,SocketConstant.SYSTEMOUT.getRssURL() +"关闭socket 主程序，不接收新的socket连接，关闭所有现有socket连接");
    	
    }


    /**
     * 负责处理wtbweb 发送的命令
     * @author wurunzhou
     *
     */
	class ServerThread1 implements Runnable {

	    private DataOutputStream dsout ;
	    private DataInputStream dsin;
	    private Socket serverfork;
		public ServerThread1(Socket serverfork) {
			logger.log(Level.INFO,SocketConstant.SYSTEMOUT.getRssURL() + "从线程池中拿一个线程，处理用户验证");
			this.serverfork = serverfork;
		}
		
		@Override
		public void run() {
			if (handlerShake()) {
				logger.log(Level.INFO, SocketConstant.SYSTEMOUT.getRssURL()+ "用户验证通过 ， 启动新的子线程处理 读写操作");
				new Thread(new ReciveThreaS4()).start();
			} 
			// new Thread(new SendThreadS4()).start();
		}
		
		/**
		 * 吾托帮web 重启，重连socketFy
		 * 清理前一个连接的数据，并将前一个socket连接和前一个线程关闭
		 */
		private void cleanOldData(){
			logger.log(Level.INFO, "wtbweb 重启，调用该方法清理无效连接");
			// old socket连接和线程已经关闭。
			// 重点任务是清理前连接的久数据
			UserUtil.cleanDataInstance();
			SignChatroomUtil.cleanDataInstance();
		}
		
	    /**
	     * 验证socket 用户
	     * <br>
	     * 吾托帮web重启动，需要将之前连接失效。
	     * 吾托帮web 用户名是wtbweb
	     * @return
	     */
		private boolean handlerShake() {
			boolean pass = false;
			try {
				dsout = new DataOutputStream(serverfork.getOutputStream());
				dsin = new DataInputStream(serverfork.getInputStream());
				int readLen = dsin.readInt();
				byte[] buf = new byte[readLen];
				dsin.read(buf, 0, readLen);
				String veriUser = new String(buf);
				logger.log(Level.INFO,SocketConstant.SYSTEMOUT.getRssURL() + "允许一个用户登录socket，其用户信息（用户ID和验证码）：" + veriUser);
				
				//  wurunzhou edit at 20150313 for 吾托帮重启，重连socketFy 删除久数据  begin
				String webLoginKey = veriUser.split(SocketConstant.splitNormalUV.getRssURL())[0];
				if(webLoginKey != null){
					
					if(SocketConstant.wtbwebKey.getRssURL().equals(webLoginKey)&&wtbweblogin != true){
						wtbweblogin = true;
					}else if(SocketConstant.wtbwebKey.getRssURL().equals(webLoginKey)&&wtbweblogin == true){
						/**
						 *  吾托帮web重启 重连socketFy
						 *  1. 关闭以前的连接，而清除已有数据
						 */
						cleanOldData();
					}
				}
				
				//  wurunzhou edit at 20150313 for 吾托帮重启，重连socketFy 删除久数据  end
				dsout.writeInt(veriUser.length());
				dsout.flush();
				dsout.writeUTF("ackPass");
				dsout.flush();
				pass = true;
			} catch (IOException e) {
				logger.log(Level.INFO,SocketConstant.ERROROUT.getRssURL() +" 读取用户信息失败，验证不成功");
			}

			return pass;
		}
		
		/**
		 * 接收wtbweb 发送命令，然后回复
		 * <br> 该线程服务器接收消息，解析消息，回复用户
		 * @author wurunzhou
		 *
		 */
	    class ReciveThreaS4 implements Runnable{

			@Override
			public void run() {
				try {
					while (true) {
						String cMsg = reciveText();
						logger.log(Level.INFO,"收到一个命令" + cMsg);
						if (process(cMsg))
							sendText(cMsg + " :200ok");

					}
				} catch (IOException e) {
					logger.log(Level.INFO,SocketConstant.ERROROUT.getRssURL()+"读写子线程 处理失败，关闭子线程");
				} finally{
					logger.log(Level.INFO,SocketConstant.SYSTEMOUT.getRssURL() + "关闭一个读写子线程");
				}
				
			}
			
			
		    /**
		     * 处理消息
		     * <br> 接收来web服务器的消息
		     * 功能包括：用户登录，用户退出，已有互动室，创建互动室（帮区，私聊）
		     * 消息内容是：用户ID：验证码，用户ID,用户ID：互动室视图ID列表，互动室视图ID：互动室用户列表
		     * @param message 来自吾托帮 web的命令
		     * @return
		     */
			private boolean process(String message) {

				if(message == null || "".equals(message))
					return false;
				
				boolean pass = false;
				
				String tmp = message.length()>=3? message.substring(0, Integer.parseInt(SocketConstant.splitLen.getRssURL())):"";

				if (SocketConstant.holeLoginUser.getRssURL().equals(tmp)) {
					// 发送的是待登录用户
					String[] vU = message.substring(3, message.length()).split(SocketConstant.splitUV.getRssURL());
					UserUtil.getInstance().add(vU[0], vU[1]);
					pass = true;
				} else if (SocketConstant.hlUserChats.getRssURL().equals(tmp)) {
					// 发送的是待登录用户 互动室列表
					String[] vU = message.substring(3, message.length()).split(SocketConstant.splitUC.getRssURL());
					logger.log(Level.INFO,"用户"+ vU[0] +" ,对应的互动室  " +string2list(vU[1]).toString());
					SignChatroomUtil.getInstance().loginIn(vU[0], string2list(vU[1]));
					pass = true;
				} else if(SocketConstant.quitUser.getRssURL().equals(tmp)) {
					String vU = message.substring(3, message.length());
					// wurunzhou add at 20150410 for 用户退出，没有移除socket begin
					UserUtil.getInstance().userLoginOut(null,vU);
					SignChatroomUtil.getInstance().loginOut(vU, null);
					
					// wurunzhou add at 20150410 for 用户退出，没有移除socket end
					logger.log(Level.INFO,"用户 "+ vU + " 退出");

				}  else if(SocketConstant.quitUserApp.getRssURL().equals(tmp)) {
					String vU = message.substring(3, message.length());
					// wurunzhou add at 20150410 for 用户退出，没有移除socket begin
					UserUtil.getInstance().userLoginOutAPP(null,vU);
					SignChatroomUtil.getInstance().loginOutApp(vU, null);
					
					// wurunzhou add at 20150410 for 用户退出，没有移除socket end
					logger.log(Level.INFO,"用户 "+ vU + " 退出");

				} else {
					// 发送的是 消息
					logger.log(Level.INFO,"client say:"+message);
					pass = true;
				}

				return pass;
			}
			
			private List<String> string2list(String mes){
				
				String tmp = mes.substring(1, mes.length()-1);
				List<String> lala = new ArrayList<String>();
				for(String lal :tmp.split(",")){
					lala.add(lal.trim());
				}
				return lala;
			}
			
		    /**
		     * 接收消息 （web 发送的命令）
		     * <br> 忽略 消息头的消息长度，直接读消息体（文本）
		     * @return 文本消息
		     * @throws IOException
		     */
		    private String reciveText() throws IOException {
		    	int readLen = dsin.readInt();
		    	//byte[] bb = new byte[1024];
		/*    	if(readLen != -1){
		    		dsin.read(bb, 0, readLen);
		    	}*/
		    	String msg = dsin.readUTF();
		    	return msg;
		    }
		    
		    private void  sendText(String message) throws IOException{
		    	int len = message.length();
		    	dsout.writeInt(len);
		    	dsout.flush();
		    	dsout.writeUTF(message);
		    	dsout.flush();
		    	
		    }
	    	
	    }
		
	}
	

    

    
/*    class SendThreadS4 implements Runnable{

		@Override
		public void run() {
			int i = 0;
			while(true){
				try {
					TimeUnit.SECONDS.sleep(5);
					sendText("hello client "+ i++);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
    	
    }
 */   

    
}



