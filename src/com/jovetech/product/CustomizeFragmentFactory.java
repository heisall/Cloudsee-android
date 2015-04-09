
package com.jovetech.product;

import android.support.v4.app.Fragment;

public class CustomizeFragmentFactory implements IFragmentFactory {

    @Override
    public Fragment newInstance() {
        return null;
    }

    @Override
    public ITabItem getTab() {
        return new CustomizeTab();
    }

}
