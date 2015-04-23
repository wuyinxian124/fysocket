package test.java;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.fy.com.websocketServer.verifyReactor.UtilUser;


public class TestMain {

	public static void main(String[] args) {
		new TestMain().test11();
	}
	
	public void test12(){
	    Set set1=new HashSet();
	    set1.add(new Date());    //向列表中添加数据

	    set1.add("apple");     //向列表中添加数据

	    set1.add(new Integer(3));   //向列表中添加数据

	    set1.add(new Socket());    //向列表中添加数据

	    int size=set1.size();

	    System.out.println("Set1集合的大小为：" + size);

	    Set set2=new HashSet();
	    set2.add("book");     //向列表中添加数据

	    set2.add(new Long(3));    //向列表中添加数据
	    set2.add("22");

	    int size2=set2.size();

	    System.out.println("Set2集合的大小为：" + size2);

	    set1.addAll(set2);

	    System.out.println("合并后Set1集合的大小为：" + set1.size());
	    
	    for (Object number : set1) {
	        System.out.println("Number = " + number);
	    }
	}
	
	public void test11(){
    
		Map<String, Integer> arrlist = new HashMap<String, Integer>();
	    arrlist.put("1",20);
	    arrlist.put("2",22);
	    arrlist.put("32",24);
		Map<String, Integer> arrlist1 = new HashMap<String, Integer>();
		arrlist1.put("12",202);
		arrlist1.put("22",222);
		arrlist1.put("32",242);
	    
		Set<String> set1=new HashSet<String>();
		Set<String> ar = arrlist.keySet();
		Set<String> ar1 = arrlist1.keySet();
		set1.addAll(ar);
		set1.addAll(ar1);
		
	    for (String number : set1) {
	        System.out.println("Number = " + number);
	    }
	    
	}
	
	public void test10(){
	    // create an empty array list with an initial capacity
	    ArrayList<Integer> arrlist = new ArrayList<Integer>(5);

	    // use add() method to add elements in the deque
	    arrlist.add(20);
	    arrlist.add(15);
	    arrlist.add(30);
	    arrlist.add(45);

	    System.out.println("Size of list: " + arrlist.size());
		
	    // let us print all the elements available in list
	    for (Integer number : arrlist) {
	      System.out.println("Number = " + number);
	    }  
		
	    // Removes element at 3rd position
	   // arrlist.remove(2);

		
	    // let us print all the elements available in list
	    for (int j=0;j<arrlist.size();j++) {
	      if(arrlist.get(j) == 15){
	    	  arrlist.remove(j);
	    	  j--;
	      }
	    }

	    System.out.println("Now, Size of list: " + arrlist.size());
	    for (Integer number : arrlist) {
	        System.out.println("Number = " + number);
	      }
	}
	
	public List<String> test9(List<Integer> aa){
		List<String> reS = new ArrayList<String>();
		for(int a : aa){
			reS.add(test8(a));
		}
		
		return reS;
	}
	
	public String test8(int a){
		
		boolean fir = false;
		boolean two = false;
		String reb = "" ;
		if(a%3 == 0){
			reb = "dou";
			fir = true;
		}else if(a%5 == 0){
			reb = "ban";
			two = true;
		}
		if(two&&fir){
			reb = "douban";
			return reb;
		}else{
			return a+"";
		}
		
	}
	
	
	
	public void test7(){
		ByteBuffer  bb = ByteBuffer.allocate(25);
		bb.put("lalalalhello world".getBytes());
		bb.flip();
		System.out.println(bb.limit());
		ByteBuffer  bb1 = ByteBuffer.allocate(36);
		bb1.put("23233".getBytes());
		bb1.put(bb);
		bb1.flip();
		System.out.println(bb1.limit());
	}
	
	public void test6(){
		ByteBuffer  bb = ByteBuffer.allocate(25);
		bb.put("lalalalhello world".getBytes());
		bb.flip();
		System.out.println(byteBufferToString(bb));
		bb.position(0);
		System.out.println(bb.position()+","+bb.limit());
		
	}
	
	public  String byteBufferToString(ByteBuffer buf){
		
		return new String(byteBufferToByte(buf));
	}
	
	/**
	 * bytebuffer 有效内容转byte数组
	 * @param buf
	 * @return
	 */
	public static byte[] byteBufferToByte(ByteBuffer buf){
		// Retrieve bytes between the position and limit
		// (see Putting Bytes into a ByteBuffer)
		byte[] bytes = new byte[buf.remaining()];

		// transfer bytes from this buffer into the given destination array
		buf.get(bytes, 0, bytes.length);
		return bytes;
	}
	
	
	public  ByteBuffer stringToBytebuffer(String msg){
		byte[] msgb = msg.getBytes();
		ByteBuffer msgBuf = ByteBuffer.allocate(msgb.length);
		msgBuf.put(msgb);
		msgBuf.flip();
		
		return msgBuf;
	}
	
	public void test5(){
		
		ByteBuffer buf = ByteBuffer.allocate(25);
		buf.put("lalalalalallhello ".getBytes());
		buf.flip();
		ByteBuffer src = ByteBuffer.allocate(buf.limit());
		src.put(buf);
		System.out.println(src.position());
		src.flip();
		buf.position(0);
		System.out.println(","+buf.limit()+","+buf.capacity()+","+buf.position());
		byte[] line = new byte[buf.limit()];
		int i = 0;
		while(buf.hasRemaining()){
			//System.out.print(buf.get());
			line[i++] =  buf.get();
		}
		
		System.out.println(new String(line));
		System.out.println(src.limit());
	}
	
	public void BitCount2( int n)
	{
	     int c =0 ;
	    for (c =0; n!= 0; ++c)
	    {
	        n &= (n -1) ; // 清除最低位的1
	    }
	    
	    System.out.println(c);
	}
	
	public void test4(){
		Map<String, String> mm = new HashMap<String, String>();
		mm.put("11", "www");
		mm.put("11", "lalal");
		System.out.println(mm.get("11"));
		
	}
	public void test3(){
		
		String message = "#U#user1:verify1";
		String tmp = message.substring(0, 3);
		if ("#U#".equals(tmp)) {
			// 发送的是待登录用户
			String[] vU = message.substring(3, message.length()).split(":");
			System.out.println(vU[0]+":"+vU[1]);
		} else if ("#C#".equals(tmp)) {
			// 发送的是待登录用户 互动室列表
			
		} else {
			// 发送的是 消息
		}
	}
	
	
	public void test2(){
		List<String> chats = new ArrayList<String>();
		
		for(int i=0;i<10 ;i++){
			chats.add("chatroom"+i);
		}
		System.out.println(chats.toString());
		String2List(chats.toString());
	}
	
	public void String2List(String message){
		String tmp = message.substring(1, message.length()-1);
		List<String> lala = new ArrayList<String>();
		for(String lal :tmp.split(",")){
			lala.add(lal.trim());
		}
		for(String it : lala){
			System.out.println(it);
		}
	}
	
	public void test1(){

/*		String lala = "hello wusir 13  0";
		System.out.println(lala.substring(12, lala.length()));*/
		
		
		ByteBuffer input = ByteBuffer.allocate(1024);
		input.clear();
		input.put("get lalal".getBytes());
		input.flip();
		byte[] all = new byte[input.remaining()];

		input.get(all, 0, all.length);

		//input.wrap(all, 0, "get lalal".length());
		System.out.println(new String(all));
	}

}
