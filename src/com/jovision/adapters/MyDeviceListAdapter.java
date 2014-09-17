package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.MainApplication;
import com.jovision.commons.JVConst;
import com.jovision.newbean.Device;

public class MyDeviceListAdapter extends BaseAdapter {
	private ArrayList<Device> deviceList;
	private Context mContext;
	private LayoutInflater inflater;

	public MyDeviceListAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Device> dataList) {
		deviceList = dataList;
	}

	@Override
	public int getCount() {
		int count = 0;
		int last = deviceList.size() % 2;
		if (0 == last) {
			count = deviceList.size() / 2;
		} else {
			count = deviceList.size() / 2 + 1;
		}
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		return deviceList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		DeviceHolder deviceHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.mydevice_list_item, null);
			deviceHolder = new DeviceHolder();
			deviceHolder.devLayoutL = (RelativeLayout) convertView
					.findViewById(R.id.dev_layout_l);
			deviceHolder.devNameL = (TextView) convertView
					.findViewById(R.id.dev_name_l);
			deviceHolder.onLineStateL = (TextView) convertView
					.findViewById(R.id.dev_online_l);
			deviceHolder.wifiStateL = (TextView) convertView
					.findViewById(R.id.dev_wifi_l);
			deviceHolder.devImgL = (ImageView) convertView
					.findViewById(R.id.dev_image_l);

			deviceHolder.devLayoutR = (RelativeLayout) convertView
					.findViewById(R.id.dev_layout_r);
			deviceHolder.devNameR = (TextView) convertView
					.findViewById(R.id.dev_name_r);
			deviceHolder.onLineStateR = (TextView) convertView
					.findViewById(R.id.dev_online_r);
			deviceHolder.wifiStateR = (TextView) convertView
					.findViewById(R.id.dev_wifi_r);
			deviceHolder.devImgR = (ImageView) convertView
					.findViewById(R.id.dev_image_r);

			convertView.setTag(deviceHolder);
		} else {
			deviceHolder = (DeviceHolder) convertView.getTag();
		}
		deviceHolder.devNameL.setText(deviceList.get(position * 2).getFullNo());
		deviceHolder.onLineStateL.setText("在线");
		deviceHolder.wifiStateL.setText("wifi");

		if (0 == (position * 2) % 4) {
			deviceHolder.devLayoutL
					.setBackgroundResource(R.drawable.device_bg_1);
		} else if (2 == (position * 2) % 4) {
			deviceHolder.devLayoutL
					.setBackgroundResource(R.drawable.device_bg_3);
		}

		if (1 == (position * 2 + 1) % 4) {
			deviceHolder.devLayoutR
					.setBackgroundResource(R.drawable.device_bg_2);
		} else if (3 == (position * 2 + 1) % 4) {
			deviceHolder.devLayoutR
					.setBackgroundResource(R.drawable.device_bg_4);
		}

		if (position * 2 + 1 < deviceList.size()) {
			deviceHolder.devLayoutR.setVisibility(View.VISIBLE);
			deviceHolder.devNameR.setText(deviceList.get(position * 2 + 1)
					.getFullNo());
			deviceHolder.onLineStateR.setText("在线");
			deviceHolder.wifiStateR.setText("wifi");
		} else {
			deviceHolder.devLayoutR.setVisibility(View.GONE);
		}

		deviceHolder.devLayoutL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MainApplication) mContext.getApplicationContext()).onNotify(
						JVConst.DEVICE_ITEM_CLICK, position * 2, 0, null);
			}
		});

		deviceHolder.devLayoutR.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MainApplication) mContext.getApplicationContext()).onNotify(
						JVConst.DEVICE_ITEM_CLICK, position * 2 + 1, 0, null);
			}
		});

		return convertView;
	}

	class DeviceHolder {
		RelativeLayout devLayoutL;
		TextView devNameL;
		TextView onLineStateL;
		TextView wifiStateL;
		ImageView devImgL;

		RelativeLayout devLayoutR;
		TextView devNameR;
		TextView onLineStateR;
		TextView wifiStateR;
		ImageView devImgR;
	}

}
