package com.jovision.adapters;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
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
import com.jovision.activities.BaseFragment;
import com.jovision.bean.Channel;

public class ChannelAdapter extends BaseAdapter {

	public static final int CHANNEL_ITEM_CLICK = 0x30;// 通道单击事件--
	public static final int CHANNEL_ITEM_LONG_CLICK = 0x31;// 通道长按事件--
	public static final int CHANNEL_ITEM_DEL_CLICK = 0x32;// 通道删除按钮事件--
	public static final int CHANNEL_ADD_CLICK = 0x33;// 通道添加事件--
	public static final int CHANNEL_EDIT_CLICK = 0x34;// 通道删除按钮事件--

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
		notifyDataSetChanged();
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
		return (null != channelList) ? (channelList.size() + 1) : 0;
	}

	@Override
	public Object getItem(int arg0) {
		return (null != channelList) ? (channelList.get(arg0)) : null;
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

		// 普通通道
		if (position < channelList.size()) {
			if (showDelete) {
				channelHolder.channelDel.setVisibility(View.VISIBLE);
				channelHolder.channelEdit.setVisibility(View.VISIBLE);
			} else {
				channelHolder.channelDel.setVisibility(View.GONE);
				channelHolder.channelEdit.setVisibility(View.GONE);
			}

			final int channel = channelList.get(position).getChannel();
			String name = mfragment.getActivity().getResources()
					.getString(R.string.channel_name);
			channelHolder.channelName.setText(name.replace("?",
					String.valueOf(channel)));

			channelHolder.channelBG
					.setBackgroundResource(channelResArray[channel % 8]);
			// 通道单击播放
			channelHolder.channelBG.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mfragment.onNotify(CHANNEL_ITEM_CLICK, channel, 0, null);
				}
			});

			// 长按删除
			channelHolder.channelBG
					.setOnLongClickListener(new OnLongClickListener() {

						@Override
						public boolean onLongClick(View arg0) {
							mfragment.onNotify(CHANNEL_ITEM_LONG_CLICK,
									channel, 0, null);
							return true;
						}
					});

			// 点击删除通道
			channelHolder.channelDel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog(channel);
				}
			});

			// 编辑通道
			channelHolder.channelEdit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mfragment.onNotify(CHANNEL_EDIT_CLICK, channel, 0, null);
				}
			});

			channelHolder.channelEditIV
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							mfragment.onNotify(CHANNEL_EDIT_CLICK, channel, 0,
									null);
						}
					});

		} else {
			// 最后一个通道用于添加通道
			channelHolder.channelName.setText("+");
			channelHolder.channelDel.setVisibility(View.GONE);
			channelHolder.channelEdit.setVisibility(View.GONE);

			// [Neo] stable
			channelHolder.channelBG.setBackgroundResource(channelResArray[5]);
			channelHolder.channelBG.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mfragment.onNotify(CHANNEL_ADD_CLICK, 0, 0, null);
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

	protected void dialog(final int channel) {
		AlertDialog.Builder builder = new Builder(mfragment.getActivity());
		builder.setMessage("确认删除该通道吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mfragment.onNotify(CHANNEL_ITEM_DEL_CLICK, channel, 0, null);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
