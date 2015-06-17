package com.fy.msgsys.servernetty;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fy.msgsys.servernetty.util.*;
import com.fy.msgsys.servernetty.util.logger.LoggerUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
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

		
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.log(Level.INFO," 方法 channelReadComplete 执行");
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.log(Level.WARNING," 方法 exceptionCaught 执行 抛出异常");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		logger.log(Level.WARNING,"channelRead0 方法被执行");
		if(msg instanceof WebSocketFrame){
			handleWebscoketFrame(ctx,(WebSocketFrame)msg);
		}else{
			logger.log(Level.WARNING, "获取的片段不是websocketFrame");
		}
	}

	/**
	 * 
	 * @param ctx
	 * @param frame
	 */
	private void handleWebscoketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
		logger.log(Level.INFO, "服务器获得frame,开始判断frame类型");
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            //handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
        	ctx.channel().close();
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()
                    .getName()));
        }
        
		dealText(frame);
	}

	private void dealText(WebSocketFrame frame){
		//WebSocketFrame frame = (WebSocketFrame)msg;
		//真正的数据是放在buf里面的
		ByteBuf buf = frame.content();  
		
		//将数据按照utf-8的方式转化为字符串
		String msg0 = buf.toString(Charset.forName("utf-8"));  
		logger.log(Level.INFO, "收到客户端消息："+msg0);
		dealMsg(msg0);
	}
	

	/**
	 * 处理客户端消息
	 * <br>
	 * 截取消息
	 * <br>
	 * 找到对应互动室
	 * <br>
	 * 转发
	 * @param msgStr
	 */
	private void dealMsg(String msgStr){
		// 截取消息
		String[] msgInfos = msgStr.split(SocketConstant.splitCT.getRssURL());
		
		int userAindex = msgInfos[2].indexOf("senderAccount");
		int userAindex1 = msgInfos[2].indexOf("\":\"", userAindex);
		int userAindex2 = msgInfos[2].indexOf("\",\"", userAindex1);
		String sendUserAccout = msgInfos[2].substring(userAindex1+3, userAindex2); 
//		String chatId = msgInfos[0];
//		String isPublice = msgInfos[1];
//		String msgContent = msgInfos[2];
		transferTextMessage1(msgInfos[0], msgInfos[1], msgStr,sendUserAccout);
			
		// 找到互动室
	}

	/**
	 * 转发文本消息到对应在线用户
	 * @param chatId 互动室视图ID
	 * @param isPublice 是否是公共互动室，1 为是，0 为否
	 * @param byteBuf
	 * @throws IOException 
	 */
	private void transferTextMessage1(String chatId, String isPublice,
			String msgcontent,String sendUserA) {

		logger.log(Level.INFO, "转发消息chatID=" + chatId + ".ispublic="+ isPublice + ".转发消息体：" + msgcontent);

		// 转发消息
		// 根据互动室 查询用户 获得connection
		List<String> olUsers = SignChatroomUtil.getInstance().sendList(chatId, isPublice);
		
		if (olUsers == null||olUsers.size()==0)
			return;
		
		logger.log(Level.INFO, "互动室 ‘" + chatId + "’ 在线用户(待登录用户)有："+ olUsers);
		
		for (String oluser : olUsers) {
			if(oluser == null) continue;
			// wurunzhou add for 自己不转发给自己 begin
			if(oluser.equals(sendUserA)) continue;
			
			// wurunzhou add for 自己不转发给自己  end
			logger.log(Level.INFO,"------" +oluser+",size=" + olUsers.size());
			// 一个用户多个连接 update by liuyan 20150207
			List<Channel> conections = UserUtil.getInstance().getConnets(oluser);
			
			if (conections == null||conections.size() == 0)
				continue;
			
			for (Channel c : conections) {
				// wurunzhou edit at 20150327 for如果出现有某个连接断开的情况，直接将其删除 begin
				if(c == null) {
					// 将该socket对象从缓存中删除
					UserUtil.getInstance().clearInvalidSocket(c, oluser);
					continue ;
				}
				logger.log(Level.INFO,"cccccccc------" +c+",conectionssize=" + conections.size());
				// wurunzhou edit at 20150327 for如果出现有某个连接断开的情况，直接将其删除 end
				if (c.isActive() ) {
					// wurunzhou edit at 20150327 for如果出现有某个连接断开的情况，直接将其删除 begin
					try {
						transferMessage1(true, msgcontent,c);
						// wurunzhou edit at 20150327 for如果出现有某个连接断开的情况，直接将其删除 end
						logger.log(Level.INFO, "转发…………………………给 " + oluser);
						logger.log(Level.INFO, c.toString());
					} catch (IOException e) {
						logger.log(Level.WARNING, "转发给  " + oluser+"-----失败");
						UserUtil.getInstance().clearInvalidSocket(c, oluser);
					} 

				}else{
					// wurunzhou edit at 20150330 for如果出现有某个连接断开的情况，直接将其删除 begin
					// 将该socket对象从缓存中删除
					UserUtil.getInstance().clearInvalidSocket(c, oluser);
					// wurunzhou edit at 20150330 for如果出现有某个连接断开的情况，直接将其删除 end
				}
				
			}
		}

	}

	/**
	 * 转发文本消息的具体执行
	 * @param finalFragment
	 * @param byteBuf
	 * @param outs
	 * @throws IOException
	 */
	private void transferMessage1(boolean finalFragment,
			String  textMsg, Channel channel)
			throws IOException {
		logger.log(Level.INFO, "执行转发操作"+"transferMessage");
//		//创建一个websocket帧，将其发送给客户端
		WebSocketFrame out = new TextWebSocketFrame(textMsg);  
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
		channel.writeAndFlush(out);
	}

}
