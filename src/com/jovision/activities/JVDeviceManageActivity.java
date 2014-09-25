package com.jovision.activities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.adapters.DeviceManageListAdapter;
import com.jovision.adapters.MyPagerAdapter;
import com.jovision.bean.Device;
import com.jovision.commons.BaseApp;
import com.jovision.commons.JVConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.views.RefreshableListView;
import com.jovision.views.RefreshableListView.OnRefreshListener;

public class JVDeviceManageActivity extends BaseActivity {

	private ViewPager helpPager;// 帮助图
	private Timer loadingTimer;// 点击播放后loading超过1分钟隐藏loading

	private Button back;// 显示隐藏左侧按钮
	private TextView currentMenu;// 当前页面名称
	private RefreshableListView deviceListView;// 设备列表
	private LinearLayout searchLayout;// 快速观看搜索背景
	private DeviceManageListAdapter dmlAdapter = null;
	public int currentOpenPosition = -1;// 当前展开pos

	// 帮助图
	ArrayList<View> listViews = new ArrayList<View>();
	private RelativeLayout help7;
	private RelativeLayout help8;
	private RelativeLayout help9;
	private boolean flag;// 标记列表初始有无内容
	private LinearLayout indicatorlayout = null;// 小圆点
	private ArrayList<ImageView> mIndicatorList = new ArrayList<ImageView>();// 装小圆点的集合
	private boolean isUpdate = false;// 下拉刷新标志位

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case JVConst.SEARCH_DEVICE_SUCCESS:
			if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
				dmlAdapter.setData(BaseApp.deviceList);
				deviceListView.setAdapter(dmlAdapter);
				dmlAdapter.notifyDataSetChanged();
				deviceListView.completeRefreshing();
				if (-1 != currentOpenPosition) {
					deviceListView.setSelection(currentOpenPosition);
				}

