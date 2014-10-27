package com.jovision.adapters;

import java.util.ArrayList;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovision.bean.Device;

public class DemoListAdapter extends BaseAdapter {
	private ArrayList<Device> deviceList;
	private Context mContext;
	private LayoutInflater inflater;
	private boolean isclicked;

	public DemoListAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Device> dataList, boolean isclick) {
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
		// if (isclicked) {
		// deviceHolder.demoaddress.setText(deviceList.get(position)
		// .getNickName());
		// deviceHolder.demodevicename.setText(deviceList.get(position)
		// .getFullNo());
		// deviceHolder.democlass.setText(deviceList.get(position).getUser());
		// deviceHolder.demotime.setText(deviceList.get(position).getPwd());
		// } else {
		// deviceHolder.demoaddress.setText(deviceList.get(position)
		// .getNickName());
		// deviceHolder.demotime.setText(deviceList.get(position).getFullNo());
		// }
		deviceHolder.demodevicename.setText(mContext.getResources().getString(
				R.string.demo)
				+ (position + 1));
		deviceHolder.demoaddress.setText(deviceList.get(position).getFullNo());

		switch (position) {
		case 0:
			deviceHolder.demopictrue
					.setBackgroundResource(R.drawable.pictrue_one);
			break;
		case 1:
			deviceHolder.demopictrue
					.setBackgroundResource(R.drawable.pictrue_two);
			break;
		case 2:
			deviceHolder.demopictrue
					.setBackgroundResource(R.drawable.pictrue_three);
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
