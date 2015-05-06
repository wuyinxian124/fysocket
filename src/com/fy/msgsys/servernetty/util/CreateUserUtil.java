package com.fy.msgsys.servernetty.util;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fy.msgsys.servernetty.util.logger.LoggerUtil;

public class CreateUserUtil {

	private Logger logger = LoggerUtil.getLogger(this.getClass().getName());
	public void start(){
		new Thread(new CreateThread()).start();
		new Thread(new checkThread()).start();
	}
	
	class CreateThread implements Runnable{

		@Override
		public void run() {
			int i = 0;
			while(true){
				i++;
				try {
					TimeUnit.SECONDS.sleep(4);
					UserUtil.getInstance().add("user"+i, "verify"+i);
					logger.log(Level.INFO,"===" + i);
				} catch (InterruptedException e) {
					logger.log(Level.WARNING," throw error" + e);
					break;
				}
			}
		}
		
	}
	
	class checkThread implements Runnable{

		@Override
		public void run() {
			
			while(true){
				try{
					TimeUnit.SECONDS.sleep(8);
					UserUtil.getInstance().iterUser();
				}catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		
	}

}
