package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.newbean.Device;

public class DemoListAdapter extends BaseAdapter {
	private ArrayList<Device> deviceList;
	private Context mContext;
	private LayoutInflater inflater;

	public DemoListAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Device> dataList) {
		deviceList = dataList;
	}

	@Override
	public int getCount() {
		return deviceList.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		DeviceHolder deviceHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.demo_item, null);
			deviceHolder = new DeviceHolder();
			deviceHolder.demoTextViewL = (TextView) convertView
					.findViewById(R.id.lefttextview);
			deviceHolder.demoTextViewR = (TextView) convertView
					.findViewById(R.id.righttextview);
			deviceHolder.demoImg = (ImageView) convertView
					.findViewById(R.id.demo_img);
			convertView.setTag(deviceHolder);
		} else {
			deviceHolder = (DeviceHolder) convertView.getTag();
		}
		deviceHolder.demoTextViewL
				.setText(deviceList.get(position).getFullNo());
		deviceHolder.demoTextViewR.setText("在线");
		// deviceHolder.demoImg.setText("wifi");

		return convertView;
	}

	class DeviceHolder {
		TextView demoTextViewL;
		TextView demoTextViewR;
		ImageView demoImg;
	}

}
