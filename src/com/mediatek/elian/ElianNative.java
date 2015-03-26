package com.mediatek.elian;

/**
 * Created by yangyuan on 12/17/14.
 */
public class ElianNative {
	static {
		// System.loadLibrary("elianjni");
	}

	public ElianNative() {

	}

	public static boolean LoadLib() {
		try {
			System.loadLibrary("elianjni");
			return true;
		} catch (UnsatisfiedLinkError ule) {
			System.err.println("WARNING: Could not load elianjni library!");
			return false;
		}
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
