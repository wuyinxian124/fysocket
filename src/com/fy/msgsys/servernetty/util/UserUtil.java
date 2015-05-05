package com.fy.msgsys.servernetty.util;

import io.netty.channel.Channel;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fy.msgsys.server.util.logger.LoggerUtil;


public final class UserUtil {

	private final Logger logger = LoggerUtil.getLogger(this.getClass().getName());
	
	private static UserUtil UV;
	/**
	 *  待登录用户以及验证码
	 */
	private static  Map<String, Object> MAPUSER ;
	// 已登录用户和连接（socket）
	//private static Map<String ,Socket> MAPUSERCON;
	/**
	 * 已登录用户和连接（socket）
	 * <br>
	 * 格式为：<userKey:<<url：socket>,<url:socket>>>
	 * 一个用户对应多个连接。多个连接通过URL来标记
	 */
	private static Map<String, Map<String, Channel>> MAPUSERCON;
	
	/**
	 * app 用户登录时
	 * <br>
	 * 已登录用户和连接（socket）
	 * <br>
	 * 格式为：<userKey:<<url：socket>,<url:socket>>>
	 * 一个用户对应多个连接。多个连接通过URL来标记
	 */
	private static Map<String, Map<String, Channel>> MAPUSERCONAPP;
	/**
	 * 是MAPUSERCON value
	 * <br>
	 * 格式是:<url：socket>
	 */
	private Map<String, Channel> URLCON;
	
	private UserUtil(){
		MAPUSER = new ConcurrentHashMap<String, Object>();
		//MAPUSERCON = new ConcurrentHashMap<String, Socket>();
		MAPUSERCON = new ConcurrentHashMap<String, Map<String, Channel>>();
		MAPUSERCONAPP = new ConcurrentHashMap<String, Map<String, Channel>>();
	}
	
	
	// 单例具体实现方法
	public synchronized static UserUtil getInstance(){
		if(UV == null)
			UV = new UserUtil();
		return UV;
	}
	
	// wurunzhou add at 20150313 for 吾托帮web 重启，重连socketFy 清理以往数据。
	public  synchronized static void cleanDataInstance(){
//		UV = null;
		MAPUSER = new ConcurrentHashMap<String, Object>();
		//MAPUSERCON = new ConcurrentHashMap<String, Socket>();
		MAPUSERCON = new ConcurrentHashMap<String, Map<String, Channel>>();
		MAPUSERCONAPP = new ConcurrentHashMap<String, Map<String, Channel>>();
	}
	/**
	 * 添加待登录用户
	 * @param userkey
	 * @param verifyCode
	 */
	public  void add(String userkey,String verifyCode){
		MAPUSER.put(userkey, verifyCode);
		logger.log(Level.INFO,"待登录用户  "+userkey + " 添加到消息服务器");
	}
	
	/** liuysn_gotoMessageCenter
	 * // 用户退出  1. 待登录用户  2. 已登录用户
	 * @param userkey 用户关键字 目前是useraccount
	 */
    public void userLoginOut(String url,String userkey){
    	
    	if(!MAPUSERCONAPP.containsKey(userkey)){
    		// 如果pc 用户退出的时候，app还有用户登录着。就不能删除该缓存，否则会刷新异常。
    		Object o = MAPUSER.remove(userkey);
    		if(o == null){
    			logger.log(Level.INFO,"用户 " + userkey + " 退出（暂未登录）-----------");
    		}else{
    			logger.log(Level.INFO,"用户 " + userkey + " 退出（暂未登录）");
    		}
    		
    	}else{
    		logger.log(Level.INFO,"用户 " + userkey + " 还在app上登录");
    	}
//    	if(MAPUSERCON.containsKey(userkey)/*&&(MAPUSERCON.get(userkey) instanceof Socket)*/){
    		Object o1 = MAPUSERCON.remove(userkey);
    		if(o1 == null){
    			logger.log(Level.INFO,"用户 " + userkey + " 退出（已登录）------------");
    		}else {
        		logger.log(Level.INFO,"用户 " + userkey + " 退出（已登录）");
    		}

//    	}
    }
    
