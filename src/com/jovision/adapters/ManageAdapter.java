package com.jovision.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseFragment;
import com.umeng.analytics.c;

public class ManageAdapter extends BaseAdapter {

	private BaseFragment mfragment;
	private LayoutInflater inflater;

	private int[] manageResArray = { R.drawable.manage_bgone,
			R.drawable.manage_bgtwo, R.drawable.manage_bgthree,
			R.drawable.manage_bgfour ,R.drawable.manage_bgfive,
			R.drawable.manage_bgsix};
	private int [] manageBgArray = {R.drawable.videoedit_set_icon,R.drawable.videoedit_devicemanager_icon,
			R.drawable.videoedit_connectmode_icon,R.drawable.videoedit_channal_icon,R.drawable.videoedit_see_icon,
			R.drawable.videoedit_add_icon};
	
	private String[] fuctionArray;

	private boolean showDelete = false;
	private int screenWidth = 0;

	public ManageAdapter(BaseFragment fragment) {
		mfragment = fragment;
		inflater = (LayoutInflater) fragment.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		fuctionArray = mfragment.getActivity().getResources()
				.getStringArray(R.array.manage_function);
	}

	public void setData(int width) {
		screenWidth = width;

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
		final ChannelHolder channelHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.manage_item, null);
			channelHolder = new ChannelHolder();
			channelHolder.manageBG = (RelativeLayout) convertView
					.findViewById(R.id.manage_rl);
			channelHolder.function = (TextView) convertView
					.findViewById(R.id.function);
			channelHolder.img = (ImageView)convertView.
					findViewById(R.id.manage_img);

			convertView.setTag(channelHolder);
		} else {
			channelHolder = (ChannelHolder) convertView.getTag();
		}

		int w = screenWidth / 3;
		int h = w - 70;
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(w, h);
		channelHolder.manageBG.setLayoutParams(rllp);
		channelHolder.function.setText(fuctionArray[position]);
		channelHolder.img.setBackgroundResource(manageBgArray[position]);
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
		TextView function;
		ImageView img;
	}

}
