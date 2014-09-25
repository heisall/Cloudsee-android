package com.jovision.adapters;

import java.util.ArrayList;
import java.util.Random;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovision.activities.JVPlayActivity;
import com.jovision.bean.ConnPoint;
import com.jovision.bean.Device;
import com.jovision.commons.AsyncImageLoader;
import com.jovision.commons.AsyncImageLoader.ImageCallback;
import com.jovision.commons.BaseApp;
import com.jovision.commons.JVConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.ImageUtil;
import com.jovision.views.MyGridView;
import com.jovision.views.Rotate3dAnimation;

//import com.jovetech.CloudSee.temp.JVGuideActivity;

public class DeviceListAdapter extends BaseAdapter {
	// AsyncImageLoader asyncImageLoader;
	public ArrayList<Device> deviceList = new ArrayList<Device>();
	public Context mContext = null;
	public LayoutInflater inflater;
	public int openPos = -1;
	public int changePos = -1;

	private Bitmap deviceBitmap;

	private boolean localLogin = false;

	public DeviceListAdapter(Context con, boolean local) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		localLogin = local;
	}

	public void setData(ArrayList<Device> list) {
		deviceList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int size = 0;
		if (null != deviceList && 0 != deviceList.size()) {
			size = deviceList.size();
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Device dev = null;
		if (null != deviceList && 0 != deviceList.size()
				&& position < deviceList.size()) {
			dev = deviceList.get(position);
		}
		return dev;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DeviceHolder deviceHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.device_item, null);
			deviceHolder = new DeviceHolder();
			deviceHolder.deviceSelectImg = (ImageView) convertView
					.findViewById(R.id.deviceselected);
			deviceHolder.deviceImg = (ImageView) convertView
					.findViewById(R.id.deviceimg);
			deviceHolder.deviceNum = (TextView) convertView
					.findViewById(R.id.devicenum);
			deviceHolder.deviceName = (TextView) convertView
					.findViewById(R.id.devicename);
			deviceHolder.deviceConnect = (ImageView) convertView
					.findViewById(R.id.deviceconnect);
			deviceHolder.pointGridView = (MyGridView) convertView
					.findViewById(R.id.connpointgridview);
			deviceHolder.deviceState = (Button) convertView
					.findViewById(R.id.devicestate);
			deviceHolder.deviceWifi = (Button) convertView
					.findViewById(R.id.devicewifi);
			convertView.setTag(deviceHolder);
		} else {
			deviceHolder = (DeviceHolder) convertView.getTag();
		}

		deviceHolder.deviceConnect.setBackgroundDrawable(mContext
				.getResources().getDrawable(R.drawable.device_item_arrow));

		PointListAdapter pointListAdapter = new PointListAdapter(mContext);
		final int devicePos = position;
		final DeviceHolder holder = deviceHolder;
		final ImageView imageView = deviceHolder.deviceImg;

		deviceHolder.pointGridView
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub

						// //首次登陆
						// if(null != sharedPreferences
						// && sharedPreferences.getBoolean("ShowPlayHelp",
						// true)){
						//
						// Intent intent = new Intent();
						// intent.setClass(mContext, JVGuideActivity.class);
						// intent.putExtra("DeviceIndex", devicePos);
						// intent.putExtra("PointIndex", arg2);
						//
						// // 获取当前语言
						// String language = Locale.getDefault().getLanguage();
						// if (language.equalsIgnoreCase("zh")) {// 中文
						// intent.putExtra("ArrayFlag", 7);
						// } else {//英文或其他
						// intent.putExtra("ArrayFlag", 8);
						// }
						// mContext.startActivity(intent);
						// }else{
						Intent playIntent = new Intent();
						playIntent.setClass(mContext, JVPlayActivity.class);
						playIntent
								.putExtra("PlayTag", JVConst.NORMAL_PLAY_FLAG);
						playIntent.putExtra("DeviceIndex", devicePos);
						playIntent.putExtra("PointIndex", arg2);
						mContext.startActivity(playIntent);
						// }

					}
				});

		// deviceHolder.deviceConnect.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent playIntent = new Intent();
		// playIntent.setClass(mContext, JVPlayActivity.class);
		// playIntent.putExtra("PlayTag", JVConst.NORMAL_PLAY_FLAG);
		// playIntent.putExtra("DeviceIndex", devicePos);
		// playIntent.putExtra("PointIndex", 0);
		// mContext.startActivity(playIntent);
		// // }
		// }
		//
		// });
		if (position == openPos) {
			ArrayList<ConnPoint> pointList = deviceList.get(position).pointList;
			if (null != pointList && 0 != pointList.size()) {
				pointListAdapter.setData(pointList, Color.WHITE);
			} else {
				pointListAdapter.setData(null, Color.WHITE);
			}
			holder.pointGridView.setAdapter(pointListAdapter);
			holder.pointGridView.setTag(pointListAdapter);
			deviceHolder.deviceSelectImg.setVisibility(View.VISIBLE);
			deviceHolder.pointGridView.setVisibility(View.VISIBLE);
		} else {
			deviceHolder.deviceSelectImg.setVisibility(View.GONE);
			deviceHolder.pointGridView.setVisibility(View.GONE);
		}

		if (null != deviceList && 0 != deviceList.size()) {
			if (position < deviceList.size()) {
				if (!localLogin
						&& MySharedPreference.getBoolean("watchType", true)
						&& deviceList.get(position).onlineState == 0) {
					imageView
							.setImageDrawable(mContext
									.getResources()
									.getDrawable(
											R.drawable.device_default_img_offline));
				} else {
					if (changePos != -1) {// 20秒钟时间到切换图片

						// MyLog.e("截图个数" + deviceList.get(position).deviceNum,
						// "----"
						// + deviceList.get(position).deviceImageList
						// .size() + "");

						if (null != deviceList.get(position).deviceImageList
								&& 0 != deviceList.get(position).deviceImageList
										.size()
								&& deviceList.get(position).imageIndex < deviceList
										.get(position).deviceImageList.size()
								&& BaseApp.LOADIMAGE) {
							String url = deviceList.get(position).deviceImageList
									.get(deviceList.get(position).imageIndex);
							// Bitmap bitmap1 = ImageUtil.getBitmap(url);
							holder.deviceImg.setTag(url);

							// Bitmap bitmap1 = null;
							deviceBitmap = AsyncImageLoader.getInstance().imageCache
									.get(url);
							MyLog.e("tag1111---------------------",
									"size: "
											+ AsyncImageLoader.getInstance().imageCache
													.size());
							// if(null != softReference){
							// bitmap1 = softReference.get();
							// // if(null == bitmap1){
							// // MyLog.e("tags", "bitmap1 === null");
							// // AsyncImageLoader.imageCache.remove(url);
							// //
							// // }
							// }

							if (null == deviceBitmap) {
								deviceBitmap = AsyncImageLoader.getInstance()
										.loadBitmap(url, new ImageCallback() {
											@Override
											public void imageLoaded(
													Bitmap imageBitmap,
													String imageUrl) {
												ImageView imageViewByTag = (ImageView) holder
														.findViewWithTag(imageUrl);
												if (imageViewByTag != null) {
													Bitmap bitmap2 = ImageUtil
															.deviceCombine(
																	mContext,
																	imageBitmap);
													if (null != bitmap2) {
														imageViewByTag
																.setImageBitmap(bitmap2);
													}
												} else {
													// load image failed from
													// Internet
												}
											}
										});
							}

							if (null != deviceBitmap) {
								// MyLog.e("截图url",url);
								Bitmap bb = ImageUtil.deviceCombine(mContext,
										deviceBitmap);
								if (null != bb) {
									imageView.setImageBitmap(bb);
								} else {
									imageView
											.setImageDrawable(mContext
													.getResources()
													.getDrawable(
															R.drawable.device_default_img));
								}

							} else {
								imageView.setImageDrawable(mContext
										.getResources().getDrawable(
												R.drawable.device_default_img));
							}
						} else {
							imageView
									.setImageDrawable(mContext
											.getResources()
											.getDrawable(
													R.drawable.device_default_img));
						}

						// holder.deviceNum.setText(deviceList.get(position).deviceNum);
						// holder.deviceName
						// .setText(deviceList.get(position).deviceName);
						// ArrayList<ConnPoint> pointList =
						// deviceList.get(position).pointList;
						// if (null != pointList && 0 != pointList.size()) {
						//
						// pointListAdapter.setData(pointList);
						// holder.pointGridView.setAdapter(pointListAdapter);
						// }
						if (changePos == position) {// 需要动画旋转
							// 计算中心点
							float centerX = imageView.getWidth() / 2.0f;
							float centerY = imageView.getHeight() / 2.0f;
							Rotate3dAnimation rotation = new Rotate3dAnimation(
									0, 180, centerX, centerY);
							rotation.setDuration(1000);
							rotation.setFillAfter(false);
							// rotation.setInterpolator(new
							// AccelerateInterpolator());
							// 设置监听
							rotation.setAnimationListener(new AnimationListener() {

								@Override
								public void onAnimationEnd(Animation animation) {
									// TODO Auto-generated method stub
									if (-1 != changePos
											&& changePos < deviceList.size()) {
										if (null != deviceList.get(changePos)
												&& null != deviceList
														.get(changePos).deviceImageList
												&& 0 != deviceList
														.get(changePos).deviceImageList
														.size()
												&& BaseApp.LOADIMAGE) {
											Random ran = new Random();
											int randomPos = ran.nextInt(deviceList
													.get(changePos).deviceImageList
													.size()); // 获得随机数
											deviceList.get(changePos).imageIndex = randomPos;

											String url = deviceList
													.get(changePos).deviceImageList.get(deviceList
													.get(changePos).imageIndex);
											// MyLog.e("设备缩略图地址", url);

											// Bitmap bitmap1 =
											// ImageUtil.getBitmap(url);
											holder.deviceImg.setTag(url);

											// Bitmap bitmap1 = null;
											deviceBitmap = AsyncImageLoader
													.getInstance().imageCache
													.get(url);
											MyLog.e("tag2222---------------------",
													"size: "
															+ AsyncImageLoader
																	.getInstance().imageCache
																	.size());
											// if(null != softReference){
											// bitmap1 = softReference.get();
											// // if(null == bitmap1){
											// //
											// AsyncImageLoader.imageCache.remove(url);
											// // }
											// }

											if (null == deviceBitmap) {
												deviceBitmap = AsyncImageLoader
														.getInstance()
														.loadBitmap(
																url,
																new ImageCallback() {
																	@Override
																	public void imageLoaded(
																			Bitmap imageBitmap,
																			String imageUrl) {
																		ImageView imageViewByTag = (ImageView) holder
																				.findViewWithTag(imageUrl);
																		if (imageViewByTag != null) {
																			Bitmap bitmap2 = ImageUtil
																					.deviceCombine(
																							mContext,
																							imageBitmap);
																			if (null != bitmap2) {
																				imageViewByTag
																						.setImageBitmap(bitmap2);
																			}
																		} else {
																			// load
																			// image
																			// failed
																			// from
																			// Internet
																		}
																	}
																});
											}

											// ImageUtil.deviceCombine(mContext,bitmap1);
											// Bitmap bitmap2 = ImageUtil
											// .getRoundedCornerBitmap(bitmap1,50.0f);
											// Log.v("动画结束"+bitmap2,
											// deviceList.get(devicePos).deviceNum+"附图"+deviceList.get(devicePos).imageIndex);
											if (null != deviceBitmap) {
												Bitmap bb = ImageUtil
														.deviceCombine(
																mContext,
																deviceBitmap);
												if (null != bb) {
													imageView
															.setImageBitmap(bb);
												} else {
													imageView
															.setImageDrawable(mContext
																	.getResources()
																	.getDrawable(
																			R.drawable.device_default_img));
												}
											} else {
												imageView
														.setImageDrawable(mContext
																.getResources()
																.getDrawable(
																		R.drawable.device_default_img));
											}
										} else {
											imageView
													.setImageDrawable(mContext
															.getResources()
															.getDrawable(
																	R.drawable.device_default_img));
										}
									}
								}

								@Override
								public void onAnimationRepeat(
										Animation animation) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onAnimationStart(Animation animation) {
									// TODO Auto-generated method stub

								}

							});

							imageView.startAnimation(rotation);

						}

					} else if (changePos == -1) {
						if (null != deviceList.get(position)
								&& null != deviceList.get(position).deviceImageList
								&& 0 != deviceList.get(position).deviceImageList
										.size()
								&& deviceList.get(position).imageIndex < deviceList
										.get(position).deviceImageList.size()
								&& BaseApp.LOADIMAGE) {
							String url = deviceList.get(position).deviceImageList
									.get(deviceList.get(position).imageIndex);
							// Bitmap bitmap1 = ImageUtil.getBitmap(url);

							holder.deviceImg.setTag(url);

							// Bitmap bitmap1 = null;
							deviceBitmap = AsyncImageLoader.getInstance().imageCache
									.get(url);
							MyLog.e("tag3333---------------------",
									"size: "
											+ AsyncImageLoader.getInstance().imageCache
													.size());
							if (null == deviceBitmap) {
								deviceBitmap = AsyncImageLoader.getInstance()
										.loadBitmap(url, new ImageCallback() {
											@Override
											public void imageLoaded(
													Bitmap imageBitmap,
													String imageUrl) {
												ImageView imageViewByTag = (ImageView) holder
														.findViewWithTag(imageUrl);
												if (imageViewByTag != null) {
													Bitmap bitmap2 = ImageUtil
															.deviceCombine(
																	mContext,
																	imageBitmap);
													if (null != bitmap2) {
														imageViewByTag
																.setImageBitmap(bitmap2);
													}
												} else {
													// load image failed from
													// Internet
												}
											}
										});
							}

							// ImageUtil.deviceCombine(mContext,bitmap1);
							// Bitmap bitmap2 = ImageUtil
							// .getRoundedCornerBitmap(bitmap1,50.0f);
							if (null != deviceBitmap) {
								Bitmap bb = ImageUtil.deviceCombine(mContext,
										deviceBitmap);
								if (null != bb) {
									imageView.setImageBitmap(bb);
								} else {
									imageView
											.setImageDrawable(mContext
													.getResources()
													.getDrawable(
															R.drawable.device_default_img));
								}
							} else {
								imageView.setImageDrawable(mContext
										.getResources().getDrawable(
												R.drawable.device_default_img));
							}
						} else {
							imageView
									.setImageDrawable(mContext
											.getResources()
											.getDrawable(
													R.drawable.device_default_img));
						}
						// holder.deviceNum.setText(
						// deviceList.get(position).deviceNum);
						// holder.deviceName
						// .setText(deviceList.get(position).deviceName);
						// ArrayList<ConnPoint> pointList =
						// deviceList.get(position).pointList;
						// if (null != pointList && 0 != pointList.size()) {
						//
						// pointListAdapter.setData(pointList);
						// holder.pointGridView.setAdapter(pointListAdapter);
						// }
					}

				}
			}
		}

		if (position < deviceList.size()) {
			deviceHolder.deviceNum.setText(deviceList.get(position).deviceNum);
			deviceHolder.deviceName
					.setText(deviceList.get(position).deviceName);
			ArrayList<ConnPoint> pointList = deviceList.get(position).pointList;
			if (null != pointList && 0 != pointList.size()) {
				pointListAdapter.setData(pointList, Color.WHITE);
				holder.pointGridView.setAdapter(pointListAdapter);
				holder.pointGridView.setTag(pointListAdapter);
			}
			if (localLogin) {
				deviceHolder.deviceState.setVisibility(View.GONE);
				deviceHolder.deviceWifi.setVisibility(View.GONE);
			} else {
				deviceHolder.deviceState.setVisibility(View.VISIBLE);
				if (0 == deviceList.get(position).onlineState) {
					deviceHolder.deviceState.setTextColor(mContext
							.getResources().getColor(R.color.offline_color));
					deviceHolder.deviceState.setText(mContext.getResources()
							.getText(R.string.str_device_offline));
					Drawable drawable = mContext.getResources().getDrawable(
							R.drawable.deviceoffline);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(),
							drawable.getMinimumHeight());
					deviceHolder.deviceState.setCompoundDrawables(drawable,
							null, null, null);
				} else {
					deviceHolder.deviceState.setTextColor(mContext
							.getResources().getColor(R.color.online_color));
					deviceHolder.deviceState.setText(mContext.getResources()
							.getText(R.string.str_device_online));
					Drawable drawable = mContext.getResources().getDrawable(
							R.drawable.deviceonline);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(),
							drawable.getMinimumHeight());
					deviceHolder.deviceState.setCompoundDrawables(drawable,
							null, null, null);
				}

				if ((MySharedPreference.getBoolean("watchType", true) && deviceList
						.get(position).onlineState == 0)
						|| 0 == deviceList.get(position).hasWifi) {
					deviceHolder.deviceWifi.setTextColor(mContext
							.getResources().getColor(R.color.offline_color));
					Drawable drawable = mContext.getResources().getDrawable(
							R.drawable.wifioffline);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(),
							drawable.getMinimumHeight());
					deviceHolder.deviceWifi.setCompoundDrawables(drawable,
							null, null, null);
				} else {
					// deviceHolder.deviceWifi.setVisibility(View.VISIBLE);
					deviceHolder.deviceWifi.setTextColor(mContext
							.getResources().getColor(R.color.online_color));
					Drawable drawable = mContext.getResources().getDrawable(
							R.drawable.wifionline);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(),
							drawable.getMinimumHeight());
					deviceHolder.deviceWifi.setCompoundDrawables(drawable,
							null, null, null);
					// if(false == deviceList.get(position).useWifi)
					// {
					// deviceHolder.deviceWifi.setTextColor(mContext.getResources().getColor(R.color.offline_color));
					// Drawable drawable=
					// mContext.getResources().getDrawable(R.drawable.wifioffline);
					// drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					// drawable.getMinimumHeight());
					// deviceHolder.deviceWifi.setCompoundDrawables(drawable,null,null,null);
					// }
					// else
					// {
					// deviceHolder.deviceWifi.setTextColor(mContext.getResources().getColor(R.color.online_color));
					// Drawable drawable=
					// mContext.getResources().getDrawable(R.drawable.wifionline);
					// drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					// drawable.getMinimumHeight());
					// deviceHolder.deviceWifi.setCompoundDrawables(drawable,null,null,null);
					// }
				}
			}
		}

		return convertView;
	}

	// 设备(parent)
	class DeviceHolder {
		ImageView deviceSelectImg;
		ImageView deviceImg;
		TextView deviceNum;
		TextView deviceName;
		ImageView deviceConnect;
		MyGridView pointGridView;

		Button deviceState;
		Button deviceWifi;

		public ImageView findViewWithTag(String imageUrl) {
			// TODO Auto-generated method stub

			if (imageUrl.equalsIgnoreCase(deviceImg.getTag().toString())) {
				return deviceImg;
			}
			return null;
		}
	}

	// void applyRotation(View view, float start, float end) {
	// // 计算中心点
	// final float centerX = view.getWidth() / 2.0f;
	// final float centerY = view.getHeight() / 2.0f;
	// final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
	// centerX, centerY);
	// rotation.setDuration(2000);
	// rotation.setFillAfter(true);
	// rotation.setInterpolator(new AccelerateInterpolator());
	// // 设置监听
	// rotation.setAnimationListener(new DisplayNextView());
	// view.startAnimation(rotation);
	//
	// }
	//
	// private final class DisplayNextView implements
	// Animation.AnimationListener {
	// public void onAnimationStart(Animation animation) {
	// }
	//
	// // 动画结束
	// public void onAnimationEnd(Animation animation) {
	// // view.post(new SwapViews(view));
	// }
	//
	// public void onAnimationRepeat(Animation animation) {
	// }
	// }

	class SwapViews implements Runnable {
		View view;

		public SwapViews(View arg) {
			view = arg;
		}

		public void run() {
			final float centerX = view.getWidth() / 2.0f;
			final float centerY = view.getHeight() / 2.0f;
			Rotate3dAnimation rotation = null;
			view.requestFocus();
			rotation = new Rotate3dAnimation(90, 0, centerX, centerY);
			rotation.setDuration(500);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());
			// 开始动画
			view.startAnimation(rotation);
			// view.setText(String.valueOf(count++));
		}
	}

}
