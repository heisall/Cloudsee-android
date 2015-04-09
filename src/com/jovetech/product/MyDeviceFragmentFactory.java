
package com.jovetech.product;

import android.support.v4.app.Fragment;

import com.jovision.activities.JVMyDeviceFragment;

public class MyDeviceFragmentFactory implements IFragmentFactory {

    @Override
    public Fragment newInstance() {
        // TODO Auto-generated method stub
        // return new MyDeviceFragment();
        return new JVMyDeviceFragment();
    }

    @Override
    public ITabItem getTab() {
        // TODO Auto-generated method stub
        return new MyDeviceTab();
    }

}
