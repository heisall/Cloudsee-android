
package com.jovision;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class ActionReceiver extends BroadcastReceiver {

    public static final String ACTION_HANDLER_NOTIFY = "com.jovision.action.HANDLERNOTIFY";
    public static final String ACTION_CONNECT_RES = "com.jovision.action.CONNECTRES";
    public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();

    public static abstract interface EventHandler {
        public abstract void onHandlerNotify(int what, int arg1, int arg2,
                Object obj);

        public abstract void onHandleConnectRes(int ret, Object obj);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (ACTION_HANDLER_NOTIFY.equals(intent.getAction())) {
            for (int i = 0; i < ehList.size(); i++) {
                int what = intent.getExtras().getInt("what");
                int arg1 = intent.getExtras().getInt("arg1");
                int arg2 = intent.getExtras().getInt("arg2");
                String obj = intent.getExtras().getString("obj");
                ((EventHandler) ehList.get(i)).onHandlerNotify(what, arg1,
                        arg2, obj);
            }

        } else if (ACTION_CONNECT_RES.equals(intent.getAction())) {
            for (int i = 0; i < ehList.size(); i++) {
                int arg1 = intent.getExtras().getInt("arg1");
                String obj = intent.getExtras().getString("obj");
                ((EventHandler) ehList.get(i)).onHandleConnectRes(arg1, obj);
            }
        } else {

        }
    }

}
