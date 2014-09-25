package com.jovision.activities;

import android.R;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jovision.adapters.PushAdapter;
import com.jovision.commons.BaseApp;
import com.jovision.commons.JVConst;

public class JVNoticeActivity extends BaseActivity {

	private Button back;// 返回按钮
	private TextView currentMenu;// 当前页面名称
	private Button refresh;// 保存按钮

	private ListView pushListView;// 列表
	private PushAdapter pushAdapter;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case JVConst.REFRESH_PUSH_MSG:// 有消息过来刷新界面
			pushAdapter.notifyDataSetChanged();
			break;
		case JVConst.REFRESH_PUSH_MSG_BTN:// 刷新按钮
			pushAdapter.notifyDataSetChanged();
			dismissDialog();
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void initUi() {
		setContentView(R.layout.notice_layout);
		back = (Button) findViewById(R.id.back);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(getResources().getString(R.string.str_alarm_info));

		back.setOnClickListener(onClickListener);
		pushListView = (ListView) findViewById(R.id.pushlistview);
		refresh = (Button) findViewById(R.id.btn_right);
		refresh.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.refresh_broadcast));
		refresh.setVisibility(View.VISIBLE);
		refresh.setOnClickListener(onClickListener);

		pushAdapter = new PushAdapter(JVNoticeActivity.this);
		pushAdapter.setData(BaseApp.pushList);
		pushListView.setAdapter(pushAdapter);
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:
				JVNoticeActivity.this.finish();
				break;
			case R.id.btn_right:
				if (null == proDialog) {
					proDialog = new ProgressDialog(JVNoticeActivity.this);
				}
				proDialog.setMessage(getResources().getString(
						R.string.str_refreshing_data));
				proDialog.setCancelable(false);
				proDialog.show();
				Thread thread = new Thread() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler.sendMessage(handler
								.obtainMessage(JVConst.REFRESH_PUSH_MSG_BTN));
						super.run();
					}

				};
				thread.start();
				break;
			}
		}
	};

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
