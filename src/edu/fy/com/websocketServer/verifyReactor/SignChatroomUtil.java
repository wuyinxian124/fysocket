package edu.fy.com.websocketServer.verifyReactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

public class SignChatroomUtil {

	// 该对象用来保存全局的 待登录用户(登录用户)及其拥有的帮区（私聊）互动室列表
	// 存储格式是 一个互动室对应多个 用户
	private  static ConcurrentMap<String, List<String>> CHAT1USER ;
	
	// 该map 用户来存储 反馈意见，公告，论坛 互动室视图ID 列表
	private static List<String> PUBLICCHAT2USER;
	//private static ConcurrentMap<String, ArrayList<String>> PUBLICCHAT2USER ;
	ConcurrentLinkedQueue<Long> lala;
	
	private SignChatroomUtil(){
		CHAT1USER = new ConcurrentHashMap<String, List<String>>();
		PUBLICCHAT2USER = Collections.synchronizedList(new ArrayList<String>());
	}
	
    private static class SingletonContainer{      
        private static SignChatroomUtil instance = new SignChatroomUtil();      
    }      
    public static SignChatroomUtil getInstance(){      
        return SingletonContainer.instance;      
    }   
	/**
	 * 添加待登录用户的 互动室列表  到消息系统
	 * @param userkey
	 * @param chats
	 */
    public void loginIn(String userkey,List<String> chats){
    	for(String chat :chats){
    		if(CHAT1USER.containsKey(chat)){
    			CHAT1USER.get(chat).add(userkey);
    		}else{
    			ArrayList<String> userIDS = new ArrayList<String>();
    			userIDS.add(userkey);
    			CHAT1USER.put(chat,  userIDS);
    		}
    	}
    	
    }
    
    public void registerNewChat(String chatVID,List<String> userIDS){
    	if(!CHAT1USER.containsKey(chatVID)){
    		CHAT1USER.put(chatVID+"",  userIDS);
    	}
    }
    
    /**
     * 将系统已有的反馈意见，公告，论坛 互动室注册到消息系统
     * 该方法在吾托帮启动的时候调用 ，还没有登录用户
     * @param chats  互动室视图ID 列表
     */
    public void registerOriginalPulbicChatroom(List<String> chats){
    	for(String chat :chats){
	    	if(CHAT1USER.containsKey(chat)){
	    		ArrayList<String> lal = new ArrayList<String>();
	    		CHAT1USER.put(chat,  lal);
	    	}
    	}
    }

    /**
     * 将新建的  反馈意见，公告，论坛 互动室 注册到消息系统
     * @param chat 新建的  反馈意见，公告，论坛 互动室视图ID
     * 该方法是在用户创建    反馈意见，公告，论坛 互动室的时候调用
     * 
     */
    public void registerPublicChatroom(String chat){
    	PUBLICCHAT2USER.add(chat);
    }
    
    /**
     * 如果一次添加多个 反馈意见，公告，论坛 互动室 注册到消息系统
     * 该方法在吾托帮初始化的时候调用
     * @param 反馈意见，公告，论坛 互动室 列表
     */
    public void registerPublicChatrooms(List<String> chats){
    	// 
    	PUBLICCHAT2USER.addAll(chats);
    }
    
    /**
     * 用户退出系统（吾托帮）
     * @param userkey
     * @param chats
     */
    public void loginOut(String userkey,List<String> chats){
    	
    	for(String chat:chats){
    		if(CHAT1USER.containsKey(chat)){
    			CHAT1USER.get(chat).remove(userkey);
    		}
    	}
    }
    
    /**
     * 用户退出  删除用户以及其对应的互动室列表
     * @param userkey
     */
    public void logoutUser(String userkey){
    	// 用户退出  删除用户以及其对应的互动室列表
    	// 迭代互动室  如果存在用户 删除其中的用户
    	if(!CHAT1USER.isEmpty()){
    		for(String cKey : CHAT1USER.keySet()){
    			for(String users: CHAT1USER.get(cKey)){
    				if(users.equals(userkey)){
    					CHAT1USER.get(cKey).remove(userkey);
    					break ;
    				}
    			}
    		}
    	}
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
    @SuppressWarnings("unchecked")
	public List<String> sendList(String chatViewId, String isPublice){
    	
    	if(null != isPublice && "1".equals(isPublice)){
    		// 公共互动室（如：反馈意见、论坛、公告、私聊公共互动室）
    		return new  ArrayList<String>(UtilUser.getInstance().getOlUser());
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


