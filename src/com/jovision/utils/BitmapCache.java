package com.jovision.utils;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video;

//http://www.360doc.com/content/14/1112/14/20361811_424545444.shtml
/**
 * 
 * @author 该类用于图片缓存，防止内存溢出
 */
public class BitmapCache {
	static BitmapCache bitmapCache;
	/** 用于Chche内容的存储 */
	Hashtable bitmapRefs;
	/** 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中） */
	ReferenceQueue q;

	/**
	 * 继承SoftReference，使得每一个实例都具有可识别的标识。
	 */
	class BtimapRef extends SoftReference {
		String _key = "";

		public BtimapRef(Bitmap bmp, ReferenceQueue q, String key) {
			super(bmp, q);
			_key = key;
		}
	}

	public BitmapCache() {
		bitmapRefs = new Hashtable();
		q = new ReferenceQueue();
	}

	/**
	 * 取得缓存器实例
	 */
	public static BitmapCache getInstance() {
		if (bitmapCache == null) {
			bitmapCache = new BitmapCache();
		}
		return bitmapCache;

	}

	/**
	 * 以软引用的方式对一个Bitmap对象的实例进行引用并保存该引用
	 */
	public void addCacheBitmap(Bitmap bmp, String key) {
		cleanCache();// 清除垃圾引用
		BtimapRef ref = new BtimapRef(bmp, q, key);
		bitmapRefs.put(key, ref);
	}

	/**
	 * 依据所指定的drawable下的图片资源ID号（可以根据自己的需要从网络或本地path下获取），重新获取相应Bitmap对象的实例
	 */
	public Bitmap getBitmap(String path, String kind) {
		Bitmap bmp = null;
		// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (bitmapRefs.containsKey(path)) {
			BtimapRef ref = (BtimapRef) bitmapRefs.get(path);
			bmp = (Bitmap) ref.get();
		}
		// 如果没有软引用，或者从软引用中得到的实例是null，重新构建一个实例，
		// 并保存对这个新建实例的软引用
		if (bmp == null) {
			if ("image".equalsIgnoreCase(kind)) {
				bmp = loadImageBitmap(path, 5);// BitmapFactory.decodeResource(context.getResources(),
												// resId);
			} else if ("video".equalsIgnoreCase(kind)) {
				bmp = loadVideoBitmap(path);
			}
			this.addCacheBitmap(bmp, path);
		}
		return bmp;
	}

	/**
	 * 依据所指定的drawable下的图片资源ID号（可以根据自己的需要从网络或本地path下获取），重新获取相应Bitmap对象的实例
	 */
	public Bitmap getCacheBitmap(String path) {
		Bitmap bmp = null;
		// 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
		if (bitmapRefs.containsKey(path)) {
			BtimapRef ref = (BtimapRef) bitmapRefs.get(path);
			bmp = (Bitmap) ref.get();
		}
		return bmp;
	}

	/**
	 * 按照路径加载图片
	 * 
	 * @param path
	 *            图片资源的存放路径
	 * @param scalSize
	 *            缩小的倍数
	 * @return
	 */
	public static Bitmap loadImageBitmap(String path, int scalSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inSampleSize = scalSize;
		Bitmap bmp = BitmapFactory.decodeFile(path, options);
		return bmp;
	}

	/**
	 * 获取视频缩略图
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap loadVideoBitmap(String filePath) {
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath,
				Video.Thumbnails.MINI_KIND);
		return bitmap;
	}

	public void cleanCache() {
		BtimapRef ref = null;
		while ((ref = (BtimapRef) q.poll()) != null) {
			bitmapRefs.remove(ref._key);
		}
	}

	// 清除Cache内的全部内容
	public void clearCache() {
		cleanCache();
		bitmapRefs.clear();
		System.gc();
		System.runFinalization();
	}

}
