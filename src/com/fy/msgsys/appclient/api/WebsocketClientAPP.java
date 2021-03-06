package com.fy.msgsys.appclient.api;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fy.msgsys.appclient.api.exception.ConnectWebsocketException;
import com.fy.msgsys.appclient.api.exception.IllegalWebsocketException;
import com.fy.msgsys.appclient.api.service.AppClientAbs;
import com.fy.msgsys.appclient.api.util.logger.LoggerUtil;



public class WebsocketClientAPP  {
	
	private AppClientAbs clientAPP ;
	private Logger logger = LoggerUtil.getLogger(this.getClass().getName());
	
	public WebsocketClientAPP(){
		// 初始化 clientapp 对象
		init();
		try {
			// 连接socketFy 服务器
			clientAPP.Connection("userkey", "virifycode");
		} catch (ConnectWebsocketException | IOException e) {
			logger.log(Level.WARNING, "用户验证异常");
		}
	}
	
	
	private void init(){
		
		clientAPP = new AppClientAbs() {

			@Override
			public void onMessageB(ByteBuffer msg) {
				logger.log(Level.INFO, "收到一条二进制消息");
			}

			@Override
			public void onMessageT(ByteBuffer msg) {
				logger.log(Level.INFO, "收到一条文本消息");
			}

			@Override
			public void onError(Exception e, String info) {
				logger.log(Level.INFO, "接收过程发生错误");
				
			}

			@Override
			public void onClose(Exception e, String info) {
				logger.log(Level.INFO, "通道关闭");
				
			}
			
		};
	}
	
	
	
	/**
	 * 定时发送文本消息
	 * @param msg 		消息内容，如果为空 ，使用默认消息
	 * @param times 	发送消息次数
	 * @param timesout 	每次消息发送时间间隔  单位为秒
	 * @throws IllegalWebsocketException	非法操作异常（未验证用户）
	 */
	public void sendTMsg(String msg ,long times,long timesout) throws IllegalWebsocketException{
		ByteBuffer msgb = ByteBuffer.allocate(10);
		if(msg == null){
			msgb.put("appclient msg".getBytes());
		}else{
			msgb.put(msg.getBytes());
		}
		while(times >= 0){
			times --;
			msgb.put((" = " +times).getBytes());
			msgb.flip();
			clientAPP.sendMsgText(msgb, 0);
		}

	}
}
