package com.jovision.commons;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.model.BucketSummary;
import com.baidu.inf.iis.bcs.model.DownloadObject;
import com.baidu.inf.iis.bcs.model.ObjectListing;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.model.ObjectSummary;
import com.baidu.inf.iis.bcs.request.GetObjectRequest;
import com.baidu.inf.iis.bcs.request.ListBucketRequest;
import com.baidu.inf.iis.bcs.request.ListObjectRequest;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;
import com.jovision.Consts;

/**
 * Baidu.OSS impl
 * 
 * @author neo
 */
public class BaiOSS extends BaseOSS {

	private static final String ENC = "UTF-8";
	private static final String HOST = "bcs.duapp.com";
	private static final String OBJECT_PREFIX = "/";

	private static BaiOSS OSS;

	private BaiduBCS bcs;
	private String bucketName;

	private BaiOSS() {
	}

	public static BaiOSS getInstance(String id, String secret, String bucketName) {
		if (null == OSS) {
			OSS = new BaiOSS();
			if (null == bucketName || 0 == bucketName.length()) {
				OSS.init(id, secret);
			} else {
				OSS.init(id, secret, bucketName);
			}
		}

		return OSS;
	}

	@Override
	protected boolean init(String id, String secret, String bucketName) {
		boolean result = false;

		bcs = new BaiduBCS(new BCSCredentials(id, secret), HOST);
		bcs.setDefaultEncoding(ENC);

		ListBucketRequest listBucketRequest = new ListBucketRequest();
		ArrayList<BucketSummary> summaries = (ArrayList<BucketSummary>) bcs
				.listBucket(listBucketRequest).getResult();
		int size = summaries.size();

		for (int i = 0; i < size; i++) {
			if (summaries.get(i).getBucket().equals(bucketName)) {
				result = true;
				break;
			}
		}

		if (false == result) {
			bcs.createBucket(bucketName);
			summaries = (ArrayList<BucketSummary>) bcs.listBucket(
					listBucketRequest).getResult();
			size = summaries.size();

			for (int i = 0; i < size; i++) {
				if (summaries.get(i).getBucket().equals(bucketName)) {
					result = true;
					break;
				}
			}
		}

		this.bucketName = result ? bucketName : null;
		return result;
	}

	@Override
	protected boolean init(String id, String secret) {
		final String current = new SimpleDateFormat(BUCKET_SUBFIX_FORMATTER)
				.format(MyUtils.getChinaTime());
		return init(id, secret, BUCKET_PREFIX + current);
	}

	@Override
	public boolean deinit() {
		bcs = null;

		OSS = null;
		return true;
	}

	@Override
	public boolean put(String bucketName, String localTarget)
			throws FileNotFoundException {
		boolean result = false;

		File file = new File(localTarget);
		if (false == file.exists()) {
			throw new FileNotFoundException();
		}

		String md5 = MyUtils.md5(localTarget);
		if (null != md5) {
			BaiduBCSResponse<ObjectMetadata> response = bcs
					.putObject(new PutObjectRequest(bucketName, OBJECT_PREFIX
							+ file.getName(), file));
			String tag = response.getResult().getETag();
			if (null != tag) {
				result = md5.equals(tag.toUpperCase());
			}
		}

		return result;
	}

	@Override
	public boolean put(String localTarget) throws FileNotFoundException {
		return put(bucketName, localTarget);
	}

	public ArrayList<ObjectSummary> genObjectSummaryList(String bucketName) {
		ArrayList<ObjectSummary> result = new ArrayList<ObjectSummary>();

		ListObjectRequest listObjectRequest = new ListObjectRequest(bucketName);
		listObjectRequest.setPrefix(OBJECT_PREFIX);

		BaiduBCSResponse<ObjectListing> response = bcs
				.listObject(listObjectRequest);
		result.addAll((ArrayList<ObjectSummary>) response.getResult()
				.getObjectSummaries());

		return result;
	}

	@Override
	public boolean get(String bucketName, String remoteTarget,
			String localFolder) throws IOException {
		boolean result = false;

		String local = localFolder + File.separator + remoteTarget;
		File file = new File(local);

		File folder = new File(file.getParent());
		if (false == folder.exists()) {
			folder.mkdirs();
		}

		BaiduBCSResponse<DownloadObject> response = bcs.getObject(
				new GetObjectRequest(bucketName, OBJECT_PREFIX + remoteTarget),
				file);

		String tag = response.getResult().getObjectMetadata().getETag();
		String md5 = MyUtils.md5(local);
		if (null != md5 && null != tag && md5.equals(tag.toUpperCase())) {
			result = true;
		} else {
			file.renameTo(new File(local + BAD_OBJECT_SUBFIX));
		}

		return result;
	}

	@Override
	public boolean get(String remoteTarget, String localFolder)
			throws IOException {
		return get(bucketName, remoteTarget, localFolder);
	}

	@Override
	public boolean pull(String bucketName, String localFolder)
			throws IOException {
		boolean result = false;

		File folder = new File(localFolder);
		if (false == folder.exists()) {
			folder.mkdirs();
		}

		if (folder.isDirectory() && folder.canWrite() && folder.canRead()) {
			ArrayList<ObjectSummary> list = genObjectSummaryList(bucketName);
			for (ObjectSummary summary : list) {
				get(bucketName, summary.getName(), localFolder);
			}

			result = true;
		}

		return result;
	}

	@Override
	public boolean pull(String localFolder) throws IOException {
		return pull(bucketName, localFolder);
	}

	@Override
	public boolean drop(String bucketName) {
		boolean result = true;

		ArrayList<ObjectSummary> list = genObjectSummaryList(bucketName);
		for (ObjectSummary summary : list) {
			bcs.deleteObject(bucketName, summary.getName());
		}

		if (genObjectSummaryList(bucketName).size() > 0) {
			result = false;
			MyLog.d(Consts.TAG_LOGICAL, "drop failed when delete objects!");
		}

		bcs.deleteBucket(bucketName);

		ListBucketRequest listBucketRequest = new ListBucketRequest();
		ArrayList<BucketSummary> summaries = (ArrayList<BucketSummary>) bcs
				.listBucket(listBucketRequest).getResult();
		int size = summaries.size();

		for (int i = 0; i < size; i++) {
			if (summaries.get(i).getBucket().equals(bucketName)) {
				result = false;
				MyLog.d(Consts.TAG_LOGICAL, "found bucket when deleted!");
				break;
			}
		}

		return result;
	}

	@Override
	public boolean drop() {
		return drop(bucketName);
	}

	@Override
	public boolean clean() {
		boolean result = true;

		ListBucketRequest listBucketRequest = new ListBucketRequest();
		ArrayList<BucketSummary> summaries = (ArrayList<BucketSummary>) bcs
				.listBucket(listBucketRequest).getResult();
		int size = summaries.size();

		for (int i = 0; i < size; i++) {
			String bucketName = summaries.get(i).getBucket();
			ArrayList<ObjectSummary> list = genObjectSummaryList(bucketName);
			for (ObjectSummary summary : list) {
				bcs.deleteObject(bucketName, summary.getName());
			}

			if (genObjectSummaryList(bucketName).size() > 0) {
				result = false;
				MyLog.d(Consts.TAG_LOGICAL, "cleaning, drop " + bucketName
						+ "failed when delete objects!");
				continue;
			}

			bcs.deleteBucket(bucketName);
		}

		return result;
	}

}
