package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.bean.RemoteVideo;

public class RemoteVideoAdapter extends BaseAdapter {

	private ArrayList<RemoteVideo> videoList = new ArrayList<RemoteVideo>();
	private Context mContext = null;
	private LayoutInflater inflater;
	private boolean supportDownload = false;

	public RemoteVideoAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<RemoteVideo> list, boolean support) {
		videoList = list;
		supportDownload = support;
	}

	@Override
	public int getCount() {
		int size = 0;
		if (null != videoList && 0 != videoList.size()) {
			size = videoList.size();
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		RemoteVideo rv = new RemoteVideo();
		if (null != videoList && 0 != videoList.size()
				&& position < videoList.size()) {
			rv = videoList.get(position);
		}
		return rv;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.remotevideo_item, null);
			viewHolder = new ViewHolder();
			viewHolder.videoDate = (TextView) convertView
					.findViewById(R.id.videodate);
			viewHolder.videoDisk = (TextView) convertView
					.findViewById(R.id.videodisk);
			viewHolder.videoDownLoad = (ImageView) convertView
					.findViewById(R.id.videodownload);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (supportDownload) {
			viewHolder.videoDownLoad.setVisibility(View.VISIBLE);
		} else {
			viewHolder.videoDownLoad.setVisibility(View.INVISIBLE);
		}

		if (null != videoList && 0 != videoList.size()
				&& position < videoList.size()) {

			// A：alarm报警录像；M：motion移动侦测；T:定时录像 N：normal手动录像
			if ("A".equalsIgnoreCase(videoList.get(position).remoteKind)) {
				viewHolder.videoDate.setText(videoList.get(position).remoteDate
						+ "-"
						+ mContext.getResources().getString(
								R.string.video_alarm));
			} else if ("M".equalsIgnoreCase(videoList.get(position).remoteKind)) {
				viewHolder.videoDate.setText(videoList.get(position).remoteDate
						+ "-"
						+ mContext.getResources().getString(
								R.string.video_motion));
			} else if ("T".equalsIgnoreCase(videoList.get(position).remoteKind)) {
				viewHolder.videoDate.setText(videoList.get(position).remoteDate
						+ "-"
						+ mContext.getResources()
								.getString(R.string.video_time));
			} else {
				viewHolder.videoDate.setText(videoList.get(position).remoteDate
						+ "-"
						+ mContext.getResources().getString(
								R.string.video_normal));
			}

			viewHolder.videoDisk.setText(videoList.get(position).remoteDisk);
		}

		viewHolder.videoDownLoad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((BaseActivity) mContext).onNotify(Consts.PLAY_BACK_DOWNLOAD,
						position, 0, null);
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView videoDate;
		TextView videoDisk;
		ImageView videoDownLoad;
	}
}
