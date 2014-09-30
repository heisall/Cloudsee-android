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
import com.jovision.bean.DemoBean;

public class DemoListAdapter extends BaseAdapter {
	private ArrayList<DemoBean> deviceList;
	private Context mContext;
	private LayoutInflater inflater;
	private boolean isclicked;

	public DemoListAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<DemoBean> dataList, boolean isclick) {
		isclicked = isclick;
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
			if (isclicked) {
				convertView = inflater.inflate(R.layout.demo_item, null);
				deviceHolder = new DeviceHolder();
				deviceHolder.demoaddress = (TextView) convertView
						.findViewById(R.id.item_address);
				deviceHolder.demopictrue = (ImageView) convertView
						.findViewById(R.id.item_pictrue);
				deviceHolder.democlass = (TextView) convertView
						.findViewById(R.id.item_class);
				deviceHolder.demodevicename = (TextView) convertView
						.findViewById(R.id.item_devicename);
				deviceHolder.demotime = (TextView) convertView
						.findViewById(R.id.item_time);
				convertView.setTag(deviceHolder);
			} else {
				convertView = inflater.inflate(R.layout.demo_itemclicked, null);
				deviceHolder = new DeviceHolder();
				deviceHolder.demoaddress = (TextView) convertView
						.findViewById(R.id.item_address_clicked);
				deviceHolder.demopictrue = (ImageView) convertView
						.findViewById(R.id.item_pictrue_clicked);
				deviceHolder.demotime = (TextView) convertView
						.findViewById(R.id.item_time_clicked);
				convertView.setTag(deviceHolder);
			}
		} else {
			deviceHolder = (DeviceHolder) convertView.getTag();
		}
		if (isclicked) {
			deviceHolder.demoaddress.setText(deviceList.get(position)
					.getDemoaddress());
			deviceHolder.demodevicename.setText(deviceList.get(position)
					.getDemodevicename());
			deviceHolder.democlass.setText(deviceList.get(position)
					.getDemoclass());
			deviceHolder.demotime.setText(deviceList.get(position)
					.getDemotime());
		} else {
			deviceHolder.demoaddress.setText(deviceList.get(position)
					.getDemoaddress());
			deviceHolder.demotime.setText(deviceList.get(position)
					.getDemotime());
		}
		switch (position % 2) {
		case 0:
			deviceHolder.demopictrue
					.setBackgroundResource(R.drawable.pictrue_one);
			break;
		case 1:
			deviceHolder.demopictrue
					.setBackgroundResource(R.drawable.pictrue_two);
			break;
		default:
			break;
		}
		return convertView;
	}

	class DeviceHolder {
		private ImageView demopictrue;
		private TextView demodevicename;
		private TextView demoaddress;
		private TextView democlass;
		private TextView demotime;
	}
}
