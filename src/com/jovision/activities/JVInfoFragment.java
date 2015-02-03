package com.jovision.activities;

import java.util.ArrayList;

import neo.droid.p2r.PullToRefreshBase;
import neo.droid.p2r.PullToRefreshBase.Mode;
import neo.droid.p2r.PullToRefreshBase.OnRefreshListener2;
import neo.droid.p2r.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.adapters.PushAdapter;
import com.jovision.bean.PushInfo;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVAlarmConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AlarmUtil;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.views.MyAlertDialog;
import com.tencent.stat.StatService;

/**
 * 消息
 */

public class JVInfoFragment extends BaseFragment {
	private static final String TAG = "JVInfoFragment";
	private ImageView noMess;
	private TextView noMessTv;
	private int pushIndex = 0;// 推送消息index
	// private XListView pushListView;// 列表
	private PullToRefreshListView mPullRefreshListView;
	private ListView pushListView;
	private PushAdapter pushAdapter;
	private View rootView;// 缓存Fragment view
	private ArrayList<PushInfo> pushList = new ArrayList<PushInfo>();

	private ArrayList<PushInfo> temList = new ArrayList<PushInfo>();
	private boolean pullUp = false;
	private boolean bfirstrun = true;
	private MyAlertDialog alertDialog;
	private MainApplication mApp = null;
	public static boolean isshow;

