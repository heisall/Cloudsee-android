package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;

public class IpcWifiAdapter extends BaseAdapter {
	private ArrayList<ScanResult> scanWifiList = new ArrayList<ScanResult>();
	private Context mContext = null;
	private LayoutInflater inflater;
	private int wifiIndex = -1;
	private String oldWifi = "";

	public IpcWifiAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<ScanResult> list, String wifi) {
		scanWifiList = list;
		oldWifi = wifi;
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

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		WifiHolder wifiHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.wifi_item, null);
			wifiHolder = new WifiHolder();
			wifiHolder.wifiName = (TextView) convertView
					.findViewById(R.id.videodate);
			wifiHolder.wifiImg = (ImageView) convertView
					.findViewById(R.id.wifistate);

			wifiHolder.wifiState = (ImageView) convertView
					.findViewById(R.id.wifistate);
			wifiHolder.wifiDetail = (ImageView) convertView
					.findViewById(R.id.wifidetail);
			convertView.setTag(wifiHolder);
		} else {
			wifiHolder = (WifiHolder) convertView.getTag();
		}

		// 与当前网络一致
		// if (oldWifi.equalsIgnoreCase(scanWifiList.get(position).SSID)) {
		// wifiHolder.wifiName.setTextColor(mContext.getResources().getColor(
		// R.color.string_content));
		// } else {
		wifiHolder.wifiName.setTextColor(mContext.getResources().getColor(
				R.color.black));
		// }
		wifiHolder.wifiState.setVisibility(View.GONE);
		wifiHolder.wifiName.setText(scanWifiList.get(position).SSID);
		wifiHolder.wifiDetail.setImageDrawable(mContext.getResources()
				.getDrawable(R.drawable.morefragment_next_icon));
		if (wifiIndex == position) {
			wifiHolder.wifiImg.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.wifi_flag_open_bg));
		} else {
			wifiHolder.wifiImg.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.wifi_flag_close_bg));
		}

		wifiHolder.wifiDetail.setVisibility(View.VISIBLE);
		convertView.setBackgroundDrawable(mContext.getResources().getDrawable(
				R.drawable.ap_wifi_list_bg));
		return convertView;
	}

	class WifiHolder {
		TextView wifiName;
		ImageView wifiImg;

		ImageView wifiState;
		ImageView wifiDetail;
	}

}
