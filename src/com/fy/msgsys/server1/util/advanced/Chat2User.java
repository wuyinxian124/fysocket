package com.fy.msgsys.server1.util.advanced;

import java.util.*;

/**
 * 
 * @author wurunzhou
 * 
 * 缓存变量  互动室视图ID以及其在线用户
 * 目的是将 map list 读写锁分开。
 * <br> 鉴于 chatUser 存储结构是Map<key,List<String>>
 * 该类的目的是考虑在list 操作的时候不将整个map 锁定，只锁定list
 */
public final class Chat2User {

	
	
	public boolean contain(String chatID){
		
		return false;
	}
	
	public void addChat(String ChatID,List<String> userKey){
		
	}
	
	
	public void addUser(String userKey,List<String> chats){
		
	}
	
	public void removeUser(String userKey){
		
	}
}