	// private boolean firstIntoPush = false;// 是否第一次进入pushmessage界面

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_info, container,
					false);
			bfirstrun = true;
		} else {
			bfirstrun = false;
		}
		mApp = (MainApplication) getActivity().getApplication();
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		alertDialog = new MyAlertDialog(getActivity());
		alertDialog.setTitle(getResources().getString(R.string.str_delete_tip));
		alertDialog.setContent(R.string.str_alarm_clear_ainfo_sure);
		alertDialog.setConfirmClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (pushList.size() == 0) {
					// mActivity.showTextToast(R.string.str_alarm_no_alarm_info);
				} else {
					StatService.trackCustomEvent(
							mActivity,
							"ClearAlarmMessage",
							mActivity.getResources().getString(
									R.string.census_clearalarmmessage));
					mActivity.createDialog("", true);
					ClearAlarmTask task = new ClearAlarmTask();
					String[] params = new String[3];
					task.execute(params);
				}
				alertDialog.dismiss();
			}

		});
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mActivity = (BaseActivity) getActivity();
		// rightBtn.setVisibility(View.GONE);
		currentMenu.setText(mActivity.getResources().getString(
				R.string.str_alarm_info));

		mPullRefreshListView = (PullToRefreshListView) mParent
				.findViewById(R.id.pushlistview);
		mActivity.getResources().getDrawable(R.drawable.refresh_broadcast);
		rightBtn.setBackgroundResource(R.drawable.clear);
		rightBtn.setOnClickListener(onClickListener);

		// pushAdapter.setData(pushList);
		// pushListView.setAdapter(pushAdapter);
		// // [lkp]
		// pushListView.setOnItemClickListener(mOnItemClickListener);
		// pushListView.setOnItemLongClickListener(mOnLongClickListener);
		pushAdapter = new PushAdapter(this);
		if (!Boolean.valueOf(mActivity.statusHashMap.get(Consts.LOCAL_LOGIN))) {// 非本地登录才加载报警信息
			// pushListView.setPullLoadEnable(true);
			// pushListView.setXListViewListener(this);
			rightBtn.setVisibility(View.VISIBLE);
			// pushListView.setVisibility(View.VISIBLE);
		} else {
			// pushListView.setPullLoadEnable(false);
			// pushListView.setPullRefreshEnable(false);
			rightBtn.setVisibility(View.GONE);
			// pushListView.setVisibility(View.INVISIBLE);
		}
		pushListView = mPullRefreshListView.getRefreshableView();
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						// 下拉
						if (!Boolean.valueOf(mActivity.statusHashMap
								.get(Consts.LOCAL_LOGIN))) {// 非本地登录才加载报警信息
							pullUp = false;
							mActivity.createDialog("", true);
							PullDownRefreshAlarmTask task = new PullDownRefreshAlarmTask();
							String[] params = new String[3];
							task.execute(params);
						} else {
							mPullRefreshListView.onRefreshComplete();
						}
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						// 上拉
						if (!Boolean.valueOf(mActivity.statusHashMap
								.get(Consts.LOCAL_LOGIN))) {// 非本地登录才加载报警信息
							pullUp = true;
							mActivity.createDialog("", true);
							PullUpRefreshAlarmTask task = new PullUpRefreshAlarmTask();
							String[] params = new String[3];
							task.execute(params);
						} else {
							mPullRefreshListView.onRefreshComplete();
						}
					}

				});
		noMess = (ImageView) mParent.findViewById(R.id.nomess);
		noMessTv = (TextView) mParent.findViewById(R.id.nomess_tv);
		noMess.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (!Boolean.valueOf(mActivity.statusHashMap
						.get(Consts.LOCAL_LOGIN))) {// 非本地登录才加载报警信息

					mActivity.createDialog("", true);
					PullDownRefreshAlarmTask task = new PullDownRefreshAlarmTask();
					String[] params = new String[3];
					task.execute(params);
				}
				return false;
			}
		});

		if (!Boolean.valueOf(mActivity.statusHashMap.get(Consts.LOCAL_LOGIN))) {// 非本地登录才加载报警信息
			// Consts.pushHisCount = 0;
			// pushList.clear();
			mActivity.createDialog("", true);
			PullDownRefreshAlarmTask task = new PullDownRefreshAlarmTask();
			String[] params = new String[3];
			task.execute(params);
		}

	}

	// // 上拉加载更多推送消息
	// private void onLoadPush() {
	// pushListView.stopRefresh();
	// pushListView.stopLoadMore();
	// pushListView.setRefreshTime(ConfigUtil.getCurrentTime());
	// }

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_right:
				alertDialog.show();
				break;
			case R.id.btn_left:
				mActivity.finish();
				break;
			}
		}
	};

	// 刷新报警信息线程
	class PullDownRefreshAlarmTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int refreshRes = -1;
			try {
				temList = AlarmUtil.getUserAlarmList(0, Consts.PUSH_PAGESIZE);
				if (temList == null) {
					// null肯定失败,不为null一定成功，可能信息为0,因为只有成功才会new
					// list，因此temList肯定不为null
					refreshRes = 0;
				} else {
					refreshRes = 1;
				}
				// if (null != temList && 0 != temList.size()) {
				// refreshRes = 1;
				// } else {
				// refreshRes = 0;
				// }

			} catch (Exception e) {
				e.printStackTrace();
			}
			return refreshRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			mActivity.dismissDialog();
			if (1 == result) {
				mPullRefreshListView.onRefreshComplete();
				if (null == temList) {
					temList = new ArrayList<PushInfo>();
				}
				if (temList.size() == 0) {
					noMess.setVisibility(View.VISIBLE);
					noMessTv.setVisibility(View.VISIBLE);
					mActivity.showTextToast(R.string.nomessage);
					pushList.clear();
					Consts.pushHisCount = 0;
					pushAdapter.setRefCount(pushList.size());
					pushListView.setAdapter(pushAdapter);
					pushAdapter.notifyDataSetChanged();
					return;
				}
				int addLen = temList.size();
				// 默认刷新下来的报警信息都是新的
				for (int j = 0; j < temList.size(); j++) {
					temList.get(j).newTag = true;
				}
				// 保存状态，例如新消息。否则刷新后新消息的标志就木有了。
				// for (int i = 0; i < temList.size(); i++) {
				// for (int j = 0; j < pushList.size(); j++) {
				// if (pushList.get(j).strGUID.equalsIgnoreCase(temList
				// .get(i).strGUID)) {
				// temList.get(i).newTag = pushList.get(j).newTag;
				// }
				// }
				// }
				if (pushListView.getVisibility() == View.INVISIBLE) {
					pushListView.setVisibility(View.VISIBLE);
				}
				pushList.clear();
				pushList.addAll(temList);
				Consts.pushHisCount = addLen;
				temList.clear();

				if (null != pushList && 0 != pushList.size()) {
					noMess.setVisibility(View.GONE);
					noMessTv.setVisibility(View.GONE);
					pushAdapter.setData(pushList);
					pushAdapter.setRefCount(pushList.size());
					pushListView.setAdapter(pushAdapter);
					if (pullUp) {
						pushListView.setSelection(pushAdapter.getCount());
					} else {
						pushListView.setSelection(0);
					}
					pushAdapter.notifyDataSetChanged();
				}
			} else {
				mPullRefreshListView.onRefreshComplete();
				mActivity.showTextToast(R.string.get_alarm_list_failed);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			mActivity.createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	// 上拉加载报警信息线程
	class PullUpRefreshAlarmTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int refreshRes = -1;
			try {
				temList = AlarmUtil.getUserAlarmList(Consts.pushHisCount + 1,
						Consts.PUSH_PAGESIZE);
				if (temList == null) {
					// null肯定失败,不为null一定成功，可能信息为0,因为只有成功才会new
					// list，因此temList肯定不为null
					refreshRes = 0;
				} else {
					refreshRes = 1;
				}
				// if (null != temList && 0 != temList.size()) {
				// refreshRes = 1;
				// } else {
				// refreshRes = 0;
				// }

			} catch (Exception e) {
				e.printStackTrace();
			}
			return refreshRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			mActivity.dismissDialog();
			if (1 == result) {
				mPullRefreshListView.onRefreshComplete();
				if (null == temList) {
					temList = new ArrayList<PushInfo>();
				}
				if (temList.size() == 0) {
					mActivity.showTextToast(R.string.xlistview_no_more);
					return;
				}
				int addLen = temList.size();
				// 默认刷新下来的报警信息都是新的
				for (int j = 0; j < temList.size(); j++) {
					temList.get(j).newTag = true;
				}
				pushList.addAll(temList);
				Consts.pushHisCount = Consts.pushHisCount + addLen;
				temList.clear();

				if (null != pushList && 0 != pushList.size()) {
					noMess.setVisibility(View.GONE);
					noMessTv.setVisibility(View.GONE);
					pushAdapter.setData(pushList);
					pushAdapter.setRefCount(pushList.size());
					pushListView.setAdapter(pushAdapter);
					if (pullUp) {
						pushListView.setSelection(pushAdapter.getCount());
					} else {
						pushListView.setSelection(0);
					}
					pullUp = false;
					pushAdapter.notifyDataSetChanged();
				}
			} else {
				mActivity.showTextToast(R.string.get_alarm_list_failed);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			mActivity.createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	@Override
	public void onPause() {
		MySharedPreference.putString("MARKED_ALARM",
				ConfigUtil.convertToString(mApp.getMarkedAlarmList()));
		super.onPause();
	}

	@Override
	public void onDestroy() {
		isshow = false;
		super.onDestroy();
	}

	@Override
	public void onResume() {
		isshow = true;
		if (Boolean.valueOf(mActivity.statusHashMap.get(Consts.LOCAL_LOGIN))) {
			super.onResume();
			return;
		}
		String arrayStr = mActivity.statusHashMap.get(Consts.PUSH_JSONARRAY);
		mActivity.statusHashMap.put(Consts.PUSH_JSONARRAY, "");
		String checkAlarmGuid = MySharedPreference
				.getString(Consts.CHECK_ALARM_KEY);
		if (null != checkAlarmGuid && !checkAlarmGuid.equals("")) {
			if (!mApp.getMarkedAlarmList().contains(checkAlarmGuid)) {
				mApp.getMarkedAlarmList().add(checkAlarmGuid);
			}
		}

		JSONArray pushArray = null;
		try {
			if (null == arrayStr || "".equalsIgnoreCase(arrayStr)) {
				pushArray = new JSONArray();
			} else {
				pushArray = new JSONArray(arrayStr);
			}

			int length = pushArray.length();
			if (0 != length) {
				if (pushListView.getVisibility() == View.INVISIBLE) {
					pushListView.setVisibility(View.VISIBLE);
				}
				for (int i = 0; i < length; i++) {
					try {
						JSONObject obj = pushArray.getJSONObject(i);
						PushInfo pi = new PushInfo();
						pi.strGUID = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_GUID);
						pi.ystNum = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_CLOUDNUM);
						pi.coonNum = obj
								.optInt(JVAlarmConst.JK_ALARM_NEW_CLOUDCHN);
						pi.alarmSolution = obj
								.optInt(JVAlarmConst.JK_ALARM_SOLUTION);
						//
						// pi.deviceNickName = BaseApp.getNikeName(pi.ystNum);
						pi.alarmType = obj
								.optInt(JVAlarmConst.JK_ALARM_NEW_ALARMTYPE);
						if (pi.alarmType == 7 || pi.alarmType == 4) {
							pi.deviceNickName = obj
									.optString(JVAlarmConst.JK_ALARM_NEW_CLOUDNAME);
						} else if (pi.alarmType == 11)// 第三方
						{
							pi.deviceNickName = obj
									.optString(JVAlarmConst.JK_ALARM_NEW_ALARM_THIRD_NICKNAME);
						} else {

						}

						// ArrayList<Device> deviceList =
						// CacheUtil.getDevList();// 再取一次
						// int dev_index = DeviceUtil.getDeivceIndex(pi.ystNum);
						String deviceNickName = CacheUtil
								.getNickNameByYstfn(pi.ystNum);
						if (deviceNickName == null || deviceNickName.equals("")) {
							deviceNickName = pi.deviceNickName;
						} else {
							// deviceNickName = deviceList.get(dev_index)
							// .getNickName();
							if (pi.alarmType == 11)// 第三方
							{
								deviceNickName = deviceNickName + "-"
										+ pi.deviceNickName;
							}
						}
						pi.deviceNickName = deviceNickName;

						// pi.timestamp = obj
						// .optString(JVAlarmConst.JK_ALARM_NEW_ALARMTIME);
						// pi.alarmTime = AlarmUtil.getStrTime(pi.timestamp);
						String strTempTime = "";
						strTempTime = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_ALARMTIME_STR);
						if (!strTempTime.equals("")) {
							// 有限取这个字段的时间，格式：yyyyMMddhhmmss
							pi.timestamp = "";
							pi.alarmTime = AlarmUtil.formatStrTime(strTempTime);
						} else {
							pi.timestamp = obj
									.optString(JVAlarmConst.JK_ALARM_NEW_ALARMTIME);
							pi.alarmTime = AlarmUtil.getStrTime(pi.timestamp);
						}
						pi.deviceName = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_CLOUDNAME);
						pi.newTag = true;
						pi.pic = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_PICURL);
						pi.messageTag = JVAccountConst.MESSAGE_NEW_PUSH_TAG;
						pi.video = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_VIDEOURL);
						RemoveRepetitiveAlarmInfoFromPushList(pi.strGUID);// 先删除重复的
						pushList.add(0, pi);// 新消息置顶
						Consts.pushHisCount++;
						noMess.setVisibility(View.GONE);
						noMessTv.setVisibility(View.GONE);
						pushAdapter.setData(pushList);
						pushAdapter.setRefCount(pushList.size());
						pushListView.setAdapter(pushAdapter);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				pushListView.setSelection(0);
				pushAdapter.notifyDataSetChanged();
			}

			// mActivity.statusHashMap.put(Consts.PUSH_JSONARRAY, "");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		super.onResume();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
		}
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.WHAT_DELETE_ALARM_MESS: {// 删除报警
			pushIndex = arg1;

			DelAlarmTask task = new DelAlarmTask();
			Integer[] params = new Integer[3];
			params[0] = pushIndex;
			task.execute(params);
		}
			break;
		case Consts.WHAT_PUSH_MESSAGE:
			// 弹出对话框
			if (null != mActivity) {
				// new AlarmDialog(mActivity).Show(obj);
				mApp.setNewPushCnt(0);
				onResume();
			} else {
				MyLog.e("Alarm",
						"onHandler mActivity is null ,so dont show the alarm dialog");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		fragHandler.sendMessage(fragHandler
				.obtainMessage(what, arg1, arg2, obj));
	}

	// 设置三种类型参数分别为String,Integer,String
	class DelAlarmTask extends AsyncTask<Integer, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(Integer... params) {
			int delRes = -1;
			if (null != pushList && 0 != pushList.size()
					&& params[0] < pushList.size()) {
				String deleteGuid = pushList.get(params[0]).strGUID;
				delRes = AlarmUtil
						.deleteAlarmInfo(
								mActivity.statusHashMap.get("KEY_USERNAME"),
								deleteGuid);
			}
			return delRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			if (result == 0) {
				if (null != pushList && 0 != pushList.size()
						&& pushIndex < pushList.size()) {
					String deleteGuid = pushList.get(pushIndex).strGUID;
					boolean doDelete = false;
					for (int i = 0; i < pushList.size(); i++) {
						if (deleteGuid
								.equalsIgnoreCase(pushList.get(i).strGUID)) {
							pushList.remove(i);
							doDelete = true;
							break;
						}
					}
					if (!doDelete) {
						mActivity.showTextToast(R.string.del_alarm_failed);
					} else {
						Consts.pushHisCount--;
						pushAdapter.setRefCount(pushList.size());
						pushAdapter.notifyDataSetChanged();
						mActivity.showTextToast(R.string.del_alarm_succ);

						if (Consts.pushHisCount == 0) {
							mActivity.createDialog("", true);
							PullDownRefreshAlarmTask task = new PullDownRefreshAlarmTask();
							String[] params = new String[3];
							task.execute(params);
						}
					}
				}
			} else {
				mActivity.showTextToast(R.string.del_alarm_failed);
			}

			mActivity.dismissDialog();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			mActivity.createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	private void RemoveRepetitiveAlarmInfoFromPushList(String dstAgid) {
		for (int i = 0; i < pushList.size(); i++) {
			PushInfo pushInfo = pushList.get(i);
			if (pushInfo.strGUID.equalsIgnoreCase(dstAgid)) {
				pushList.remove(i);
				Consts.pushHisCount--;
			}
		}

	}

	// 设置三种类型参数分别为String,Integer,String
	class ClearAlarmTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int ret = -1;
			ret = AlarmUtil.clearAlarmInfo();
			return ret;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			if (result == 0) {
				pushList.clear();
				Consts.pushHisCount = 0;
				pushAdapter.setRefCount(0);
				pushAdapter.setData(pushList);
				pushAdapter.notifyDataSetChanged();
				pushListView.setVisibility(View.INVISIBLE);
				mActivity.showTextToast(R.string.clear_alarm_succ);
				noMess.setVisibility(View.VISIBLE);
				noMessTv.setVisibility(View.VISIBLE);
				mApp.getMarkedAlarmList().clear();
			} else {
				mActivity.showTextToast(R.string.clear_alarm_failed);
			}

			mActivity.dismissDialog();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			mActivity.createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}
}
