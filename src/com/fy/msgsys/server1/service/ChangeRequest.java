package com.fy.msgsys.server1.service;

import java.nio.channels.SocketChannel;

public class ChangeRequest {
	
	/**
	 * =1
	 */
	public static final int REGISTER = 1;
	/**
	 * =2
	 */
	public static final int CHANGEOPS = 2;
	
	/**
	 * =3
	 */
	public static final int VERIFY = 3;
	
	/**
	 * 
	 */
	public SocketChannel socket;
	/**
	 * 
	 */
	public int type;
	/**
	 * channel 可选操作
	 * <br>
	 * 如 op_accept ,read ,write
	 */
	public int ops;
	
	public ChangeRequest(SocketChannel socket, int type, int ops) {
		this.socket = socket;
		this.type = type;
		this.ops = ops;
	}
}
