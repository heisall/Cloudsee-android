package com.jovision.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.Device;
import com.jovision.bean.OneKeyUpdate;
import com.jovision.commons.MyLog;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.DeviceUtil;

public class JVDeviceUpdateActivity extends BaseActivity {

	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private int devIndex;

	/** 一键升级功能 */
	private OneKeyUpdate updateObj;
	private ProgressDialog updateDialog;

	/** topBar **/
	private TextView devModel;
	private TextView devVersion;
	private Button updateBtn;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.WHAT_DOWNLOAD_KEY_UPDATE_SUCCESS:
			if (null != updateDialog && updateDialog.isShowing()) {
				updateDialog.dismiss();
				updateDialog = null;
			}
			updateDialog = null;
			if (updateDialog == null) {
				updateDialog = new ProgressDialog(JVDeviceUpdateActivity.this);
				updateDialog.setCancelable(false);
				updateDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				updateDialog.setTitle(getResources().getString(
						R.string.writing_update));
				updateDialog.setIndeterminate(false);
				updateDialog.setMax(100);
			}
			updateDialog.show();
			new Thread() {
				public void run() {
					try {
						boolean flag = true;
						int time = 0;
						while (flag) {
							int pro = DeviceUtil.getUpdateProgress(
									JVDeviceUpdateActivity.this.statusHashMap
											.get(Consts.KEY_USERNAME),
									deviceList.get(devIndex).getFullNo());
							MyLog.v("DownPro", pro + "");
							if (100 <= pro) {
								flag = false;
								handler.sendMessage(handler
										.obtainMessage(Consts.WHAT_WRITE_KEY_UPDATE_SUCCESS));
							} else if (-1 == pro) {
								time++;
							} else {
								time = 0;
								handler.sendMessage(handler.obtainMessage(
										Consts.WHAT_DOWNLOADING_KEY_UPDATE,
										pro, 0));
								Thread.sleep(1000);
							}
							if (time >= 5) {
								flag = false;
								handler.sendMessage(handler
										.obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_ERROR));
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						handler.sendMessage(handler
								.obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_ERROR));
					}
				}
			}.start();
			break;
		case Consts.WHAT_DOWNLOADING_KEY_UPDATE:
			if (null != updateDialog && updateDialog.isShowing()) {
				MyLog.e("sss", arg1 + " arg1");
				updateDialog.setProgress(arg1);
			}
			break;
		case Consts.WHAT_DOWNLOAD_KEY_UPDATE_ERROR:
			if (null != updateDialog && updateDialog.isShowing()) {
				updateDialog.dismiss();
			}
			JVDeviceUpdateActivity.this
					.showTextToast(R.string.check_key_update_error);
			break;

