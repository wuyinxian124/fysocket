package com.fy.msgsys.servernetty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fy.msgsys.servernetty.util.SocketConstant;
import com.fy.msgsys.servernetty.util.UserUtil;
import com.fy.msgsys.servernetty.util.logger.LoggerUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class VerifyHandler extends SimpleChannelInboundHandler<Object>   {

	private final Logger logger= LoggerUtil.getLogger(this.getClass().getName());

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		WebSocketFrame frame = (WebSocketFrame)msg;
		//真正的数据是放在buf里面的
		ByteBuf buf = frame.content();  
		
		//将数据按照utf-8的方式转化为字符串
		String aa = buf.toString(Charset.forName("utf-8"));  
		logger.log(Level.INFO,"收到客户端消息："+aa);
		//创建一个websocket帧，将其发送给客户端
		if(verifyUser(aa,ctx.channel())){
			WebSocketFrame out = new TextWebSocketFrame("ok");  
			ctx.pipeline().remove("verify");
			ctx.writeAndFlush(out);
			logger.log(Level.INFO, "用户验证成功");
		}else{
			logger.log(Level.INFO, "用户验证失败");
			WebSocketFrame out = new TextWebSocketFrame("ack");  
			ctx.writeAndFlush(out);
			ctx.channel().close();
		}

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		//ctx.pipeline().writeAndFlush("client "+ctx.channel()+" join");
		logger.log(Level.INFO, "channelReadComplete"+"client "+ctx.channel()+" join");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		logger.log(Level.INFO, "channelRead0");
	}

	/**
	 * 读取登录用户信息，准备验证
	 * @param array
	 * @return
	 * @throws IOException 
	 */
	private final boolean verifyUser(String verifyCode,Channel channel) throws IOException {

		logger.log(Level.INFO, "获取登录用户发送用户名和验证码（app用户登录含有：app）=" + verifyCode);

		return verifyUser1(verifyCode.split(SocketConstant.splitUV.getRssURL()), channel);

	}

	/**
	 * 用户验证
	 * @param lala
	 * @return
	 */
	private final boolean verifyUser1(String[] lala,Channel channel) {
		//if(serverfork == null||serverfork.isClosed()){ logger.log(Level.INFO,"socket close");return false;}
		if(lala.length ==3){
			// 等于3 表示是pc用户登录
			return UserUtil.getInstance().verify(lala[0], lala[1], lala[2],  channel);
		}else if(lala.length ==4){
			// 等于4 表示是app 用户登录
			return UserUtil.getInstance().verifyapp(lala[0], lala[1], lala[2],lala[3], channel);
		}else{
			logger.log(Level.WARNING,"验证码格式不对（分隔符为 ：）,分割结构为3或4");
			return false;
		}
		
		
	}
	
}