	/** liuysn_gotoMessageCenter
	 * // 用户退出  1. 待登录用户  2. 已登录用户
	 * @param userkey 用户关键字 目前是useraccount
	 */
    public void userLoginOutAPP(String url,String userkey){
    	
    	if(!MAPUSERCON.containsKey(userkey)){
    		Object o = MAPUSER.remove(userkey);
    		if(o == null){
    			logger.log(Level.INFO,"用户 " + userkey + " 退出（暂未登录）-----------");
    		}else{
    			logger.log(Level.INFO,"用户 " + userkey + " 退出（暂未登录）");
    		}
    		
    	}else{
    		logger.log(Level.INFO,"用户 " + userkey + " 还在pc浏览器上登录");
    	}
//    	if(MAPUSERCON.containsKey(userkey)/*&&(MAPUSERCON.get(userkey) instanceof Socket)*/){
    		Object o1 = MAPUSERCONAPP.remove(userkey);
    		if(o1 == null){
    			logger.log(Level.INFO,"用户 " + userkey + " 退出（已登录）------------");
    		}else {
        		logger.log(Level.INFO,"用户 " + userkey + " 退出（已登录）");
    		}

//    	}
    }
	
    /**
     *  用户登录消息系统
     *  <br> 用户已经通过第一次握手（websocket协议规定的）
     *  <br> 现在进行第二次握手（验证用户），通过用户ID和验证码，确认其已经登录吾托帮
     *  验证用户，如果验证通过 存储用户id:socket 键值对
     * @param userkey 用户ID
     * @param verifyCode 用户验证码，该验证码在用户登录吾托帮的时候，由吾托帮产生并提交给消息系统
     * @param url 用户所访问的url
     * @param connetion  用户（客户端）与消息服务器的连接对象（socket）
     * @return
     */
	public synchronized  boolean verify(String userkey, String verifyCode, String url, Channel connetion){
		
		if(userkey == null || verifyCode == null || "".equals(userkey )||  "".equals(verifyCode))
			return false;
		
		boolean pass = false;
		String lal ;
		if(MAPUSER.get(userkey) != null && (( lal = (String) MAPUSER.get(userkey) )!= null && verifyCode.equals(lal)) ){
			// 将用户ID从待登录缓存中删除
//			MAPUSER.remove(userkey);
			// 将socket 连接对象保存在缓存中
			if(MAPUSERCON.get(userkey) == null){
				URLCON = new HashMap<String, Channel>();
				URLCON.put(url, connetion);
				MAPUSERCON.put(userkey, URLCON);
			}else{
				// 如果同样的url对应的socket对象已经存在，那么首先要移除对应的socket对象，然后再添加。
				if(MAPUSERCON.get(userkey).containsKey(url)){
					MAPUSERCON.get(userkey).remove(url);
				}
				URLCON = MAPUSERCON.get(userkey);
				URLCON.put(url, connetion);
				// wurunzhou add at 20150410 for 解决用户刷新无法获取转发消息的问题
				MAPUSERCON.put(userkey, URLCON);
			}
			pass = true;
			logger.log(Level.INFO,"用户  ‘"+ userkey +"’ 登录验证通过");

		} else if(MAPUSER.get(userkey) != null && (( lal = (String) MAPUSER.get(userkey) )!= null && !verifyCode.equals(lal))&&(MAPUSERCON.containsKey(userkey)||MAPUSERCONAPP.containsKey(userkey))){
			// 如果用户存在mapuser ，而且用户还在pc或者app上登录 ，则不再验证用户验证码
			// 将socket 连接对象保存在缓存中
			if(MAPUSERCON.get(userkey) == null){
				URLCON = new HashMap<String, Channel>();
				URLCON.put(url, connetion);
				MAPUSERCON.put(userkey, URLCON);
			}else{
				// 如果同样的url对应的socket对象已经存在，那么首先要移除对应的socket对象，然后再添加。
				if(MAPUSERCON.get(userkey).containsKey(url)){
					MAPUSERCON.get(userkey).remove(url);
				}
				URLCON = MAPUSERCON.get(userkey);
				URLCON.put(url, connetion);
				// wurunzhou add at 20150410 for 解决用户刷新无法获取转发消息的问题
				MAPUSERCON.put(userkey, URLCON);
			}
			pass = true;
			logger.log(Level.INFO,"用户  ‘"+ userkey +"’ 登录验证通过（app在登录）");
		} else{
//			MAPUSER.remove(userkey);
//			MAPUSERCON.remove(userkey);
			userLoginOut("",userkey);
			logger.log(Level.INFO,"用户  ’"+ userkey +"‘ 登录验证不 bu通过");
		}

		return pass;
	}
	
