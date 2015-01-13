package com.jovision.views;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.activities.JVPlayActivity;
import com.jovision.bean.Device;
import com.jovision.bean.PushInfo;
import com.jovision.commons.JVAlarmConst;
import com.jovision.commons.MyActivityManager;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.PlayUtil;

//单例模式使用
public class AlarmDialog extends Dialog {
	private static AlarmDialog sSingleton = null;
	private Context context;

	private TextView dialogCancel;
	private TextView dialogView;
	private TextView dialogDeviceName;
	private TextView dialogDeviceModle;
	private TextView dialogAlarmTime;
	private ImageView dialogCancleImg;
	private String ystNum; // 云视通号
	private String deviceNickName;// 昵称
	private String alarmTypeName;// 报警类型
	private String alarmTime;
	// private static int alarmDialogObjs = 0;// new 出来的对象数量
	private static boolean isshowing = false;
	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private Toast toast;
	private String strAlarmGUID;

	public AlarmDialog(Context context) {
		super(context, R.style.mydialog);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.alarm_dialog_summary);
		setCanceledOnTouchOutside(false);
		dialogCancel = (TextView) findViewById(R.id.dialog_cancel);
		dialogView = (TextView) findViewById(R.id.dialog_view);
		dialogCancleImg = (ImageView) findViewById(R.id.dialog_cancle_img);
		dialogDeviceName = (TextView) findViewById(R.id.dialog_devicename);
		dialogDeviceModle = (TextView) findViewById(R.id.dialog_devicemodle);
		dialogAlarmTime = (TextView) findViewById(R.id.dialog_alarm_time);

		dialogCancel.setOnClickListener(myOnClickListener);
		dialogView.setOnClickListener(myOnClickListener);
		dialogCancleImg.setOnClickListener(myOnClickListener);
		// MyLog.e("AlarmDialog", "onCreate" + getDialogObjs());
	}

	@Override
	protected void onStart() {
		dialogDeviceName.setText(deviceNickName);
		dialogDeviceModle.setText(alarmTypeName);
		dialogAlarmTime.setText(alarmTime);
		// MyLog.e("AlarmDialog", "onStart" + getDialogObjs());
	}

	@Override
	protected void onStop() {
		synchronized (AlarmDialog.class) {
			isshowing = false;
		}
	}

	// public synchronized static int getDialogObjs() {
	// return alarmDialogObjs;
	// }

	android.view.View.OnClickListener myOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialog_cancel:
				dismiss();
				break;
			case R.id.dialog_cancle_img:
				dismiss();
				break;
			case R.id.dialog_view:
				// String contextString = context.toString();
				// String strTempNameString = contextString.substring(
				// contextString.lastIndexOf(".") + 1,
				// contextString.indexOf("@"));
				// if (strTempNameString.equals("JVPlayActivity")) {
				// if (!mApp.getMarkedAlarmList().contains(
				// strAlarmGUID)) {
				// mApp.getMarkedAlarmList().add(
				// strAlarmGUID);
				// }
				MySharedPreference.putString(Consts.CHECK_ALARM_KEY,
						strAlarmGUID);
				Activity currentActivity = MyActivityManager
						.getActivityManager().currentActivity();
				if (currentActivity == null
						|| currentActivity
								.getClass()
								.getName()
								.equals("com.jovision.activities.JVPlayActivity")) {
				} else {
					try {
						deviceList = CacheUtil.getDevList();// 再取一次
						int dev_index = getDeivceIndex(ystNum);
						if (dev_index == -1 || dev_index >= deviceList.size()) {
							showTextToast(context, "error index:" + dev_index
									+ ", size:" + deviceList.size());
							return;
						}

						ArrayList<Device> playList = PlayUtil.prepareConnect(
								deviceList, dev_index);// 该函数里已经调用SaveList了
						// MyLog.v("Alarm",
						// "prepareConnect2--" + deviceList.toString());
						// CacheUtil.saveDevList(deviceList);
						// deviceList = CacheUtil.getDevList();//再取一次
						Intent intentPlay = new Intent(context,
								JVPlayActivity.class);
						intentPlay.putExtra("PlayFlag", Consts.PLAY_NORMAL);
						intentPlay.putExtra(Consts.KEY_PLAY_NORMAL,
								playList.toString());
						intentPlay.putExtra("DeviceIndex", dev_index);
						if (deviceList.get(dev_index).getChannelList().size() == 0) {
							showTextToast(context,
									"error channel list size 0, dev_index:"
											+ dev_index);
							return;
						}
						intentPlay.putExtra("ChannelofChannel",
								deviceList.get(dev_index).getChannelList()
										.toList().get(0).getChannel());
						// Toast.makeText(context, "DeviceIndex:" + dev_index,
						// Toast.LENGTH_SHORT).show();
						context.startActivity(intentPlay);
						dismiss();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				dismiss();
				break;

			default:
				break;
			}
		}
	};

	private int getDeivceIndex(String strYstNum) {
		Device devs = null;
		boolean bfind = false;
		for (int j = 0; j < CacheUtil.getDevList().size(); j++) {
			devs = CacheUtil.getDevList().get(j);
			MyLog.v("AlarmConnect",
					"dst:" + strYstNum + "---yst-num = " + devs.getFullNo());
			if (strYstNum.equalsIgnoreCase(devs.getFullNo())) {
				bfind = true;
				MyLog.v("New Alarm Dialog", "find dev num " + strYstNum
						+ ", index:" + j);
				return j;
			}
		}
		return -1;
	}

	public void Show(Object obj) {

		synchronized (AlarmDialog.class) {

			if (obj == null) {
				return;
			}
			// 已经在显示了，就不显示了
			// if (getDialogObjs() <= 1) {
			if (!isshowing) {
				isshowing = true;
				PushInfo pi = (PushInfo) obj;
				ystNum = pi.ystNum;
				deviceList = CacheUtil.getDevList();// 再取一次
				int dev_index = getDeivceIndex(ystNum);
				if (dev_index == -1) {
					deviceNickName = pi.deviceNickName;
				} else {
					deviceNickName = deviceList.get(dev_index).getNickName();
					if (pi.alarmType == 11)// 第三方
					{
						deviceNickName = deviceNickName + "-"
								+ pi.deviceNickName;
					}
				}

				// deviceNickName = pi.deviceNickName;

				alarmTime = pi.alarmTime;
				String strAlarmTypeName = "";
				if (pi.alarmType == 7) {
					strAlarmTypeName = context.getResources().getString(
							R.string.str_alarm_type_move);
				} else if (pi.alarmType == 11) {
					strAlarmTypeName = context.getResources().getString(
							R.string.str_alarm_type_third);
				} else if (pi.alarmType == 4) {
					strAlarmTypeName = context.getResources().getString(
							R.string.str_alarm_type_external);
				} else {
					strAlarmTypeName = context.getResources().getString(
							R.string.str_alarm_type_unknown);
				}
				alarmTypeName = strAlarmTypeName;
				strAlarmGUID = pi.strGUID;
				show();
			} else {
				MyLog.e("Alarm", "收到信息，但不提示");
				// Toast.makeText(context, "收到信息，但不提示",
				// Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}

	/**
	 * 弹系统消息
	 * 
	 * @param context
	 * @param id
	 */
	public void showTextToast(Context context, int id) {
		String msg = context.getResources().getString(id);
		if (toast == null) {
			toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}

	public void showTextToast(Context context, String msg) {
		if (toast == null) {
			toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}
}
