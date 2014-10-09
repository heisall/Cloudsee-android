package com.jovision.activities;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.PushAdapter;
import com.jovision.bean.PushInfo;
import com.jovision.utils.AlarmUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.views.XListView;
import com.jovision.views.XListView.IXListViewListener;

/**
 * 消息
 */

public class JVInfoFragment extends BaseFragment implements IXListViewListener {

	private ImageView noMess;
	private int pushIndex = 0;// 推送消息index
	private XListView pushListView;// 列表
	private PushAdapter pushAdapter;

	public ArrayList<PushInfo> pushList = new ArrayList<PushInfo>();

	public ArrayList<PushInfo> temList = new ArrayList<PushInfo>();
	public static Handler mHandler;

	private boolean firstIntoPush = false;// 是否第一次进入pushmessage界面

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

		leftBtn.setOnClickListener(onClickListener);
		pushListView = (XListView) mParent.findViewById(R.id.pushlistview);
		getResources().getDrawable(R.drawable.refresh_broadcast);
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setOnClickListener(onClickListener);

		// pushAdapter.setData(pushList);
		// pushListView.setAdapter(pushAdapter);
		// // [lkp]
		// pushListView.setOnItemClickListener(mOnItemClickListener);
		// pushListView.setOnItemLongClickListener(mOnLongClickListener);
		pushAdapter = new PushAdapter(mActivity);
		pushListView.setPullLoadEnable(true);
		pushListView.setXListViewListener(this);
		noMess = (ImageView) mParent.findViewById(R.id.nomess);
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
				Consts.pushHisCount = Consts.pushHisCount + temList.size();
				temList.clear();

				if (null != pushList && 0 != pushList.size()) {
					noMess.setVisibility(View.GONE);
					pushAdapter.setData(pushList);
					pushAdapter.setRefCount(pushList.size());
					pushListView.setAdapter(pushAdapter);
					if (!firstIntoPush) {
						firstIntoPush = true;
					} else {
						pushListView.setSelection(pushAdapter.getCount());

					}
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

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		fragHandler.sendMessage(fragHandler
				.obtainMessage(what, arg1, arg2, obj));
	}

}
