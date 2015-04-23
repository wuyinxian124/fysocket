package edu.fy.com;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


public class ClientTest3 {
    
    private Socket socket = null;
    private DataOutputStream out = null;
    private DataInputStream fis = null;
    private BufferedReader br = null;
	
//    private String ip = "test.wetoband.com"; //设置成服务器IP
    private String ip = "localhost";  //本地测试
    
    private int port = 8877;

    private String filePath = "D:\\123.bmp"; //进行传输的文件

    private Thread th;
    public ClientTest3() {
        try {
            if (createConnection()) {
            	//1、接收服务器消息
            	//获得输入流
            	br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            	th = new ReadServerMessage3(br, socket);
            	
            	//2、发送图片给服务器
            	while(true){
            		if(sendFile(filePath)){
            			break;
            		}
            	}
            }else{
            	System.out.println("客户端连接未打开！！");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally{
        	//关闭br socket
        	System.out.println("client end............");
        }
    }

    private boolean createConnection() {
        try {
        	
        	socket = new Socket(ip, port);
            System.out.print("连接服务器成功!" + "\n");
            return true;
        } catch (Exception e) {
            System.out.print("连接服务器失败!" + "\n");
            return false;
        }
    }

    /**
     * 发送截屏文件
     * @param filePath 文件路径
     */
    public boolean sendFile(String filePath){
    	try {
    		th.start();
    		//获得输出流
    		out = new DataOutputStream(socket.getOutputStream());
    		
    		//向输出流写入文件
    		fis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
			File fi = new File(filePath);
            
            int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];
            long len = fi.length();
            System.out.println("文件名称:" + fi.getName());
            System.out.println("文件大小:" + len);
            while (true) {
                int read = 0;
                if (fis != null) {
                    read = fis.read(buf);
                }
                if (read == -1) {
                    break;
                }
                System.out.println("文件传输中...");
                out.write(buf, 0, read);
            }
            System.out.println("文件传输完成");
            Thread.yield();
            TimeUnit.SECONDS.sleep(8);
            System.out.println("传输完成 等 8秒");
            if (out != null){
            	out.flush();
                out.close();
                out = null;
            }
            if(fis != null){
            	fis.close();
            	fis = null;
            }
            return true;
            
		} catch (InterruptedException e) {
			System.out.print("发送文件失败!" + "\n");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			// 注意关闭socket链接哦，不然客户端会等待server的数据过来，
            // 直到socket超时，导致数据不完整。  
//			shutDownConnection();
		}
		return false;
    }
    
    public void shutDownConnection() {
        try {
            if (out != null){
            	out.flush();
                out.close();
                out = null;
            }
            if(fis != null){
            	fis.close();
            	fis = null;
            }
            if (socket != null){
            	socket.close();
            	socket = null;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    public static void main(String arg[]) {
        new ClientTest3();
    }
}

//从服务器读取数据
class ReadServerMessage3 extends Thread{
	BufferedReader br;
	Socket socket;
	
	public ReadServerMessage3(BufferedReader br, Socket socket){
		this.br = br;
		this.socket = socket;
		
	}
	
	@Override
	public void run(){
		try {
			while (true) {
				if (socket != null && (!socket.isInputShutdown())&& (!socket.isClosed())) {
					System.out.println("11111111111");
					// br = new BufferedReader(new
					// InputStreamReader(socket.getInputStream()));
					String fileName = br.readLine();
					if (fileName != null) {
						System.out.println("截屏图片名=======" + fileName);
						break;
					}
				} else
					System.out.println("socket over");
				
				System.out.println("0000000");
			}
			TimeUnit.SECONDS.sleep(1);
			System.out.println("接收完之后 等一秒 over");
		} catch (IOException e) {
			System.out.println("客户端：获取截屏图片名出错");
			e.printStackTrace();
			try {
				if(br != null)
				br.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}