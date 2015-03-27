package com.mediatek.elian;

import com.jovision.utils.MobileUtil;

import android.os.Build;
import android.util.Log;

/**
 * Created by yangyuan on 12/17/14.
 */
public class ElianNative {
	private final static String TAG = "Elian";
	private final static int ARM_V6 = 0;
	private final static int ARM_V7 = 1;
	private final static int X86 = 2;
	private final static int OTHER = 9;
	static {
		// System.loadLibrary("elianjni");
	}

	public ElianNative() {

	}

	public static boolean LoadLib() {
		 //记录CPU的类型，注意android2.2以后，会有两个CPU类型        
		 String CPU_ABI = android.os.Build.CPU_ABI;        
		 String CPU_ABI2 = "none";        
		 if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) 
		 { // CPU_ABI2 since 2.2            
			 try {                
				 CPU_ABI2 = (String)android.os.Build.class.getDeclaredField("CPU_ABI2").get(null);            
			 } catch (Exception e) 
			 { 
				 
			 }       
		 }//各种CPU参数记录 
		 boolean hasNeon = false, hasFpu = false, hasArmV6 = false,  hasArmV7 = false, hasMips = false, hasX86 = false;  
		 float bogoMIPS = -1;        
		 int processors = 0;
		 //判断CPU的架构    		 
		 try { 
			 if(CPU_ABI.equals("x86")){
				 hasX86 = true;
				 return false;
			 }
			 else if(CPU_ABI.equals("armeabi-v7a") || CPU_ABI2.equals("armeabi-v7a")){
				 hasArmV7 = true;  
				 System.loadLibrary("elianjni-v7a");
				 Log.e(TAG, "load elianjni-v7a");			 
				 //hasArmV6 = true; 
			 }else if(CPU_ABI.equals("armeabi") || CPU_ABI2.equals("armeabi")) {
				 hasArmV6 = true;
				 System.loadLibrary("elianjni");
				 Log.e(TAG, "load elianjni");			 
			 }
			 else{
				 Log.e(TAG, "其他非主流arch");
				 return false;
			 }
		} catch (UnsatisfiedLinkError ule) {
			System.err.println("WARNING: Could not load elianjni library!");
			return false;
		}catch (Exception e) {  
	        e.printStackTrace();  
	        return false;
	    }
		return true;	
	}

	public native int GetProtoVersion();

	public native int GetLibVersion();

	/*
	 * Init SmartConnection
	 */
	public native int InitSmartConnection(String Target, int sendV1, int sendV4);

	/**
	 * Start SmartConnection with Home AP
	 * 
	 * @SSID : SSID of Home AP
	 * @Password : Password of Home AP
	 * @Auth : Auth of Home AP
	 */
	public native int StartSmartConnection(String SSID, String Password,
			String Custom, byte Auth);

	/**
	 * Stop SmartConnection by user
	 * 
	 */

	public native int StopSmartConnection();

}
