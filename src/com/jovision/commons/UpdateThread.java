package com.jovision.commons;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.jovetech.CloudSee.temp.R;
import com.jovision.utils.JSONUtil;

public class UpdateThread extends Thread {
	Context context;
	int activityFlag;// JVMAIN_ACTIVITY_FLAG,JVMoreFeatureActivity

	public UpdateThread(Context arg1, int arg2) {
		context = arg1;
		activityFlag = arg2;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		String language = "";
		String lan = "1";
		language = Locale.getDefault().getLanguage();
		if (language.equalsIgnoreCase("zh")) {// 中文
			lan = "1";
		} else {// 英文
			lan = "2";
		}
		try {

			// HashMap<String, String> map = new HashMap<String, String>();
			// map.put("Language", String.valueOf(lan));
			// map.put("RequestType", "3");//3代表android
			// map.put("Version",
			// getResources().getString(R.string.str_update_app_version));//中维版本值为1，oem版为2

			JSONArray array = JSONUtil.getJSON(Url.CHECK_UPDATE_URL
					+ "?Language=" + String.valueOf(lan)
					+ "&"
					+ "RequestType=3&"
					+ "MobileType=1&"// 1 代表android
					+ "Version="
					+ context.getResources().getString(
							R.string.str_update_app_version));
			int curVersion = context.getPackageManager()
					.getPackageInfo(
							context.getResources().getString(
									R.string.str_package_name), 0).versionCode;
			String versionCode = "";
			String fileSize = "";
			String versionStr = "";

			if (null != array && 0 != array.length()) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject temp = (JSONObject) array.get(i);
					if (-1 == Integer.parseInt(temp.getString("Num"))) {
						versionCode = temp.getString("Content");
					} else if (0 == Integer.parseInt(temp.getString("Num"))) {
						fileSize = temp.getString("Content");
					} else if (1 == Integer.parseInt(temp.getString("Num"))) {
						versionStr = temp.getString("Content");
					}
				}
			} else {
				// 进程序自动检查更新
				if (activityFlag == JVConst.JVMAIN_ACTIVITY_FLAG) {
					// BaseApp.mainHandler
					// .sendEmptyMessage(BaseApp.mainHandler
					// .obtainMessage().what = JVConst.CHECK_UPDATE_FAILED);//
					// 检查更新失败
					// 手动检查更新
				} else if (activityFlag == JVConst.JVMORE_ACTIVITY_FLAG) {
					// BaseApp.moreHandler
					// .sendEmptyMessage(BaseApp.moreHandler
					// .obtainMessage().what = JVConst.CHECK_UPDATE_FAILED);//
					// 检查更新失败
				}

			}

			versionStr = versionStr.replaceAll("&", "\n");
			if (null != versionCode && !"".equals(versionCode)) {
				if (curVersion < Integer.parseInt(versionCode)) {
					// 进程序自动检查更新
					if (activityFlag == JVConst.JVMAIN_ACTIVITY_FLAG) {
						// Message msg = BaseApp.mainHandler.obtainMessage();
						// msg.what = JVConst.CHECK_UPDATE_SUCCESS;
						// Bundle bundle = new Bundle();
						// bundle.putInt("versionCode",
						// Integer.parseInt(versionCode));
						// bundle.putString("packetSize", fileSize);
						// bundle.putString("updateContent", versionStr);
						// msg.setData(bundle);
						// BaseApp.mainHandler.sendMessage(msg);// 有更新
						// // 手动检查更新
					} else if (activityFlag == JVConst.JVMORE_ACTIVITY_FLAG) {
						// Message msg = BaseApp.moreHandler.obtainMessage();
						// msg.what = JVConst.CHECK_UPDATE_SUCCESS;
						// Bundle bundle = new Bundle();
						// bundle.putInt("versionCode",
						// Integer.parseInt(versionCode));
						// bundle.putString("packetSize", fileSize);
						// bundle.putString("updateContent", versionStr);
						// msg.setData(bundle);
						// BaseApp.moreHandler.sendMessage(msg);// 有更新
					}

				} else {
					// // 进程序自动检查更新
					// if (activityFlag == JVConst.JVMAIN_ACTIVITY_FLAG) {
					// BaseApp.mainHandler
					// .sendEmptyMessage(BaseApp.mainHandler
					// .obtainMessage().what = JVConst.CHECK_NO_UPDATE);// 没有更新
					// // 手动检查更新
					// } else if (activityFlag == JVConst.JVMORE_ACTIVITY_FLAG)
					// {
					// BaseApp.moreHandler
					// .sendEmptyMessage(BaseApp.moreHandler
					// .obtainMessage().what = JVConst.CHECK_NO_UPDATE);// 没有更新
					// }

				}
			} else {
				// 进程序自动检查更新
				// if (activityFlag == JVConst.JVMAIN_ACTIVITY_FLAG) {
				// BaseApp.mainHandler
				// .sendEmptyMessage(BaseApp.mainHandler
				// .obtainMessage().what = JVConst.CHECK_UPDATE_FAILED);//
				// 检查更新失败
				// // 手动检查更新
				// } else if (activityFlag == JVConst.JVMORE_ACTIVITY_FLAG) {
				// BaseApp.moreHandler
				// .sendEmptyMessage(BaseApp.moreHandler
				// .obtainMessage().what = JVConst.CHECK_UPDATE_FAILED);//
				// 检查更新失败
				// }

			}
		} catch (Exception e) {
			// TODO: handle exception
			// // 进程序自动检查更新
			// if (activityFlag == JVConst.JVMAIN_ACTIVITY_FLAG) {
			// BaseApp.mainHandler.sendEmptyMessage(BaseApp.mainHandler
			// .obtainMessage().what = JVConst.CHECK_NO_UPDATE);// 没有更新
			//
			// // 手动检查更新
			// } else if (activityFlag == JVConst.JVMORE_ACTIVITY_FLAG) {
			// BaseApp.moreHandler.sendEmptyMessage(BaseApp.moreHandler
			// .obtainMessage().what = JVConst.CHECK_NO_UPDATE);// 没有更新
			// }

		}

	}

}
