package com.fy.msgsys.servernetty;

import java.util.logging.Logger;

import com.fy.msgsys.server.util.logger.LoggerUtil;

import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;

public class ServerN {

	private final Logger logger= LoggerUtil.getLogger(this.getClass().getName());
	
	private final ChannelGroup group = new DefaultChannelGroup(
			ImmediateEventExecutor.INSTANCE);

	
	public void run() throws Exception {

		// 这个是用于serversocketchannel的eventloop
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		// 这个是用于处理accept到的channel
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			// 构建serverbootstrap对象
			ServerBootstrap b = new ServerBootstrap();

			// 设置时间循环对象，前者用来处理accept事件，后者用于处理已经建立的连接的io
			b.group(bossGroup, workerGroup);

			// 用它来建立新accept的连接，用于构造serversocketchannel的工厂类
			b.channel(NioServerSocketChannel.class);

			// 为accept channel的pipeline预添加的inboundhandler
			b.childHandler(new ChannelInitializer<SocketChannel>() {

				// 当新连接accept的时候，这个方法会调用
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {

					// ch.pipeline().addLast(new ReadTimeoutHandler(10));
					// ch.pipeline().addLast(new WriteTimeoutHandler(1));
					// 用于解析http报文的handler
					ch.pipeline().addLast("decoder", new HttpRequestDecoder());

					// 用于将解析出来的数据封装成http对象，httprequest什么的
					ch.pipeline().addLast("aggregator",
							new HttpObjectAggregator(65536));

					// 用于将response编码成httpresponse报文发送
					ch.pipeline().addLast("encoder", new HttpResponseEncoder());

					// websocket的handler部分定义的，它会自己处理握手等操作
					ch.pipeline().addLast("handshake",
							new WebSocketServerProtocolHandler("", "", true));
					// ch.pipeline().addLast("chunkedWriter", new
					// ChunkedWriteHandler());
					// ch.pipeline().addLast(new HttpHanlder());
					ch.pipeline().addLast("verify",new VerifyHandler());
					
					
					ch.pipeline().addLast(new WebSocketHandler());
				}

			});
			// bind方法会创建一个serverchannel，并且会将当前的channel注册到eventloop上面，
			// 会为其绑定本地端口，并对其进行初始化，为其的pipeline加一些默认的handler
			ChannelFuture f = b.bind(8877).sync();
			// 相当于在这里阻塞，直到serverchannel关闭
			f.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String args[]) throws Exception {
		new ServerN().run();
	}
}
