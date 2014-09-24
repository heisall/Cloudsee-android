package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.newbean.Channel;

public class ChannelAdapter extends BaseAdapter {
	private ArrayList<Channel> channelList;
	private Context mContext;
	private LayoutInflater inflater;

	private int[] channelResArray = { R.drawable.channel_bg_1,
			R.drawable.channel_bg_2, R.drawable.channel_bg_3,
			R.drawable.channel_bg_4, R.drawable.channel_bg_5,
			R.drawable.channel_bg_6, R.drawable.channel_bg_7,
			R.drawable.channel_bg_8 };

	private boolean showDelete = false;

	public ChannelAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Channel> dataList) {
		channelList = dataList;
	}

	// 控制是否显示删除按钮
	public void setShowDelete(boolean flag) {
		showDelete = flag;
	}

	@Override
	public int getCount() {
		return channelList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return channelList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChannelHolder channelHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.channel_item, null);
			channelHolder = new ChannelHolder();
			channelHolder.channelBG = (RelativeLayout) convertView
					.findViewById(R.id.channel_rl);
			channelHolder.channelName = (TextView) convertView
					.findViewById(R.id.channel_name);
			channelHolder.channelDel = (ImageView) convertView
					.findViewById(R.id.channel_delete_l);
			convertView.setTag(channelHolder);
		} else {
			channelHolder = (ChannelHolder) convertView.getTag();
		}

		if (showDelete) {
			channelHolder.channelDel.setVisibility(View.VISIBLE);
		} else {
			channelHolder.channelDel.setVisibility(View.GONE);
		}
		final int channel = channelList.get(position).getChannel();
		channelHolder.channelName.setText(channelList.get(position)
				.getChannel() + "");

		int resID = channelResArray[channel % 8];
		channelHolder.channelBG.setBackgroundResource(resID);

		// 通道单击播放
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MainApplication) mContext.getApplicationContext()).onNotify(
						Consts.CHANNEL_ITEM_CLICK, channel, 0, null);

			}
		});

		// 长按删除
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				((MainApplication) mContext.getApplicationContext()).onNotify(
						Consts.CHANNEL_ITEM_LONG_CLICK, channel, 0, null);
				return false;
			}
		});

		// 点击删除通道
		channelHolder.channelDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MainApplication) mContext.getApplicationContext()).onNotify(
						Consts.CHANNEL_ITEM_DEL_CLICK, channel, 0, null);

			}
		});
		return convertView;
	}

	class ChannelHolder {
		RelativeLayout channelBG;
		TextView channelName;

		ImageView channelDel;
	}

}
