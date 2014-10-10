package com.jovision.commons;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

/**
 * Logger, can output into files
 * 
 * @author neo
 * 
 */
public class MyLog {

	public static final String UB = "UB";

	private static final String V = "V";
	private static final String D = "D";
	private static final String I = "I";
	private static final String W = "W";
	private static final String E = "e";
	private static final String Ex = "E";

	private static final String RW = "rw";
	private static final String TAG = "MyLog";
	private static final String LINE_SEPARATOR = "\n";
	private static final String FORMATTER = "yyyy-MM-dd HH:mm:ss.SSS";

	private static String FOLDER = null;

	private static boolean ENABLE_UB = true;
	private static boolean ENABLE_FILE = true;
	private static boolean ENABLE_LOGCAT = true;

	private static File checkTag(String tag) {
		File result = null;

		if (null != FOLDER && init(FOLDER)) {
			result = new File(FOLDER + File.separator + tag);
			if (false == result.exists()) {
				try {
					result.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	private static String prepare(String level, String msg) {
		StringBuilder sBuilder = new StringBuilder(msg.length() + 64);
		sBuilder.append(new SimpleDateFormat(FORMATTER).format(new Date()))
				.append(File.pathSeparatorChar);

		if (null != level) {
			sBuilder.append(level).append(File.separatorChar);
		}

		if (null != msg) {
			sBuilder.append(msg);
		}

		sBuilder.append(LINE_SEPARATOR);
		return sBuilder.toString();
	}

	private static synchronized boolean append(File file, String string) {
		boolean result = false;

		if (null != file) {
			RandomAccessFile randomAccessFile = null;
			try {
				randomAccessFile = new RandomAccessFile(file.getPath(), RW);
				randomAccessFile.seek(randomAccessFile.length());
				randomAccessFile.writeBytes(string);
				randomAccessFile.close();
				randomAccessFile = null;
				result = true;
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
					Log.e(TAG, "append failed when write");
					ENABLE_FILE = false;
				}
			}
		} else {
			Log.e(TAG, "append without file");
		}

		return result;
	}

	public static void v(String tag, String msg) {
		if (ENABLE_LOGCAT) {
			Log.v(tag, msg);
		}

		if (ENABLE_FILE) {
			append(checkTag(tag), prepare(V, msg));
		}
	}

	public static void d(String tag, String msg) {
		if (ENABLE_LOGCAT) {
			Log.d(tag, msg);
		}

		if (ENABLE_FILE) {
			append(checkTag(tag), prepare(D, msg));
		}
	}

	public static void i(String tag, String msg) {
		if (ENABLE_LOGCAT) {
			Log.i(tag, msg);
		}

		if (ENABLE_FILE) {
			append(checkTag(tag), prepare(I, msg));
		}
	}

	public static void ub(String data) {
		if (ENABLE_UB) {
			append(checkTag(UB), prepare(null, data));
			Log.d(UB, data);
		}
	}

	public static void ub(String tag, String msg) {
		if (ENABLE_UB) {
			append(checkTag(UB), prepare(tag, msg));
			Log.d(UB, tag + ": " + msg);
		}
	}

	public static String getPath(String tag) {
		String filePath = null;

		File file = checkTag(tag);
		if (null != file && file.exists()) {
			filePath = file.getPath();
		}

		return filePath;
	}

	public static String getFolder(String tag) {
		String filePath = null;

		File file = checkTag(tag);
		if (null != file && file.exists()) {
			filePath = file.getParent();
		}

		return filePath;
	}

	public static boolean clean(String tag) {
		boolean result = false;

		File file = checkTag(tag);
		if (null != file && file.exists()) {
			result = file.delete();
		}

		return result;
	}

	public static void w(String tag, String msg) {
		if (ENABLE_LOGCAT) {
			Log.w(tag, msg);
		}

		if (ENABLE_FILE) {
			append(checkTag(tag), prepare(W, msg));
		}
	}

	public static void e(String tag, String msg) {
		if (ENABLE_LOGCAT) {
			Log.e(tag, msg);
		}

		if (ENABLE_FILE) {
			append(checkTag(tag), prepare(E, msg));
		}
	}

	public static void e(String tag, Exception e) {
		StringBuilder sBuilder = new StringBuilder(16 * 1024);
		sBuilder.append(e.getMessage()).append(LINE_SEPARATOR);

		StackTraceElement[] elements = e.getStackTrace();
		int size = elements.length;

		for (int i = 0; i < size; i++) {
			sBuilder.append(elements[i].getClassName()).append(".")
					.append(elements[i].getMethodName()).append("@")
					.append(elements[i].getLineNumber()).append(LINE_SEPARATOR);
		}

		String msg = sBuilder.toString();

		if (ENABLE_LOGCAT) {
			Log.e(tag, msg);
		}

		if (ENABLE_FILE) {
			append(checkTag(tag), prepare(Ex, msg));
		}
	}

	/**
	 * set log files' folder
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

	public static void enableUB(boolean enable) {
		ENABLE_UB = enable;
	}

	public static void enableFile(boolean enable) {
		ENABLE_FILE = enable;
	}

	public static void enableLogcat(boolean enable) {
		ENABLE_LOGCAT = enable;
	}

}
