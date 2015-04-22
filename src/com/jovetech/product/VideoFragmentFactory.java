
package com.jovetech.product;

import android.support.v4.app.Fragment;

import com.jovision.activities.JVVideoFragment;

public class VideoFragmentFactory implements IFragmentFactory {

    @Override
    public Fragment newInstance() {
        // TODO Auto-generated method stub
        // return new VideoSquareFragment();
        return new JVVideoFragment();
    }

    @Override
    public ITabItem getTab() {
        // TODO Auto-generated method stub
        return new VideoTab();
    }

}
