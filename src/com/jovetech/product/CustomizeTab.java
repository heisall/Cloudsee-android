package com.jovetech.product;

import android.content.Context;

import com.jovetech.CloudSee.temp.R;

public class CustomizeTab implements ITabItem {

    @Override
    public String getName(Context context) {
        return "";
    }

    @Override
    public int getDrawable() {
        return R.drawable.tab_customize_selector;
    }

    @Override
    public void setMessage(String count) {
        // TODO Auto-generated method stub

    }

    @Override
    public char getTag() {
        return 'c';
    }

    @Override
    public int getLayoutType() {
        return 1;
    }

    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCustomize() {
        return true;
    }

}
