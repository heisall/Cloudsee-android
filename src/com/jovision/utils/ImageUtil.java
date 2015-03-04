package com.jovision.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.jovision.commons.MyLog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageUtil {

	public static boolean hasCompare = false;// 根本地比较过是否同一张图片
	public final String TAG = "ImageUtil";
	public static HashMap<String, SoftReference<Bitmap>> imagesCache = new HashMap<String, SoftReference<Bitmap>>(); // 图片缓存

	/**
	 * 根据一个网络连接(URL)获取bitmapDrawable图像
	 * 
	 * @param imageUri
	 * @return
	 */
	public static BitmapDrawable getfriendicon(URL imageUri) {

		BitmapDrawable bitmap = null;
		try {
			HttpURLConnection hp = (HttpURLConnection) imageUri
					.openConnection();
			bitmap = new BitmapDrawable(hp.getInputStream());// 将输入流转换成bitmap
			hp.disconnect();// 关闭连接
		} catch (Exception e) {
		}
		return bitmap;
	}

	/**
	 * 根据一个网络连接(String)获取bitmapDrawable图像
	 * 
	 * @param imageUri
	 * @return
	 */
	public static BitmapDrawable getcontentPic(String imageUri) {
		URL imgUrl = null;
		try {
			imgUrl = new URL(imageUri);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		BitmapDrawable icon = null;
		try {
			HttpURLConnection hp = (HttpURLConnection) imgUrl.openConnection();
			icon = new BitmapDrawable(hp.getInputStream());// 将输入流转换成bitmap
			hp.disconnect();// 关闭连接
		} catch (Exception e) {
		}
		return icon;
	}

	/**
	 * 根据一个网络连接(URL)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 */
	// public static Bitmap getusericon(URL imageUri) {
	// // 显示网络上的图片
	// URL myFileUrl = imageUri;
	// Bitmap bitmap = null;
	// try {
	// HttpURLConnection conn = (HttpURLConnection) myFileUrl
	// .openConnection();
	// conn.setDoInput(true);
	// conn.connect();
	// InputStream is = conn.getInputStream();
	// bitmap = BitmapFactory.decodeStream(is);
	// is.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return bitmap;
	// }
	//
	// public static Bitmap getBitmapByUrl(String imageUrl) {
	// // 显示网络上的图片
	// Bitmap bitmap = null;
	// try {
	// SoftReference<Bitmap> softReference = imagesCache.get(imageUrl);
	// bitmap = softReference != null ? softReference.get() : null;
	// if (bitmap == null) {
	// bitmap = getBitmap(imageUrl);
	// }
	//
	// } catch (Exception e) {
	// bitmap = null;
	// e.printStackTrace();
	// }
	// return bitmap;
	// }

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getBitmap(String imageUrl) {
		// 显示网络上的图片
		Bitmap bitmap = null;
		// Log.v("读取图片url", imageUrl);

		if (imagesCache.containsKey(imageUrl)) {
			// 从缓存中读取
			SoftReference<Bitmap> softReference = imagesCache.get(imageUrl);
			bitmap = softReference.get();
			// Log.e("tags", "getBitmap 从缓存中获取图片");
			return bitmap;
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		options.inSampleSize = 4;
		// options.inJustDecodeBounds
		options.inPreferredConfig = Bitmap.Config.RGB_565; // 默认是Bitmap.Config.ARGB_8888
		/* 下面两个字段需要组合使用 */
		options.inPurgeable = true;
		options.inInputShareable = true;

		if (null == bitmap) {

			// Log.e("无图", "无真相");
			InputStream is = null;
			HttpURLConnection conn = null;
			try {
				URL myFileUrl = new URL(imageUrl);
				conn = (HttpURLConnection) myFileUrl.openConnection();
				conn.setDoInput(true);
				conn.setConnectTimeout(45000);
				conn.connect();
				is = conn.getInputStream();
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(is, null, options);
				if (bitmap != null) {
					imagesCache
							.put(imageUrl, new SoftReference<Bitmap>(bitmap));
				}
				myFileUrl = null;
				options = null;
				// Log.e("tags", "getBitmap 从网络上获取图片");
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (conn != null) {
					conn.disconnect();
				}

			}
		}
		// Log.v("bitmap.getByteCount()", bitmap.getByteCount()+"");

		return bitmap;
	}

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static Bitmap getShowBitmap(String imageUrl) {
		// 显示网络上的图片
		Bitmap bitmap = null;
		// Log.v("读取图片url", imageUrl);

		if (imagesCache.containsKey(imageUrl)) {
			// 从缓存中读取
			SoftReference<Bitmap> softReference = imagesCache.get(imageUrl);
			bitmap = softReference.get();
			// Log.e("tags", "getBitmap 从缓存中获取图片");
			if (null != bitmap) {
				return bitmap;
			}

		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		options.inSampleSize = 1;
		// options.inJustDecodeBounds
		options.inPreferredConfig = Bitmap.Config.RGB_565; // 默认是Bitmap.Config.ARGB_8888
		/* 下面两个字段需要组合使用 */
		options.inPurgeable = true;
		options.inInputShareable = true;

		if (null == bitmap) {

			// Log.e("无图", "无真相");
			InputStream is = null;
			HttpURLConnection conn = null;
			try {
				URL myFileUrl = new URL(imageUrl);
				conn = (HttpURLConnection) myFileUrl.openConnection();
				conn.setDoInput(true);
				conn.setConnectTimeout(45000);
				conn.connect();
				is = conn.getInputStream();
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(is, null, options);
				if (bitmap != null) {
					imagesCache
							.put(imageUrl, new SoftReference<Bitmap>(bitmap));
				}
				myFileUrl = null;
				options = null;
				// Log.e("tags", "getBitmap 从网络上获取图片");
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (conn != null) {
					conn.disconnect();
				}

			}
		}
		// Log.v("bitmap.getByteCount()", bitmap.getByteCount()+"");

		return bitmap;
	}

	// 生成固定宽度和高度bitmap
	private static Bitmap createNewBitmap(String path, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inInputShareable = true;
		options.inPurgeable = true;
		options.outWidth = width;
		options.outHeight = height;
		options.inJustDecodeBounds = true;
		options.inSampleSize = computeSampleSize(options, -1, 260 * 280);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;

		Bitmap newbmp = BitmapFactory.decodeFile(path, options);

		return newbmp;
	}

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	// /* 重叠两张图片 */
	// public static Bitmap CombinePictures(Bitmap bmpBottom, Bitmap bmpTop, int
	// Width, int Height, float x, float y, int Density){
	// //参数依次为：下层图像，上层图像，返回图像宽度，返回图像高度，上层图像 x 坐标，上层图像 y 坐标，像素密度
	// // Bitmap bmpTemp = ImageScale(bmpBottom, Width, Height);
	// Bitmap bmpTemp.setDensity(Density);
	// Canvas canvas = new Canvas(bmpTemp);
	// canvas.drawBitmap(bmpTop, x, y, null);
	//
	// return bmpTemp;
	// }

	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	/**
	 * 下载图片 同时写道本地缓存文件中
	 * 
	 * @param context
	 * @param imageUri
	 * @return http://api.jovecloud.com/UploadFiles/BK_C_1.png
	 * @throws MalformedURLException
	 */

	/**
	 * 
	 * context File file=context.getFilesDir(); String
	 * path=file.getAbsolutePath(); //此处返回的路劲
	 */
	public static Bitmap getbitmapAndwrite(Context context, String imageUri) {
		MyLog.v("获取图片地址：", imageUri);
		Bitmap bitmap = null;

		BitmapFactory.Options opts = new BitmapFactory.Options();
		// opts.inJustDecodeBounds = true;
		// opts.inSampleSize = 4;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		// opts.inPreferredConfig =Bitmap.Config.ARGB_4444;

		// imageUri = "http://api.jovecloud.com/UploadFiles/BK_C_1.png";
		// 获取图片名
		String fileName = imageUri.substring(imageUri.lastIndexOf("/") + 1,
				imageUri.length());
		// 保存到系统内置存储里面
		File file = context.getFilesDir();
		String folderPath = file.getAbsolutePath();

		String filePath = folderPath + "/" + fileName;
		File imageFile = new File(filePath);

		if (!imageFile.exists()) {// 不存在去下载
			try {
				// 显示网络上的图片
				URL myFileUrl = new URL(imageUri);
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();

				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedOutputStream bos = null;
				bos = new BufferedOutputStream(new FileOutputStream(filePath));
				byte[] buf = new byte[1024];
				int len = 0;
				// 将网络上的图片存储到本地
				while ((len = is.read(buf)) > 0) {
					bos.write(buf, 0, len);
				}
				hasCompare = true;
				is.close();
				bos.close();
				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {// 存在判断文件大小是否相同

			if (hasCompare) {// 已比较过，什么也不做

			} else {
				try {
					// 显示网络上的图片
					URL myFileUrl = new URL(imageUri);
					HttpURLConnection conn = (HttpURLConnection) myFileUrl
							.openConnection();

					double fileSizeS = conn.getContentLength();// 服务器文件大小

					double fileSizeL = imageFile.length();// 本地文件大小

					// Log.e("新品图片大小对比：",
					// "fileSizeS--"+fileSizeS+";;;;fileSizeL"+fileSizeL);

					if (fileSizeS == fileSizeL) {// 同一个文件
						hasCompare = true;
						// 从本地加载图片
						bitmap = BitmapFactory.decodeFile(filePath, opts);
						conn.disconnect();
						return bitmap;
					} else {// 同一个文件不同大小，把老的先删掉，防止文件过多导致内存满了
						imageFile.delete();
						conn.setDoInput(true);
						conn.connect();
						InputStream is = conn.getInputStream();
						BufferedOutputStream bos = null;
						bos = new BufferedOutputStream(new FileOutputStream(
								filePath));
						byte[] buf = new byte[1024];
						int len = 0;
						// 将网络上的图片存储到本地
						while ((len = is.read(buf)) > 0) {
							bos.write(buf, 0, len);
						}
						hasCompare = true;
						is.close();
						bos.close();
						conn.disconnect();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		// 从本地加载图片,抛oom
		try {
			bitmap = BitmapFactory.decodeFile(filePath, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	// 根据url获取本地图片
	public static Bitmap getBitmapFromSDCard(Context context, String imageUri) {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// opts.inSampleSize = 4;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		// opts.inPreferredConfig =Bitmap.Config.ARGB_4444;

		// 获取文件名
		String fileName = imageUri.substring(imageUri.lastIndexOf("/") + 1,
				imageUri.length());

		// 保存到系统内置存储里面
		File file = context.getFilesDir();
		String folderPath = file.getAbsolutePath();

		String filePath = folderPath + "/" + fileName;
		File imageFile = new File(filePath);

		if (imageFile.exists()) {
			// 从本地加载图片
			bitmap = BitmapFactory.decodeFile(filePath, opts);
		}

		return bitmap;
	}

	public static boolean downpic(String picName, Bitmap bitmap) {
		boolean nowbol = false;
		try {
			File saveFile = new File("/mnt/sdcard/download/weibopic/" + picName
					+ ".png");
			if (!saveFile.exists()) {
				saveFile.createNewFile();
			}
			FileOutputStream saveFileOutputStream;
			saveFileOutputStream = new FileOutputStream(saveFile);
			nowbol = bitmap.compress(Bitmap.CompressFormat.PNG, 100,
					saveFileOutputStream);
			saveFileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowbol;
	}

	public static void writeTofiles(Context context, Bitmap bitmap,
			String filename) {
		BufferedOutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(context.openFileOutput(
					filename, Context.MODE_PRIVATE));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将文件写入缓存系统中
	 * 
	 * @param filename
	 * @param is
	 * @return
	 */
	public static String writefile(Context context, String filename,
			InputStream is) {
		BufferedInputStream inputStream = null;
		BufferedOutputStream outputStream = null;
		try {
			inputStream = new BufferedInputStream(is);
			outputStream = new BufferedOutputStream(context.openFileOutput(
					filename, Context.MODE_PRIVATE));
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}
		} catch (Exception e) {
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return context.getFilesDir() + "/" + filename + ".jpg";
	}

	// 放大缩小图片
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	// 将Drawable转化为Bitmap
	public static Bitmap drawableToBitmap(Drawable drawable, int width,
			int height) {
		// int width = drawable.getIntrinsicWidth();
		// int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	public static Drawable loadImageFromNetwork(String imageUrl) {
		// Log.e("test", imageUrl);
		Drawable drawable = null;
		try {
			// 可以在这里通过文件名来判断，是否本地有此图片
			drawable = Drawable.createFromStream(
					new URL(imageUrl).openStream(), "");
		} catch (IOException e) {
			MyLog.e("test", e.getMessage());
		}
		if (drawable == null) {
			MyLog.e("test", "null drawable");
		} else {
			MyLog.e("test", "not null drawable");
		}
		return drawable;
	}

	/**
	 * 上传图片
	 * 
	 * @return
	 */
	public static String upLoadPic(String filename, Map<String, String> params,
			String picpath, String uri) {

		class FormFile {
			// 定义了使用的文件的特点
			// 上传文件的数据
			private InputStream inStream;
			// 文件名称
			private String fileName;
			// 请求参数名称
			private String Formname;
			// 内容类型
			private String contentType = "application/octet-stream";

			public FormFile(InputStream inStream, String fileName,
					String formnames, String contentType) {
				this.inStream = inStream;
				this.fileName = fileName;
				this.Formname = formnames;
				this.contentType = contentType;
			}

			public InputStream getInStream() {
				return inStream;
			}

			public String getFileName() {
				return fileName;
			}

			public String getFormname() {
				return Formname;
			}

			public String getContentType() {
				return contentType;
			}

		}

		DataOutputStream outStream = null;
		// InputStream is = null;
		InputStream is1 = null;
		HttpURLConnection conn = null;

		// 图片所在位置
		File mFile = new File(picpath);
		if (!mFile.exists())
			return "2";

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(mFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// String fileName = mFile.getName();
		String formnames = "Picture";
		String contentType = "application/octet-stream";
		// String contentType = "image/x-png";

		FormFile mFormFile = new FormFile(fis, filename, formnames, contentType);

		FormFile[] files = new FormFile[] { mFormFile };

		// 添加参数
		try {
			String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
			String MULTIPART_FORM_DATA = "multipart/form-data"; // 数据类型
			byte[] end_data = ("--" + BOUNDARY + "\r\n").getBytes();// 数据结束标志

			URL url = new URL(uri);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(15 * 1000);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ "; boundary=" + BOUNDARY);

			StringBuilder sb = new StringBuilder();

			if (params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {

					sb.append("--");
					sb.append(BOUNDARY);
					sb.append("\r\n");
					sb.append("Content-Disposition: form-data; name=\""
							+ entry.getKey() + "\"\r\n\r\n");
					sb.append(entry.getValue());
					sb.append("\r\n");
				}
			}
			// Log.e("tags", sb.toString());
			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());// 发送表单字段数据
			for (FormFile file : files) {// 发送文件数据
				StringBuilder split = new StringBuilder();
				split.append("--");
				split.append(BOUNDARY);
				split.append("\r\n");
				split.append("Content-Disposition: form-data;name=\""
						+ file.getFormname() + "\";filename=\""
						+ file.getFileName() + "\"\r\n");
				split.append("Content-Type: " + file.getContentType()
						+ "\r\n\r\n");
				// Log.e("tags", split.toString());
				outStream.write(split.toString().getBytes());

				// ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
				// InputStream is = new ByteArrayInputStream(baos
				// .toByteArray());
				InputStream is = file.getInStream();
				byte[] buf = new byte[1024];
				int len = 0;

				while ((len = is.read(buf)) != -1) {
					outStream.write(buf, 0, len);
				}

				if (null != is) {
					is.close();
					is = null;
				}
				outStream.write("\r\n".getBytes());
			}

			outStream.write(end_data);
			outStream.flush();

			// int cah = conn.getResponseCode();
			// if (cah != 200)
			// throw new
			// RuntimeException("error when upload the file,the return is not 200 ok");
			int ch;
			StringBuilder b = new StringBuilder();
			is1 = conn.getInputStream();

			while ((ch = is1.read()) != -1) {
				b.append((char) ch);
			}

			String result = new String(b.toString().getBytes("ISO-8859-1"),
					"UTF-8");
			// Log.e("信息", result);

			if (mFile.exists()) {
				mFile.delete();
			}
			return String.valueOf(result);
		} catch (Exception e) {
			e.printStackTrace();
			if (mFile.exists()) {
				mFile.delete();
			}
			return null;
		} finally {
			try {
				if (null != fis) {
					fis.close();
					fis = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				if (null != is1) {
					is1.close();
					is1 = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				if (null != outStream) {
					outStream.close();
					outStream = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}

			if (null != conn) {
				conn.disconnect();
				conn = null;
			}
		}

	}

	// 获得圆角图片的方法
	// public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx)
	// {
	// if (bitmap == null) {
	// return null;
	// }
	//
	// int targetWidth = 140;
	//
	// //比例缩小
	// int width = bitmap.getWidth();
	// int height = bitmap.getHeight();
	// // 计算缩放比例
	// float scaleWidth = ((float) targetWidth) / width;
	// float scaleHeight = ((float) targetWidth) / height;
	// // 取得想要缩放的matrix参数
	// Matrix matrix = new Matrix();
	// matrix.postScale(scaleWidth, scaleHeight);
	// // 得到新的图片
	// Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
	// matrix, true);
	//
	//
	// //得到圆角图片
	// Bitmap output = Bitmap.createBitmap(targetWidth,targetWidth,
	// Config.ARGB_8888);
	// Canvas canvas = new Canvas(output);
	//
	// final int color = 0xffffffff;
	// final Paint paint = new Paint();
	// final Rect rect = new Rect(0, 0, targetWidth, targetWidth);
	// final RectF rectF = new RectF(rect);
	//
	// paint.setAntiAlias(true);
	// canvas.drawARGB(0, 0, 0, 0);
	// paint.setColor(color);
	// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	//
	// // canvas.drawColor(R.color.white);
	// canvas.drawBitmap(newBitmap, rect, rect, paint);
	// return output;
	// }

	//
	//
	// // float roundPx = 20.0f;//通道
	// // float roundPx = 50.0f;//设备
	//
	// // Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	// // bitmap.getHeight(), Config.ARGB_8888);
	// Bitmap output = Bitmap.createBitmap(bitmap.getHeight(),
	// bitmap.getHeight(), Config.ARGB_8888);
	// Canvas canvas = new Canvas(output);
	//
	// final int color = 0xffffffff;
	// final Paint paint = new Paint();
	//
	// final Rect rect = new Rect(0, 0, bitmap.getHeight(), bitmap.getHeight());
	// final RectF rectF = new RectF(rect);
	//
	// paint.setAntiAlias(true);
	// canvas.drawARGB(0, 0, 0, 0);
	// paint.setColor(color);
	// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	//
	// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	//
	// // canvas.drawColor(R.color.white);
	// canvas.drawBitmap(bitmap, rect, rect, paint);
	// return output;
	// }
	//
	// // 获得带倒影的图片方法
	// public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
	// final int reflectionGap = 4;
	// int width = bitmap.getWidth();
	// int height = bitmap.getHeight();
	//
	// Matrix matrix = new Matrix();
	// matrix.preScale(1, -1);
	//
	// Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
	// width, height / 2, matrix, false);
	//
	// Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
	// (height + height / 2), Config.ARGB_8888);
	//
	// Canvas canvas = new Canvas(bitmapWithReflection);
	// canvas.drawBitmap(bitmap, 0, 0, null);
	// Paint deafalutPaint = new Paint();
	// canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
	//
	// canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
	//
	// Paint paint = new Paint();
	// LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
	// bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
	// 0x00ffffff, TileMode.CLAMP);
	// paint.setShader(shader);
	// // Set the Transfer mode to be porter duff and destination in
	// paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	// // Draw a rectangle using the paint with our linear gradient
	// canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
	// + reflectionGap, paint);
	//
	// return bitmapWithReflection;
	// }

	public static void displayImage(ImageView imageView,
			String resourceUri, int defaultResId) {
		
		Log.d("displayimage", "displayImage resourceUri:"+resourceUri+", defeaultResourceId:"+defaultResId);

		if (resourceUri == null) {
			resourceUri = "";
		}
		
		boolean showDefaultImage = !(defaultResId <= 0);
		
		if (TextUtils.isEmpty(resourceUri) && !showDefaultImage) {
			Log.e("displayimage","unable to display image");
			return;
		}

	
		DisplayImageOptions options;
		if (showDefaultImage) {
			options = new DisplayImageOptions.Builder().
			showImageOnLoading(defaultResId).
			showImageForEmptyUri(defaultResId).
			showImageOnFail(defaultResId).
			cacheInMemory(true).
			cacheOnDisk(true).
			considerExifParams(true).
	//		displayer(new RoundedBitmapDisplayer(5)).
	//		imageScaleType(ImageScaleType.EXACTLY).
			build();
		} else {
			options = new DisplayImageOptions.Builder().				
			cacheInMemory(true).
			cacheOnDisk(true).
			considerExifParams(true).
			build();
		}

		ImageLoader.getInstance().displayImage(resourceUri, imageView, options, null);
	}
}
