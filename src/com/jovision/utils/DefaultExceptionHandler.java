package com.jovision.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.format.DateFormat;

import com.jovision.Consts;
import com.jovision.activities.JVOffLineDialogActivity;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {

	private Context context = null;
	public static Boolean tipDialog = false;

	public DefaultExceptionHandler(Context act) {
		// this.context = act;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		// 收集异常信息 并且发送到服务器
		String error = sendCrashReport(ex);
		ex.printStackTrace();
		// 等待半秒
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// //处理异常
		// handleException(error);

		// System.out.println("CaughtException: " + ex.toString());
		// Intent intent = new Intent(context, JVWelcomeActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(intent);

		if (!tipDialog) {
			tipDialog = true;
			Intent intent = new Intent(context, JVOffLineDialogActivity.class);
			intent.putExtra("ErrorCode", Consts.WHAT_APP_CRASH);
			intent.putExtra("ErrorMsg", error);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			android.os.Process.killProcess(android.os.Process.myPid());
		}

	}

	private String sendCrashReport(Throwable ex) {

		StringBuffer sb = new StringBuffer();
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();

		String result = writer.toString();
		sb.append(result);
		sb.append("\r\n");
		sb.append("MODEL----" + android.os.Build.MODEL);
		sb.append("\r\n");
		sb.append("VERSION----" + android.os.Build.VERSION.RELEASE);
		sb.append("\r\n");
		sb.append("FINGERPRINT----" + android.os.Build.FINGERPRINT);

		String stacktrace = sb.toString();

		// 这里把刚才异常堆栈信息写入SD卡的Log日志里面
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String sdcardPath = Environment.getExternalStorageDirectory()
					.getPath();
			writeLog(stacktrace, sdcardPath + File.separator);
		}

		return stacktrace;
	}

	// private void handleException(String error) {
	// // ((MainService) act).onNotify(Consts.APP_CRASH, 0, 0, error);
	// Intent intent = new Intent(context, JVOffLineDialogActivity.class);
	// intent.putExtra("ErrorCode", Consts.APP_CRASH);
	// intent.putExtra("ErrorMsg", error);
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// context.startActivity(intent);
	//
	// android.os.Process.killProcess(android.os.Process.myPid());
	// }

	// 写入Log信息的方法，写入到SD卡里面
	private void writeLog(String log, String name) {
		CharSequence timestamp = DateFormat.format("yyyyMMdd_kkmmss",
				System.currentTimeMillis());
		File file = new File(Consts.BUG_PATH);
		MobileUtil.createDirectory(file);

		String filename = Consts.BUG_PATH + "Bug_" + timestamp + ".txt";
		File logFile = new File(filename);
		try {
			FileOutputStream stream = new FileOutputStream(filename);
			OutputStreamWriter output = new OutputStreamWriter(stream);
			BufferedWriter bw = new BufferedWriter(output);
			bw.write(log);
			bw.newLine();
			bw.close();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

// import java.lang.Thread.UncaughtExceptionHandler;
//
// import android.content.Context;
//
// public class DefaultExceptionHandler implements UncaughtExceptionHandler {
//
// private Context act = null;
//
// public DefaultExceptionHandler(Context act) {
//
// this.act = act;
// }
//
// @Override
// public void uncaughtException(Thread thread, Throwable ex) {
//
// // 收集异常信息 并且发送到服务器
//
// sendCrashReport(ex);
//
// // 等待半秒
// try {
//
// Thread.sleep(500);
//
// } catch (InterruptedException e) {
//
//
// //
//
// }
//
// // 处理异常
//
// handleException();
//
// }
//
// private void sendCrashReport(Throwable ex) {
//
// StringBuffer exceptionStr = new StringBuffer();
//
// exceptionStr.append(ex.getMessage());
//
// StackTraceElement[] elements = ex.getStackTrace();
//
// for (int i = 0; i < elements.length; i++) {
// exceptionStr.append(elements[i].toString());
// }
//
// //发送收集到的Crash信息到服务器
// }
//
// private void handleException() {
//
//
// //这里可以对异常进行处理。
// //比如提示用户程序崩溃了。
// //比如记录重要的信息，尝试恢复现场。
// //或者干脆记录重要的信息后，直接杀死程序。
// }
//
//
// } 