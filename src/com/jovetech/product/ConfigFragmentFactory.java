
package com.jovetech.product;

import android.support.v4.app.Fragment;

import com.jovision.activities.JVDeviceManageFragment;

public class ConfigFragmentFactory implements IFragmentFactory {

    @Override
    public Fragment newInstance() {
        // TODO Auto-generated method stub
        // return new ConfigurationFragment();
        return new JVDeviceManageFragment();
    }

    @Override
    public ITabItem getTab() {
        // TODO Auto-generated method stub
        return new ConfigTab();
    }

}
