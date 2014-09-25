package com.jovision.adapters;

import java.util.ArrayList;
import java.util.Locale;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.MainApplication;
import com.jovision.activities.JVEditDeviceActivity;
import com.jovision.activities.JVIPConnectActivity;
import com.jovision.bean.Device;
import com.jovision.commons.BaseApp;
import com.jovision.commons.JVConst;
import com.jovision.utils.ConfigUtil;

public class DeviceManageListAdapter extends BaseAdapter {
	public ArrayList<Device> deviceList = new ArrayList<Device>();
	public Context mContext = null;
	public LayoutInflater inflater;
	public int openPos = -1;
	public DisplayMetrics dm;

	// public static DeviceAdapterHandler daHandler;

	public DeviceManageListAdapter(Context con, DisplayMetrics dms) {
		mContext = con;
		dm = dms;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// daHandler = new DeviceAdapterHandler();
	}

	public void setData(ArrayList<Device> list) {
		deviceList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int size = 0;
		if (null != deviceList && 0 != deviceList.size()) {
			size = deviceList.size();
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Device dev = null;
		if (null != deviceList && 0 != deviceList.size()
				&& position < deviceList.size()) {
			dev = deviceList.get(position);
		}
		return dev;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DeviceHolder deviceHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.device_manage_item, null);
			deviceHolder = new DeviceHolder();
			deviceHolder.deviceImg = (ImageView) convertView
					.findViewById(R.id.deviceimg);
			deviceHolder.deviceNum = (TextView) convertView
					.findViewById(R.id.devicenum);
			deviceHolder.deviceName = (TextView) convertView
					.findViewById(R.id.devicename);
			deviceHolder.wifiState = (ImageView) convertView
					.findViewById(R.id.deviceconnect);
			deviceHolder.funcLayout = (RelativeLayout) convertView
					.findViewById(R.id.functionlayout);
			deviceHolder.deviceSetting = (Button) convertView
					.findViewById(R.id.devicesetting);
			deviceHolder.remoteSetting = (Button) convertView
					.findViewById(R.id.remotesetting);
			deviceHolder.ipConnect = (Button) convertView
					.findViewById(R.id.ipconnect);
			deviceHolder.deviceSettingText = (TextView) convertView
					.findViewById(R.id.devicesetting_text);
			deviceHolder.remoteSettingText = (TextView) convertView
					.findViewById(R.id.remotesetting_text);
			deviceHolder.ipConnectText = (TextView) convertView
					.findViewById(R.id.ipconnect_text);
			deviceHolder.deleteItem = (Button) convertView
					.findViewById(R.id.deleteitem);
			convertView.setTag(deviceHolder);
		} else {
			deviceHolder = (DeviceHolder) convertView.getTag();
		}

		Device dev = null;
		if (null != deviceList && 0 != deviceList.size()
				&& position < deviceList.size()) {
			dev = deviceList.get(position);
			// if(dev.isDevice)
			// {
			// deviceHolder.ipConnectText.setTextColor(Color.RED);
			// }
			// else
			// {
			// deviceHolder.ipConnectText.setTextColor(Color.WHITE);
			// }
		}
		String language = Locale.getDefault().getLanguage();
		if (null != dev && dev.newTag) {
			// 获取当前语言
			if (language.equalsIgnoreCase("zh")) {// 中文
				deviceHolder.deviceImg.setBackgroundDrawable(mContext
						.getResources().getDrawable(R.drawable.device_icon_ch));
			} else {// 英文或其他
				deviceHolder.deviceImg.setBackgroundDrawable(mContext
						.getResources().getDrawable(R.drawable.device_icon_en));
			}
		} else {
			deviceHolder.deviceImg
					.setBackgroundDrawable(mContext.getResources().getDrawable(
							R.drawable.device_icon_default));
		}

		// 界面动态布局 suifupeng
		int drawLen = mContext.getResources()
				.getDrawable(R.drawable.remotesetting_bg).getMinimumWidth();
		int len = (dm.widthPixels - drawLen * 3) / 4;
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = len;
		deviceHolder.remoteSetting.setLayoutParams(lp);
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp2.leftMargin = len * 2 + drawLen;
		deviceHolder.deviceSetting.setLayoutParams(lp2);
		RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp3.leftMargin = len * 3 + drawLen * 2;
		deviceHolder.ipConnect.setLayoutParams(lp3);
		RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		deviceHolder.remoteSettingText.measure(w, h);
		lp4.leftMargin = len + drawLen / 2
				- deviceHolder.remoteSettingText.getMeasuredWidth() / 2;
		lp4.addRule(RelativeLayout.BELOW, R.id.remotesetting);
		deviceHolder.remoteSettingText.setLayoutParams(lp4);
		RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		deviceHolder.deviceSettingText.measure(w, h);
		lp5.leftMargin = len * 2 + drawLen * 3 / 2
				- deviceHolder.deviceSettingText.getMeasuredWidth() / 2;
		lp5.addRule(RelativeLayout.BELOW, R.id.remotesetting);
		deviceHolder.deviceSettingText.setLayoutParams(lp5);
		RelativeLayout.LayoutParams lp6 = new RelativeLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		deviceHolder.ipConnectText.measure(w, h);
		lp6.leftMargin = len * 3 + drawLen * 5 / 2
				- deviceHolder.ipConnectText.getMeasuredWidth() / 2;
		lp6.addRule(RelativeLayout.BELOW, R.id.remotesetting);
		deviceHolder.ipConnectText.setLayoutParams(lp6);

