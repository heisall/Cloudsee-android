package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.Device;
import com.jovision.utils.BitmapCache;

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
				deviceHolder.demodevicename = (TextView) convertView
						.findViewById(R.id.item_devicename);
				convertView.setTag(deviceHolder);
			} else {
				convertView = inflater.inflate(R.layout.demo_itemclicked, null);
				deviceHolder = new DeviceHolder();
				deviceHolder.demoaddress = (TextView) convertView
						.findViewById(R.id.item_time_clicked);
				deviceHolder.demopictrue = (ImageView) convertView
						.findViewById(R.id.item_pictrue_clicked);
				deviceHolder.demodevicename = (TextView) convertView
						.findViewById(R.id.item_address_clicked);
				convertView.setTag(deviceHolder);
			}
		} else {
			deviceHolder = (DeviceHolder) convertView.getTag();
		}

		deviceHolder.demoaddress
				.setText(deviceList.get(position).getNickName());
		deviceHolder.demodevicename.setText(mContext.getResources().getString(
				R.string.demo)
				+ (position + 1));
		deviceHolder.demoaddress.setText(deviceList.get(position).getFullNo());

		Bitmap bmp = BitmapCache.getInstance().getBitmap(
				Consts.SCENE_PATH + "demo_"
						+ deviceList.get(position).getFullNo()
						+ Consts.IMAGE_PNG_KIND, "image", "");
		deviceHolder.demopictrue.setScaleType(ScaleType.FIT_XY);
		if (null != bmp) {
			// TODO
			deviceHolder.demopictrue.setImageBitmap(bmp);
			deviceHolder.demopictrue.setBackground(null);
		} else {
			switch (position) {
			case 0:
				if (isclicked) {
					deviceHolder.demopictrue
							.setBackgroundResource(R.drawable.pictrue_one);
				} else {
					deviceHolder.demopictrue
							.setBackgroundResource(R.drawable.demopictrue1);
				}
				break;
			case 1:
				if (isclicked) {
					deviceHolder.demopictrue
							.setBackgroundResource(R.drawable.pictrue_two);
				} else {
					deviceHolder.demopictrue
							.setBackgroundResource(R.drawable.demopictrue2);
				}
				break;
			case 2:
				if (isclicked) {
					deviceHolder.demopictrue
							.setBackgroundResource(R.drawable.pictrue_three);
				} else {
					deviceHolder.demopictrue
							.setBackgroundResource(R.drawable.demopictrue3);
				}
				break;
			default:
				break;
			}
		}

		return convertView;
	}

	class DeviceHolder {
		private ImageView demopictrue;
		private TextView demodevicename;
		private TextView demoaddress;
	}
}
