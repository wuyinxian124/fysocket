package edu.fy.com.ansyDemoPic;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.sun.org.apache.regexp.internal.recompile;

public class ServerPicSync {
	

    public static void main(String[] args) {

        try {
			new ServerPicSync().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
    
    private void start() throws IOException{
		ServerSocket server = new ServerSocket(8877);
    	while(true){
    		try {
				new Thread(new ServerThread1(server.accept())).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    }
    
    class ServerThread1 implements Runnable{

    	private Socket serverfork;
    	
    	public ServerThread1(Socket serverfork){
    		this.serverfork = serverfork;
    	}
    	
		@Override
		public void run() {
			try {
	            BufferedReader in = new BufferedReader(new InputStreamReader(serverfork.getInputStream()));
	            PrintWriter out=new PrintWriter(serverfork.getOutputStream());
	            DataInputStream datain = new DataInputStream(serverfork.getInputStream());
	            new ReceiveTread1(datain,out).start();
	            new SendThread1(out).start();
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
    	
    }

}


class SendThread1 extends Thread {

	PrintWriter out;

	public SendThread1(PrintWriter out) {
		this.out = out;
	}

	@Override
	public void run() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int jj = 0;
		try {
			while (true) {

				TimeUnit.SECONDS.sleep(7);
				out.println("Server  " + sf.format(new Date()) + "\n\t"
						+ "hello client" + jj++);

				out.flush();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}

class ReceiveTread1 extends Thread {
	private BufferedReader in;
	private InputStream ins;
	private PrintWriter out;
	private DataInputStream datain;
	
	public ReceiveTread1( BufferedReader in) {
		this.in = in;
	}
	
	public ReceiveTread1(InputStream ins,	PrintWriter out){
		this.ins = ins;
		this.out = out;
	}
	
	public ReceiveTread1(DataInputStream datain,PrintWriter out){
		this.out = out;
		this.datain = datain;
	}

	@Override
	public void run() {
		try {

			while (true) {

				long len = 0;
				if ((len = datain.readLong()) > 0) {

					// 本地保存路径，文件名会自动从客户端端继承而来。
					String fileName =  createFileName();
					String savePath = "e://" + fileName;
					// 服务器保存路径

					System.out.println("图片保存路径：" + savePath);
					BufferedOutputStream fileOut = new BufferedOutputStream(
							new FileOutputStream(savePath));
					System.out.println("准备接收文件！" + "\n");

					int cbufferSize = 8192;
					byte[] cbuf = new byte[cbufferSize];

					int read = 0;
					while(read<len){
						
						int read1 = datain.read(cbuf);
						read += read1;
						if(read1 == -1)
							break;
						
						fileOut.write(cbuf, 0, read1);
						fileOut.flush();
					}
					
					// 下面进度条本为图形界面的prograssBar做的，这里如果是打文件，可能会重复打印出一些相同的百分比
					System.out.println("图片大小为：" + read + "\n");
					System.out.println("接收完成！\n");
					out.println("接收的图片名称为： " + fileName + "\n");
					out.flush();
					fileOut.close();
					

				}
					
/*				while ((len = ins.read(cbuf)) != -1) {

					// 本地保存路径，文件名会自动从客户端端继承而来。
					String savePath = "e://" + createFileName();
					// 服务器保存路径

					System.out.println("图片保存路径：" + savePath);
					BufferedOutputStream fileOut = new BufferedOutputStream(
							new FileOutputStream(savePath));

					System.out.println("准备接收文件！" + "\n");
					int read = (int) len;
					fileOut.write(cbuf, 0, read);
					fileOut.flush();
					// 下面进度条本为图形界面的prograssBar做的，这里如果是打文件，可能会重复打印出一些相同的百分比
					System.out.println("图片大小为：" + read + "\n");
					System.out.println("接收完成！\n");
					out.println("收到图片 图片大小为：" + read + "\n");
					out.flush();
					fileOut.close();
				}*/

			}


		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("退出了");
	}
	
	
	private String getCurrentTime(String format) {
		format = format == null ? "yyyy-MM-dd HH:mm:ss" : format;
		Calendar c = Calendar.getInstance();
		SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(format);
		c = Calendar.getInstance(Locale.CHINESE);
		return simpleDateTimeFormat.format(c.getTime());// 输出这种形式 2008-03-12 10:11:21
	}

	private String createFileName(){
		//图片名: 以“wetoband截屏时分秒_4位随机数.jpg”命名
		StringBuffer fileName;
        fileName = new StringBuffer();
        fileName.append("wtb_screen");
        fileName.append(this.getCurrentTime("yyyyMMdd_HHmmss"));
        Random random = new Random();
        fileName.append("_" + random.nextInt(10000));
        fileName.append(".bmp");
		return fileName.toString();
	}
	

protected void receiveFile(Socket socket) {}

}