	/**
	 * 判断userkey 是否在pc登录用户 缓存中。
	 * @param userkey
	 * @return
	 */
	public synchronized boolean contianUser(String userkey){
		return MAPUSERCON.containsKey(userkey);
		
	}
	
	/**
	 * 判断userkey 是否在app登录用户 缓存中。
	 * @param userkey
	 * @return
	 */
	public synchronized boolean contianAPPUser(String userkey){
		
		return MAPUSERCONAPP.containsKey(userkey);
	}
	
    /**
     *  用户登录消息系统
     *  <br> 用户已经通过第一次握手（websocket协议规定的）
     *  <br> 现在进行第二次握手（验证用户），通过用户ID和验证码，确认其已经登录吾托帮
     *  验证用户，如果验证通过 存储用户id:socket 键值对
     * @param userkey 用户ID
     * @param verifyCode 用户验证码，该验证码在用户登录吾托帮的时候，由吾托帮产生并提交给消息系统
     * @param url 用户所访问的url
     * @param connetion  用户（客户端）与消息服务器的连接对象（socket）
     * @return
     */
	public synchronized  boolean verifyapp(String userkey, String verifyCode, String url,String tag, Channel connetion){
		
		if(userkey == null || verifyCode == null || "".equals(userkey )||  "".equals(verifyCode))
			return false;
		if("" == tag|| "".equals(tag)){
			return false;
		}
		boolean pass = false;
		String lal ;
		if(MAPUSER.get(userkey) != null || (( lal = (String) MAPUSER.get(userkey) )!= null && verifyCode.equals(lal)) ){
			// 将用户ID从待登录缓存中删除
//			MAPUSER.remove(userkey);
			// 将socket 连接对象保存在缓存中
			if(MAPUSERCONAPP.get(userkey) == null){
				URLCON = new HashMap<String, Channel>();
				URLCON.put(url, connetion);
				MAPUSERCONAPP.put(userkey, URLCON);
			}else{
				// 如果同样的url对应的socket对象已经存在，那么首先要移除对应的socket对象，然后再添加。
				if(MAPUSERCONAPP.get(userkey).containsKey(url)){
					MAPUSERCONAPP.get(userkey).remove(url);
				}
				URLCON = MAPUSERCONAPP.get(userkey);
				URLCON.put(url, connetion);
				// wurunzhou add at 20150410 for 解决用户刷新无法获取转发消息的问题
				MAPUSERCONAPP.put(userkey, URLCON);
			}
			pass = true;
			logger.log(Level.INFO,"用户  ‘"+ userkey +"’ app登录验证通过");

		}else if(MAPUSER.get(userkey) != null && (( lal = (String) MAPUSER.get(userkey) )!= null && !verifyCode.equals(lal))&&(MAPUSERCON.containsKey(userkey)||MAPUSERCONAPP.containsKey(userkey))){
			// 如果用户存在mapuser ，而且用户还在pc或者app上登录 ，则不再验证用户验证码
			// 将socket 连接对象保存在缓存中
			if(MAPUSERCONAPP.get(userkey) == null){
				URLCON = new HashMap<String, Channel>();
				URLCON.put(url, connetion);
				MAPUSERCONAPP.put(userkey, URLCON);
			}else{
				// 如果同样的url对应的socket对象已经存在，那么首先要移除对应的socket对象，然后再添加。
				if(MAPUSERCONAPP.get(userkey).containsKey(url)){
					MAPUSERCONAPP.get(userkey).remove(url);
				}
				URLCON = MAPUSERCONAPP.get(userkey);
				URLCON.put(url, connetion);
				// wurunzhou add at 20150410 for 解决用户刷新无法获取转发消息的问题
				MAPUSERCONAPP.put(userkey, URLCON);
			}
			pass = true;
			logger.log(Level.INFO,"用户  ‘"+ userkey +"’ app登录验证通过(pc在登录)");
		}  else{
			userLoginOutAPP("", userkey);
			logger.log(Level.INFO,"用户  ’"+ userkey +"‘ app登录验证不 bu通过");
		}

		return pass;
	}
	
