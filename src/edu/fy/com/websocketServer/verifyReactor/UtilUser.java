package edu.fy.com.websocketServer.verifyReactor;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public final class UtilUser {

	private static UtilUser UV;
	// 待登录用户以及验证码
	private static  Map<String, Object> MAPUSER ;
	// 已登录用户和连接（socket）
	private static Map<String ,Socket> MAPUSERCON;
	
	private UtilUser(){
		MAPUSER = new ConcurrentHashMap<String, Object>();
		MAPUSERCON = new ConcurrentHashMap<String, Socket>();
	}
	
	public synchronized static UtilUser getInstance(){
		if(UV == null)
			UV = new UtilUser();
		return UV;
	}
	
	/**
	 * 添加待登录用户
	 * @param userkey
	 * @param verifyCode
	 */
	public  void add(String userkey,String verifyCode){
		MAPUSER.put(userkey, verifyCode);
System.out.println("待登录用户  "+userkey + " 添加到消息服务器");
	}
	
	/**
	 * // 用户退出  1. 待登录用户  2. 已登录用户
	 * @param userkey
	 */
    public void userLoginOut(String userkey){
    	
    	if(MAPUSER.containsKey(userkey)){
    		MAPUSER.remove(userkey);
System.out.println("用户 " + userkey + " 退出（暂未登录）");
    	}
    	if(MAPUSERCON.containsKey(userkey)/*&&(MAPUSERCON.get(userkey) instanceof Socket)*/){
    		MAPUSERCON.remove(userkey);
System.out.println("用户 " + userkey + " 退出（已登录）");
    	}
    }
	
    /**
     *  用户登录消息系统
     *  用户已经通过第一次握手（websocket协议规定的）
     *  现在进行第二次握手（验证用户），通过用户ID和验证码，确认其已经登录吾托帮
     *  验证用户，如果验证通过 存储用户id:socket 键值对
     * @param userkey 用户ID
     * @param verifyCode 用户验证码，该验证码在用户登录吾托帮的时候，由吾托帮产生并提交给消息系统
     * @param connetion  用户（客户端）与消息服务器的连接对象（socket）
     * @return
     */
	public synchronized  boolean verify(String userkey,String verifyCode,Socket connetion){
		if(userkey == null || verifyCode == null || "".equals(userkey )||  "".equals(verifyCode))
			return false;
		
		boolean pass = false;
		String lal ;
		if(( lal = (String) MAPUSER.get(userkey) )!= null && verifyCode.equals(lal)){
			//if(MAPUSER.remove(userkey) == null);
//System.out.println("用户  ‘"+ userkey +"’ 登录通过 ，但其验证账号注销失败");
			MAPUSERCON.put(userkey, connetion);
			pass = true;
System.out.println("用户  ‘"+ userkey +"’ 登录验证通过");

		// 每个登录用户，都在反馈意见，论坛，公告的互动室中，一登录就要订阅该互动室

		}else{
			MAPUSER.remove(userkey);
			MAPUSERCON.remove(userkey);
System.out.println("用户  ’"+ userkey +"‘ 登录验证不 bu通过");
		}

		return pass;
	}
	
	

	/**
	 * 获取消息服务器 所有待登录用户列表
	 */
	public  void iterUser(){
	    //Iterator<String> it1 = MAPUSER.keySet().iterator();
System.out.println("myMap =="+MAPUSER);
	}
	
	/**
	 * 获取消息系统目前在线的所有用户
	 * @return
	 */
	public Set getOlUser(){
		return MAPUSERCON.keySet();
	}
	
	/**
	 * 通过用户ID ，获取用户在消息系统的连接对象 （socket）
	 * @param userKey
	 * @return
	 */
	public Socket getConnet(String userKey){
		return  MAPUSERCON.get(userKey);
	}
}
