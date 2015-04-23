package edu.fy.com.websocketServer.simpleDemo;
/**
 * 
 * @功能 响应消息帧
 * @作者 蛋蛋
 */
public class ResponseFrame
{
	private String header;//报文头
	private byte[] md5;//md5
	public String getHeader()
    {
    	return header;
    }
	public void setHeader(String header)
    {
    	this.header = header;
    }
	public byte[] getMd5()
    {
    	return md5;
    }
	public void setMd5(byte[] md5)
    {
    	this.md5 = md5;
    }
}