	/**
	 * wurunzhou add at 20150327
	 * <br>
	 * 如果发现有无效socket ，将其从缓存中删除。
	 * @param invalid socket 对象
	 * @param userKey	用户关键字
	 */
	public void clearInvalidSocket(Channel invalid,String userKey){
		
		if(MAPUSERCON.containsKey(userKey)){
//			for(String url:MAPUSERCON.get(userKey)){
//				
//			}
			if(MAPUSERCON.get(userKey).containsValue(invalid)){
				// 如果判断该socket对象存在，那么从缓存中清除
				logger.log(Level.INFO,"用户  ‘"+ userKey +"’ 存在非有效socket连接");
				for(Map.Entry<String, Channel> entry: MAPUSERCON.get(userKey).entrySet()){
					if(entry.getValue() == invalid||invalid.equals(entry.getValue())){
						String url = entry.getKey();
						logger.log(Level.INFO,"用户  ‘"+ userKey +"’ 存在非有效socket连接" + ",在对应的url中" + url);
						MAPUSERCON.get(userKey).remove(url);
						break;
					}
				}
			}

		}
		
	}

	/**
	 * 获取消息服务器 所有待登录用户列表
	 */
	public  void iterUser(){
	    //Iterator<String> it1 = MAPUSER.keySet().iterator();
		logger.log(Level.INFO,"myMap =="+MAPUSER);
	}
	
	/**
	 * 获取消息系统目前在线的所有用户
	 * <br>
	 * 包括pc用户和app 用户
	 * @return
	 */
	public Set<String> getOlUser(){
		Set<String> allUser=new HashSet<String>();
		Set<String> olUser = MAPUSERCON.keySet();
		Set<String> olUserapp = MAPUSERCONAPP.keySet();
		allUser.addAll(olUser);
		allUser.addAll(olUserapp);
		return allUser;
	}
	
	/**
	 * 通过用户ID ，获取用户在消息系统的连接对象 （socket）
	 * @param userKey 
	 * @return socket连接集合
	 */
	public List<Channel> getConnets(String userKey){
		List<Channel> conections = new ArrayList<Channel>();
		Map<String, Channel> urlCon = MAPUSERCON.get(userKey);
		Map<String, Channel> urlConApp = MAPUSERCONAPP.get(userKey);
		if(urlCon == null&&urlConApp==null){
			return null;
		}
		if(urlCon != null){
			for(String key : urlCon.keySet()){
				conections.add(urlCon.get(key));
			}
		}
		if(urlConApp != null){
		for(String url : urlConApp.keySet()){
			conections.add(urlConApp.get(url));
		}
		}
		return conections;
	}
	
	/**
	 * 通过用户ID和url ，获取用户在消息系统的连接对象 （socket）
	 * @param userKey 
	 * @param url
	 * @return socket连接
	 */
//	public Socket getConnet(String userKey, String url){
//		Map<String, Socket> urlCon = MAPUSERCON.get(userKey);
//		Map<String, Socket> urlConApp = MAPUSERCON.get(userKey);
//		if(urlCon != null){
//			return urlCon.get(url);
//		}
//		return null;
//	}
}
