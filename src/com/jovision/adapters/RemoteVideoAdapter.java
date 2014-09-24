package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.bean.RemoteVideo;

public class RemoteVideoAdapter extends BaseAdapter {

	ArrayList<RemoteVideo> videoList = new ArrayList<RemoteVideo>();
	public Context mContext = null;
	public LayoutInflater inflater;

	public RemoteVideoAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<RemoteVideo> list) {
		videoList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int size = 0;
		if (null != videoList && 0 != videoList.size()) {
			size = videoList.size();
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		RemoteVideo rv = new RemoteVideo();
		if (null != videoList && 0 != videoList.size()
				&& position < videoList.size()) {
			rv = videoList.get(position);
		}
		return rv;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.remotevideo_item, null);
			viewHolder = new ViewHolder();
			viewHolder.videoDate = (TextView) convertView
					.findViewById(R.id.videodate);
			viewHolder.videoDisk = (TextView) convertView
					.findViewById(R.id.videodisk);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (null != videoList && 0 != videoList.size()
				&& position < videoList.size()) {
			if ("A".equalsIgnoreCase(videoList.get(position).remoteKind)) {
				viewHolder.videoDate.setText(videoList.get(position).remoteDate
						+ "-"
						+ mContext.getResources().getString(
								R.string.video_alarm));
			} else {
				viewHolder.videoDate.setText(videoList.get(position).remoteDate
						+ "-"
						+ mContext.getResources().getString(
								R.string.video_normal));
			}

			viewHolder.videoDisk.setText(videoList.get(position).remoteDisk);
		}
		return convertView;
	}

	class ViewHolder {
		TextView videoDate;
		TextView videoDisk;
	}
}
