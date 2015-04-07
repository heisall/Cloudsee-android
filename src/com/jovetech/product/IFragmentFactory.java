
package com.jovetech.product;

import android.support.v4.app.Fragment;

public interface IFragmentFactory {

    public Fragment newInstance();

    public ITabItem getTab();
}
