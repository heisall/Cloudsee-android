package com.jovision.views;

import java.util.ArrayList;

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
import com.jovision.activities.JVPlayActivity;
import com.jovision.bean.Device;
import com.jovision.bean.PushInfo;
import com.jovision.commons.MyLog;
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
	private static int alarmDialogObjs = 0;//new 出来的对象数量
	private ArrayList<Device> deviceList = new ArrayList<Device>();;
	public AlarmDialog(Context context) {
		super(context, R.style.mydialog);
		this.context = context;
		// TODO Auto-generated constructor stub
		synchronized(AlarmDialog.class){
			alarmDialogObjs++;
		}		
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
	}

	@Override
	protected void onStart() {
		dialogDeviceName.setText(deviceNickName);
		dialogDeviceModle.setText(alarmTypeName);
		dialogAlarmTime.setText(alarmTime);
	}

	@Override
	protected void onStop() {
		synchronized(AlarmDialog.class){
			alarmDialogObjs--;
		}
	}
	public synchronized static int getDialogObjs(){
		return alarmDialogObjs;
	}
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
				String contextString = context.toString();
				String strTempNameString = contextString.substring(
						contextString.lastIndexOf(".") + 1,
						contextString.indexOf("@"));
				if (strTempNameString.equals("JVPlayActivity")) {
				} else {
					int dev_index = getDeivceIndex(ystNum);
					deviceList = CacheUtil.getDevList();
					if(dev_index >= deviceList.size()){
						Toast.makeText(context, "error index:"+dev_index+", size:"+deviceList.size(), Toast.LENGTH_SHORT).show();
						return;
					}
					
					MyLog.v("Alarm", "prepareConnect1--" + deviceList.toString());
					PlayUtil.prepareConnect(deviceList, dev_index);//该函数里已经调用SaveList了
					MyLog.v("Alarm", "prepareConnect2--" + deviceList.toString());	
					CacheUtil.saveDevList(deviceList);
//					deviceList = CacheUtil.getDevList();//再取一次
					Intent intentPlay = new Intent(context,
							JVPlayActivity.class);
					intentPlay.putExtra("PlayFlag", Consts.PLAY_NORMAL);
					
					intentPlay.putExtra("DeviceIndex", dev_index);
					intentPlay.putExtra("ChannelofChannel", deviceList.get(dev_index).getChannelList()
							.toList().get(0).getChannel());
					Toast.makeText(context, "DeviceIndex:"+dev_index, Toast.LENGTH_SHORT).show();
					context.startActivity(intentPlay);		
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

	public void Show(Object obj) {
		
		if (obj == null) {
			return;
		}
		
		// 已经在显示了，就不显示了
		if (getDialogObjs() <= 1) {
			PushInfo pi = (PushInfo) obj;
			deviceNickName = pi.deviceNickName;
			ystNum = pi.ystNum;
			alarmTime = pi.alarmTime;
			String strAlarmTypeName = "";
			if (pi.alarmType == 7) {
				strAlarmTypeName = context.getResources().getString(R.string.str_alarm_type_move);
			} else if (pi.alarmType == 11) {
				strAlarmTypeName = context.getResources().getString(R.string.str_alarm_type_third);
			} else {
				strAlarmTypeName = context.getResources().getString(R.string.str_alarm_type_unknown);
			}
			alarmTypeName = strAlarmTypeName;
			show();
		}
	}
}
