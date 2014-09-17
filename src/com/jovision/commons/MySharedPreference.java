package com.jovision.commons;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MySharedPreference {
	private static Context mContext;
	private static SharedPreferences sharedPreferences = null;
	private static Editor editor = null;// 获取编辑器

	/**
	 * 初始化
	 * 
	 * @param con
	 */
	public static void init(Context con) {
		mContext = con;
		sharedPreferences = mContext.getSharedPreferences("JVCONFIG",
				Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
	}

	/**
	 * 保存String
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean putString(String key, String value) {
		editor.putString(key, value);
		return editor.commit();
	}

	/**
	 * 保存boolean
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean putBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		return editor.commit();
	}

	/**
	 * 保存int
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean putInt(String key, int value) {
		editor.putInt(key, value);
		return editor.commit();
	}

	/**
	 * 读取String
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		String value = sharedPreferences.getString(key, "");
		return value;
	}

	/**
	 * 读取boolean
	 * 
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(String key) {
		boolean value = sharedPreferences.getBoolean(key, false);
		return value;
	}

	/**
	 * 读取boolean
	 * 
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(String key, boolean def) {
		boolean value = sharedPreferences.getBoolean(key, def);
		return value;
	}

	/**
	 * 读取int
	 * 
	 * @param key
	 * @return
	 */
	public static int getInt(String key) {
		int value = sharedPreferences.getInt(key, 0);
		return value;
	}

}
