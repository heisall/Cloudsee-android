
package com.jovision.commons;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class AsyncImageLoader {
    private static AsyncImageLoader instance;
    // public static HashMap<String, SoftReference<Bitmap>> imageCache = new
    // HashMap<String, SoftReference<Bitmap>>();
    public HashMap<String, Bitmap> imageCache = new HashMap<String, Bitmap>();

    public AsyncImageLoader() {
        if (null == imageCache) {
            imageCache = new HashMap<String, Bitmap>();
        }
    }

    public Bitmap loadBitmap(final String imageUrl,
            final ImageCallback imageCallback) {
        // Log.e("缓存图片大小", imageCache.size()+"个,缺" +imageUrl);
        if (imageCache.containsKey(imageUrl)) {
            Bitmap bitmap = imageCache.get(imageUrl);
            // Bitmap bitmap = softReference.get();
            if (bitmap != null) {
                // Log.e("tags", "get iamge from cache  11111"
                // +" url: "+imageUrl);
                return bitmap;
            }
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
            }
        };
        new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = loadBitmapFromUrl(imageUrl);
                if (null != bitmap) {
                    // 不包含或包含的图片为空
                    if (!imageCache.containsKey(imageUrl)
                            || null == imageCache.get(imageUrl)) {
                        imageCache.put(imageUrl, bitmap);
                        // Log.e("tags",
                        // "put iamge to cache"+", imageURl: "+imageUrl);
                    }

                    Message message = handler.obtainMessage(0, bitmap);
                    handler.sendMessage(message);
                }

            }
        }.start();
        return null;
    }

    public Bitmap loadBitmapFromUrl(String imageUrl) {

        //
        // 显示网络上的图片
        Bitmap bitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        options.inSampleSize = 1;
        options.inPreferredConfig = Bitmap.Config.RGB_565; // 默认是Bitmap.Config.ARGB_8888
        /* 下面两个字段需要组合使用 */
        options.inPurgeable = true;
        options.inInputShareable = true;

        if (null == bitmap) {
            MyLog.e("无图", imageUrl);
            InputStream is = null;
            HttpURLConnection conn = null;
            try {
                HttpURLConnection.setFollowRedirects(false);
                URL myFileUrl = new URL(imageUrl);
                conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.setConnectTimeout(15 * 1000);
                // conn.setReadTimeout(6*1000);
                // conn.setDoOutput(true);
                conn.connect();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    is = new BufferedInputStream(conn.getInputStream());
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(is, null, options);
                }

                // if(!imageCache.containsKey(imageUrl) || null ==
                // imageCache.get(imageUrl)){
                // Log.e("tags",
                // "!imageCache.containsKey(imageUrl) || null == imageCache.get(imageUrl)");
                // imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));
                // }
            } catch (IOException e) {
                e.printStackTrace();
                // if(is != null){
                // try {
                // Log.e("tags", "run here colse is");
                // is.close();
                // } catch (IOException e1) {
                // // TODO Auto-generated catch block
                // e1.printStackTrace();
                // }
                // }
                // if(conn != null){
                // Log.e("tags", "run here conn is");
                // conn.disconnect();
                // }

                bitmap = null;
            } finally {
                if (is != null) {
                    try {
                        // Log.e("tags", "run here colse is");
                        is.close();
                        is = null;
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                if (conn != null) {
                    // Log.e("tags", "run here conn is");
                    conn.disconnect();
                    conn = null;
                }

            }
        }
        // Log.v("bitmap.getByteCount()", bitmap.getByteCount()+"");

        return bitmap;
    }

    public Bitmap getBitmap(String imageUrl) {
        // 显示网络上的图片
        Bitmap bitmap = null;
        // Log.v("读取图片url", imageUrl);

        if (imageCache.containsKey(imageUrl)) {
            // 从缓存中读取
            bitmap = imageCache.get(imageUrl);
        }
        return bitmap;

    }

    public interface ImageCallback {
        public void imageLoaded(Bitmap imageBitmap, String imageUrl);
    }

    // 两种方式获取
    public Bitmap getBitmapFromUrl(String imageUrl) {
        Bitmap bitmap = getBitmap(imageUrl);// 缓存取
        if (null != bitmap) {
            return bitmap;
        } else {
            bitmap = loadBitmapFromUrl(imageUrl);// 往上读
            if (null != bitmap) {
                // 不包含或包含的图片为空
                if (!imageCache.containsKey(imageUrl)
                        || null == imageCache.get(imageUrl)) {// 放到缓存中
                    imageCache.put(imageUrl, bitmap);
                }
            }
        }
        return bitmap;
    }

    public static AsyncImageLoader getInstance() {
        if (null == instance) {
            instance = new AsyncImageLoader();
        }

        return instance;
    }

}
