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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.BaseFragment;
import com.jovision.bean.Channel;

public class ChannelAdapter extends BaseAdapter {

	private ArrayList<Channel> channelList;
	private BaseFragment mfragment;
	private LayoutInflater inflater;

	private int[] channelResArray = { R.drawable.channel_bg_1,
			R.drawable.channel_bg_2, R.drawable.channel_bg_3,
			R.drawable.channel_bg_4, R.drawable.channel_bg_1,
			R.drawable.channel_bg_2, R.drawable.channel_bg_3,
			R.drawable.channel_bg_4, R.drawable.channel_bg_1,
			R.drawable.channel_bg_2, R.drawable.channel_bg_3,
			R.drawable.channel_bg_4, R.drawable.channel_bg_1,
			R.drawable.channel_bg_2, R.drawable.channel_bg_3,
			R.drawable.channel_bg_4 };

	private boolean showDelete = false;
	private int screenWidth = 0;

	public ChannelAdapter(BaseFragment fragment) {
		mfragment = fragment;
		inflater = (LayoutInflater) fragment.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Channel> dataList, int width) {
		channelList = dataList;

		if (width <= 0) {
			screenWidth = ((BaseActivity) mfragment.getActivity()).disMetrics.widthPixels;
		} else {
			screenWidth = width;
		}

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
			channelHolder.channelBG = (FrameLayout) convertView
					.findViewById(R.id.channel_rl);
			channelHolder.channelName = (TextView) convertView
					.findViewById(R.id.channel_name);
			channelHolder.channelDel = (ImageView) convertView
					.findViewById(R.id.channeldelect);
			channelHolder.channelEdit = (RelativeLayout) convertView
					.findViewById(R.id.channel_edit);
			channelHolder.channelEditIV = (ImageView) convertView
					.findViewById(R.id.channel_cancleedit);

			convertView.setTag(channelHolder);

		} else {
			channelHolder = (ChannelHolder) convertView.getTag();
		}
		try {
			int w = screenWidth / 4;
			int h = w - 20;
			RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
					w, h);
			rllp.setMargins(0, 10, 10, 0);
			channelHolder.channelBG.setLayoutParams(rllp);

			// 普通通道
			if (position < channelList.size()) {
				if (showDelete) {
					channelHolder.channelEdit.setVisibility(View.VISIBLE);
					channelHolder.channelDel.setVisibility(View.VISIBLE);
				} else {
					channelHolder.channelEdit.setVisibility(View.GONE);
					channelHolder.channelDel.setVisibility(View.GONE);
				}

				final int channel = channelList.get(position).getChannel();
				channelHolder.channelName.setText(channelList.get(position).getChannelName());
				channelHolder.channelBG
						.setBackgroundResource(channelResArray[((channel - 1) / 4) % 4]);

				// 通道单击播放
				channelHolder.channelBG
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								mfragment.onNotify(
										Consts.WHAT_CHANNEL_ITEM_CLICK,
										channel, 0, null);
							}
						});

				// 长按删除
				channelHolder.channelBG
						.setOnLongClickListener(new OnLongClickListener() {

							@Override
							public boolean onLongClick(View arg0) {
								mfragment.onNotify(
										Consts.WHAT_CHANNEL_ITEM_LONG_CLICK,
										channel, 0, null);
								return true;
							}
						});

				// 点击删除通道
				channelHolder.channelDel
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								dialog(channel);
							}
						});

				// 编辑通道
				channelHolder.channelEditIV
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								mfragment.onNotify(
										Consts.WHAT_CHANNEL_EDIT_CLICK,
										channel, 0, null);
							}
						});
			} else {
				// 最后一个通道用于添加通道
				channelHolder.channelName.setText("+");
				channelHolder.channelDel.setVisibility(View.GONE);
				channelHolder.channelEdit.setVisibility(View.GONE);

				// [Neo] stable
				channelHolder.channelBG
						.setBackgroundResource(channelResArray[0]);
				channelHolder.channelBG
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								mfragment.onNotify(
										Consts.WHAT_CHANNEL_ADD_CLICK, 0, 0,
										null);
							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

		return convertView;
	}

	class ChannelHolder {
		FrameLayout channelBG;
		TextView channelName;
		ImageView channelDel;

		RelativeLayout channelEdit;
		ImageView channelEditIV;
	}

	protected void dialog(final int channel) {
		String okString = mfragment.getActivity().getResources()
				.getString(R.string.ok);
		String delectString = mfragment.getActivity().getResources()
				.getString(R.string.str_delete_sure);
		String warmString = mfragment.getActivity().getResources()
				.getString(R.string.str_delete_tip);
		String cancleString = mfragment.getActivity().getResources()
				.getString(R.string.str_crash_cancel);
		AlertDialog.Builder builder = new Builder(mfragment.getActivity());
		builder.setMessage(delectString);
		builder.setTitle(warmString);
		builder.setPositiveButton(okString,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mfragment.onNotify(Consts.WHAT_CHANNEL_ITEM_DEL_CLICK,
								channel, 0, null);
					}
				});
		builder.setNegativeButton(cancleString,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}
