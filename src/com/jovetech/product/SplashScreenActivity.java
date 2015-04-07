
package com.jovetech.product;

import android.app.Activity;
import android.os.Bundle;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        boolean toGuide = isShowGuide();
        if (toGuide)
            showGuide();
        else {
            boolean toLogin = isToLogin();
            if (toLogin) {
                jumpTologin();
            } else {
                jumpToMain();
            }
        }
    }

    private boolean isShowGuide() {

        return false;
    }

    private void showGuide() {

    }

    /**
     * �ж��Ƿ񵽵�½����
     */
    private boolean isToLogin() {

        return true;
    }

    /**
     * ��ת����½����
     */
    private void jumpTologin() {

    }

    /**
     * ��ת��������
     */
    private void jumpToMain() {

    }

}
