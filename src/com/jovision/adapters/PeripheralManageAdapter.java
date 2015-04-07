
package com.jovision.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.AddThirdDeviceMenuFragment;

public class PeripheralManageAdapter extends BaseAdapter {
    private AddThirdDeviceMenuFragment mfragment;
    private LayoutInflater inflater;
    private int h, w;
    private int screenWidth = 0;
    private int[] manageBgArray = {
            R.drawable.third_door_bg,
            R.drawable.third_bracelet_bg, R.drawable.third_telecontrol_bg,
            R.drawable.third_smoke_bg, R.drawable.third_curtain_bg,
            R.drawable.third_infrared_bg, R.drawable.third_gas_bg
    };

    private String[] PeripheralArray;

    public PeripheralManageAdapter(AddThirdDeviceMenuFragment fragment) {
        mfragment = fragment;
        inflater = (LayoutInflater) fragment.getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        PeripheralArray = mfragment.getActivity().getResources()
                .getStringArray(R.array.peripherals_manage);
    }

    public void SetData(int width) {
        screenWidth = width;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int length = 0;
        if (null != PeripheralArray) {
            length = PeripheralArray.length;
        }
        return length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        Object obj = null;
        if (null != PeripheralArray && position < PeripheralArray.length) {
            obj = PeripheralArray[position];
        }
        return obj;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ManageHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.peripheral_item, null);
            holder = new ManageHolder();
            holder.rlItem = (RelativeLayout) convertView
                    .findViewById(R.id.rl_item_container);
            holder.imgPeripheral = (ImageView) convertView
                    .findViewById(R.id.peripherals_img);
            holder.tvPeripheral = (TextView) convertView
                    .findViewById(R.id.peripherals_name_tv);
            convertView.setTag(holder);
        } else {
            holder = (ManageHolder) convertView.getTag();
        }
        h = w = screenWidth / 3;
        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(w,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        holder.rlItem.setLayoutParams(rllp);
        LayoutParams para = holder.imgPeripheral.getLayoutParams();
        para.width = w - 50;
        para.height = h - 50;
        holder.imgPeripheral.setLayoutParams(para);
        holder.imgPeripheral.setBackgroundResource(manageBgArray[position]);
        holder.tvPeripheral.setText(PeripheralArray[position]);

        holder.rlItem.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // position从0开始，外设功能从1开始，因此这里+1，需注意
                mfragment.MyOnNotify(Consts.WHAT_PERI_ITEM_CLICK, position + 1,
                        0, null);
            }
        });

        holder.imgPeripheral.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // position从0开始，外设功能从1开始，因此这里+1，需注意
                mfragment.MyOnNotify(Consts.WHAT_PERI_ITEM_CLICK, position + 1,
                        0, null);
            }
        });

        return convertView;
    }

    private class ManageHolder {
        ImageView imgPeripheral;
        TextView tvPeripheral;
        RelativeLayout rlItem;
    }
}
