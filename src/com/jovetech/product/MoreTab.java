
package com.jovetech.product;

import android.content.Context;

import com.jovetech.CloudSee.temp.R;

public class MoreTab implements ITabItem {

    @Override
    public String getName(Context context) {
        return context.getResources().getStringArray(R.array.array_tab)[3];
    }

    @Override
    public int getDrawable() {
        return R.drawable.tab_moremessage_selector;
    }

    @Override
    public void setMessage(String count) {
        // TODO Auto-generated method stub

    }

    @Override
    public char getTag() {
        return 'e';
    }

    @Override
    public int getLayoutType() {
        return 0;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public boolean isCustomize() {
        return false;
    }

}
