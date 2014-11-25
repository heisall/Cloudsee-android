package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;

public class FuntionAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<String> functionList = new ArrayList<String>();
	public int selectIndex = -1;
	private boolean bigScreen = false;
	private int playFlag;

	public FuntionAdapter(Context con, boolean flag, int playFlag) {
		mContext = con;
		bigScreen = flag;
		this.playFlag = playFlag;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<String> list) {
		functionList = list;
	}

	@Override
	public int getCount() {
		return functionList.size();
	}

	@Override
	public Object getItem(int position) {
		return functionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.function_item, null);
			viewHolder = new ViewHolder();
			viewHolder.funcLayout = (RelativeLayout) convertView
					.findViewById(R.id.funclayout);
			viewHolder.funtionImageView = (ImageView) convertView
					.findViewById(R.id.funtion_image);
			viewHolder.funtionTitle1 = (TextView) convertView
					.findViewById(R.id.funtion_titile1);
			viewHolder.funtionTitle2 = (TextView) convertView
					.findViewById(R.id.funtion_titile2);
			viewHolder.funtionArrow = (ImageView) convertView
					.findViewById(R.id.function_arrow);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (null != functionList && 0 != functionList.size()
				&& position < functionList.size()) {
			viewHolder.funtionTitle1.setText(functionList.get(position));
		}

		if (bigScreen) {// 大屏小dpi
			if (0 == position) {
				viewHolder.funtionArrow.setVisibility(View.GONE);
				viewHolder.funcLayout
						.setBackgroundResource(R.drawable.voice_normal_bg);
				viewHolder.funtionImageView
						.setImageResource(R.drawable.voice_monitor_small_1);
				viewHolder.funtionTitle2
						.setText(R.string.str_audio_monitor_tips);
			} else if (1 == position) {
				viewHolder.funtionImageView
						.setImageResource(R.drawable.yt_controller);
				viewHolder.funcLayout
						.setBackgroundResource(R.drawable.yt_normal_bg);
				viewHolder.funtionTitle2.setText(R.string.str_yt_operate_tips);
			} else if (2 == position) {
				viewHolder.funtionArrow.setVisibility(View.VISIBLE);
				if (Consts.PLAY_AP == playFlag) {
					viewHolder.funtionImageView
							.setImageResource(R.drawable.apv_call);
					viewHolder.funcLayout
							.setBackgroundResource(R.drawable.talk_normal_bg);
					viewHolder.funtionTitle1.setText(R.string.voice_call_ap);
					viewHolder.funtionTitle2
							.setText(R.string.voice_call_ap_tips);
				} else {
					viewHolder.funcLayout
							.setBackgroundResource(R.drawable.remote_normal_bg);
					viewHolder.funtionImageView
							.setImageResource(R.drawable.remote_playback);
					viewHolder.funtionTitle2
							.setText(R.string.str_remote_playback_tips);
				}

			}
			if (selectIndex == position && selectIndex == 0) {
				viewHolder.funtionImageView
						.setImageResource(R.drawable.voice_monitor_small_2);
				viewHolder.funcLayout
						.setBackgroundResource(R.drawable.voice_hover_bg);
			}
			if (selectIndex == position && selectIndex == 2) {
				if (Consts.PLAY_AP == playFlag) {
					viewHolder.funtionImageView
							.setImageResource(R.drawable.apv_call_2);
					viewHolder.funcLayout
							.setBackgroundResource(R.drawable.talk_hover_bg);
				}
			}
			if (2 == position && selectIndex != position) {
				if (Consts.PLAY_AP == playFlag) {
					viewHolder.funtionImageView
							.setImageResource(R.drawable.apv_call_1);
					viewHolder.funcLayout
							.setBackgroundResource(R.drawable.talk_normal_bg);
				}
			}

		} else {
			if (0 == position) {
				viewHolder.funtionArrow.setVisibility(View.GONE);
				viewHolder.funtionImageView
						.setImageResource(R.drawable.voice_monitor_small_1);
				viewHolder.funcLayout
						.setBackgroundResource(R.drawable.voice_normal_bg);
				viewHolder.funtionTitle2
						.setText(R.string.str_audio_monitor_tips);
			} else if (1 == position) {
				viewHolder.funtionImageView
						.setImageResource(R.drawable.yt_controller);
				viewHolder.funtionTitle2.setText(R.string.str_yt_operate_tips);
			} else if (2 == position) {
				viewHolder.funtionImageView
						.setImageResource(R.drawable.remote_playback);
				viewHolder.funtionTitle2
						.setText(R.string.str_remote_playback_tips);
			}
			if (selectIndex == position && selectIndex == 0) {
				viewHolder.funtionImageView
						.setImageResource(R.drawable.voice_monitor_small_2);
				viewHolder.funcLayout
						.setBackgroundResource(R.drawable.voice_hover_bg);
			}
		}

		return convertView;
	}

	class ViewHolder {
		RelativeLayout funcLayout;

		ImageView funtionImageView;
		TextView funtionTitle1;
		TextView funtionTitle2;
		ImageView funtionArrow;
	}

}