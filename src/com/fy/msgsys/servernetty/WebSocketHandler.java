package com.fy.msgsys.servernetty;

import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fy.msgsys.servernetty.util.logger.LoggerUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

	
	private final Logger logger = LoggerUtil.getLogger(this.getClass().getName());
	
	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		WebSocketFrame frame = (WebSocketFrame)msg;
		//真正的数据是放在buf里面的
		ByteBuf buf = frame.content();  
		
		//将数据按照utf-8的方式转化为字符串
		String msg0 = buf.toString(Charset.forName("utf-8"));  
		logger.log(Level.INFO, "收到客户端消息："+msg0);
		dealMsg(msg0);
//		//创建一个websocket帧，将其发送给客户端
//		WebSocketFrame out = new TextWebSocketFrame(aa);  
//		ctx.pipeline().writeAndFlush(out).addListener(new ChannelFutureListener(){
//
//			@Override
//			public void operationComplete(ChannelFuture future)
//					throws Exception {
//				//从pipeline上面关闭的时候，会关闭底层的chanel，而且会从eventloop上面取消注册
//				//ctx.pipeline().close();  
//			}
//			
//		});
		
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("channelRead0 有用");
	}


	/**
	 * 处理客户端消息
	 * <br>
	 * 截取消息
	 * <br>
	 * 找到对应互动室
	 * <br>
	 * 转发
	 * @param msg
	 */
	private void dealMsg(String msg){
		// 截取消息
		
		// 找到互动室
	}


}
