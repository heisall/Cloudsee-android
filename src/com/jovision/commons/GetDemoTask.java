package com.jovision.commons;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.test.JVACCOUNT;
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
	private String fragmentString;

	public GetDemoTask(Context con) {
		mContext = con;
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	@Override
	protected Integer doInBackground(String... params) {
		int getRes = -1;// 0成功 1失败
		sid = params[0];
		if (!Boolean.valueOf(((BaseActivity) mContext).statusHashMap
				.get(Consts.LOCAL_LOGIN))) {// 在线
			sid = JVACCOUNT.GetSession();
		} else {
			sid = "";
		}

		count = params[1];
		fragmentString = params[2];
		// demoUrl = DeviceUtil.getDemoDeviceList2(Consts.APP_NAME);
		// demoUrl = "http://www.cloudsee.net/phone.action";

		webUrl = DeviceUtil.getWebUrl();
		if (null != webUrl) {
			getRes = 0;
		} else {
			webUrl = DeviceUtil.getWebUrl();
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

			((BaseActivity) mContext).statusHashMap.put(Consts.MORE_DEMOURL,
					webUrl.getDemoUrl());
			int counts = Integer.valueOf(count);

			String lan = "";
			if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(mContext)) {
				lan = "zh_cn";
			} else if (Consts.LANGUAGE_ZHTW == ConfigUtil
					.getLanguage2(mContext)) {
				lan = "zh_tw";
			} else {
				lan = "en_us";
			}

			if (null != webUrl.getGcsUrl()) {// 获取工程商开关
				((BaseActivity) mContext).statusHashMap.put(
						Consts.MORE_GCS_SWITCH,
						String.valueOf(webUrl.getGcsSwitch()));
			}

			switch (counts) {
			case 0:// 2015-3-13从我要装监控变成工程商入驻
				String custurl;
				if (null != webUrl.getGcsUrl()) {
					Intent intentAD0 = new Intent(mContext,
							JVWebViewActivity.class);
					custurl = webUrl.getGcsUrl() + "&sid=" + sid;
					// + "?" + "&lang=" + lan + "&d="
					// + System.currentTimeMillis();
					((BaseActivity) mContext).statusHashMap.put(
							Consts.MORE_GCSURL, custurl);
					Log.i("TAG", custurl);
					intentAD0.putExtra("URL", custurl);
					intentAD0.putExtra("title", -2);
					mContext.startActivity(intentAD0);
				} else {
					((BaseActivity) mContext)
					.showTextToast(R.string.str_video_load_failed);
				}
				break;

			case 1:
				demoUrl = webUrl.getDemoUrl() + "?" + "plat=android&platv="
						+ Build.VERSION.SDK_INT + "&lang=" + lan + "&d="
						+ System.currentTimeMillis() + "&sid=" + sid;
				if (!"fragmentString".equals(fragmentString)
						&& null != webUrl.getDemoUrl()) {
					Intent intentAD = new Intent(mContext,
							JVWebViewActivity.class);
					intentAD.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intentAD.putExtra("URL", demoUrl);
					intentAD.putExtra("title", -2);
					mContext.startActivity(intentAD);
				} else if (!"fragmentString".equals(fragmentString)
						&& null == webUrl.getDemoUrl()) {
					((BaseActivity) mContext)
					.showTextToast(R.string.demo_get_failed);
				}
				break;

			case 2:// 云视通指数
				String staturl = "";
				if (null != webUrl.getStatUrl()) {
					Intent intentAD2 = new Intent(mContext,
							JVWebViewActivity.class);
					staturl = webUrl.getStatUrl() + "?" + "&lang=" + lan
							+ "&d=" + System.currentTimeMillis();
					((BaseActivity) mContext).statusHashMap.put(
							Consts.MORE_STATURL, staturl);

					Log.i("TAG", staturl);
					intentAD2.putExtra("URL", staturl);
					intentAD2.putExtra("title", -2);
					mContext.startActivity(intentAD2);
				} else {
					((BaseActivity) mContext)
					.showTextToast(R.string.str_video_load_failed);
				}

				break;
			case 3:
				String bbsurl = "";
				if (null != webUrl.getBbsUrl()) {
					Intent intentAD2 = new Intent(mContext,
							JVWebViewActivity.class);

					if (!Boolean
							.valueOf(((BaseActivity) mContext).statusHashMap
									.get(Consts.LOCAL_LOGIN))) {// 在线
						bbsurl = webUrl.getBbsUrl() + "&sid=" + sid;
						// + "&act=login";
					} else {// 本地
						bbsurl = webUrl.getBbsUrl() + "&sid=" + sid;
						// + "&act=logout";
					}

					((BaseActivity) mContext).statusHashMap.put(
							Consts.MORE_BBS, bbsurl);

					Log.i("TAG", bbsurl);
					intentAD2.putExtra("URL", bbsurl);
					intentAD2.putExtra("title", -2);
					mContext.startActivity(intentAD2);
				} else {
					((BaseActivity) mContext)
					.showTextToast(R.string.str_video_load_failed);
				}
				break;
			case 4:
				
				String bbsnum  = " ";
				if (null != webUrl.getBbsUrl()) {
					bbsnum = webUrl.getBbsUrl();
					String [] array = bbsnum.split("mod");
					if (!Boolean
							.valueOf(((BaseActivity) mContext).statusHashMap
									.get(Consts.LOCAL_LOGIN))) {
						bbsnum = array[0]+"mod=api&act=user_pm&sid="
								+ JVACCOUNT.GetSession();
					}else {
						bbsnum = array[0]+"mod=api&act=user_pm";
					}
					((BaseActivity) mContext).statusHashMap.put(
							Consts.MORE_BBSNUM, bbsnum);
				}
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
