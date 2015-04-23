package edu.fy.com.websocketServer.verifyReactor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.crypto.Data;

public class NormalforJavaServer  extends Thread{

	
	
    @Override
	public void run() {
    	try {
			start1();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start1() throws IOException{
		ServerSocket server = new ServerSocket(8866);
		Executor pool =
			    Executors.newFixedThreadPool(2);
    	while(true){
    		try {
    			final Socket ss = server.accept();
    			if(handlerShake(ss))
    				pool.execute((new ServerThread1(ss)));
    			else
    				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    }
    private DataOutputStream dsout ;
    private DataInputStream dsin;
    
    private boolean handlerShake(Socket ss){
    	boolean pass = false;
    	try {
			dsout = new DataOutputStream(ss.getOutputStream());
	    	dsin  = new DataInputStream(ss.getInputStream());
	    	int readLen = dsin.readInt();
	    	byte[] buf = new byte[readLen];
	    	dsin.read(buf, 0, readLen);
	    	String veriUser = new String(buf);
System.out.println("获得验证用户名："+veriUser);
	    	
	    	dsout.writeInt(veriUser.length());
	    	dsout.flush();
	    	dsout.writeUTF("ackPass");
	    	dsout.flush();
	    	pass = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

    	return pass;
    }
    
	class ServerThread1 implements Runnable {

		public ServerThread1(Socket serverfork) {
System.out.println("接受一个连接……");
		}

		@Override
		public void run() {
			new Thread(new ReciveThreaS4()).start();
		
		}
	}
    class ReciveThreaS4 implements Runnable{

		@Override
		public void run() {
			while(true){
				try {
					String cMsg = reciveText();
					if(process(cMsg))
						sendText(cMsg + " :200ok");
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
		}
    	
    }
    
	private boolean process(String message) {

		if(message == null || "".equals(message))
			return false;
		boolean pass = false;
		
		String tmp = message.length()>=3? message.substring(0, 3):"";

		if ("#U#".equals(tmp)) {
			// 发送的是待登录用户
			String[] vU = message.substring(3, message.length()).split(":");
			UtilUser.getInstance().add(vU[0], vU[1]);
System.out.println("注册待登录用户："+ vU[0] + " :" +vU[1]);
			pass = true;
		} else if ("#C#".equals(tmp)) {
			// 发送的是待登录用户 互动室列表
			String[] vU = message.substring(3, message.length()).split(":");
			//System.out.println(vU[0]);
System.out.println("注册待登录用户互动室 ："+string2list(vU[1]).toString());
			SignChatroomUtil.getInstance().loginIn(vU[0], string2list(vU[1]));
			pass = true;
		} else if ("#V#".equals(tmp)) {
			// 发送的是待登录用户 互动室列表
			String[] vU = message.substring(3, message.length()).split(":");
			//System.out.println(vU[0]);
System.out.println("用户新建帮区互动室 ："+string2list(vU[0]).toString());
			SignChatroomUtil.getInstance().registerNewChat(vU[0], string2list(vU[1]));
			pass = true;
		} else if("#O#".equals(tmp)) {
			String vU = message.substring(3, message.length());
			// 将互动室列表删除
			SignChatroomUtil.getInstance().logoutUser(vU );
			// 将socket对象删除
			UtilUser.getInstance().userLoginOut(vU);
System.out.println("用户  "+ vU + " 退出 ..;");

		} else if("#3#".equals(tmp)) {
			String cc = message.substring(3, message.length());
//			List<String> mem = new ArrayList();
//			Collections.addAll(mem, cc.substring(1, cc.length()-1).split(", "));
			// 将互动室列表删除
			SignChatroomUtil.getInstance().registerPublicChatroom(cc);
System.out.println("吾托帮注册 反馈意见互动室 。。单个。。。。 ");

		} else if("#4#".equals(tmp)) {
			String cc = message.substring(3, message.length());
//			List<String> mem = new ArrayList();
//			Collections.addAll(mem, cc.substring(1, cc.length()-1).split(", "));
//			// 将互动室列表删除
//			SignChatroomUtil.getInstance().registerPublicChatrooms(mem);
			SignChatroomUtil.getInstance().registerPublicChatroom(cc);
System.out.println("吾托帮注册 论坛互动室 。。。单个。。。。");

		} else if("#5#".equals(tmp)) {
			String cc = message.substring(3, message.length());
//			List<String> mem = new ArrayList();
//			Collections.addAll(mem, cc.substring(1, cc.length()-1).split(", "));
			// 将互动室列表删除
			SignChatroomUtil.getInstance().registerPublicChatroom(cc);
System.out.println("吾托帮注册 公告互动室。。单个。。。。。");

		} else if("#7#".equals(tmp)) {
			String cc = message.substring(3, message.length());
			List<String> mem = new ArrayList();
			Collections.addAll(mem, cc.substring(1, cc.length()-1).split(", "));
			// 将互动室列表删除
			SignChatroomUtil.getInstance().registerPublicChatrooms(mem);
System.out.println("吾托帮注册 反馈意见互动室 。。多个。。。。 ");

		} else if("#8#".equals(tmp)) {
			String cc = message.substring(3, message.length());
			List<String> mem = new ArrayList();
			Collections.addAll(mem, cc.substring(1, cc.length()-1).split(", "));
			// 将互动室列表删除
			SignChatroomUtil.getInstance().registerPublicChatrooms(mem);
System.out.println("吾托帮注册 论坛互动室 。。。多个。。。。");

		} else if("#9#".equals(tmp)) {
			String cc = message.substring(3, message.length());
			List<String> mem = new ArrayList();
			Collections.addAll(mem, cc.substring(1, cc.length()-1).split(", "));
			// 将互动室列表删除
			SignChatroomUtil.getInstance().registerPublicChatrooms(mem);
System.out.println("吾托帮注册 公告互动室。。。。多个。。。");

		}else {
			// 发送的是 消息
System.out.println("client say:"+message);
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
    
    class SendThreadS4 implements Runnable{

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
    
    private String reciveText() throws IOException{
    	int readLen = dsin.readInt();
    	byte[] bb = new byte[1024];
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



