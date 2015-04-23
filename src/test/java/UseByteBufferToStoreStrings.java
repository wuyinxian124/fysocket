package test.java;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class UseByteBufferToStoreStrings {
	
	public static void main(String[] args) {
		UseByteBufferToStoreStrings u =  new UseByteBufferToStoreStrings();
		//u.method1();
		u.method4();
		
	}
	
	public void method1(){
			ByteBuffer buf = ByteBuffer.allocate(50); 
			
			CharBuffer cbuf = buf.asCharBuffer();
			cbuf.put("Java Code Geeks");

			cbuf.flip();
			
			String s = cbuf.toString();  // a string

			System.out.println(s);
	}
	
	public void method2(){
		ByteBuffer buf = ByteBuffer.allocate(50); 
		buf.put("helllo sad阿萨德覅后".getBytes());
		buf.flip();
		while (buf.hasRemaining()){
			System.out.print(buf.get());
		}
		//System.out.println(byteBufferToString(buf));
	}
	
	public void method3(){
		ByteBuffer buf = ByteBuffer.allocate(50); 
		buf.put("helllo sad阿萨德覅后".getBytes());
		buf.flip();
		CharBuffer cbuf = buf.asCharBuffer();
		cbuf.flip();
		String s = cbuf.toString();  // a string
		System.out.println(s);
	}
	
	public void method4(){
		String lal = "helllo sad阿萨德覅后";
		System.out.println(getString(getByteBuffer(lal)));
	}
	
	public  String byteBufferToString(ByteBuffer buffer) {
		CharBuffer charBuffer = null;
		try {
			Charset charset = Charset.forName("UTF-8");
			CharsetDecoder decoder = charset.newDecoder();
			charBuffer = decoder.decode(buffer);
			buffer.flip();
			return charBuffer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	  public ByteBuffer getByteBuffer(String str)  
	    {  
	        return ByteBuffer.wrap(str.getBytes());  
	    }  
	  
	    /** 
	     * ByteBuffer 转换 String 
	     * @param buffer 
	     * @return 
	     */  
	    public  String getString(ByteBuffer buffer)  
	    {  
	        Charset charset = null;  
	        CharsetDecoder decoder = null;  
	        CharBuffer charBuffer = null;  
	        try  
	        {  
	            charset = Charset.forName("UTF-8");  
	            decoder = charset.newDecoder();  
	            // charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空  
	            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());  
	            return charBuffer.toString();  
	        }  
	        catch (Exception ex)  
	        {  
	            ex.printStackTrace();  
	            return "";  
	        }  
	    }  

}
