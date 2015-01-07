package com.jovision.activities;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.SystemInfo;
import com.jovision.utils.DeviceUtil;
import com.jovision.views.XListView;
import com.jovision.views.XListView.IXListViewListener;

public class JVSystemInfoActivity extends BaseActivity implements
		IXListViewListener {
	private static final String TAG = "JVSystemInfoActivity";
	private static final int PAGECOUNT = 5;

	private Button back;// 左侧返回按钮
	private Button rightButton;
	private TextView currentMenu;// 当前页面名称

	private XListView infoListView;
	private LinearLayout noMessLayout;
	private ArrayList<SystemInfo> infoList = new ArrayList<SystemInfo>();

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {
		GetInfoTask task = new GetInfoTask();
		// task.execute(params);
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.systeminfo_layout);
		back = (Button) findViewById(R.id.btn_left);
		rightButton = (Button) findViewById(R.id.btn_right);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.more_modifypwd);
		rightButton.setVisibility(View.GONE);
		back.setOnClickListener(myOnClickListener);

		infoListView = (XListView) findViewById(R.id.infolistview);
		noMessLayout = (LinearLayout) findViewById(R.id.noinfolayout);

		infoListView.setPullLoadEnable(false);
		infoListView.setXListViewListener(this);

	}

	// onclick事件
	OnClickListener myOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			}
		}
	};

	// 设置三种类型参数分别为String,Integer,String
	class GetInfoTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int getRes = -1;// 0成功 1失败
			try {
				infoList = DeviceUtil.getSystemInfoList(Consts.APP_NAME, 0, 0,
						PAGECOUNT);
			} catch (Exception e) {
				e.printStackTrace();
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		if (null == infoList || 0 == infoList.size()) {
			infoListView.setVisibility(View.GONE);
		} else {
			infoListView.setVisibility(View.VISIBLE);
		}
		super.onResume();
	}

}
