package com.fy.msgsys.servernetty;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		WebSocketFrame frame = (WebSocketFrame)msg;
		//真正的数据是放在buf里面的
		ByteBuf buf = frame.content();  
		
		//将数据按照utf-8的方式转化为字符串
		String aa = buf.toString(Charset.forName("utf-8"));  
		System.out.println("收到客户端消息："+aa);
		//创建一个websocket帧，将其发送给客户端
		WebSocketFrame out = new TextWebSocketFrame(aa);  
		ctx.pipeline().writeAndFlush(out).addListener(new ChannelFutureListener(){

			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				//从pipeline上面关闭的时候，会关闭底层的chanel，而且会从eventloop上面取消注册
				//ctx.pipeline().close();  
			}
			
		});
		
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




}