				createDialog(R.string.str_uploading);
				Thread thread = new Thread() {
					public void run() {
						sycData();
					}
				};
				thread.start();

			}
			break;
		case JVConst.SEARCH_DEVICE_FAILED:
			dismissDialog();
			showTextToast(R.string.login_str_search_nodevice);
			deviceListView.completeRefreshing();
			if (isUpdate) {
				isUpdate = false;
			}
			break;

		case JVConst.SYC_DATA_COMPLETEDED:
			dmlAdapter.setData(BaseApp.deviceList);
			deviceListView.setAdapter(dmlAdapter);
			dmlAdapter.notifyDataSetChanged();
			deviceListView.completeRefreshing();
			dismissDialog();
			if (isUpdate) {
				isUpdate = false;
			}
			break;
		case JVConst.DEVICE_MAN_START_CONNECT:// 开始连接
			// connectDevice();
			loadingTimer = new Timer();
			loadingTimer.schedule(new BackTask(), 40 * 1000);

			createDialog(R.string.str_host_connect);
			break;
		case JVConst.DEVICE_MAN_CONNECT_FAILED:// 连接失败
			MyLog.e("连接 失败啦-----------", "连接 失败啦");
			Bundle bundle = (Bundle) obj;
			String temStr = "";
			if (null != bundle) {
				temStr = bundle.getString("ERROR_MESSAGE");
			}
			String error = "";

			if (temStr.equalsIgnoreCase("password is wrong!")) {// 身份验证失败
				error = getResources().getString(R.string.connfailed_auth);
			} else {
				error = getResources().getString(
						R.string.str_host_connect_failed);
			}

			if (null != loadingTimer) {
				loadingTimer.cancel();
			}
			loadingTimer = null;

			dismissDialog();

			AlertDialog.Builder builder = new AlertDialog.Builder(
					JVDeviceManageActivity.this);

			builder.setTitle(R.string.tips);
			builder.setMessage(error);

			builder.setPositiveButton(R.string.str_sure,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();
			break;
		case JVConst.DEVICE_MAN_IS_IPC:// 是IPC
			MyLog.e("是IPC-----------", "是IPC，发聊天请求");
			MyLog.e("连接成功发暂停命令：", "JVN_CMD_VIDEOPAUSE");
			// JVSUDT.JVC_SendData(1, (byte) JVNetConst.JVN_CMD_VIDEOPAUSE,
			// new byte[0], 0);
			//
			// JVSUDT.JVC_SendData(1, (byte) JVNetConst.JVN_REQ_TEXT, new
			// byte[0],
			// 8);// new
			// // byte[0]

			break;
		case JVConst.DEVICE_MAN_HOST_AGREE_TEXT:// 主控同意文本聊天
			MyLog.e("主控同意文本聊天-----------", "主控同意文本聊天");
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// // 获取主控文本聊天信息请求
			// JVSUDT.JVC_SendTextData(1, (byte) JVNetConst.JVN_RSP_TEXTDATA, 8,
			// JVNetConst.JVN_REMOTE_SETTING);
			break;
		case JVConst.DEVICE_MAN_HOST_NOT_AGREE_TEXT:
			// JVSUDT.DEVICE_MANAGE_FLAG = false;
			// 主控不同意断开连接
			if (null != loadingTimer) {
				loadingTimer.cancel();
			}
			loadingTimer = null;

			dismissDialog();

			// 断开连接
			disconnect();
			showTextToast(R.string.str_only_administator_use_this_function);
			break;
		case JVConst.DEVICE_MAN_HOST_TEXT:// 获取到配置文本数据

			break;
		case JVConst.DEVICE_MAN_NOT_IPC:// 不是IPC
			if (null != loadingTimer) {
				loadingTimer.cancel();
			}
			loadingTimer = null;

			// JVSUDT.DEVICE_MANAGE_FLAG = false;
			// 断开连接
			disconnect();

			if (null != proDialog && proDialog.isShowing()) {
				proDialog.dismiss();
			}
			proDialog = null;

			AlertDialog.Builder builder1 = new AlertDialog.Builder(
					JVDeviceManageActivity.this);

			builder1.setTitle(R.string.tips);
			builder1.setMessage(R.string.str_not_support_this_device);
			builder1.setNegativeButton(R.string.str_cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder1.create().show();

			break;
		case JVConst.DEVICE_MAN_CONN_TIME_OUT:
			if (null != loadingTimer) {
				loadingTimer.cancel();
			}
			loadingTimer = null;

			// JVSUDT.DEVICE_MANAGE_FLAG = false;

			// 断开连接
			disconnect();
			dismissDialog();

			showTextToast(R.string.str_host_connect_timeout);

			break;
		case JVConst.THREE_MIN_BROADCAST_DEVICE:
			// JVSUDT.DEVICE_MANAGE_FLAG = false;
			break;
		case JVConst.DELETE_DEVICE_SUCCESS:
			BaseApp.lengthOfDeviceList = BaseApp.deviceList.size();
			dmlAdapter.setData(BaseApp.deviceList);
			deviceListView.setAdapter(dmlAdapter);
			deviceListView.setSelection(arg1);
			dmlAdapter.notifyDataSetChanged();
			showTextToast(R.string.login_str_device_delete_success);
			dismissDialog();
			break;
		case JVConst.DELETE_DEVICE_FAILED:
			showTextToast(R.string.login_str_device_delete_failed);
			dismissDialog();
			break;
		}

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.handleMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	// /**
	// * 连接设备
	// *
	// * @param ipcWifi
	// */
	// private void connectDevice() {
	// getGroupYST(deviceNum);
	// Jni.connect(Consts.AP_CHANNEL, 0, JVConst.IPC_DEFAULT_IP,
	// JVConst.IPC_DEFAULT_PORT, JVConst.IPC_DEFAULT_USER,
	// JVConst.IPC_DEFAULT_PWD, yst, group, true, 1, true, 6, null);
	// }

	/**
	 * 断开视频
	 */
	private void disconnect() {
		Jni.disconnect(Consts.CHANNEL_JY);
	}

	@Override
	protected void initSettings() {
		// 首次登陆
		if (MySharedPreference.getBoolean("ShowManageHelp", true)) {
			flag = true; // 列表是否有数据
			if (null == BaseApp.deviceList || 0 == BaseApp.deviceList.size()) {
				flag = false;
				Device device = new Device();
				device.deviceName = "A123456";
				device.deviceNum = "A123456";
				BaseApp.deviceList.add(device);
				dmlAdapter.setData(BaseApp.deviceList);
				dmlAdapter.openPos = 0;
				deviceListView.setAdapter(dmlAdapter);
				dmlAdapter.notifyDataSetChanged();
			} else {
				dmlAdapter.openPos = 0;
				currentOpenPosition = 0;
				deviceListView.setSelection(currentOpenPosition);
				dmlAdapter.notifyDataSetChanged();
			}
			LayoutInflater mInflater = JVDeviceManageActivity.this
					.getLayoutInflater();
			listViews = new ArrayList<View>();
			listViews.add(help7);
			listViews.add(help8);
			listViews.add(help9);
			for (int i = 0; i < 3; i++) {
				ImageView indicator = new ImageView(this);
				if (i == 0) {
					indicator.setImageResource(R.drawable.feature_point_cur1);
				} else {
					indicator.setImageResource(R.drawable.feature_point1);
				}
				LayoutParams param = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				indicator.setPadding(0, 0, 10, 0);
				indicator.setLayoutParams(param);

				indicatorlayout.addView(indicator);
				// 把小圆点图片存入集合,好让切换图案片的时候动态改变小圆点背景
				mIndicatorList.add(indicator);
			}
			helpPager.setAdapter(new MyPagerAdapter(listViews));
			helpPager.setCurrentItem(0);
			helpPager.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageSelected(int arg0) {
					for (int i = 0; i < 3; i++) {
						mIndicatorList.get(i).setImageResource(
								R.drawable.feature_point1);
					}
					mIndicatorList.get(arg0).setImageResource(
							R.drawable.feature_point_cur1);
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
			final boolean flags = flag;
			help9.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					indicatorlayout.setVisibility(View.GONE);
					BaseApp.editor.putBoolean("ShowManageHelp", false);
					BaseApp.editor.commit();
					if (!flags) {
						BaseApp.deviceList.clear();
						BaseApp.lengthOfDeviceList = BaseApp.deviceList.size();
						dmlAdapter.setData(BaseApp.deviceList);
						dmlAdapter.openPos = -1;
						deviceListView.setAdapter(dmlAdapter);
						dmlAdapter.notifyDataSetChanged();
					}
					helpPager.setVisibility(View.GONE);
				}
			});
		} else {
			helpPager.setVisibility(View.GONE);
		}

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.device_layout);
		// BaseApp.NODEVICE = false;
		helpPager = (ViewPager) findViewById(R.id.helppager);
		back = (Button) findViewById(R.id.back);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_device_manage);
		deviceListView.setOnItemClickListener(onItemClickListener);
		deviceListView.setOnItemLongClickListener(onItemLongClickListener);
		searchLayout = (LinearLayout) findViewById(R.id.searchlayout);
		back.setOnClickListener(onClickListener);
		indicatorlayout = (LinearLayout) findViewById(R.id.indicatorlayout);
		searchLayout.setVisibility(View.GONE);

		deviceListView = (RefreshableListView) findViewById(R.id.devicelistview);
		deviceListView.setFooterDividersEnabled(true);
		dmlAdapter = new DeviceManageListAdapter(JVDeviceManageActivity.this,
				disMetrics);
		deviceListView.setAdapter(dmlAdapter);
		deviceListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(RefreshableListView listView) {
				// 加载设备通道数据,只刷新设备列表数据，不刷新新品数据
				if (Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))
						&& BaseApp.deviceList.size() >= 100) {
					showTextToast(R.string.str_device_most_count);
					listView.completeRefreshing();
				} else {
					if (!ConfigUtil.is3G(JVDeviceManageActivity.this, false)) {
						isUpdate = true;
						setNewTagFalse();
						if (null == BaseApp.tempList) {
							BaseApp.tempList = new ArrayList<Device>();
						} else {
							BaseApp.tempList.clear();
						}
						// startBroadCast();
					} else {
						listView.completeRefreshing();
					}
				}
			}
		});

		if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {
			dmlAdapter.setData(BaseApp.deviceList);
			deviceListView.setAdapter(dmlAdapter);
			dmlAdapter.notifyDataSetChanged();
		}
	}

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if (currentOpenPosition == arg2) {// 已经展开了，要收缩回去
				currentOpenPosition = -1;
				dmlAdapter.openPos = -1;
			} else {// 展开
				currentOpenPosition = arg2;
				dmlAdapter.openPos = arg2;
			}

			deviceListView.setAdapter(dmlAdapter);
			dmlAdapter.notifyDataSetChanged();
			deviceListView.setSelection(arg2);

		}

	};
	OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int arg2, long arg3) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					JVDeviceManageActivity.this);
			builder.setMessage(R.string.str_delete_sure);
			builder.setTitle(R.string.str_delete_tip);
			builder.setPositiveButton(R.string.str_sure,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (Boolean.valueOf(statusHashMap
									.get(Consts.LOCAL_LOGIN))) {// 本地登录操作数据库
								// JVConnectInfo jvInfo =
								// BaseApp.deviceList.get(
								// arg2 - 1).toJVConnectInfo();
								// jvInfo.setParent(true);
								// int res =
								// BaseApp.completeDeleteChannel(jvInfo);
								// if (res > 0) {
								// BaseApp.deviceList.remove(arg2 - 1);
								// handler.sendMessage(handler
								// .obtainMessage(
								// JVConst.DELETE_DEVICE_SUCCESS,
								// arg2));
								// } else {
								// handler.sendMessage(handler
								// .obtainMessage(JVConst.DELETE_DEVICE_FAILED));
								// }

							} else {// 非本地操作，提交到服务器上

								createDialog(R.string.str_deleting);
								DeleteDeviceThread ddt = new DeleteDeviceThread(
										JVDeviceManageActivity.this,
										BaseApp.deviceList.get(arg2 - 1), arg2);
								ddt.start();
							}
						}
					});

			builder.setNegativeButton(R.string.str_cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			builder.create().show();
			return false;
		}

	};

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:
				if (!isUpdate) {
					finish();
				}
				break;
			case R.id.quicksetting:// 快速设置
				Intent intent = new Intent(JVDeviceManageActivity.this,
						JVQuickSettingActivity.class);
				JVDeviceManageActivity.this.startActivity(intent);
				break;
			}
		}

	};

	// 同步数据
	private void sycData() {
		if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()
				&& BaseApp.lengthOfDeviceList != -1) {
			for (int i = 0; i < BaseApp.deviceList.size()
					- BaseApp.lengthOfDeviceList; i++) {
				Device device1 = BaseApp.deviceList.get(i);
				if (BaseApp.deviceList.size() >= 100) {
					BaseApp.deviceList.remove(i);
				} else {
					if (null == BaseApp.addDeviceMethod(device1)) {
						if (i > BaseApp.deviceList.size() - 1) {
							break;
						}
						BaseApp.deviceList.remove(i);
						i--;
					} else {
						BaseApp.setHelpToDevice(device1);
					}
				}
			}

		}
		BaseApp.lengthOfDeviceList = BaseApp.deviceList.size();
		handler.sendMessage(handler.obtainMessage(JVConst.SYC_DATA_COMPLETEDED));
	}

	/**
	 * 删除设备线程
	 * 
	 * @author Administrator
	 * 
	 */
	private static class DeleteDeviceThread extends Thread {
		private Device device;
		private int arg;
		private final WeakReference<JVDeviceManageActivity> mActivity;

		public DeleteDeviceThread(JVDeviceManageActivity activity, Device dev,
				int arg2) {
			mActivity = new WeakReference<JVDeviceManageActivity>(activity);
			device = dev;
			arg = arg2;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			JVDeviceManageActivity activity = mActivity.get();
			if (null != activity) {
				// LoginUtil.deviceId = device.deviceOID;
				int flag = DeviceUtil.unbindDevice(
						activity.statusHashMap.get("KEY_USERNAME"),
						device.deviceNum);
				if (0 == flag) {
					BaseApp.deviceList.remove(device);
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVConst.DELETE_DEVICE_SUCCESS, arg));
				} else {
					activity.handler.sendMessage(activity.handler
							.obtainMessage(JVConst.DELETE_DEVICE_FAILED));
				}
			}
		}

	}

	/**
	 * 返回任务
	 * 
	 * @author Administrator
	 * 
	 */
	private class BackTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendMessage(handler
					.obtainMessage(JVConst.DEVICE_MAN_CONN_TIME_OUT));
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != BaseApp.getSP(getApplicationContext())
					&& BaseApp.getSP(getApplicationContext()).getBoolean(
							"ShowManageHelp", true) && !flag
					&& null != BaseApp.deviceList) {
				BaseApp.deviceList.clear();
			}
			if (!isUpdate) {
				finish();
			}
		}
		return true;
	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		setNewTagFalse();

	}

	public void setNewTagFalse() {
		if (null != BaseApp.deviceList && BaseApp.deviceList.size() > 0) {
			for (int i = 0; i < BaseApp.deviceList.size(); i++) {
				BaseApp.deviceList.get(i).newTag = false;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
