
package com.jovision.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.BaseFragment;
import com.jovision.activities.JVMyDeviceFragment;
import com.jovision.bean.Device;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.BitmapCache;
import com.jovision.utils.ConfigUtil;

import java.util.ArrayList;

@SuppressLint("NewApi")
public class MyDeviceListAdapter extends BaseAdapter {
    private ArrayList<Device> deviceList;
    private BaseFragment mfragment;
    private LayoutInflater inflater;
    public boolean showDelete = false;
    private Context context_;
    private int[] devResArray = {
            R.drawable.device_bg_1,
            R.drawable.device_bg_2, R.drawable.device_bg_3,
            R.drawable.device_bg_4
    };

    private int[] devTopResArray = {
            R.drawable.devicetop_bg_1,
            R.drawable.devicetop_bg_2, R.drawable.devicetop_bg_3,
            R.drawable.devicetop_bg_4
    };

    public MyDeviceListAdapter(Context con, BaseFragment fragment) {
        context_ = con;
        mfragment = fragment;
        inflater = (LayoutInflater) fragment.getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(ArrayList<Device> dataList) {
        deviceList = dataList;
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
        int count = 0;

        try {
            if (null != deviceList && 0 != deviceList.size()) {
                int last = deviceList.size() % 2;
                if (0 == last) {
                    count = deviceList.size() / 2;
                } else {
                    count = deviceList.size() / 2 + 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    @Override
    public Object getItem(int arg0) {
        return deviceList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final DeviceHolder deviceHolder;
        try {

            if (null == convertView) {
                convertView = inflater.inflate(R.layout.mydevice_list_item,
                        null);
                deviceHolder = new DeviceHolder();
                deviceHolder.mydeviceParentL = (FrameLayout) convertView
                        .findViewById(R.id.mydevice_parent_l);
                deviceHolder.devLayoutL = (RelativeLayout) convertView
                        .findViewById(R.id.dev_layout_l);
                deviceHolder.devNameL = (TextView) convertView
                        .findViewById(R.id.dev_name_l);
                deviceHolder.onLineStateL = (TextView) convertView
                        .findViewById(R.id.dev_online_l);
                deviceHolder.wifiStateL = (TextView) convertView
                        .findViewById(R.id.dev_wifi_l);
                deviceHolder.devOnlineImgL = (ImageView) convertView
                        .findViewById(R.id.dev_online_img_l);
                deviceHolder.devWifiImgL = (ImageView) convertView
                        .findViewById(R.id.wifi_online_img_l);
                deviceHolder.devImgL = (ImageView) convertView
                        .findViewById(R.id.dev_image_l);
                deviceHolder.devImgTopL = (ImageView) convertView
                        .findViewById(R.id.dev_image_top_l);
                deviceHolder.devDeleteL = (LinearLayout) convertView
                        .findViewById(R.id.mydevice_cancle_l);
                deviceHolder.editDevL = (RelativeLayout) convertView
                        .findViewById(R.id.dev_edit_l);
                deviceHolder.offline_edit_l = (RelativeLayout) convertView
                        .findViewById(R.id.offline_edit_l);
                deviceHolder.editDevIVL = (LinearLayout) convertView
                        .findViewById(R.id.mydevice_edit_l);
                // deviceHolder.devnicknameL = (TextView) convertView
                // .findViewById(R.id.dev_nickname_l);
                // 云存储状态
                deviceHolder.tv_dev_cloud_state_L = (TextView) convertView
                        .findViewById(R.id.tv_dev_cloud_state_l);
                deviceHolder.tv_dev_cloud_state_R = (TextView) convertView
                        .findViewById(R.id.tv_dev_cloud_state_r);

                deviceHolder.mydeviceParentR = (FrameLayout) convertView
                        .findViewById(R.id.mydevice_parent_r);
                deviceHolder.devLayoutR = (RelativeLayout) convertView
                        .findViewById(R.id.dev_layout_r);
                deviceHolder.devNameR = (TextView) convertView
                        .findViewById(R.id.dev_name_r);
                deviceHolder.onLineStateR = (TextView) convertView
                        .findViewById(R.id.dev_online_r);
                deviceHolder.wifiStateR = (TextView) convertView
                        .findViewById(R.id.dev_wifi_r);
                deviceHolder.devOnlineImgR = (ImageView) convertView
                        .findViewById(R.id.dev_online_img_r);
                deviceHolder.devWifiImgR = (ImageView) convertView
                        .findViewById(R.id.wifi_online_img_r);
                deviceHolder.devImgR = (ImageView) convertView
                        .findViewById(R.id.dev_image_r);
                deviceHolder.devImgTopR = (ImageView) convertView
                        .findViewById(R.id.dev_image_top_r);
                deviceHolder.devDeleteR = (LinearLayout) convertView
                        .findViewById(R.id.mydevice_cancle_r);
                deviceHolder.editDevR = (RelativeLayout) convertView
                        .findViewById(R.id.dev_edit_r);
                deviceHolder.offline_edit_r = (RelativeLayout) convertView
                        .findViewById(R.id.offline_edit_r);
                deviceHolder.editDevIVR = (LinearLayout) convertView
                        .findViewById(R.id.mydevice_edit_r);
                // deviceHolder.devnicknameR = (TextView) convertView
                // .findViewById(R.id.dev_nickname_r);

                convertView.setTag(deviceHolder);
            } else {
                deviceHolder = (DeviceHolder) convertView.getTag();
            }

            if (2 == deviceList.get(position * 2).getIsDevice()) {
                deviceHolder.devNameL.setText(deviceList.get(position * 2)
                        .getNickName());
                // deviceHolder.devnicknameL.setText(deviceList.get(position *
                // 2)
                // .getNickName());
            } else {
                deviceHolder.devNameL.setText(deviceList.get(position * 2)
                        .getNickName());

                if (MySharedPreference.getBoolean(Consts.MORE_LITTLE)) {
                    deviceHolder.devNameL.setText(deviceList.get(position * 2)
                            .getNickName()
                            + "-"
                            + deviceList.get(position * 2).getIp());
                }
                // + "-"
                // + deviceList.get(position * 2).getOnlineStateNet());
                // deviceHolder.devnicknameL.setText(deviceList.get(position *
                // 2)
                // .getNickName());
            }
            // TODO
            deviceHolder.devImgL.setScaleType(ScaleType.FIT_XY);
            deviceHolder.devImgL.setImageBitmap(BitmapCache.getInstance()
                    .getBitmap(
                            ConfigUtil.getImgPath(deviceList.get(position * 2),
                                    false), "image", ""));
            if (Boolean
                    .valueOf(((BaseActivity) mfragment.getActivity()).statusHashMap
                            .get(Consts.LOCAL_LOGIN))) {
                deviceHolder.onLineStateL.setVisibility(View.GONE);
                deviceHolder.wifiStateL.setVisibility(View.GONE);
                deviceHolder.devOnlineImgL.setVisibility(View.GONE);
                deviceHolder.devWifiImgL.setVisibility(View.GONE);
            } else {
                if (deviceList.get(position * 2).getHasWifi() == 1) {
                    deviceHolder.devWifiImgL
                            .setImageResource(R.drawable.wifionline);
                    deviceHolder.wifiStateL.setTextColor(mfragment
                            .getActivity().getResources()
                            .getColor(R.color.encode_view));
                } else {
                    deviceHolder.devWifiImgL
                            .setImageResource(R.drawable.wifioffline);
                    deviceHolder.wifiStateL.setTextColor(mfragment
                            .getActivity().getResources()
                            .getColor(R.color.mydevice_online));

                    if (Build.VERSION_CODES.HONEYCOMB < Build.VERSION.SDK_INT) {
                        deviceHolder.wifiStateL.setAlpha((float) 0.8);
                    } else {
                        // deviceHolder.wifiStateL.getBackground().setAlpha(80);
                    }
                }

                if (deviceList.get(position * 2).getOnlineStateNet() == 1) {
                    deviceHolder.onLineStateL
                            .setText(R.string.str_device_online_net);
                    deviceHolder.onLineStateL.setTextColor(mfragment
                            .getActivity().getResources()
                            .getColor(R.color.encode_view));
                    deviceHolder.devOnlineImgL
                            .setImageResource(R.drawable.deviceonline);
                } else if (deviceList.get(position * 2).getOnlineStateLan() == 1) {
                    deviceHolder.onLineStateL
                            .setText(R.string.str_device_online_lan);
                    deviceHolder.onLineStateL.setTextColor(mfragment
                            .getActivity().getResources()
                            .getColor(R.color.encode_view));
                    deviceHolder.devOnlineImgL
                            .setImageResource(R.drawable.deviceonline);
                } else {
                    deviceHolder.onLineStateL
                            .setText(R.string.str_device_offline);
                    deviceHolder.onLineStateL.setTextColor(mfragment
                            .getActivity().getResources()
                            .getColor(R.color.mydevice_online));
                    if (Build.VERSION_CODES.HONEYCOMB < Build.VERSION.SDK_INT) {
                        deviceHolder.onLineStateL.setAlpha((float) 0.8);
                    } else {
                        // deviceHolder.onLineStateL.getBackground().setAlpha(80);
                    }

                    deviceHolder.devOnlineImgL
                            .setImageResource(R.drawable.deviceoffline);
                }
                // 云存储状态
                if (deviceList.get(position * 2).getCloudEnabled() == 1) {
                    Drawable draw_cloud_on = context_.getResources().getDrawable(
                            R.drawable.cloud_state_on);
                    draw_cloud_on.setBounds(0, 0, draw_cloud_on.getMinimumWidth(),
                            draw_cloud_on.getMinimumHeight());
                    deviceHolder.tv_dev_cloud_state_L.setCompoundDrawables(draw_cloud_on, null,
                            null, null);
                } else if (deviceList.get(position * 2).getCloudEnabled() == 0) {
                    Drawable draw_cloud_off = context_.getResources().getDrawable(
                            R.drawable.cloud_state_off);
                    draw_cloud_off.setBounds(0, 0, draw_cloud_off.getMinimumWidth(),
                            draw_cloud_off.getMinimumHeight());
                    deviceHolder.tv_dev_cloud_state_L.setCompoundDrawables(draw_cloud_off, null,
                            null, null);
                } else {
                    // 不显示
                    deviceHolder.tv_dev_cloud_state_L.setVisibility(View.INVISIBLE);
                }

            }
            int lastL = (position * 2) % 4;
            int lastR = (position * 2 + 1) % 4;
            // 按规律设置背景色
            if (0 == lastL || 2 == lastL) {
                deviceHolder.devLayoutL
                        .setBackgroundResource(devResArray[lastL]);
                deviceHolder.devImgTopL
                        .setBackgroundResource(devTopResArray[lastL]);
            }
            if (1 == lastR || 3 == lastR) {
                deviceHolder.devLayoutR
                        .setBackgroundResource(devResArray[lastR]);
                deviceHolder.devImgTopR
                        .setBackgroundResource(devTopResArray[lastR]);
            }
            // 控制删除按钮显示隐藏
            if (showDelete) {
                deviceHolder.devDeleteL.setVisibility(View.VISIBLE);
                deviceHolder.devDeleteR.setVisibility(View.VISIBLE);
                deviceHolder.editDevL.setVisibility(View.VISIBLE);
                deviceHolder.editDevR.setVisibility(View.VISIBLE);
                deviceHolder.offline_edit_r.setVisibility(View.GONE);
                deviceHolder.offline_edit_l.setVisibility(View.GONE);
            } else {
                deviceHolder.devDeleteL.setVisibility(View.GONE);
                deviceHolder.devDeleteR.setVisibility(View.GONE);
                deviceHolder.editDevL.setVisibility(View.GONE);
                deviceHolder.editDevR.setVisibility(View.GONE);
                if (!Boolean
                        .valueOf(((BaseActivity) mfragment.getActivity()).statusHashMap
                                .get(Consts.LOCAL_LOGIN))) {
                    if ((deviceList.get(deviceList.size() - 1)
                            .getOnlineStateNet() == 0 && deviceList.get(
                            deviceList.size() - 1).getOnlineStateLan() == 0)) {
                        deviceHolder.offline_edit_l.setVisibility(View.VISIBLE);
                    } else {
                        deviceHolder.offline_edit_l.setVisibility(View.GONE);
                    }
                    if (position * 2 + 1 < deviceList.size()) {
                        if ((deviceList.get(position * 2).getOnlineStateNet() == 0 && deviceList
                                .get(position * 2).getOnlineStateLan() == 0)) {
                            deviceHolder.offline_edit_l
                                    .setVisibility(View.VISIBLE);
                        } else {
                            deviceHolder.offline_edit_l
                                    .setVisibility(View.GONE);
                        }
                        if ((deviceList.get(position * 2 + 1)
                                .getOnlineStateNet() == 0 && deviceList.get(
                                position * 2 + 1).getOnlineStateLan() == 0)) {
                            deviceHolder.offline_edit_r
                                    .setVisibility(View.VISIBLE);
                        } else {
                            deviceHolder.offline_edit_r
                                    .setVisibility(View.GONE);
                        }
                        // 云存储状态
                        if (deviceList.get(position * 2 + 1).getCloudEnabled() == 1) {
                            Drawable draw_cloud_on = context_.getResources().getDrawable(
                                    R.drawable.cloud_state_on);
                            draw_cloud_on.setBounds(0, 0, draw_cloud_on.getMinimumWidth(),
                                    draw_cloud_on.getMinimumHeight());
                            deviceHolder.tv_dev_cloud_state_R.setCompoundDrawables(draw_cloud_on,
                                    null, null, null);
                        } else if (deviceList.get(position * 2 + 1).getCloudEnabled() == 0) {
                            Drawable draw_cloud_off = context_.getResources().getDrawable(
                                    R.drawable.cloud_state_off);
                            draw_cloud_off.setBounds(0, 0, draw_cloud_off.getMinimumWidth(),
                                    draw_cloud_off.getMinimumHeight());
                            deviceHolder.tv_dev_cloud_state_R.setCompoundDrawables(draw_cloud_off,
                                    null, null, null);
                        } else {
                            // 不显示
                            deviceHolder.tv_dev_cloud_state_R.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
            deviceHolder.editDevL
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            JVMyDeviceFragment.isshow = false;
                            setShowDelete(false);
                            notifyDataSetChanged();
                        }
                    });
            deviceHolder.editDevR
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            JVMyDeviceFragment.isshow = false;
                            setShowDelete(false);
                            notifyDataSetChanged();
                        }
                    });
            int rightPos = position * 2 + 1;
            int size = deviceList.size();
            if (rightPos < size) {
                deviceHolder.mydeviceParentR.setVisibility(View.VISIBLE);
                if (2 == deviceList.get(position * 2 + 1).getIsDevice()) {
                    deviceHolder.devNameR.setText(deviceList.get(
                            position * 2 + 1).getNickName());
                    // deviceHolder.devnicknameR.setText(deviceList.get(
                    // position * 2 + 1).getNickName());
                } else {
                    deviceHolder.devNameR.setText(deviceList.get(
                            position * 2 + 1).getNickName());

                    if (MySharedPreference.getBoolean(Consts.MORE_LITTLE)) {
                        deviceHolder.devNameR.setText(deviceList.get(position * 2 + 1)
                                .getNickName()
                                + "-"
                                + deviceList.get(position * 2 + 1).getIp());
                    }

                    // + "-"
                    // + deviceList.get(position * 2 + 1).getOnlineStateNet());
                    // deviceHolder.devnicknameR.setText(deviceList.get(
                    // position * 2 + 1).getNickName());
                }

                // TODO
                deviceHolder.devImgR.setScaleType(ScaleType.FIT_XY);
                deviceHolder.devImgR
                        .setImageBitmap(BitmapCache.getInstance()
                                .getBitmap(
                                        ConfigUtil.getImgPath(deviceList
                                                .get(position * 2 + 1), false),
                                        "image", ""));

                if (Boolean
                        .valueOf(((BaseActivity) mfragment.getActivity()).statusHashMap
                                .get(Consts.LOCAL_LOGIN))) {
                    deviceHolder.onLineStateR.setVisibility(View.GONE);
                    deviceHolder.wifiStateR.setVisibility(View.GONE);
                    deviceHolder.devOnlineImgR.setVisibility(View.GONE);
                    deviceHolder.devWifiImgR.setVisibility(View.GONE);
                } else {
                    if (deviceList.get(position * 2 + 1).getHasWifi() == 1) {
                        deviceHolder.devWifiImgR
                                .setImageResource(R.drawable.wifionline);
                        deviceHolder.wifiStateR.setTextColor(mfragment
                                .getActivity().getResources()
                                .getColor(R.color.encode_view));
                    } else {
                        deviceHolder.devWifiImgR
                                .setImageResource(R.drawable.wifioffline);
                        deviceHolder.wifiStateR.setTextColor(mfragment
                                .getActivity().getResources()
                                .getColor(R.color.mydevice_online));
                        if (Build.VERSION_CODES.HONEYCOMB < Build.VERSION.SDK_INT) {
                            deviceHolder.wifiStateR.setAlpha((float) 0.8);
                        } else {
                            // deviceHolder.wifiStateR.getBackground().setAlpha(80);
                        }
                    }
                    if (deviceList.get(position * 2 + 1).getOnlineStateNet() == 1) {
                        deviceHolder.onLineStateR
                                .setText(R.string.str_device_online_net);
                        deviceHolder.onLineStateR.setTextColor(mfragment
                                .getActivity().getResources()
                                .getColor(R.color.encode_view));
                        deviceHolder.devOnlineImgR
                                .setImageResource(R.drawable.deviceonline);
                    } else if (deviceList.get(position * 2 + 1)
                            .getOnlineStateLan() == 1) {
                        deviceHolder.onLineStateR
                                .setText(R.string.str_device_online_lan);
                        deviceHolder.onLineStateR.setTextColor(mfragment
                                .getActivity().getResources()
                                .getColor(R.color.encode_view));
                        deviceHolder.devOnlineImgR
                                .setImageResource(R.drawable.deviceonline);
                    } else {
                        deviceHolder.onLineStateR
                                .setText(R.string.str_device_offline);
                        deviceHolder.onLineStateR.setTextColor(mfragment
                                .getActivity().getResources()
                                .getColor(R.color.mydevice_online));
                        if (Build.VERSION_CODES.HONEYCOMB < Build.VERSION.SDK_INT) {
                            deviceHolder.onLineStateR.setAlpha((float) 0.8);
                        } else {
                            // deviceHolder.onLineStateR.getBackground().setAlpha(80);
                        }
                        deviceHolder.devOnlineImgR
                                .setImageResource(R.drawable.deviceoffline);
                    }
                }
            } else {
                deviceHolder.mydeviceParentR.setVisibility(View.GONE);
            }

            // 左侧按钮事件
            deviceHolder.devLayoutL.setOnClickListener(new DevOnClickListener(
                    1, 1, position));
            deviceHolder.devDeleteL.setOnClickListener(new DevOnClickListener(
                    1, 3, position));
            deviceHolder.devLayoutL
                    .setOnLongClickListener(new OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View arg0) {
                            mfragment.onNotify(
                                    Consts.WHAT_DEVICE_ITEM_LONG_CLICK,
                                    position, 0, null);
                            return false;
                        }
                    });
            deviceHolder.editDevIVL.setOnClickListener(new DevOnClickListener(
                    1, 4, position));
            // 右侧按钮事件
            deviceHolder.devLayoutR.setOnClickListener(new DevOnClickListener(
                    2, 1, position));
            deviceHolder.devDeleteR.setOnClickListener(new DevOnClickListener(
                    2, 3, position));
            deviceHolder.devLayoutR
                    .setOnLongClickListener(new OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View arg0) {
                            mfragment.onNotify(
                                    Consts.WHAT_DEVICE_ITEM_LONG_CLICK,
                                    position, 0, null);
                            return false;
                        }
                    });
            deviceHolder.editDevIVR.setOnClickListener(new DevOnClickListener(
                    2, 4, position));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    // 设备单击事件
    class DevOnClickListener implements OnClickListener {
        private int tag = 0;// 左右标志 1：左 2：右
        private int operate = 1;// 1：点击播放 2：点击查看设备通道 3.点击删除设备 4.编辑设备
        private int line = 0;// 行号
        private int position = 0;// 列表中的位置

        public DevOnClickListener(int leftOrRight, int method, int linePos) {
            tag = leftOrRight;
            operate = method;
            line = linePos;
            if (1 == tag) {
                position = line * 2;
            } else if (2 == tag) {
                position = line * 2 + 1;
            }

        }

        @Override
        public void onClick(View arg0) {
            if (1 == operate || 2 == operate) {
                mfragment.onNotify(Consts.WHAT_DEVICE_ITEM_CLICK, position, 0,
                        null);
            } else if (3 == operate) {
                dialog(position);
            } else if (4 == operate) {
                mfragment.onNotify(Consts.WHAT_DEVICE_EDIT_CLICK, position, 0,
                        null);
            }
        }

    }

    class DeviceHolder {
        FrameLayout mydeviceParentL;
        RelativeLayout offline_edit_l;
        RelativeLayout devLayoutL;
        TextView devNameL;
        TextView onLineStateL;
        TextView wifiStateL;
        ImageView devImgL;
        ImageView devImgTopL;
        LinearLayout devDeleteL;
        RelativeLayout editDevL;
        LinearLayout editDevIVL;
        ImageView devOnlineImgL;
        ImageView devWifiImgL;
        // TextView devnicknameL;
        TextView tv_dev_cloud_state_L;
        TextView tv_dev_cloud_state_R;
        FrameLayout mydeviceParentR;
        RelativeLayout offline_edit_r;
        RelativeLayout devLayoutR;
        TextView devNameR;
        TextView onLineStateR;
        TextView wifiStateR;
        ImageView devImgR;
        ImageView devImgTopR;
        LinearLayout devDeleteR;
        RelativeLayout editDevR;
        LinearLayout editDevIVR;
        ImageView devOnlineImgR;
        ImageView devWifiImgR;
        // TextView devnicknameR;
    }

    protected void dialog(final int position) {
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
                        mfragment.onNotify(Consts.WHAT_DEVICE_ITEM_DEL_CLICK,
                                position, 0, null);
                    }
                });
        builder.setNegativeButton(cancleString,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

}
