
package com.jovetech.product;

import android.content.res.Resources;

import com.jovetech.CloudSee.temp.R;

public class VideoTab implements ITabItem {

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return Resources.getSystem().getStringArray(R.array.array_tab)[1];
    }

    @Override
    public int getDrawable() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setMessage(String count) {
        // TODO Auto-generated method stub

    }

    @Override
    public char getTag() {
        // TODO Auto-generated method stub
        return 'b';
    }

    @Override
    public int getLayoutType() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return null;
    }

}
