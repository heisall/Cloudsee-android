package com.jovision.activities;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;

public class JVMediaActivity extends BaseActivity {

	/** topBar **/
	private Button back;// 左侧返回按钮
	private TextView currentMenu;// 当前页面名称
	private Button rigButton;

	ImageButton imageBtn;
	ImageButton videoBtn;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {

	}

	@Override
	protected void initUi() {
		setContentView(R.layout.media_layout);

		/** topBar **/
		back = (Button) findViewById(R.id.btn_left);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.media);
		back.setOnClickListener(myOnClickListener);
		rigButton = (Button) findViewById(R.id.btn_right);
		rigButton.setVisibility(View.GONE);

		imageBtn = (ImageButton) findViewById(R.id.images);
		videoBtn = (ImageButton) findViewById(R.id.videos);

		imageBtn.setOnClickListener(myOnClickListener);
		videoBtn.setOnClickListener(myOnClickListener);
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.btn_left: {
				JVMediaActivity.this.finish();
				break;
			}
			case R.id.images: {
				Intent mediaListIntent = new Intent();
				mediaListIntent.setClass(JVMediaActivity.this,
						JVMediaListActivity.class);
				mediaListIntent.putExtra("Media", "image");
				JVMediaActivity.this.startActivity(mediaListIntent);
				break;
			}
			case R.id.videos: {
				Intent mediaListIntent = new Intent();
				mediaListIntent.setClass(JVMediaActivity.this,
						JVMediaListActivity.class);
				mediaListIntent.putExtra("Media", "video");
				JVMediaActivity.this.startActivity(mediaListIntent);
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
