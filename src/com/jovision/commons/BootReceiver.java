
package com.jovision.commons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.test.JVACCOUNT;

import com.jovision.utils.BitmapCache;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().compareTo(Intent.ACTION_LOCALE_CHANGED) == 0) {
            // 处理
            MyLog.v("切换语言了", "received ACTION_LOCALE_CHANGED");
            BitmapCache.getInstance().clearAllCache();
            // ConfigUtil.stopBroadCast();
            MyActivityManager.getActivityManager()
                    .popAllActivityExceptOne(null);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }

        // // 接收广播：系统启动完成后运行程序
        // if
        // (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
        // String packageName = "com.jovetech.CloudSee.temp";
        // System.out.println("---------------" + packageName);
        // Toast.makeText(context, "开机运行：" + packageName, Toast.LENGTH_LONG)
        // .show();
        // if (packageName.equalsIgnoreCase("com.jovetech.CloudSee.temp")) {
        // Intent newIntent = new Intent();
        // newIntent.setClassName(packageName, packageName
        // + ".JVWelcomeActivity");
        // newIntent.setAction("android.intent.action.MAIN");
        // newIntent.addCategory("android.intent.category.LAUNCHER");
        // newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // context.startActivity(newIntent);
        // }
        //
        // }
        // // // // 接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
        // if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED"))
        // {
        // ComponentName componetName = new ComponentName(
        // // 这个是另外一个应用程序的包名
        // "com.jovetech.CloudSee.temp",
        // // 这个参数是要启动的Activity
        // "com.jovision.activities.JVWelcomeActivity");
        //
        // try {
        // Intent intents = new Intent();
        // intents.setComponent(componetName);
        // context.startActivity(intent);
        // } catch (Exception e) {
        // }
        // }

        // // 覆盖安装
        // if
        // (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED"))
        // {
        // Toast.makeText(context, "覆盖安装", Toast.LENGTH_LONG).show();
        // String packageName = intent.getDataString().substring(8);
        // if (packageName.equalsIgnoreCase("com.jovetech.CloudSee.temp")) {
        // Intent newIntent = new Intent();
        // newIntent.setClassName("com.jovetech.CloudSee.temp",
        // "com.jovision.activities.JVWelcomeActivity");
        // newIntent.setAction("android.intent.action.MAIN");
        // newIntent.addCategory("android.intent.category.LAUNCHER");
        // newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // context.startActivity(newIntent);
        // }
        // }
        // // 接收广播：设备上删除了一个应用程序包。
        // if
        // (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED"))
        // {
        // Toast.makeText(context, "卸掉一个软件", Toast.LENGTH_LONG).show();
        // System.out.println("********************************");
        // }
    }

    class SetUserOnlineStatusThread implements Runnable {
        private int tag;

        public SetUserOnlineStatusThread(int arg1) {
            tag = arg1;
        }

        @Override
        public void run() {
            JVACCOUNT.SetUserOnlineStatus(tag);
        }
    }
}
