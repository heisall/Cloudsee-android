
package com.jovision.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.bean.SystemInfo;

import java.util.ArrayList;

public class SystemInfoAdapter extends BaseAdapter {

    private ArrayList<SystemInfo> infoList = new ArrayList<SystemInfo>();
    private LayoutInflater inflater;
    private Context mContext;

    public SystemInfoAdapter(Context context) {
        mContext = context;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<SystemInfo> list) {
        if (null == infoList) {
            infoList = new ArrayList<SystemInfo>();
        }
        infoList.clear();
        infoList.addAll(list);
    }

    // public boolean setRefCount(int counts) {
    // int count = 0;
    // refCount = counts;
    // count = pushList.size();
    // if (refCount > count) {
    // refCount = count;
    // return false;
    // } else {
    // return true;
    // }
    // }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return infoList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.systeminfo_item, null);
            viewHolder = new ViewHolder();
            viewHolder.infoTime = (TextView) convertView
                    .findViewById(R.id.infotime);
            viewHolder.infoContent = (TextView) convertView
                    .findViewById(R.id.infocontent);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (null != infoList && position < infoList.size()) {
            SystemInfo si = infoList.get(position);
            viewHolder.infoTime.setText(si.getInfoTime());
            viewHolder.infoContent.setText(si.getInfoContent());
        }

        return convertView;
    }

    class ViewHolder {
        TextView infoTime;
        TextView infoContent;
    }
}
