package com.fy.msgsys.appclient.api.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import com.fy.msgsys.appclient.api.core.ClientConSend;
import com.fy.msgsys.appclient.api.core.FeedbackInterface;
import com.fy.msgsys.appclient.api.exception.ConnectWebsocketException;

public abstract class  AppClientAbs extends ClientConSend implements FeedbackInterface{

		
	private final static String HOST = "localhost";
	private final static int PORT = 8876;
	
	 public AppClientAbs(){
		// super(HOST,PORT);
	 }
	 
	 public AppClientAbs(String host,int port){
		// super(host,port);
	 }
	
	/**
	 * 接收二进制消息
	 * @param msg
	 */
	public abstract void onMessageB(ByteBuffer msg);
	
	/**
	 * 接收文本消息
	 * @param msg 文件消息内容
	 */
	public abstract void onMessageT(ByteBuffer msg);
	
	/**
	 * 读取和写入异常		
	 * @param e 异常
	 * @param info 异常的补充消息
	 */
	public abstract void onError(Exception e,String info);
	
	/**
	 * 异常关闭
	 * @param e 异常
	 * @param info 异常的补充消息
	 */
	public abstract void onClose(Exception e,String info);

	@Override
	public void onWebsocketMessageB(ByteBuffer msg) {
		onMessageB(msg);
		
	}

	@Override
	public void onWebsocketMessageT(ByteBuffer msg) {

		onMessageT(msg);
	}

	@Override
	public void onWebsocketError(Exception e, String info) {
		onError(e,info);
		
	}

	@Override
	public void onWebsocketClose(Exception e, String info) {

		onClose(e,info);
	}
	
	
	

}