		case Consts.WHAT_RESTART_DEVICE_SUCCESS:
			deviceList.get(devIndex).setDeviceVerName(updateObj.getUfver());
			devVersion.setText(updateObj.getUfver());
			// version.setText(deviceList.get(devIndex).deviceVersion);
			JVDeviceUpdateActivity.this
					.showTextToast(R.string.update_reset_success);
			break;
		case Consts.WHAT_DOWNLOAD_KEY_UPDATE_CANCEL:
			if (null != updateDialog && updateDialog.isShowing()) {
				updateDialog.dismiss();
			}
			break;
		case Consts.WHAT_RESTART_DEVICE_FAILED:
			JVDeviceUpdateActivity.this
					.showTextToast(R.string.update_reset_failed);
			break;
		case Consts.WHAT_WRITE_KEY_UPDATE_SUCCESS:
			if (null != updateDialog && updateDialog.isShowing()) {
				updateDialog.dismiss();
				updateDialog = null;
			}
			AlertDialog ads = new AlertDialog.Builder(
					JVDeviceUpdateActivity.this)
					.setTitle(R.string.key_update_reset)
					.setCancelable(false)
					.setPositiveButton(getResources().getString(R.string.sure),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									new Thread() {
										public void run() {
											DeviceUtil
													.cancelUpdate(
															JVDeviceUpdateActivity.this.statusHashMap
																	.get(Consts.KEY_USERNAME),
															deviceList
																	.get(devIndex)
																	.getFullNo());
											if (0 == DeviceUtil
													.pushRestart(
															JVDeviceUpdateActivity.this.statusHashMap
																	.get(Consts.KEY_USERNAME),
															deviceList
																	.get(devIndex)
																	.getFullNo())) {
												handler.sendMessage(handler
														.obtainMessage(Consts.WHAT_RESTART_DEVICE_SUCCESS));
											} else {
												handler.sendMessage(handler
														.obtainMessage(Consts.WHAT_RESTART_DEVICE_FAILED));
											}
										};
									}.start();
									dialog.dismiss();
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.str_crash_cancel),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									new Thread() {
										public void run() {
											DeviceUtil
													.cancelUpdate(
															JVDeviceUpdateActivity.this.statusHashMap
																	.get(Consts.KEY_USERNAME),
															deviceList
																	.get(devIndex)
																	.getFullNo());
										};
									}.start();
									dialog.dismiss();
								}
							}).create();
			ads.show();
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {
		deviceList = CacheUtil.getDevList();
		devIndex = getIntent().getIntExtra("deviceIndex", 0);
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.deviceupdate_layout);
		/** topBar **/
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout)findViewById(R.id.alarmnet);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.device_version_info);
		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);

		devModel = (TextView) findViewById(R.id.devmodel);
		devVersion = (TextView) findViewById(R.id.devversion);
		updateBtn = (Button) findViewById(R.id.update);
		updateBtn.setOnClickListener(myOnClickListener);

		if ("unknown".equalsIgnoreCase(deviceList.get(devIndex)
				.getDeviceModel())) {
			devModel.setText(R.string.unknown);
		} else {
			devModel.setText(deviceList.get(devIndex).getDeviceModel());
		}
		devVersion.setText(deviceList.get(devIndex).getDeviceVerName());
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.btn_left: {
				JVDeviceUpdateActivity.this.finish();
				break;
			}
			case R.id.update: {// 一键升级
				createDialog("", true);
				CheckUpdateTask task = new CheckUpdateTask();
				String[] params = new String[3];
				task.execute(params);
				break;
			}
			}

		}

	};

	// 设置三种类型参数分别为String,Integer,String
	class CheckUpdateTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int updateRes = -1;// 0成功， 14失败，其他出错
			try {
				updateObj = DeviceUtil.checkUpdate(
						JVDeviceUpdateActivity.this.statusHashMap
								.get(Consts.KEY_USERNAME),
						deviceList.get(devIndex).getDeviceType(), deviceList
								.get(devIndex).getDeviceVerNum(), deviceList
								.get(devIndex).getDeviceVerName());
				updateRes = updateObj.getResultCode();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return updateRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			JVDeviceUpdateActivity.this.dismissDialog();
			if (0 == result) {
				AlertDialog ad = new AlertDialog.Builder(
						JVDeviceUpdateActivity.this)
						.setTitle(R.string.key_update_title)
						.setMessage(updateObj.getUfdes())
						.setCancelable(false)
						.setPositiveButton(
								getResources().getString(R.string.update),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										PushUpdateTask task = new PushUpdateTask();
										String[] params = new String[3];
										task.execute(params);
										JVDeviceUpdateActivity.this
												.createDialog("", true);
										dialog.dismiss();
									}
								})
						.setNegativeButton(
								getResources().getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				ad.show();
			} else if (14 == result) {
				JVDeviceUpdateActivity.this
						.showTextToast(R.string.check_key_update_failed);
			} else {
				JVDeviceUpdateActivity.this
						.showTextToast(R.string.check_key_update_error);
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
			JVDeviceUpdateActivity.this.createDialog("", true);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度,此方法在主线程执行，用于显示任务执行的进度。

		}
	}

	// 设置三种类型参数分别为String,Integer,String
	class PushUpdateTask extends AsyncTask<String, Integer, Integer> {
		// 可变长的输入参数，与AsyncTask.exucute()对应
		@Override
		protected Integer doInBackground(String... params) {
			int pushRes = -1;// 0成功 1失败
			try {
				DeviceUtil.cancelUpdate(
						JVDeviceUpdateActivity.this.statusHashMap
								.get(Consts.KEY_USERNAME),
						deviceList.get(devIndex).getFullNo());
				pushRes = DeviceUtil.pushUpdateCommand(
						JVDeviceUpdateActivity.this.statusHashMap
								.get(Consts.KEY_USERNAME),
						deviceList.get(devIndex).getFullNo(), updateObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return pushRes;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Integer result) {
			// 返回HTML页面的内容此方法在主线程执行，任务执行的结果作为此方法的参数返回。
			JVDeviceUpdateActivity.this.dismissDialog();
			if (0 == result) {
				updateDialog = null;
				if (updateDialog == null) {
					updateDialog = new ProgressDialog(
							JVDeviceUpdateActivity.this);
					updateDialog.setCancelable(false);
					updateDialog
							.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					updateDialog.setTitle(getResources().getString(
							R.string.downloading_update));
					updateDialog.setIndeterminate(false);
					updateDialog.setMax(100);
					updateDialog
							.setButton(
									getResources().getString(
											R.string.str_crash_cancel),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											new Thread() {
												@Override
												public void run() {
													DeviceUtil
															.cancelUpdate(
																	JVDeviceUpdateActivity.this.statusHashMap
																			.get(Consts.KEY_USERNAME),
																	deviceList
																			.get(devIndex)
																			.getFullNo());
												}
											}.start();
											dialog.dismiss();
										}
									});
				}
				updateDialog.show();
				new Thread() {
					public void run() {
						try {
							boolean flag = true;
							int time = 0;
							int pro2 = 0;
							while (flag) {
								int pro = DeviceUtil
										.getDownloadProgress(
												JVDeviceUpdateActivity.this.statusHashMap
														.get(Consts.KEY_USERNAME),
												deviceList.get(devIndex)
														.getFullNo());
								MyLog.v("sss", pro + " pro");
								if (100 <= pro) {
									flag = false;
									handler.sendMessage(handler
											.obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_SUCCESS));
								} else if (-1 == pro && 0 == pro && pro2 == pro) {
									time++;
									Thread.sleep(1000);
								} else if (pro2 > pro) {
									flag = false;
									handler.sendMessage(handler
											.obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_CANCEL));
								} else {
									time = 0;
									handler.sendMessage(handler.obtainMessage(
											Consts.WHAT_DOWNLOADING_KEY_UPDATE,
											pro, 0));
									Thread.sleep(1000);
								}
								if (time >= 5) {
									flag = false;
									handler.sendMessage(handler
											.obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_ERROR));
								}
								pro2 = pro;
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
							handler.sendMessage(handler
									.obtainMessage(Consts.WHAT_DOWNLOAD_KEY_UPDATE_ERROR));
						}
					}
				}.start();
			} else {
				JVDeviceUpdateActivity.this
						.showTextToast(R.string.check_key_update_error);
			}
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
	protected void onPause() {
		CacheUtil.saveDevList(deviceList);
		super.onPause();
	}

}
