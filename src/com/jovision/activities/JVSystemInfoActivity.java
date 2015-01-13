package com.jovision.activities;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.SystemInfoAdapter;
import com.jovision.bean.SystemInfo;
import com.jovision.commons.MyLog;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DeviceUtil;
import com.jovision.views.XListView;
import com.jovision.views.XListView.IXListViewListener;

public class JVSystemInfoActivity extends BaseActivity implements
		IXListViewListener {
	private static final String TAG = "JVSystemInfoActivity";
	private static final int PAGECOUNT = 5;// 每次一页加载多少条

	private SystemInfoAdapter infoAdapter;
	private XListView infoListView;
	private LinearLayout noMessLayout;
	private ArrayList<SystemInfo> infoList = new ArrayList<SystemInfo>();
	private ArrayList<SystemInfo> tempList = new ArrayList<SystemInfo>();

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.WHAT_SYSTEMINFO_REFRESH_SUCC: {// 刷新成功
			refreshLayout();
			dismissDialog();
			refreshFinished();
			break;
		}
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.systeminfo_layout);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout)findViewById(R.id.alarmnet);
		rightBtn = (Button) findViewById(R.id.btn_right);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.system_info);
		rightBtn.setVisibility(View.GONE);
		leftBtn.setOnClickListener(myOnClickListener);

		infoListView = (XListView) findViewById(R.id.infolistview);
		noMessLayout = (LinearLayout) findViewById(R.id.noinfolayout);
		infoAdapter = new SystemInfoAdapter(JVSystemInfoActivity.this);

		infoListView.setPullLoadEnable(true);// 设置上拉刷新
		infoListView.setXListViewListener(this);// 设置监听事件，重写两个方法
		infoListView.setPullRefreshEnable(true);// 设置下拉刷新

		infoListView.setVisibility(View.GONE);
		noMessLayout.setVisibility(View.GONE);
		noMessLayout.setOnClickListener(myOnClickListener);

		GetInfoTask task = new GetInfoTask();
		String[] params = new String[3];
		task.execute(params);
	}

	// onclick事件
	OnClickListener myOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left: {
				finish();
				break;
			}
			case R.id.noinfolayout: {
				GetInfoTask task = new GetInfoTask();
				String[] params = new String[3];
				task.execute(params);
				break;
			}
			}
		}
	};

	// 设置三种类型参数分别为String,Integer,String
	class GetInfoTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int getRes = -1;// 0成功 1失败

			if (null == tempList) {
				tempList = new ArrayList<SystemInfo>();
			} else {
				tempList.clear();
			}
			try {
				int lang = ConfigUtil.getLanguage2(JVSystemInfoActivity.this) - 1;
				getRes = DeviceUtil.getSystemInfoList(Consts.APP_NAME, lang,
						infoList.size(), PAGECOUNT, tempList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			MyLog.v(TAG,
					"getRes=" + getRes + ";tempList.size=" + tempList.size());
			return getRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			if (0 == result) {// 正确
				if (null == infoList) {
					infoList = new ArrayList<SystemInfo>();
				} else {
					if (null != tempList && 0 != tempList.size()) {
						infoList.addAll(tempList);
						tempList.clear();
					}
				}
			} else if (6 == result) {// 没有数据
				showTextToast(R.string.system_info_nomore);// 没有更多了
			} else {
				showTextToast(R.string.system_info_load_error);// 加载失败
			}

			refreshLayout();
			refreshFinished();

		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。
		}
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	@Override
	public void onRefresh() {
		createDialog("", false);
		handler.sendMessageDelayed(handler.obtainMessage(
				Consts.WHAT_SYSTEMINFO_REFRESH_SUCC, 0, 0, null), 1000);
	}

	/**
	 * 刷新完成
	 */
	private void refreshFinished() {
		infoListView.stopRefresh();
		infoListView.stopLoadMore();
		infoListView.setRefreshTime(ConfigUtil.getCurrentTime());
		dismissDialog();
	}

	@Override
	public void onLoadMore() {
		GetInfoTask task = new GetInfoTask();
		String[] params = new String[3];
		task.execute(params);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void refreshLayout() {
		if (null == infoList || 0 == infoList.size()) {
			infoListView.setVisibility(View.GONE);
			noMessLayout.setVisibility(View.VISIBLE);
		} else {
			infoListView.setVisibility(View.VISIBLE);
			infoAdapter.setData(infoList);
			infoListView.setAdapter(infoAdapter);
			noMessLayout.setVisibility(View.GONE);
		}
	}
}
