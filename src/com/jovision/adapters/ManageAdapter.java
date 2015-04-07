
package com.jovision.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseFragment;
import com.jovision.activities.ManageFragment;
import com.jovision.bean.Device;

public class ManageAdapter extends BaseAdapter {

    private BaseFragment mfragment;
    private LayoutInflater inflater;
    private int h;

    private int[] manageResArray = {
            R.drawable.manage_bgone,
            R.drawable.manage_bgtwo, R.drawable.manage_bgthree,
            R.drawable.manage_bgfour, R.drawable.manage_bgfive,
            R.drawable.manage_bgsix, R.drawable.manage_bgseven
    };
    private int[] manageBgArray = {
            R.drawable.videoedit_set_icon,
            R.drawable.videoedit_devicemanager_icon,
            R.drawable.videoedit_connectmode_icon,
            R.drawable.videoedit_channal_icon, R.drawable.videoedit_see_icon,
            R.drawable.videoedit_add_icon, R.drawable.dev_update
    };

    private String[] fuctionArray;

    private boolean showDelete = false;
    private int screenWidth = 0;

    private int devIndex;
    private Device device;
    private boolean loacal;// 本地登陆

    public ManageAdapter(BaseFragment fragment) {
        mfragment = fragment;
        inflater = (LayoutInflater) fragment.getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        fuctionArray = mfragment.getActivity().getResources()
                .getStringArray(R.array.manage_function);
    }

    public void setData(int width, int index, Device dev, boolean local) {
        screenWidth = width;
        devIndex = index;
        device = dev;
        this.loacal = local;
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
        int length = 0;
        if (null != fuctionArray) {
            length = fuctionArray.length;
        }
        return length;
    }

    @Override
    public Object getItem(int arg0) {
        Object obj = null;
        if (null != fuctionArray && arg0 < fuctionArray.length) {
            obj = fuctionArray[arg0];
        }
        return obj;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ChannelHolder channelHolder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.manage_item, null);
            channelHolder = new ChannelHolder();
            channelHolder.manageBG = (RelativeLayout) convertView
                    .findViewById(R.id.manage_rl);
            channelHolder.function = (TextView) convertView
                    .findViewById(R.id.function);
            channelHolder.img = (ImageView) convertView
                    .findViewById(R.id.manage_img);

            convertView.setTag(channelHolder);
        } else {
            channelHolder = (ChannelHolder) convertView.getTag();
        }

        int w = screenWidth / 3;
        h = w - 80;
        if (ManageFragment.mScreenWidth == 480
                || ManageFragment.mScreenWidth == 540) {
            h = w - 50;
            if (position == 6) {
                RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
                        w - 20, h - 5);
                channelHolder.manageBG.setLayoutParams(rllp);
            }
        }
        if (ManageFragment.mScreenWidth == 1080) {
            h = w - 105;
        }
        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(w, h);
        channelHolder.manageBG.setLayoutParams(rllp);
        channelHolder.function.setText(fuctionArray[position]);
        channelHolder.img.setBackgroundResource(manageBgArray[position]);
        int resID = manageResArray[position % 6];
        channelHolder.manageBG.setBackgroundResource(resID);
        // 本地登陆
        if (loacal) {
            if (5 == position || 6 == position) {
                convertView.setVisibility(View.GONE);
            }
        } else {
            if (5 == position || 6 == position) {
                if (2 == device.getDeviceType()) {
                    convertView.setVisibility(View.VISIBLE);
                    // if (5 == position) {
                    // if (JVDeviceConst.DEVICE_SWITCH_OPEN == device
                    // .getAlarmSwitch()) {
                    // channelHolder.img
                    // .setBackgroundResource(R.drawable.protect_open);
                    // } else if (JVDeviceConst.DEVICE_SWITCH_CLOSE == device
                    // .getAlarmSwitch()) {
                    // channelHolder.img
                    // .setBackgroundResource(R.drawable.protect_close);
                    // }
                    // }
                } else {
                    convertView.setVisibility(View.GONE);
                }
            }

        }

        channelHolder.manageBG.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mfragment.onNotify(Consts.WHAT_MANAGE_ITEM_CLICK, position,
                        devIndex, device);
            }
        });

        channelHolder.function.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mfragment.onNotify(Consts.WHAT_MANAGE_ITEM_CLICK, position,
                        devIndex, device);
            }
        });
        return convertView;
    }

    class ChannelHolder {
        RelativeLayout manageBG;
        TextView function;
        ImageView img;
    }

}
