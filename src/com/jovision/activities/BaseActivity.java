package com.jovision.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.test.JVACCOUNT;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.IHandlerLikeNotify;
import com.jovision.IHandlerNotify;
import com.jovision.MainApplication;
import com.jovision.commons.MyActivityManager;
import com.jovision.utils.BitmapCache;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.MobileUtil;
import com.tencent.stat.StatService;

/**
 * 抽象的活动基类，所有活动都应该继承这个类，并实现其抽象方法和接口
 * 
 * @author neo
 * 
 */
public abstract class BaseActivity extends FragmentActivity implements
		IHandlerNotify, IHandlerLikeNotify {
	protected ProgressDialog proDialog;
	protected Toast toast;
	public Configuration configuration;
	public DisplayMetrics disMetrics;
	public Button leftBtn;
	public Button rightBtn;
	public TextView currentMenu;
	protected static RelativeLayout alarmnet;
	protected TextView accountError;
	private static boolean local = false;// 是否本地登陆

	// private long duration;
	// private static final String RUNTIME = ".runtime";

	public HashMap<String, String> statusHashMap;
	public ArrayList<String> markedAlarmList;

	// DataHelper dataHelper = null;

	/** 初始化设置，不要在这里写费时的操作 */
	protected abstract void initSettings();

	/** 初始化界面，不要在这里写费时的操作 */
	protected abstract void initUi();

	/** 保存设置，不要在这里写费时的操作 */
	protected abstract void saveSettings();

	/** 释放资源、解锁、删除不用的对象，不要在这里写费时的操作 */
	protected abstract void freeMe();

	IHandlerNotify notify = this;
	protected MyHandler handler = new MyHandler(this);

	protected static class MyHandler extends Handler {

		private BaseActivity activity;

		public MyHandler(BaseActivity activity) {
			this.activity = activity;
		}

		@Override
		public void handleMessage(Message msg) {
			try {
				activity.notify
						.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
				// switch (msg.what) {
				// case Consts.WHAT_ALARM_NET:
				// if (null != alarmnet && !local) {
				// alarmnet.setVisibility(View.VISIBLE);
				// }
				// break;
				// case Consts.WHAT_ALARM_NET_WEEK:
				// if (null != alarmnet) {
				// alarmnet.setVisibility(View.GONE);
				// }
				// break;
				// case Consts.WHAT_SESSION_FAILURE:// session失效
				//
				// break;
				// default:
				// break;
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}

			super.handleMessage(msg);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getActivityManager().pushActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// MySharedPreference.init(getApplication());//放在MainApplication里了
		// markedAlarmList = ((MainApplication)
		// getApplicationContext()).getMarkedAlarmList();
		// markedAlarmList =
		// ConfigUtil.convertToArray(MySharedPreference.getString("MARKED_ALARM"));
		statusHashMap = ((MainApplication) getApplicationContext())
				.getStatusHashMap();
		configuration = getResources().getConfiguration();
		disMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
		local = Boolean.valueOf(this.statusHashMap.get(Consts.LOCAL_LOGIN));
		initSettings();
		initUi();
		// if (null != alarmnet) {
		// alarmnet.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// if (null != statusHashMap.get(Consts.ACCOUNT_ERROR)) {
		// int errorCode = Integer.parseInt(statusHashMap
		// .get(Consts.ACCOUNT_ERROR));
		// switch (errorCode) {
		// case Consts.WHAT_HEART_ERROR:// 心跳异常
		// if (android.os.Build.VERSION.SDK_INT > 10) {
		// startActivity(new Intent(
		// android.provider.Settings.ACTION_SETTINGS));
		// } else {
		// startActivity(new Intent(
		// android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		// }
		// break;
		// case Consts.WHAT_HAS_NOT_LOGIN:// 账号未登录
		// createDialog("", false);
		// LoginTask task = new LoginTask(BaseActivity.this,
		// (MainApplication) getApplication(),
		// statusHashMap, alarmnet);
		// String[] params = new String[3];
		// task.execute(params);
		// break;
		// }
		// }
		// }
		// });
		// }

	}

	@Override
	protected void onResume() {
		((MainApplication) getApplication()).setCurrentNotifyer(this);
		super.onResume();
		StatService.onResume(this);
		// if (null != statusHashMap.get(Consts.ACCOUNT_ERROR)) {
		// int errorCode = Integer.parseInt(statusHashMap
		// .get(Consts.ACCOUNT_ERROR));
		// switch (errorCode) {
		// case Consts.WHAT_ALARM_NET:// 网络异常
		// if (null != alarmnet && !local) {
		// alarmnet.setVisibility(View.VISIBLE);
		// if (null != accountError) {
		// accountError.setText(R.string.network_error_tips);
		// }
		// }
		//
		// break;
		// case Consts.WHAT_HAS_LOGIN_SUCCESS:// 账号正常登陆
		// case Consts.WHAT_ALARM_NET_WEEK:// 网络恢复正常
		// case Consts.WHAT_ACCOUNT_NORMAL:// 账号恢复正常
		// if (null != alarmnet) {
		// alarmnet.setVisibility(View.GONE);
		// }
		// break;
		// case Consts.WHAT_HAS_NOT_LOGIN:// 账号未登录
		// if (null != alarmnet && !local) {
		// alarmnet.setVisibility(View.VISIBLE);
		// if (null != accountError) {
		// accountError.setText(R.string.account_error_tips);
		// }
		// }
		// break;
		// case Consts.WHAT_SESSION_FAILURE:// session失效
		//
		// break;
		// }
		// }

		// duration = System.currentTimeMillis();
	}

	@Override
	protected void onPause() {
		// duration = System.currentTimeMillis() - duration;
		// MyLog.ubStat(TAG + RUNTIME, (int) (duration / 1000));
		StatService.onPause(this);
		dismissDialog();
		saveSettings();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		freeMe();
		MyActivityManager.getActivityManager().popActivity(this);
		super.onDestroy();
	}

	/**
	 * 弹dialog
	 * 
	 * @param id
	 */
	protected void createDialog(int id, boolean cancel) {
		try {
			if (null != BaseActivity.this && !BaseActivity.this.isFinishing()) {
				if (null == proDialog) {
					proDialog = new ProgressDialog(BaseActivity.this);
				}
				proDialog.setMessage(BaseActivity.this.getString(id));
				if (null != proDialog) {
					if (null != BaseActivity.this
							&& !BaseActivity.this.isFinishing()) {
						proDialog.show();
						proDialog.setCancelable(cancel);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 弹dialog
	 * 
	 * @param msg
	 */
	public void createDialog(String msg, boolean cancel) {

		if (null == msg || "".equalsIgnoreCase(msg)) {
			msg = getResources().getString(R.string.waiting);
		}
		try {
			if (null != BaseActivity.this && !BaseActivity.this.isFinishing()) {
				if (null == proDialog) {
					proDialog = new ProgressDialog(BaseActivity.this);
				}
				proDialog.setMessage(msg);
				if (null != proDialog) {
					if (null != BaseActivity.this
							&& !BaseActivity.this.isFinishing()) {
						proDialog.show();
						proDialog.setCancelable(cancel);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 关闭dialog
	 * 
	 * @param dialog
	 */
	public void dismissDialog() {
		if (null != proDialog && !this.isFinishing()) {
			proDialog.dismiss();
			proDialog = null;
		}
	}

	/**
	 * 弹系统消息
	 * 
	 * @param context
	 * @param id
	 */
	public void showTextToast(int id) {
		String msg = getApplication().getResources().getString(id);
		if (toast == null) {
			toast = Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}

	/**
	 * 弹系统消息
	 * 
	 * @param context
	 * @param msg
	 */
	public void showTextToast(String msg) {
		if (toast == null) {
			toast = Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @param activity
	 * @return
	 */
	public int getStatusHeight(Activity activity) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass
						.getField("status_bar_height").get(localObject)
						.toString());
				statusHeight = activity.getResources()
						.getDimensionPixelSize(i5);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

	public Dialog netErrorDialog;

	/**
	 * 没有网络提示 打开设置网络界面
	 * */
	public void alertNetDialog() {
		statusHashMap.put(Consts.HAS_LOAD_DEMO, "false");
		if (null != netErrorDialog && netErrorDialog.isShowing()) {
			return;
		}
		try {
			// 提示对话框
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(R.string.tips)
					.setMessage(R.string.network_error)
					.setPositiveButton(R.string.setting,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									if (android.os.Build.VERSION.SDK_INT > 10) {
										startActivity(new Intent(
												android.provider.Settings.ACTION_SETTINGS));
									} else {
										startActivity(new Intent(
												android.provider.Settings.ACTION_WIRELESS_SETTINGS));
									}

								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

			netErrorDialog = builder.create();
			netErrorDialog.show();
		} catch (ActivityNotFoundException e) {
			showTextToast(R.string.network_error);
			e.printStackTrace();
		}

	}

	/**
	 * 判断是否有sd卡
	 */
	public boolean hasSDCard(int minSize) {
		boolean canSave = true;
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			showTextToast(R.string.str_out_memery);
			canSave = false;
		} else {
			if (MobileUtil.getSDFreeSize() < minSize) {
				showTextToast(R.string.str_sdcard_notenough);
				canSave = false;
			}
		}
		return canSave;
	}

	/**
	 * 退出程序方法
	 */
	protected void openExitDialog() {
		exit();
		// // http://www.jb51.net/article/32031.htm
		// AlertDialog.Builder builder = new
		// AlertDialog.Builder(BaseActivity.this);
		//
		// builder.setTitle(R.string.tips);
		// builder.setMessage(R.string.sure_exit);
		//
		// builder.setPositiveButton(R.string.sure,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// MyActivityManager.getActivityManager()
		// .popAllActivityExceptOne(null);
		// android.os.Process.killProcess(android.os.Process
		// .myPid());
		// }
		// });
		// builder.setNegativeButton(R.string.cancel,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		//
		// }
		// });
		// builder.create().show();

	}

	private long exitTime = 0;

	public void exit() {

		// File file = CacheManager.getCacheFileBaseDir();
		// if (file != null && file.exists() && file.isDirectory()) {
		// for (File item : file.listFiles()) {
		// item.delete();
		// }
		// file.delete();
		// }
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), R.string.pressback_exit,
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			statusHashMap.put(Consts.HAS_LOAD_DEMO, "false");

			clearCacheFolder(BaseActivity.this.getCacheDir(),
					System.currentTimeMillis());

			BaseActivity.this.deleteDatabase("webview.db");
			BaseActivity.this.deleteDatabase("webviewCache.db");

			if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {// 非本地登录才加载报警信息
				new Thread(new SetUserOnlineStatusThread(0)).start();
			}
			BitmapCache.getInstance().clearAllCache();
			ConfigUtil.stopBroadCast();
			statusHashMap.put(Consts.HAG_GOT_DEVICE, "false");
			statusHashMap.put(Consts.KEY_LAST_LOGIN_TIME,
					ConfigUtil.getCurrentDate());
			MyActivityManager.getActivityManager()
					.popAllActivityExceptOne(null);
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

	class SetUserOnlineStatusThread implements Runnable {
		private int tag;

		public SetUserOnlineStatusThread(int arg1) {
			tag = arg1;
		}

		@Override
		public void run() {
			JVACCOUNT.SetUserOnlineStatus(tag);
		}
	}

	private int clearCacheFolder(File dir, long numDays) {

		int deletedFiles = 0;

		if (dir != null && dir.isDirectory()) {

			try {

				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {

						deletedFiles += clearCacheFolder(child, numDays);

					}

					if (child.lastModified() < numDays) {

						if (child.delete()) {

							deletedFiles++;

						}

					}

				}

			} catch (Exception e) {

				e.printStackTrace();

			}

		}

		return deletedFiles;

	}

}
