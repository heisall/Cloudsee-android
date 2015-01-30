package com.jovision.commons;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.JVWebViewActivity;
import com.jovision.bean.WebUrl;
import com.jovision.utils.DeviceUtil;

//获取演示点 设置三种类型参数分别为String,Integer,String
public class GetUrlTask extends AsyncTask<String, Integer, Integer> {
	private Context mContext;
	private WebUrl demoUrl;
	private String sid;

	public GetUrlTask(Context con) {
		mContext = con;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	@Override
	protected Integer doInBackground(String... params) {
		int getRes = -1;// 0成功 1失败
		sid = params[0];
		demoUrl = DeviceUtil.getWebUrl();
		if (null != demoUrl) {
			getRes = 0;
		}
		return getRes;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(Integer result) {
		// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
		((BaseActivity) mContext).dismissDialog();
		if (0 == result) {
			int error = Integer.valueOf(sid);
			switch (error) {
			case 0:
				Intent intentAD = new Intent(mContext, JVWebViewActivity.class);
				intentAD.putExtra("URL", demoUrl.getCustUrl());
				intentAD.putExtra("title", -2);
				mContext.startActivity(intentAD);
				break;
			case 1:
				Intent intentAD1 = new Intent(mContext, JVWebViewActivity.class);
				intentAD1.putExtra("URL", demoUrl.getDemoUrl());
				intentAD1.putExtra("title", -2);
				mContext.startActivity(intentAD1);
				break;
			case 2:
				Intent intentAD2 = new Intent(mContext, JVWebViewActivity.class);
				intentAD2.putExtra("URL", demoUrl.getStatUrl());
				intentAD2.putExtra("title", -2);
				mContext.startActivity(intentAD2);
				break;
			default:
				break;
			}

		} else {
			((BaseActivity) mContext)
					.showTextToast(R.string.str_video_load_failed);
		}
	}

	@Override
	protected void onPreExecute() {
		// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
		((BaseActivity) mContext).createDialog("", true);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
	}
}
