package com.jovetech.product;

import android.content.Context;

import com.jovetech.CloudSee.temp.R;

public class MyDeviceTab implements ITabItem {

    @Override
    public String getName(Context context) {
        return context.getResources().getStringArray(R.array.array_tab)[0];
    }

    @Override
    public int getDrawable() {
        return R.drawable.tab_device_selector;
    }

    @Override
    public void setMessage(String count) {
        // TODO Auto-generated method stub

    }

    @Override
    public char getTag() {
        return 'a';
    }

    @Override
    public int getLayoutType() {
        return 0;
    }

    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCustomize() {
        return false;
    }

}
