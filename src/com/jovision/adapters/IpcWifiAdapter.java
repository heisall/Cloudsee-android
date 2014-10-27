package com.jovision.adapters;

import java.util.ArrayList;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IpcWifiAdapter extends BaseAdapter {
	private ArrayList<ScanResult> scanWifiList = new ArrayList<ScanResult>();
	private ArrayList<Boolean> dataList = new ArrayList<Boolean>();
	private Context mContext = null;
	private LayoutInflater inflater;
	private int index = 9000;
	private String oldWifi = "";

	public IpcWifiAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<ScanResult> list, String wifi) {
		scanWifiList = list;
		// this.dataList = dataList;
		oldWifi = wifi;
	}

	public void init(int index) {
		this.index = index;
	}

	@Override
	public int getCount() {
		int size = 0;
		if (null != scanWifiList && 0 != scanWifiList.size()) {
			size = scanWifiList.size();
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		Object obj = null;
		if (null != scanWifiList && 0 != scanWifiList.size()) {
			obj = scanWifiList.get(position);
		}
		return obj;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final WifiHolder wifiHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.ipcwifi_item, null);
			wifiHolder = new WifiHolder();
			wifiHolder.wifiName = (TextView) convertView
					.findViewById(R.id.videodate);
			wifiHolder.wifiImg = (ImageView) convertView
					.findViewById(R.id.wifistate);

			wifiHolder.wifiState = (ImageView) convertView
					.findViewById(R.id.wifistate);
			wifiHolder.wifiDetail = (ImageView) convertView
					.findViewById(R.id.wifidetail);
			wifiHolder.relativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.wifiRelative);
			convertView.setTag(wifiHolder);
		} else {
			wifiHolder = (WifiHolder) convertView.getTag();
		}

		if (position == index - 1) {
			wifiHolder.wifiName.setTextColor(mContext.getResources().getColor(
					R.color.dialogchannaltext));
			wifiHolder.relativeLayout.setBackgroundResource(R.drawable.hover);
		} else {
			wifiHolder.wifiName.setTextColor(mContext.getResources().getColor(
					R.color.more_fragment_color2));
			wifiHolder.relativeLayout.setBackgroundResource(R.drawable.normal);
		}
		wifiHolder.wifiState.setVisibility(View.GONE);
		wifiHolder.wifiName.setText(scanWifiList.get(position).SSID);
		wifiHolder.wifiDetail.setImageDrawable(mContext.getResources()
				.getDrawable(R.drawable.morefragment_next_icon));

		wifiHolder.wifiDetail.setVisibility(View.VISIBLE);
		return convertView;
	}

	class WifiHolder {
		TextView wifiName;
		ImageView wifiImg;
		RelativeLayout relativeLayout;
		ImageView wifiState;
		ImageView wifiDetail;
	}

}
