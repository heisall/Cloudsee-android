
package com.jovision.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.bean.Device;
import com.jovision.utils.ConfigUtil;

import java.util.ArrayList;

public class WaveDevlListAdapter extends BaseAdapter {

    private BaseActivity activity;
    private LayoutInflater inflater;
    private ArrayList<Device> devList;

    public WaveDevlListAdapter(BaseActivity activity) {
        this.activity = activity;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<Device> dataList) {
        if (null == this.devList) {
            this.devList = new ArrayList<Device>();
        }
        this.devList.addAll(dataList);

    }

    @Override
    public int getCount() {
        return devList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return devList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final DeviceHolder devHolder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.wave_item_layout, null);
            devHolder = new DeviceHolder();
            devHolder.channel_list_text = (TextView) convertView
                    .findViewById(R.id.channel_item_text);

            devHolder.channel_list_img = (ImageView) convertView
                    .findViewById(R.id.channel_item_img);

            devHolder.channel_list_edit = (EditText) convertView
                    .findViewById(R.id.channel_item_edit);

            devHolder.channellist_pull = (LinearLayout) convertView
                    .findViewById(R.id.channellist_pull);
            devHolder.parent_relative = (RelativeLayout) convertView
                    .findViewById(R.id.parent_relative);
            convertView.setTag(devHolder);
        } else {
            devHolder = (DeviceHolder) convertView.getTag();
        }

        try {
            devHolder.channel_list_text.setText(devList.get(position)
                    .getFullNo());
            devHolder.channellist_pull.setVisibility(View.GONE);
            if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(activity)
                    || Consts.LANGUAGE_ZHTW == ConfigUtil
                            .getLanguage2(activity)) {
                devHolder.parent_relative
                        .setBackgroundResource(R.drawable.wave_newone);
            } else {
                devHolder.parent_relative
                        .setBackgroundResource(R.drawable.wave_new);
            }

            if (devList.get(position).isHasAdded()) {
                devHolder.channel_list_text.setTextColor(activity
                        .getResources().getColor(R.color.more_fragment_color2));
                // devHolder.channel_list_img.setImageDrawable(activity
                // .getResources().getDrawable(R.drawable.has_added));
                devHolder.parent_relative
                        .setBackgroundResource(R.drawable.feedbackedit_bg);
                devHolder.channel_list_img.setVisibility(View.GONE);
                // convertView.setVisibility(View.GONE);
            } else {
                devHolder.channel_list_text.setTextColor(activity
                        .getResources().getColor(R.color.more_fragment_color2));
                devHolder.channel_list_img.setImageDrawable(activity
                        .getResources().getDrawable(R.drawable.wave_add));
                // convertView.setVisibility(View.VISIBLE);
            }

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (!devList.get(position).isHasAdded()) {
                        activity.onNotify(Consts.WHAT_ADD_DEVICE, position, 0,
                                null);
                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    class DeviceHolder {
        private RelativeLayout parent_relative;
        private TextView channel_list_text;
        private ImageView channel_list_img;
        private EditText channel_list_edit;
        private LinearLayout channellist_pull;
    }
}
