package com.fy.msgsys.server1.util.constant;


/**
 * 设置常量
 * @author wurunzhou
 *
 */
public enum UtilConstant {
	
	Handshake("1"),
	Veify("2"),
	Transfer("3");
	
	private String res;
	

	private UtilConstant(String res){
		this.res = res;
	}
	
	public String getRes(){
		return this.res;
	}
}
