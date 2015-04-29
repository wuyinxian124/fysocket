package com.fy.msgsys.server1.service;

import java.nio.channels.SocketChannel;

/**
 * 
 * @author wurunzhou
 *
 */
class ServerDataEvent {
	
	public NioServer server;
	public SocketChannel socket;
	public byte[] data;
	private int type;
	
	public ServerDataEvent(NioServer server, SocketChannel socket, byte[] data) {
		this.server = server;
		this.socket = socket;
		this.data = data;
		this.type = 0;
	}
}
