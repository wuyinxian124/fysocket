package com.fy.msgsys.server.util.advanced;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public final class SignChatroomUtil1 {

	// 该对象用来保存全局的 待登录用户以及待 用户互动室列表
	// 存储格式是 一个互动室对应多个 用户
	private static Map<String, ArrayList<String>> CHAT1USER ;
			
	// 读写锁 保证 CHAT1USER 数据在多线程访问的情况下，数据一致
	private ReentrantReadWriteLock lock;
	
	/**
	 *  下面三个方法用于获取单例对象
	 */
	private SignChatroomUtil1(){
		CHAT1USER = new HashMap<String, ArrayList<String>>();
		lock = new ReentrantReadWriteLock(true);
	}
	
    private static class SingletonContainer{      
        private static SignChatroomUtil1 instance = new SignChatroomUtil1();      
    }      
    public static SignChatroomUtil1 getInstance(){      
        return SingletonContainer.instance;      
    }   
	
    /**
     * 缓存待登录用户对应的互动室列表
     * @param userkey 用户ID
     * @param chats 	互动室视图ID列表
     */
    public void loginIn(String userkey,List<String> chats){
    	for(String chatID :chats){
    		if(CHAT1USER.containsKey(chatID)){
    			lock.writeLock().lock();
    			
    			CHAT1USER.get(chatID).add(userkey);
    			
    			lock.writeLock().unlock();
    		}else{
    			ArrayList<String> userlist = new ArrayList<String>();
    			userlist.add(userkey);
    			lock.writeLock().lock();
    			
    			CHAT1USER.put(chatID,  userlist);
    			
    			lock.writeLock().unlock();
    		}
    	}
    	
    }
    

    /**
     * 用户下线
     * <br> 用户下线，将用户从缓存变量中删除。
     * <br> 后台服务器将不会向该用户推送即时消息。
     * @param userkey
     * @param chats
     */
    public void loginOut(String userkey,List<String> chats){
    	
    	for(String chat:chats){
    		if(CHAT1USER.containsKey(chat)){
    			lock.writeLock().lock();
    			
    			CHAT1USER.get(chat).remove(userkey);
    			
    			lock.writeLock().unlock();
    		}
    	}
    }
    
    /**
     * 通过互动室视图id获取该互动室对应的所有在线用户
     * @param chatView
     * @return
     */
	public List<String> sendList(String chatView) {
		lock.readLock().lock();

		List<String> userList = CHAT1USER.get(chatView);

		lock.readLock().unlock();
		return userList;
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
    		return new ArrayList<String>(UserUtil1.getInstance().getOlUser());
    	}else{
    		// 如果是帮区互动室或者是私聊室 发送给指定在线用户（ 注册了该互动室的用户）
//    		return CHAT1USER.get(chatViewId);
    		return sendList(chatViewId);
    	}
    }
}


