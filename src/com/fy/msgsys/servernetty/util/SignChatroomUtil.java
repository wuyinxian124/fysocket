package com.fy.msgsys.servernetty.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public final class SignChatroomUtil {

	/**
	 * 该对象用来保存全局的 <b>待</b>登录用户以及<b>待</b> 用户互动室列表
	 * 存储格式是 一个互动室对应多个 用户
	 */
	private  static ConcurrentMap<String, ArrayList<String>> CHAT1USER ;
			
	private SignChatroomUtil(){
		CHAT1USER = new ConcurrentHashMap<String, ArrayList<String>>();
	}
	
    private static class SingletonContainer{      
        private static SignChatroomUtil instance = new SignChatroomUtil();      
    }      
    public synchronized static SignChatroomUtil getInstance(){    
    	if(SingletonContainer.instance == null)
    		SingletonContainer.instance = new SignChatroomUtil();
        return SingletonContainer.instance;      
    }   
    // wurunzhou add at 20150313 for 吾托帮web 重启，重连socketFy 清理以往数据。
    public synchronized static void cleanDataInstance(){
//    	SingletonContainer.instance = null;
    	CHAT1USER = new ConcurrentHashMap<String, ArrayList<String>>();
    }
	
    /**
     * 缓存待登录用户对应的互动室列表
     * @param userkey
     * @param chats
     */
    public void loginIn(String userkey,List<String> chats){
    	for(String chat :chats){
    		if(CHAT1USER.containsKey(chat)){
    			if(CHAT1USER.get(chat).contains(userkey))
    				;
    			else
    				CHAT1USER.get(chat).add(userkey);
    		}else{
    			ArrayList<String> lal = new ArrayList<String>();
    			lal.add(userkey);
    			CHAT1USER.put(chat,  lal);
    		}
    	}
    	
    }
    

    /**
     * 用户退出
     * <br>
     * 如果 chats 为空，则移除所有互动室中存在的该用户
     * <br>
     * 如果chats 不为空，表示只是用户被请出了某互动室
     * @param userkey
     * @param chats
     */
    public void loginOut(String userkey,List<String> chats){
    	// 用户从pc退出，删除 互动室对应用户列表记录的 时候
    	// 需要考虑是否 用户是否还在app上登录。
    	// 如果用户从pc退出，却还没有在app上退出，则不要删除该条缓存
    	if(UserUtil.getInstance().contianAPPUser(userkey)){
    		return ;
    	}
    	if(chats == null){

			for (String chat : CHAT1USER.keySet()) {
			
			    ArrayList<String> users = CHAT1USER.get(chat);

			    for (int j=0;j<users.size();j++) {
				      if(users.get(j) == userkey||userkey.equals(users.get(j))){
				    	  users.remove(j);
				    	  j--;
				      }
				    }
			    CHAT1USER.put(chat, users);
			}
			
			return ;
    	}
    	for(String chat:chats){
    		if(CHAT1USER.containsKey(chat)){
    			CHAT1USER.get(chat).remove(userkey);
    		}
    	}
    }
    /**
     * app用户退出
     * <br>
     * 如果 chats 为空，则移除所有互动室中存在的该用户
     * <br>
     * 如果chats 不为空，表示只是用户被请出了某互动室
     * @param userkey
     * @param chats
     */
    public void loginOutApp(String userkey,List<String> chats){
    	// 用户从app退出，删除 互动室对应用户列表记录的 时候
    	// 需要考虑是否 用户是否还在pc 浏览器上登录。
    	// 如果用户从app退出，却还没有在pc上退出，则不要删除该条缓存
    	if(UserUtil.getInstance().contianUser(userkey)){
    		return ;
    	}
    	if(chats == null){

			for (String chat : CHAT1USER.keySet()) {
			
			    ArrayList<String> users = CHAT1USER.get(chat);

			    for (int j=0;j<users.size();j++) {
				      if(users.get(j) == userkey||userkey.equals(users.get(j))){
				    	  users.remove(j);
				    	  j--;
				      }
				    }
			    CHAT1USER.put(chat, users);
			}
			
			return ;
    	}
    	for(String chat:chats){
    		if(CHAT1USER.containsKey(chat)){
    			CHAT1USER.get(chat).remove(userkey);
    		}
    	}
    }
    
    public List<String> sendList(String chatView){
    	return CHAT1USER.get(chatView);
    }
	
    /**
     * @author liuyan 2015-1-29
     * 通过互动室ID和互动室类型
     * 如果是公共互动室，返回所有在线用户
     * 如果不是，则查询注册了该互动室的所有在线用户
     * @param chatViewId 互动室视图ID
     * @param isPublic 是否是公共互动室 , "1"：是; 不是："0"
     * @return
     */
	public List<String> sendList(String chatViewId, String isPublice){
    	
    	if(null != isPublice && "1".equals(isPublice)){
    		// 公共互动室（如：反馈意见、论坛、公告、私聊公共互动室）
    		return new ArrayList<String>(UserUtil.getInstance().getOlUser());
    	}else{
    		// 如果是帮区互动室或者是私聊室 发送给指定在线用户（ 注册了该互动室的用户）
    		return CHAT1USER.get(chatViewId);
    	}
    	
//    	if(PUBLICCHAT2USER.contains(chatViewId)){
//    		// 如果是 反馈意见 论坛 公告 消息 发给所有在线用户
//    		return new  ArrayList<String>(UtilUser.getInstance().getOlUser());
//    	}else{
//    		// 如果是帮区互动室或者是私聊室 发送给指定在线用户（指定用是指  注册了该互动室的用户）
//    		return CHAT1USER.get(chatViewId);
//    	}
    	
    }
}


