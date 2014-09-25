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
import com.jovision.activities.BaseFragment;
import com.jovision.newbean.Channel;

public class ChannelAdapter extends BaseAdapter {
	private ArrayList<Channel> channelList;
	private BaseFragment mfragment;
	private LayoutInflater inflater;

	private int[] channelResArray = { R.drawable.channel_bg_1,
			R.drawable.channel_bg_2, R.drawable.channel_bg_3,
			R.drawable.channel_bg_4, R.drawable.channel_bg_5,
			R.drawable.channel_bg_6, R.drawable.channel_bg_7,
			R.drawable.channel_bg_8 };

	private boolean showDelete = false;
	private int screenWidth = 0;

	public ChannelAdapter(BaseFragment fragment) {
		mfragment = fragment;
		inflater = (LayoutInflater) fragment.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Channel> dataList, int width) {
		channelList = dataList;
		screenWidth = width;
	}

	// 控制是否显示删除按钮
	public void setShowDelete(boolean flag) {
		showDelete = flag;
	}

	@Override
	public int getCount() {
		return channelList.size() + 1;
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
					.findViewById(R.id.channel_delete);
			channelHolder.channelEdit = (RelativeLayout) convertView
					.findViewById(R.id.channel_edit);
			channelHolder.channelEditIV = (ImageView) convertView
					.findViewById(R.id.channel_edit_iv);

			convertView.setTag(channelHolder);
		} else {
			channelHolder = (ChannelHolder) convertView.getTag();
		}

		int w = screenWidth / 4;
		int h = w - 20;
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(w, h);
		channelHolder.channelBG.setLayoutParams(rllp);

		if (position >= channelList.size()) {// 最后一个通道用于添加通道
			channelHolder.channelName.setText("+");
			channelHolder.channelDel.setVisibility(View.GONE);
			channelHolder.channelEdit.setVisibility(View.GONE);
			channelHolder.channelBG.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mfragment.onNotify(Consts.CHANNEL_ADD_CLICK, 0, 0, null);

				}
			});

		} else {// 普通通道
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
					mfragment.onNotify(Consts.CHANNEL_ITEM_CLICK, channel, 0,
							null);

				}
			});

			// 长按删除
			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					mfragment.onNotify(Consts.CHANNEL_ITEM_LONG_CLICK, channel,
							0, null);
					return false;
				}
			});

			// 点击删除通道
			channelHolder.channelDel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mfragment.onNotify(Consts.CHANNEL_ITEM_DEL_CLICK, channel,
							0, null);

				}
			});

			// 编辑通道
			channelHolder.channelEdit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mfragment.onNotify(Consts.CHANNEL_EDIT_CLICK, channel, 0,
							null);

				}
			});
			channelHolder.channelEditIV
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							mfragment.onNotify(Consts.CHANNEL_EDIT_CLICK,
									channel, 0, null);

						}
					});
		}

		return convertView;
	}

	class ChannelHolder {
		RelativeLayout channelBG;
		TextView channelName;
		ImageView channelDel;

		RelativeLayout channelEdit;
		ImageView channelEditIV;
	}

}
