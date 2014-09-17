package com.jovision.audio;

public class Audio {

	public static native boolean init();

	public static native byte[] dec(byte[] in);

	public static native byte[] enc(byte[] in);

	public static native boolean deinit();

}
