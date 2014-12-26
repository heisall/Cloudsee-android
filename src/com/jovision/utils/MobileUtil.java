package com.jovision.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

public class MobileUtil {

	/**
	 * 收集手机信息
	 * 
	 * @param context
	 * @return
	 */
	public static String mobileSysVersion(Context context) {
		String mobileVersion = "";
		try {
			// String model = android.os.Build.MODEL;
			mobileVersion = android.os.Build.VERSION.RELEASE;
			// String fingerprint = android.os.Build.FINGERPRINT;
			// String ip = getIP();
			// String cpu = Build.CPU_ABI;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mobileVersion;

	}

	/**
	 * 获取手机IP
	 * 
	 * @return
	 */
	public static String getIP() {
		String ip = "";
		BufferedReader in = null;
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			in = new BufferedReader(new InputStreamReader(
					whatismyip.openStream()));
			ip = in.readLine();

		} catch (Exception e) {

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ip;
	}

	/**
	 * 获取剩余sdk卡空间
	 * 
	 * @return
	 */
	public static long getSDFreeSize() {
		// 取得SD卡文件路径
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		// return freeBlocks * blockSize; //单位Byte
		// return (freeBlocks * blockSize)/1024; //单位KB
		return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	}

	/**
	 * 递归创建文件目录
	 * 
	 * @author
	 * @param path
	 *            要创建的目录路径
	 */
	public static void createDirectory(File file) {
		if (null != file && file.getParentFile().exists()) {
			boolean res = file.mkdir();
			if (!res) {
				file.delete();
			}
		} else {
			createDirectory(file.getParentFile());
			boolean res = file.mkdir();
			if (!res) {
				file.delete();
			}
		}
	}

	/**
	 * 递归删除文件和文件夹,清空文件夹
	 * 
	 * @param file
	 *            要删除的根目录
	 */
	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				deleteFile(f);
			}
			file.delete();
		}
	}
}
