package com.jovision;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.jovision.activities.JVOffLineDialogActivity;
import com.jovision.utils.DefaultExceptionHandler;

public class MainService extends Service implements IHandlerLikeNotify {

	@Override
	public void onCreate() {
		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(
				MainService.this));
		super.onCreate();
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		Intent intent = new Intent(this, JVOffLineDialogActivity.class);
		intent.putExtra("ErrorCode", Consts.APP_CRASH);
		intent.putExtra("ErrorMsg", obj.toString());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
