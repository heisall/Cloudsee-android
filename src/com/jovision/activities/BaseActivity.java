package com.jovision.activities;

import java.util.HashMap;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;

import com.jovision.Consts;
import com.jovision.IHandlerLikeNotify;
import com.jovision.IHandlerNotify;
import com.jovision.Jni;
import com.jovision.MainApplication;
import com.jovision.commons.MyActivityManager;
import com.jovision.commons.MyLog;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.MobileUtil;

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

	protected DisplayMetrics disMetrics;

	private long duration;
	protected String TAG = "BaseA";
	private static final String RUNTIME = ".runtime";

	public HashMap<String, String> statusHashMap;

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
			activity.notify.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
			super.handleMessage(msg);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.getActivityManager().pushActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		statusHashMap = ((MainApplication) getApplicationContext())
				.getStatusHashMap();
		configuration = getResources().getConfiguration();
		disMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
		initSettings();
		initUi();
	}

	@Override
	protected void onResume() {
		((MainApplication) getApplication()).setCurrentNotifyer(this);
		super.onResume();

		duration = System.currentTimeMillis();
	}

	@Override
	protected void onPause() {
		duration = System.currentTimeMillis() - duration;
		MyLog.ubStat(TAG + RUNTIME, (int) (duration / 1000));

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
	protected void createDialog(int id) {
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
	public void createDialog(String msg) {

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
		if (null != proDialog) {
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
		String msg = BaseActivity.this.getResources().getString(id);
		if (toast == null) {
			toast = Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT);
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
			toast = Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT);
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

	/**
	 * 没有网络提示 打开设置网络界面
	 * */
	public void alertNetDialog() {
		// 提示对话框
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.tips)
				.setMessage(R.string.network_error)
				.setPositiveButton(R.string.setting,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = null;
								// 判断手机系统的版本 即API大于10 就是3.0或以上版本
								if (android.os.Build.VERSION.SDK_INT > 10) {
									intent = new Intent(
											android.provider.Settings.ACTION_WIRELESS_SETTINGS);
								} else {
									intent = new Intent();
									ComponentName component = new ComponentName(
											"com.android.settings",
											"com.android.settings.WirelessSettings");
									intent.setComponent(component);
									intent.setAction("android.intent.action.VIEW");
								}
								BaseActivity.this.startActivity(intent);
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create().show();
	}

	/**
	 * 是否是3G网络环境
	 * 
	 * @param context
	 *            上下文
	 * @param alert
	 *            是否弹出提示
	 * @return 是否是3G网络
	 */
	public boolean is3G(boolean alert) {
		ConnectivityManager cManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			int type = info.getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
				return false;
			} else {
				if (alert) {
					this.showTextToast(R.string.tips_3g);
				}
				return true;
			}
		} else
			return false;
	}

	/**
	 * 判断是否有sd卡
	 */
	public boolean hasSDCard() {
		boolean canSave = true;
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			showTextToast(R.string.str_out_memery);
			canSave = false;
		} else {
			if (MobileUtil.getSDFreeSize() < 5) {
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
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), R.string.pressback_exit,
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			Jni.stopSearchLanServer();
			statusHashMap.put(Consts.HAG_GOT_DEVICE, "false");
			statusHashMap.put(Consts.KEY_LAST_LOGIN_TIME,
					ConfigUtil.getCurrentTime());
			MyActivityManager.getActivityManager()
					.popAllActivityExceptOne(null);
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}
}
