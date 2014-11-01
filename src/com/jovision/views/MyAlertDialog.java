package com.jovision.views;

import com.jovetech.CloudSee.temp.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MyAlertDialog extends Dialog {

	private Context context;

	private TextView dialogCancel;
	private TextView dialogOK;
	private TextView dialogTitle;
	private TextView dialogContent;
	private android.view.View.OnClickListener confrimClickListener;
	// private OnClickListener cancelClickListener;

	private String title;
	private String content;

	public MyAlertDialog(Context context) {
		super(context, R.style.mydialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.myalert_dialog_layout);
		setCanceledOnTouchOutside(false);
		dialogCancel = (TextView) findViewById(R.id.dialog_cancel);
		dialogOK = (TextView) findViewById(R.id.dialog_ok);
		dialogTitle = (TextView) findViewById(R.id.dialog_title);
		dialogContent = (TextView) findViewById(R.id.tip_message);

		dialogCancel.setOnClickListener(myOnClickListener);
		dialogOK.setOnClickListener((android.view.View.OnClickListener) this.confrimClickListener);
	}

	@Override
	protected void onStart() {
		dialogTitle.setText(title);
		dialogContent.setText(content);
	}

	android.view.View.OnClickListener myOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialog_cancel:
				dismiss();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * set title for alert dialog
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * set content for alert dialog
	 * 
	 * @param content
	 */
	public void setContent(String text) {
		this.content = text;
	}

	/**
	 * set content for alert dialog
	 * 
	 * @param contentId
	 */
	public void setContent(int contentId) {
		this.content = context.getString(contentId);
	}

	/**
	 * set listener for the cancel button
	 * 
	 * @param confrimClickListener
	 */
	public void setConfirmClickListener(
			android.view.View.OnClickListener confrimClickListener) {
		this.confrimClickListener = confrimClickListener;
	}
}
