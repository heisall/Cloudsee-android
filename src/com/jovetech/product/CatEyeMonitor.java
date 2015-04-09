/*
 * zhongwei
 * 
 * 
 * 
 * 
 */

package com.jovetech.product;

import android.content.Context;
import android.content.Intent;

public class CatEyeMonitor extends MonitorDevice {

    @Override
    public void startToVoDActivity(Context context) {
        // TODO Auto-generated method stub

        Intent intent = new Intent(context, VODActivity.class);

        context.startActivity(intent);
    }

    @Override
    public void setNetwork() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDevice() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setConnectMode() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setVideoSource() {
        // TODO Auto-generated method stub

    }

    @Override
    public void playVideoNow() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAlarm() {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkVersion() {
        // TODO Auto-generated method stub

    }

}
