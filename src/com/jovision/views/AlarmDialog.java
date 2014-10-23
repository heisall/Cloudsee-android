package com.jovision.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.JVPlayActivity;
import com.jovision.bean.Device;
import com.jovision.bean.PushInfo;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;

//单例模式使用
public class AlarmDialog extends Dialog {
	private static AlarmDialog sSingleton = null;
	private Context context;

	private TextView dialogCancel;
	private TextView dialogView;
	private TextView dialogDeviceName;
	private TextView dialogDeviceModle;
	private ImageView dialogCancleImg;

	private static AlarmDialog mAlarmDialog;
	private String ystNum; // 云视通号
	private String deviceNickName;// 昵称
	private String alarmTypeName;// 报警类型
	private String alarmTime;

	private AlarmDialog(Context context) {
		super(context);
		mAlarmDialog = this;
		// TODO Auto-generated constructor stub
	}

	private AlarmDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		mAlarmDialog = this;
	}

	private AlarmDialog(Context context, int theme, String ystnum) {
		super(context, theme);
		this.context = context;
		this.ystNum = ystnum;
		mAlarmDialog = this;
	}

	public static synchronized AlarmDialog getInstance(Context context) {
		if (sSingleton == null) {
			sSingleton = new AlarmDialog(context, R.style.mydialog);
		}
		return sSingleton;
	}

	public void setDeviceNickName(String strNickName) {
		dialogDeviceName.setText(strNickName);
	}

	public void setDeviceAlarmType(String strAlarmType) {
		dialogDeviceName.setText(strAlarmType);
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

		dialogDeviceName.setText(deviceNickName + "(" + alarmTime + ")");
		dialogDeviceModle.setText(alarmTypeName);
		dialogCancel.setOnClickListener(myOnClickListener);
		dialogView.setOnClickListener(myOnClickListener);
		dialogCancleImg.setOnClickListener(myOnClickListener);
	}

	@Override
	protected void onStart() {
		dialogDeviceName.setText(deviceNickName + "(" + alarmTime + ")");
		dialogDeviceModle.setText(alarmTypeName);
	}

	android.view.View.OnClickListener myOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialog_cancel:
				mAlarmDialog.cancel();
				break;
			case R.id.dialog_cancle_img:
				mAlarmDialog.cancel();
				break;
			case R.id.dialog_view:
				String contextString = context.toString();
				String strTempNameString = contextString.substring(
						contextString.lastIndexOf(".") + 1,
						contextString.indexOf("@"));
				if (strTempNameString.equals("JVPlayActivity")) {
				} else {
					// Intent playIntent = new Intent();
					// playIntent.setClass(context, JVPlayActivity.class);
					// playIntent
					// .putExtra("PlayTag", JVConst.NORMAL_PLAY_FLAG);
					// playIntent.putExtra("DeviceIndex",
					// getDeivceIndex(ystNum));
					// playIntent.putExtra("PointIndex", 0);
					// context.startActivity(playIntent);

					Intent intentPlay = new Intent(context,
							JVPlayActivity.class);
					intentPlay.putExtra("PlayFlag", Consts.PLAY_NORMAL);
					int dev_index = getDeivceIndex(ystNum);
					intentPlay.putExtra("DeviceIndex", dev_index);
					intentPlay.putExtra("ChannelofChannel", CacheUtil
							.getDevList().get(dev_index).getChannelList()
							.toList().get(0).getChannel());
					context.startActivity(intentPlay);
				}

				mAlarmDialog.cancel();
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
			MyLog.e("AlarmConnect",
					"dst:" + strYstNum + "---yst-num = " + devs.getFullNo());
			if (strYstNum.equalsIgnoreCase(devs.getFullNo())) {
				bfind = true;
				MyLog.e("New Alarm Dialog", "find dev num " + strYstNum
						+ ", index:" + j);
				return j;
			}
		}
		return -1;
	}

	public static void Show(Object obj) {
		if (obj == null) {
			return;
		}
		PushInfo pi = (PushInfo) obj;
		// 已经在显示了，就不显示了
		if (!mAlarmDialog.isShowing()) {
			mAlarmDialog.deviceNickName = pi.deviceNickName + "--"
					+ pi.alarmTime;
			mAlarmDialog.ystNum = pi.ystNum;
			String strAlarmTypeName = "";
			if (pi.alarmType == 7) {
				strAlarmTypeName = "移动侦测";
			} else if (pi.alarmType == 11) {
				strAlarmTypeName = "第三方设备报警";
			} else {
				strAlarmTypeName = "未知报警类型";
			}
			mAlarmDialog.alarmTypeName = strAlarmTypeName;
			mAlarmDialog.show();
		}
	}
}
