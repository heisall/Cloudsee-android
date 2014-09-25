package com.jovision.commons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.jovetech.CloudSee.temp.R;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.mails.MailSenderInfo;
import com.jovision.utils.mails.SimpleMailSender;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {

	private Context act = null;
	private LayoutInflater inflater;
	private SharedPreferences sharedPreferences = null;
	AlertDialog alert;
	AlertDialog alert1;

	public DefaultExceptionHandler(Context act) {
		this.act = act;
		this.inflater = (LayoutInflater) act
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		sharedPreferences = act.getSharedPreferences("JVCONFIG",
				Context.MODE_PRIVATE);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		// 收集异常信息 并且发送到服务器

		String error = sendCrashReport(ex);

		// 等待半秒
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 处理异常
		handleException(error);

	}

	private String saveCrashInfo2File(Throwable ex) {
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
		try {
			long timestamp = System.currentTimeMillis();
			// String fileName = "crash-" + time + "-" + timestamp + ".log";
			String fileName = "crash-" + ".log";
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String path = "/sdcard/log/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}

			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
			MyLog.e("an error occured while writing file...", e + "");
		}

		return null;
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

		//
		// final Writer result = new StringWriter();
		// final PrintWriter printWriter = new PrintWriter(result);
		// // 获取跟踪的栈信息，除了系统栈信息，还把手机型号、系统版本、编译版本的唯一标示
		// StackTraceElement[] trace = ex.getStackTrace();
		// StackTraceElement[] trace2 = new StackTraceElement[trace.length + 3];
		// System.arraycopy(trace, 0, trace2, 0, trace.length);
		// trace2[trace.length + 0] = new StackTraceElement("Android", "MODEL",
		// android.os.Build.MODEL, -1);
		// trace2[trace.length + 1] = new StackTraceElement("Android",
		// "VERSION",
		// android.os.Build.VERSION.RELEASE, -1);
		// trace2[trace.length + 2] = new StackTraceElement("Android",
		// "FINGERPRINT", android.os.Build.FINGERPRINT, -1);
		// // 追加信息，因为后面会回调默认的处理方法
		// ex.setStackTrace(trace2);
		// ex.printStackTrace(printWriter);
		// // 把上面获取的堆栈信息转为字符串，打印出来
		// String stacktrace = result.toString();
		// printWriter.close();
		// Log.e("log", stacktrace);

		// 这里把刚才异常堆栈信息写入SD卡的Log日志里面
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			String sdcardPath = Environment.getExternalStorageDirectory()
					.getPath();
			writeLog(stacktrace, sdcardPath + File.separator);
		}

		return stacktrace;
	}

	private void handleException(final String error) {
		// TODO
		// 这里可以对异常进行处理。
		// 比如提示用户程序崩溃了。
		// 比如记录重要的信息，尝试恢复现场。
		// 或者干脆记录重要的信息后，直接杀死程序。
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				AlertDialog.Builder builder = new Builder(
						act.getApplicationContext());
				builder.setTitle(act.getResources().getString(R.string.tips));

				builder.setMessage(act.getResources().getString(
						R.string.app_name)
						+ act.getResources().getString(R.string.str_crash_tips));

				builder.setPositiveButton(R.string.str_crash_send,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								try {
									Calendar rightNow = Calendar.getInstance();
									String str = rightNow.get(Calendar.YEAR)
											+ "_"
											+ (rightNow.get(Calendar.MONTH) + 1
													+ "_" + rightNow
														.get(Calendar.DATE));
									MailSenderInfo mailInfo = new MailSenderInfo();
									mailInfo.setMailServerHost("smtp.qq.com");
									mailInfo.setMailServerPort("25");
									mailInfo.setValidate(true);
									mailInfo.setUserName("741376209@qq.com"); // 你的邮箱地址
									mailInfo.setPassword("mfq_zsw");// 您的邮箱密码
									mailInfo.setFromAddress("741376209@qq.com");
									mailInfo.setToAddress("jovision1203@163.com");// jovetech1203**
									// mailInfo.setToAddress("jy0329@163.com");
									mailInfo.setSubject("[BUG]["
											+ act.getResources().getString(
													R.string.app_name)
											+ "]"
											+ act.getResources()
													.getString(
															R.string.str_current_version));
									mailInfo.setContent("[" + str + "]" + error);

									// 这个类主要来发送邮件
									SimpleMailSender sms = new SimpleMailSender();
									boolean flag = sms.sendTextMail(mailInfo);// 发送文体格式
									// sms.sendHtmlMail(mailInfo);//发送html格式
									if (flag) {
										AlertDialog.Builder builder1 = new Builder(
												act.getApplicationContext());
										builder1.setTitle(act.getResources()
												.getString(R.string.tips));
										builder1.setMessage(R.string.str_send_success);
										builder1.setPositiveButton(
												R.string.login_str_close,
												new DialogInterface.OnClickListener() {

													public void onClick(
															DialogInterface dialog,
															int which) {
														// BaseApp.isAppKilled =
														// true;
														AccountUtil
																.signOut(act);
														System.exit(0);
														android.os.Process
																.killProcess(android.os.Process
																		.myPid());
													}
												});
										alert1 = builder1.create();
										alert1.getWindow()
												.setType(
														WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
										alert1.setCancelable(false);
										alert1.show();
									} else {
										// BaseApp.isAppKilled = true;
										AccountUtil.signOut(act);
										System.exit(0);
										android.os.Process
												.killProcess(android.os.Process
														.myPid());
									}
								} catch (Exception e) {
									MyLog.e("SendMail", e.getMessage());
								}

							}
						});
				builder.setNegativeButton(R.string.str_crash_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// BaseApp.isAppKilled = true;
								AccountUtil.signOut(act);
								System.exit(0);
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						});
				alert = builder.create();
				alert.getWindow().setType(
						WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				alert.setCancelable(false);
				alert.show();

				Looper.loop();
			}
		}.start();
	}

	// 写入Log信息的方法，写入到SD卡里面
	private void writeLog(String log, String name) {
		CharSequence timestamp = DateFormat.format("yyyyMMdd_kkmmss",
				System.currentTimeMillis());
		String filename = name + "Bug_" + timestamp + ".txt";
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