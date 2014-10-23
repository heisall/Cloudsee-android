package com.jovision.activities;

import java.util.ArrayList;

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
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.PushAdapter;
import com.jovision.bean.PushInfo;
import com.jovision.commons.JVAccountConst;
import com.jovision.commons.JVAlarmConst;
import com.jovision.utils.AlarmUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.views.AlarmDialog;
import com.jovision.views.XListView;
import com.jovision.views.XListView.IXListViewListener;

/**
 * 消息
 */

public class JVInfoFragment extends BaseFragment implements IXListViewListener {
	private static final String TAG = "JVInfoFragment";

	private ImageView noMess;
	private TextView noMessTv;
	private int pushIndex = 0;// 推送消息index
	private XListView pushListView;// 列表
	private PushAdapter pushAdapter;

	private ArrayList<PushInfo> pushList = new ArrayList<PushInfo>();

	private ArrayList<PushInfo> temList = new ArrayList<PushInfo>();
	private boolean pullUp = false;

	// private boolean firstIntoPush = false;// 是否第一次进入pushmessage界面

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_info, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mActivity = (BaseActivity) getActivity();
		rightBtn.setVisibility(View.GONE);
		currentMenu.setText(getResources().getString(R.string.str_alarm_info));

		pushListView = (XListView) mParent.findViewById(R.id.pushlistview);
		getResources().getDrawable(R.drawable.refresh_broadcast);
		rightBtn.setVisibility(View.GONE);
		rightBtn.setOnClickListener(onClickListener);

		// pushAdapter.setData(pushList);
		// pushListView.setAdapter(pushAdapter);
		// // [lkp]
		// pushListView.setOnItemClickListener(mOnItemClickListener);
		// pushListView.setOnItemLongClickListener(mOnLongClickListener);
		pushAdapter = new PushAdapter(this);
		pushListView.setPullLoadEnable(true);
		pushListView.setXListViewListener(this);
		noMess = (ImageView) mParent.findViewById(R.id.nomess);
		noMessTv = (TextView) mParent.findViewById(R.id.nomess_tv);
		noMess.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (!Boolean.valueOf(mActivity.statusHashMap
						.get(Consts.LOCAL_LOGIN))) {// 非本地登录才加载报警信息

					((BaseActivity) mActivity).createDialog("");
					RefreshAlarmTask task = new RefreshAlarmTask();
					String[] params = new String[3];
					task.execute(params);
				}
				return false;
			}
		});

		// 先获取报警信息
		if (!Boolean.valueOf(mActivity.statusHashMap.get(Consts.LOCAL_LOGIN))) {// 非本地登录才加载报警信息

			((BaseActivity) mActivity).createDialog("");
			RefreshAlarmTask task = new RefreshAlarmTask();
			String[] params = new String[3];
			task.execute(params);
		}

	}

	// 上拉加载更多推送消息
	private void onLoadPush() {
		pushListView.stopRefresh();
		pushListView.stopLoadMore();
		pushListView.setRefreshTime(getResources().getString(
				R.string.str_update_at)
				+ ConfigUtil.getCurrentTime());
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_right:
				((BaseActivity) mActivity).createDialog("");
				RefreshAlarmTask task = new RefreshAlarmTask();
				String[] params = new String[3];
				task.execute(params);
				break;
			}
		}
	};

	// 下拉刷新
	@Override
	public void onRefresh() {
		fragHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				pushAdapter.setRefCount(5);
				pushAdapter.notifyDataSetChanged();
				onLoadPush();
			}
		}, 1000);
	}

	// 上拉加载更多
	@Override
	public void onLoadMore() {
		pullUp = true;
		((BaseActivity) mActivity).createDialog("");
		RefreshAlarmTask task = new RefreshAlarmTask();
		String[] params = new String[3];
		task.execute(params);
	}

	// 刷新报警信息线程
	class RefreshAlarmTask extends AsyncTask<String, Integer, Integer> {// A,361,2000
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int refreshRes = -1;
			try {
				temList = AlarmUtil.getUserAlarmList(Consts.pushHisCount,
						Consts.PUSH_PAGESIZE);

				if (null != temList && 0 != temList.size()) {
					refreshRes = 1;
				} else {
					refreshRes = 0;
				}

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
			((BaseActivity) mActivity).dismissDialog();
			if (1 == result) {
				onLoadPush();
				if (null == temList) {
					temList = new ArrayList<PushInfo>();
				}

				int addLen = temList.size();
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
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			((BaseActivity) mActivity).createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	@Override
	public void onResume() {
		String arrayStr = mActivity.statusHashMap.get(Consts.PUSH_JSONARRAY);
		JSONArray pushArray = null;
		try {
			if (null == arrayStr || "".equalsIgnoreCase(arrayStr)) {
				pushArray = new JSONArray();
			} else {
				pushArray = new JSONArray(arrayStr);
			}

			int length = pushArray.length();
			if (0 != length) {
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
						//
						// pi.deviceNickName = BaseApp.getNikeName(pi.ystNum);
						pi.deviceNickName = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_CLOUDNAME);
						pi.alarmType = obj
								.optInt(JVAlarmConst.JK_ALARM_NEW_ALARMTYPE);
						pi.timestamp = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_ALARMTIME);
						pi.alarmTime = AlarmUtil.getStrTime(pi.timestamp);

						pi.deviceName = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_CLOUDNAME);
						pi.newTag = true;
						pi.pic = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_PICURL);
						pi.messageTag = JVAccountConst.MESSAGE_NEW_PUSH_TAG;
						pi.video = obj
								.optString(JVAlarmConst.JK_ALARM_NEW_VIDEOURL);
						pushList.add(0, pi);// 新消息置顶
						Consts.pushHisCount++;
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			pushListView.setSelection(0);
			pushAdapter.notifyDataSetChanged();
			mActivity.statusHashMap.put(Consts.PUSH_JSONARRAY, "");
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
		case PushAdapter.DELETE_ALARM_MESS: {// 删除报警
			pushIndex = arg1;

			DelAlarmTask task = new DelAlarmTask();
			Integer[] params = new Integer[3];
			params[0] = pushIndex;
			task.execute(params);
		}
			break;
		case Consts.PUSH_MESSAGE:
			// 弹出对话框
			AlarmDialog.getInstance(getActivity()).Show(obj);

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
			String deleteGuid = pushList.get(params[0]).strGUID;
			delRes = AlarmUtil.deleteAlarmInfo(
					mActivity.statusHashMap.get("KEY_USERNAME"), deleteGuid);
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
				String deleteGuid = pushList.get(pushIndex).strGUID;
				boolean doDelete = false;
				for (int i = 0; i < pushList.size(); i++) {
					if (deleteGuid.equalsIgnoreCase(pushList.get(i).strGUID)) {
						pushList.remove(i);
						doDelete = true;
						break;
					}
				}
				if (!doDelete) {
					mActivity.showTextToast(R.string.del_alarm_failed);
				} else {
					pushAdapter.setRefCount(pushList.size());
					pushAdapter.notifyDataSetChanged();
					mActivity.showTextToast(R.string.del_alarm_succ);
				}
			} else {
				mActivity.showTextToast(R.string.del_alarm_failed);
			}

			((BaseActivity) mActivity).dismissDialog();
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			((BaseActivity) mActivity).createDialog("");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

}
