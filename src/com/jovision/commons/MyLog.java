package com.jovision.commons;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

/**
 * 日志工具类
 * 
 * 含输出至文件，以 tag 作为文件区分的标志。需要对输出目录有写入权限。
 * 
 * @author neo
 * 
 */
public class MyLog {
	private static boolean ENABLE_FILE = true;
	private static boolean ENABLE_LOGCAT = true;

	private static String FOLDER = null;

	private static boolean append(String level, String tag, String msg) {
		boolean result = false;
		if (null != FOLDER && init(FOLDER)) {
			File file = new File(FOLDER + File.separator + tag);
			if (false == file.exists()) {
				try {
					result = file.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				result = true;
			}

			if (false == result) {
				Log.e("Log", "append file failed when create");
				ENABLE_FILE = false;
			}

			if (result) {
				result = false;
				RandomAccessFile randomAccessFile = null;
				try {
					synchronized (FOLDER) {
						randomAccessFile = new RandomAccessFile(file.getPath(),
								"rw");
						randomAccessFile.seek(randomAccessFile.length());
						StringBuilder sBuilder = new StringBuilder(
								msg.length() + 32);
						sBuilder.append(
								new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
										.format(new Date())).append(": ")
								.append(level).append("\\").append(msg)
								.append("\n");
						randomAccessFile.writeBytes(sBuilder.toString());
						sBuilder.delete(0, sBuilder.length() - 1);
						sBuilder = null;
						randomAccessFile.close();
						randomAccessFile = null;
						result = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (randomAccessFile != null) {
						try {
							randomAccessFile.close();
							randomAccessFile = null;
						} catch (IOException e2) {
						}
					}

					if (false == result) {
						Log.e("Log", "append file failed when write");
						ENABLE_FILE = false;
					}
				}
			}
		} else {
			ENABLE_FILE = false;
		}

		if (false == result) {
			Log.e("Log", "append file failed");
			ENABLE_FILE = false;
		}

		return result;
	}

	public static void d(String tag, String msg) {
		if (ENABLE_LOGCAT) {
			Log.d(tag, msg);
		}

		if (ENABLE_FILE) {
			append("D", tag, msg);
		}
	}

	public static void e(String tag, Exception e) {
		StringBuilder sBuilder = new StringBuilder(16 * 1024);
		sBuilder.append(e.getMessage()).append("\n");

		StackTraceElement[] elements = e.getStackTrace();
		int size = elements.length;

		for (int i = 0; i < size; i++) {
			sBuilder.append(elements[i].getClassName()).append(".")
					.append(elements[i].getMethodName()).append("@")
					.append(elements[i].getLineNumber()).append("\n");
		}

		if (ENABLE_LOGCAT) {
			Log.e(tag, sBuilder.toString());
		}

		if (ENABLE_FILE) {
			append("Ex", tag, sBuilder.toString());
		}
	}

	public static void e(String tag, String msg) {
		if (ENABLE_LOGCAT) {
			Log.e(tag, msg);
		}

		if (ENABLE_FILE) {
			append("E", tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (ENABLE_LOGCAT) {
			Log.i(tag, msg);
		}

		if (ENABLE_FILE) {
			append("I", tag, msg);
		}
	}

	/**
	 * 指定日志输出目录
	 * 
	 * @param logPath
	 * @return
	 */
	public static boolean init(String logPath) {
		boolean result = false;

		File folder = new File(logPath);

		if (false == folder.exists()) {
			result = folder.mkdirs();
		} else {
			result = true;
		}

		if (result) {
			result = false;
			if (folder.canRead() && folder.canWrite() && folder.isDirectory()) {
				FOLDER = logPath;
				result = true;
			}
		}

		return result;
	}

	public static void setENABLE_FILE(boolean enable_file) {
		ENABLE_FILE = enable_file;
	}

	public static void setENABLE_LOGCAT(boolean enable_logcat) {
		ENABLE_LOGCAT = enable_logcat;
	}

	public static void v(String tag, String msg) {
		if (ENABLE_LOGCAT) {
			Log.v(tag, msg);
		}

		if (ENABLE_FILE) {
			append("V", tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (ENABLE_LOGCAT) {
			Log.w(tag, msg);
		}

		if (ENABLE_FILE) {
			append("W", tag, msg);
		}
	}

}
