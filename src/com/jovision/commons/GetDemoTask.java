package com.jovision.commons;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.JVWebViewActivity;
import com.jovision.bean.WebUrl;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;

//获取演示点 设置三种类型参数分别为String,Integer,String
public class GetDemoTask extends AsyncTask<String, Integer, Integer> {
	private Context mContext;
	private String demoUrl;
	private WebUrl webUrl;
	private String sid;
	private String count;

	public GetDemoTask(Context con) {
		mContext = con;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	@Override
	protected Integer doInBackground(String... params) {
		int getRes = -1;// 0成功 1失败
		sid = params[0];
		count = params[1];
		// demoUrl = DeviceUtil.getDemoDeviceList2(Consts.APP_NAME);
		// demoUrl = "http://www.cloudsee.net/phone.action";
		webUrl = DeviceUtil.getWebUrl();
		if (null != webUrl) {
			getRes = 0;
			((BaseActivity) mContext).statusHashMap.put("DEMOURL",
					webUrl.getDemoUrl());
			((BaseActivity) mContext).statusHashMap.put("CUSTURL",
					webUrl.getCustUrl());
			((BaseActivity) mContext).statusHashMap.put("STATURL",
					webUrl.getStatUrl());
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
			int counts = Integer.valueOf(count);
			switch (counts) {
			case 0:
				Intent intentAD0 = new Intent(mContext, JVWebViewActivity.class);
				intentAD0.putExtra("URL", webUrl.getCustUrl());
				intentAD0.putExtra("title", -2);
				mContext.startActivity(intentAD0);
				break;
			case 2:
				Intent intentAD2 = new Intent(mContext, JVWebViewActivity.class);
				intentAD2.putExtra("URL", webUrl.getStatUrl());
				intentAD2.putExtra("title", -2);
				mContext.startActivity(intentAD2);
				break;
			case 1:
				Log.i("TAG", webUrl.getDemoUrl());
				Intent intentAD = new Intent(mContext, JVWebViewActivity.class);
				String lan = "";
				if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(mContext)) {
					lan = "zh_cn";
				} else if (Consts.LANGUAGE_ZHTW == ConfigUtil
						.getLanguage2(mContext)) {
					lan = "zh_tw";
				} else {
					lan = "en_us";
				}
				demoUrl = webUrl.getDemoUrl() + "?" + "plat=android&platv="
						+ Build.VERSION.SDK_INT + "&lang=" + lan + "&d="
						+ System.currentTimeMillis() + "&sid=" + sid;
				MyLog.v("demoUrl", demoUrl);
				intentAD.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intentAD.putExtra("URL", demoUrl);
				intentAD.putExtra("title", -2);
				mContext.startActivity(intentAD);
				break;
			default:
				break;
			}
			// http://192.168.10.17:8080/WebPlatform/mobile/index.html?plat=A&platv=B&lang=C&sid=D
			// A：取值范围:iphone/ipad/android/
			// B：平台版本号,android系统版本
			// C：语言，参考如下连接中的各语言简写，如简体中文为：zh_cn，http://www.douban.com/group/topic/37393602/
			// D：登录会话号：未登录则为空
			//
			// 以上参数顺序没有要求，大小写没有要求
			//
			// 注意：aaa.html后为"?",参数之间使用"&"进行连接
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
