package com.jovision.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.adapters.MediaSelectorAdapter;

public class JVMediaActivity extends BaseActivity {

	/** topBar **/
	private Button back;// 左侧返回按钮
	private TextView currentMenu;// 当前页面名称
	private Button rigButton;

	private ListView mediaListView;
	private MediaSelectorAdapter msAdapter;
	private ArrayList<String> mediaList = new ArrayList<String>();

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {
		if (null == mediaList) {
			mediaList = new ArrayList<String>();
		}
		mediaList.clear();
		mediaList.add(getResources().getString(R.string.media_image));
		mediaList.add(getResources().getString(R.string.media_video));
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.mediaselector_layout);

		/** topBar **/
		back = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.media);
		back.setOnClickListener(myOnClickListener);
		rigButton = (Button) findViewById(R.id.btn_right);
		rigButton.setVisibility(View.GONE);

		mediaListView = (ListView) findViewById(R.id.medialistview);
		msAdapter = new MediaSelectorAdapter(JVMediaActivity.this);
		msAdapter.setData(mediaList);
		mediaListView.setAdapter(msAdapter);
		mediaListView.setOnItemClickListener(mOnItemClickListener);
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.btn_left: {
				JVMediaActivity.this.finish();
				break;
			}
			}
		}

	};

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent mediaListIntent = new Intent();
			mediaListIntent.setClass(JVMediaActivity.this,
					JVMediaListActivity.class);
			switch (arg2) {
			case 0: {
				mediaListIntent.putExtra("Media", "image");
				break;
			}
			case 1: {
				mediaListIntent.putExtra("Media", "video");
				break;
			}
			}
			JVMediaActivity.this.startActivity(mediaListIntent);
		}

	};

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

}
