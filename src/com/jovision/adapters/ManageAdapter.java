package com.jovision.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseFragment;
import com.jovision.bean.Device;

public class ManageAdapter extends BaseAdapter {
	private Device device;
	private BaseFragment mfragment;
	private LayoutInflater inflater;

	private int[] manageResArray = { R.drawable.manage_bg_1,
			R.drawable.manage_bg_2, R.drawable.manage_bg_3,
			R.drawable.manage_bg_4, R.drawable.manage_bg_5,
			R.drawable.manage_bg_6 };

	private String[] fuctionArray;

	private boolean showDelete = false;
	private int screenWidth = 0;

	public ManageAdapter(BaseFragment fragment) {
		mfragment = fragment;
		inflater = (LayoutInflater) fragment.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(Device dev, int width) {
		device = dev;
		screenWidth = width;
		fuctionArray = mfragment.getActivity().getResources()
				.getStringArray(R.array.manage_function);
	}

	// 控制是否显示删除按钮
	public boolean setShowDelete(boolean flag) {
		boolean changeSucc;
		if (showDelete == flag) {
			changeSucc = false;
		} else {
			showDelete = flag;
			changeSucc = true;
		}
		return changeSucc;
	}

	@Override
	public int getCount() {
		return fuctionArray.length;
	}

	@Override
	public Object getItem(int arg0) {
		return fuctionArray[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ChannelHolder channelHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.manage_item, null);
			channelHolder = new ChannelHolder();
			channelHolder.manageBG = (RelativeLayout) convertView
					.findViewById(R.id.manage_rl);
			channelHolder.function = (Button) convertView
					.findViewById(R.id.function);

			convertView.setTag(channelHolder);
		} else {
			channelHolder = (ChannelHolder) convertView.getTag();
		}

		int w = screenWidth / 3;
		int h = w - 70;
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(w, h);
		channelHolder.manageBG.setLayoutParams(rllp);
		channelHolder.function.setText(fuctionArray[position]);
		int resID = manageResArray[position % 6];
		channelHolder.manageBG.setBackgroundResource(resID);

		channelHolder.manageBG.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mfragment.onNotify(Consts.MANAGE_ITEM_CLICK, position, 0, null);
			}
		});

		channelHolder.function.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mfragment.onNotify(Consts.MANAGE_ITEM_CLICK, position, 0, null);
			}
		});

		return convertView;
	}

	class ChannelHolder {
		RelativeLayout manageBG;
		Button function;
	}

}
