
package com.jovetech.product;

import android.support.v4.app.Fragment;

import com.jovision.activities.JVMoreFragment;

public class MoreFragmentFactory implements IFragmentFactory {

    @Override
    public Fragment newInstance() {
        // TODO Auto-generated method stub
        // return new MoreFragment();
        return new JVMoreFragment();
    }

    @Override
    public ITabItem getTab() {
        // TODO Auto-generated method stub
        return new MoreTab();
    }

}