		// 设备管理
		deviceHolder.deviceSetting
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mContext,
								JVEditDeviceActivity.class);
						intent.putExtra("listIndex", position);
						mContext.startActivity(intent);
					}

				});

		// 远程设置
		deviceHolder.remoteSetting
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						((MainApplication) mContext.getApplicationContext())
								.onNotify(JVConst.DEVICE_MAN_START_CONNECT, 0,
										0, null);
						Device devs = null;
						if (null != BaseApp.deviceList
								&& 0 != BaseApp.deviceList.size()
								&& position < BaseApp.deviceList.size()) {
							devs = BaseApp.deviceList.get(position);
						}
						if (null != devs) {
							Jni.connect(Consts.CHANNEL_JY, 0,
									devs.deviceLocalIp, devs.deviceLocalPort,
									devs.deviceLoginUser, devs.deviceLoginPwd,
									ConfigUtil.getYST(devs.deviceNum),
									ConfigUtil.getGroup(devs.deviceNum), true,
									1, true, 6, null, false);
						}
					}
				});

		deviceHolder.ipConnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Device devs = null;
				if (null != deviceList && 0 != deviceList.size()
						&& position < deviceList.size()) {
					if (null != deviceList.get(position)) {
						Intent intent = new Intent(mContext,
								JVIPConnectActivity.class);
						intent.putExtra("EditDevice", deviceList.get(position));
						mContext.startActivity(intent);
					}
				}

			}
		});

		deviceHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// BaseApp.deviceList.remove(position);
				// dmlAdapter.notifyDataSetChanged();
			}
		});

		if (position < deviceList.size()) {
			deviceHolder.deviceNum.setText(deviceList.get(position).deviceNum);
			deviceHolder.deviceName
					.setText(deviceList.get(position).deviceName);
		}

		if (openPos == position) {
			deviceHolder.funcLayout.setVisibility(View.VISIBLE);
			deviceHolder.deviceSetting.setVisibility(View.VISIBLE);
			deviceHolder.remoteSetting.setVisibility(View.VISIBLE);
			deviceHolder.wifiState.setBackgroundDrawable(mContext
					.getResources().getDrawable(R.drawable.wifi_flag_open_bg));
		} else {
			deviceHolder.funcLayout.setVisibility(View.GONE);
			deviceHolder.deviceSetting.setVisibility(View.GONE);
			deviceHolder.remoteSetting.setVisibility(View.GONE);
			deviceHolder.wifiState.setBackgroundDrawable(mContext
					.getResources().getDrawable(R.drawable.wifi_flag_close_bg));
		}

		if (null != dev && dev.hasWifi == 1) {
			deviceHolder.wifiState.setVisibility(View.VISIBLE);
			// if (JVMainActivity.currentOpenPosition!=position) {//开口展开状态
			// deviceHolder.wifiState.setBackgroundResource(R.drawable.wifi_flag_open_bg);
			// }else{
			// deviceHolder.wifiState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.wifi_flag_close_bg));
			// }
		} else {
			deviceHolder.wifiState.setVisibility(View.GONE);
		}
		return convertView;
	}

	// 设备(parent)
	class DeviceHolder {
		ImageView deviceImg;
		TextView deviceNum;
		TextView deviceName;
		ImageView wifiState;

		RelativeLayout funcLayout;
		Button deviceSetting;
		Button remoteSetting;
		Button ipConnect; // ip直连按钮 suifupeng
		TextView deviceSettingText;
		TextView remoteSettingText;
		TextView ipConnectText;
		Button deleteItem;
	}

	// class DeviceAdapterHandler extends Handler{
	//
	// @Override
	// public void handleMessage(Message msg) {
	// // TODO Auto-generated method stub
	// switch (msg.what){
	// case JVConst.DEVICE_MAN_CONNECT_FAILED:
	// break;
	// case JVConst.DEVICE_MAN_IS_IPC:
	// break;
	// case JVConst.DEVICE_MAN_NOT_IPC:
	// break;
	// }
	//
	// super.handleMessage(msg);
	// }
	//
	// }

}
