package edu.fy.com.websocketServer.simpleDemo;



/**
 * 
 * @功能 二进制工具类
 * @作者 蛋蛋
 */
public class BinaryUtil
{
	/**
	 * 将int 转换成 byte ,高字节序
	 * @param i
	 * @return
	 */
	public static byte[] formatInteage(int n)
	{
		byte[] bytes = new byte[4];
		for (int i = 0;i < bytes.length;i++)
		{
			bytes[i] = (byte) (n >> ((7 - i) * 8));
		}
		return bytes;
	}
	
	
	public static String formatBytes(byte ... bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (byte byt : bytes)
		{
			sb.append(String.format("%02X ", byt));
		}
		return sb.toString();
	}
	
	public static void main(String[] args)
    {
	    int n = 30;
	    System.out.println(formatBytes(formatInteage(n)));
    }
}
