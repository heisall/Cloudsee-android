package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.BaseFragment;
import com.jovision.activities.CustomDialogActivity;
import com.jovision.bean.PushInfo;
import com.jovision.commons.JVConst;
import com.jovision.utils.ConfigUtil;

public class PushAdapter extends BaseAdapter {

	public static final int DELETE_ALARM_MESS = 0x40;// 删除报警

	private ArrayList<PushInfo> pushList = new ArrayList<PushInfo>();
	private LayoutInflater inflater;
	// private Bitmap bitmap;
	int refCount = 0;
	public boolean deleteState = false;
	private boolean bdeleting = false;
	private String[] alarmArray = null;

	private BaseFragment mfragment;

	public PushAdapter(BaseFragment fragment) {
		mfragment = fragment;
		inflater = (LayoutInflater) fragment.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getRefCount() {
		return refCount;
	}

	public boolean setRefCount(int counts) {
		int count = 0;
		refCount = counts;
		count = pushList.size();
		if (refCount > count) {
			refCount = count;
			return false;
		} else {
			return true;
		}
	}

	public void setData(ArrayList<PushInfo> list) {
		pushList = list;
	}

	public void setDeleteFlag(boolean flag) {
		bdeleting = flag;
	}

	@Override
	public int getCount() {
		return refCount;
	}

	@Override
	public Object getItem(int arg0) {
		Object obj = null;
		obj = pushList.get(arg0);
		return obj;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.alarm_mess_item, null);
			viewHolder = new ViewHolder();
			viewHolder.messTime = (TextView) convertView
					.findViewById(R.id.alarmmesstime);
			viewHolder.alarmImg = (ImageView) convertView
					.findViewById(R.id.alarmimage);
			viewHolder.alarmTitle = (TextView) convertView
					.findViewById(R.id.alarmmsg);
			viewHolder.alarmFrom = (TextView) convertView
					.findViewById(R.id.alarmfrom);
			viewHolder.func1 = (Button) convertView.findViewById(R.id.func1);
			viewHolder.alarmLevel = (RatingBar) convertView
					.findViewById(R.id.alarmlevel);
			viewHolder.deleteItem = (ImageView) convertView
					.findViewById(R.id.deleteitem);
			viewHolder.newTag = (ImageView) convertView
					.findViewById(R.id.newtag);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (alarmArray == null) {
			alarmArray = mfragment.getActivity().getResources()
					.getStringArray(R.array.alarm_type);
		}
		if (null != pushList && 0 != pushList.size()
				&& position < pushList.size()) {
			if (pushList.get(position).newTag) {// 新的未读消息
				viewHolder.newTag.setVisibility(View.VISIBLE);
				if (JVConst.LANGUAGE_ZH == ConfigUtil.getLanguage()) {
					viewHolder.newTag.setImageDrawable(mfragment.getActivity()
							.getResources().getDrawable(R.drawable.new_tag_ch));
				} else {
					viewHolder.newTag.setImageDrawable(mfragment.getActivity()
							.getResources().getDrawable(R.drawable.new_tag_en));
				}
				try {
					viewHolder.alarmTitle.setText(alarmArray[pushList
							.get(position).alarmType].replace("%%",
							pushList.get(position).deviceNickName));
				} catch (Exception e) {
					viewHolder.alarmTitle.setText("");
				}

			} else {
				viewHolder.newTag.setVisibility(View.GONE);
				try {
					viewHolder.alarmTitle.setText(alarmArray[pushList
							.get(position).alarmType].replace("%%",
							pushList.get(position).ystNum));
				} catch (Exception e) {
					viewHolder.alarmTitle.setText("");
				}

			}

			if (deleteState) {// 删除按钮显示
				viewHolder.deleteItem.setVisibility(View.VISIBLE);
			} else {
				viewHolder.deleteItem.setVisibility(View.GONE);
			}

			viewHolder.messTime.setText(pushList.get(position).alarmTime);

			viewHolder.alarmLevel.setRating(pushList.get(position).alarmLevel);
		}
		final ViewHolder holder = viewHolder;

		if (null != pushList && 0 != pushList.size()) {
			// [TODO] lkp 获取图片Uri，改为点击获取
			viewHolder.alarmImg.setImageDrawable(mfragment.getActivity()
					.getResources().getDrawable(R.drawable.device_alarm_img));

		}
		viewHolder.func1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (position >= pushList.size()) {
					return;
				}
				pushList.get(position).newTag = false;
				holder.newTag.setVisibility(View.GONE);

				if (pushList.get(position).messageTag == 4604) {// new alarm
					// ------new alarm-----
					PushInfo pushInfo = new PushInfo();
					pushInfo.alarmTime = pushList.get(position).alarmTime;
					pushInfo.messageTag = pushList.get(position).messageTag;
					pushInfo.pic = pushList.get(position).pic;
					pushInfo.video = pushList.get(position).video;
					pushInfo.ystNum = pushList.get(position).ystNum;

					Intent intent = new Intent();
					intent.setClass(mfragment.getActivity(),
							CustomDialogActivity.class);
					intent.putExtra("PUSH_INFO", pushInfo);
					mfragment.getActivity().startActivity(intent);
					// --------end---------
				}
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (position >= pushList.size()) {
					return;
				}
				pushList.get(position).newTag = false;
				holder.newTag.setVisibility(View.GONE);

				deleteState = false;
				notifyDataSetChanged();
				
				if (pushList.get(position).messageTag == 4604) {// new alarm
					// ------new alarm-----
					PushInfo pushInfo = new PushInfo();
					pushInfo.alarmTime = pushList.get(position).alarmTime;
					pushInfo.messageTag = pushList.get(position).messageTag;
					pushInfo.pic = pushList.get(position).pic;
					pushInfo.video = pushList.get(position).video;
					pushInfo.ystNum = pushList.get(position).ystNum;

					Intent intent = new Intent();
					intent.setClass(mfragment.getActivity(),
							CustomDialogActivity.class);
					intent.putExtra("PUSH_INFO", pushInfo);
					mfragment.getActivity().startActivity(intent);
					// --------end---------
				}
			}
		});
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				deleteState = true;
				notifyDataSetChanged();
				return true;
			}

		});
		viewHolder.deleteItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (null != pushList && 0 != pushList.size()
							&& position < pushList.size()) {
						mfragment
								.onNotify(DELETE_ALARM_MESS, position, 0, null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView messTime;
		ImageView alarmImg;
		TextView alarmTitle;
		TextView alarmFrom;
		Button func1;
		RatingBar alarmLevel;
		// Button func2;
		ImageView deleteItem;
		ImageView newTag;

		public ImageView findViewWithTag(String imageUrl) {
			if (imageUrl.equalsIgnoreCase(alarmImg.getTag().toString())) {
				return alarmImg;
			}
			return null;
		}
	}

	// class AlarmThread extends Thread {
	// int position = 0;
	//
	// public AlarmThread(int index) {
	// position = index;
	// };
	//
	// @Override
	// public void run() {
	// // [lkp]先获取图片uri
	// Message msg = BaseApp.pushHandler.obtainMessage();
	// // Message msg = new Message();
	// if (null == pushList.get(position).pic
	// || "".equalsIgnoreCase(pushList.get(position).pic)) {
	// pushList.get(position).pic = AlarmUtil.getAlarmPic(
	// LoginUtil.userName, pushList.get(position).strGUID);
	// if (pushList.get(position).pic == null
	// || pushList.get(position).pic.equals("")) {
	// Log.e("[new_alarm]", "获取图片url失败");
	// msg.what = JVConst.MESSAGE_ALARMVIDEO_LOAD_SUCC;
	// } else {
	// msg.what = JVConst.MESSAGE_ALARMVIDEO_LOAD_SUCC;
	// }
	// } else {
	// msg.what = JVConst.MESSAGE_ALARMVIDEO_LOAD_SUCC;
	// }
	// String url = AlarmUtil.getAlarmVideo(LoginUtil.userName,
	// pushList.get(position).strGUID);
	// Log.v("推送的回调函数--video-----", "pi.video----:" + url);
	//
	// if (null == url || "".equalsIgnoreCase(url.trim())) {
	// // msg.what = JVConst.MESSAGE_ALARMVIDEO_LOAD_FAILED; //[lkp]
	// Bundle data = new Bundle();
	// data.putString("URL", url);
	// data.putInt("POS", position);// [lkp] index
	// msg.setData(data);
	// Log.e("[new_alarm]", "获取视频url失败");
	// } else {
	// // Intent it = new Intent(Intent.ACTION_VIEW);
	// // it.setDataAndType(Uri.parse(url), "video/mp4");
	// // mContext.startActivity(it);
	// msg.what = JVConst.MESSAGE_ALARMVIDEO_LOAD_SUCC;
	// Bundle data = new Bundle();
	// data.putString("URL", url);
	// data.putInt("POS", position);// [lkp] index
	// msg.setData(data);
	// }
	//
	// BaseApp.pushHandler.sendMessage(msg);
	// super.run();
	// }
	//
	// }
}
