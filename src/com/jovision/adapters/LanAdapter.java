
package com.jovision.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.BaseActivity;
import com.jovision.bean.Device;

import java.util.ArrayList;

public class LanAdapter extends BaseAdapter {

    private BaseActivity activity;
    private LayoutInflater inflater;
    private ArrayList<Device> dataList;

    public LanAdapter(BaseActivity activitys) {
        activity = activitys;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<Device> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return dataList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final DeviceHolder Holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.lan_item, null);
            Holder = new DeviceHolder();

            Holder.lan_item_name = (TextView) convertView
                    .findViewById(R.id.lan_item_name);

            Holder.lan_item_img = (ImageView) convertView
                    .findViewById(R.id.lan_item_img);

            convertView.setTag(Holder);
        } else {
            Holder = (DeviceHolder) convertView.getTag();
        }
        Holder.lan_item_name.setText(dataList.get(position).getFullNo());
        if (dataList.get(position).isIslanselect()) {
            Holder.lan_item_img
                    .setBackgroundResource(R.drawable.morefragment_selector_icon);
        } else {
            Holder.lan_item_img
                    .setBackgroundResource(R.drawable.morefragment_normal_icon);
        }
        return convertView;
    }

    class DeviceHolder {
        private TextView lan_item_name;
        private ImageView lan_item_img;
    }
}
