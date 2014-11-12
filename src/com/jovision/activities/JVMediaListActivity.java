package com.jovision.activities;

import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.MediaFolderAdapter;

public class JVMediaListActivity extends BaseActivity {

	private String media = "";
	private String mediaPath = "";
	private boolean noFile = true;
	private ArrayList<File> fileList = new ArrayList<File>();
	private MediaFolderAdapter mfAdapter;

	/** topBar **/
	private Button back;// 左侧返回按钮
	private TextView currentMenu;// 当前页面名称
	private Button rigButton;

	private LinearLayout fileLayout;
	private ListView fileListView;
	private LinearLayout noFileLayout;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {
		Intent intent = getIntent();
		media = intent.getStringExtra("Media");
		if ("image".equalsIgnoreCase(media)) {
			mediaPath = Consts.CAPTURE_PATH;
		} else if ("video".equalsIgnoreCase(media)) {
			mediaPath = Consts.VIDEO_PATH;
		}
		File file = new File(mediaPath);
		if (file.exists()) {
			File[] fileArray = file.listFiles();
			if (null != fileArray && 0 != fileArray.length) {
				int length = fileArray.length;
				for (int i = 0; i < length; i++) {
					fileList.add(fileArray[i]);
				}
				noFile = false;
			}
		}

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.medialist_layout);

		/** topBar **/
		back = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.str_find_pass);
		back.setOnClickListener(myOnClickListener);
		rigButton = (Button) findViewById(R.id.btn_right);
		rigButton.setVisibility(View.GONE);

		fileLayout = (LinearLayout) findViewById(R.id.filelayout);
		noFileLayout = (LinearLayout) findViewById(R.id.nofilelayout);
		if (noFile) {
			noFileLayout.setVisibility(View.VISIBLE);
			fileLayout.setVisibility(View.GONE);
		} else {
			noFileLayout.setVisibility(View.GONE);
			fileLayout.setVisibility(View.VISIBLE);
			fileListView = (ListView) findViewById(R.id.filelistview);
			mfAdapter = new MediaFolderAdapter(JVMediaListActivity.this);
			mfAdapter.setData(media, fileList);
			fileListView.setAdapter(mfAdapter);
		}
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.btn_left: {
				JVMediaListActivity.this.finish();
				break;
			}
			}

		}

	};

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

}
