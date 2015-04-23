package edu.fy.com;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ServerTest3 {
	private ServerSocket ss = null;
	private Socket s = null;
	private int port = 8877;

    public void start() {
        try {
        	ss = new ServerSocket(port);
            System.out.println("已启动server socket!");
            while (true) {
            	//接受请求
        		s = ss.accept();
        		new ThreadedServer3(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
        		if(s != null){
        			s.close();
        			s = null;
        		}
    		} catch (IOException e1) {
    			e.printStackTrace();
    		}
        } finally{
        	try {
        		if(ss != null){
        			ss.close();
        			ss = null;
        		}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
    }
    
    public static void main(String arg[]) {
        new ServerTest3().start();
    }
}

class ThreadedServer3 extends Thread {

	private Socket socket = null;
	private DataInputStream inputStream = null;
	private BufferedWriter bw = null;
	private StringBuffer fileName = null;
	
	public ThreadedServer3(Socket s) {
		this.socket = s;
		try {
			//图片名: 以“wetoband截屏时分秒_4位随机数.jpg”命名
	        fileName = new StringBuffer();
	        fileName.append("wtb_screen");
	        fileName.append(this.getCurrentTime("yyyyMMdd_HHmmss"));
	        Random random = new Random();
	        fileName.append("_" + random.nextInt(10000));
	        fileName.append(".jpg");
	        
			//输入流
			inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			//输出流
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			start();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if(socket != null){
					socket.close();
					socket = null;
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void run() {
		
		//获得客户端信息
		new ReadClientMessage3(inputStream, fileName.toString(),this);
		
		//读取客户端文件数据，并保存为物理文件
	    try {
	    	while(true){
	    		bw.write(fileName.toString());
	    		break;
	    	}
	    	TimeUnit.SECONDS.sleep(8);
	    } catch (Exception e) {
	    	e.printStackTrace();
	        System.out.println("接收消息，保存图片错误" + "\n");
	    } finally{
	    	System.out.println("ThreadedServer3 end...");
	    }

	}
	
	private String getCurrentTime(String format) {
		format = format == null ? "yyyy-MM-dd HH:mm:ss" : format;
		Calendar c = Calendar.getInstance();
		SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(format);
		c = Calendar.getInstance(Locale.CHINESE);
		return simpleDateTimeFormat.format(c.getTime());// 输出这种形式 2008-03-12 10:11:21
	}
	
	public void Callbake(){
		if(socket.isClosed())
			System.out.println("socket closed");
		else
			System.out.println("socket ok");
	}
}

//获得客户端信息
class ReadClientMessage3 extends Thread{
	private DataInputStream inputStream = null;
	private BufferedOutputStream fileOut = null;
	private String fileName = null;
	private ThreadedServer3 t3;
	public ReadClientMessage3(DataInputStream inputStream, String fileName,ThreadedServer3 t3){
		this.inputStream = inputStream;
		this.fileName = fileName;
		this.t3 = t3;
		start();
	}
	
	public void run() {
		//读取客户端文件数据，并保存为物理文件
	    try {
	        
	        //本地保存路径，文件名会自动从客户端端继承而来。
	    	String savePath = "e://" + fileName;
	    	//服务器保存路径
//			String savePath = System.getProperty("wtbsyqd.root")+ "screen_temp/"+ fileName;
	        
	    	//创建一个字节缓冲数组，用于指定每次写入的字节大小(8192=8k)
	        int bufferSize = 8192;
	        byte[] buf = new byte[bufferSize];
	        long len = 0;
	        
	        System.out.println("图片保存路径：" + savePath);
	        fileOut = new BufferedOutputStream(new FileOutputStream(savePath));
	        
	        System.out.println("准备接收文件！" + "\n");
	        while (true) {
	            int read = 0;
	            if (inputStream != null) {
	                read = inputStream.read(buf);
	            }
	            len += read;
	            if (read == -1) {
	                break;
	            }
	            //下面进度条本为图形界面的prograssBar做的，这里如果是打文件，可能会重复打印出一些相同的百分比
	            fileOut.write(buf, 0, read);
	        }
	        System.out.println("图片大小为：" + len + "\n");
	        System.out.println("接收完成！\n");
	        TimeUnit.SECONDS.sleep(5);
	        System.out.println("等 5 秒");
	        if(t3.isAlive())
	        	System.out.println("ok");
	        //Callbake
	        
	    } catch (Exception e) {
	    	e.printStackTrace();
	        System.out.println("接收消息，保存图片错误" + "\n");
	    } finally{
	    	try {
	    		if(fileOut != null){
	    			fileOut.close();
	    			fileOut = null;
	    		}


	    		if(inputStream != null){
	    			inputStream.close();
	    			inputStream = null;
	    		}
	    		
	    		t3.Callbake();
			} catch (IOException  e) {
				e.printStackTrace();
			}
	    }
	}
}

