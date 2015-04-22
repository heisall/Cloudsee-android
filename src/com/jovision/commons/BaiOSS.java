
package com.jovision.commons;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

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

    // private BaiduBCS bcs;
    private String bucketName;

    private BaiOSS() {
    }

    public static BaiOSS getInstance(String id, String secret, String bucketName) {
        boolean result = false;

        if (null == OSS) {
            OSS = new BaiOSS();
            if (null == bucketName || 0 == bucketName.length()) {
                result = OSS.init(id, secret);
            } else {
                result = OSS.init(id, secret, bucketName);
            }
        }

        if (false == result) {
            OSS = null;
        }

        return OSS;
    }

    @Override
    protected boolean init(String id, String secret, String bucketName) {
        boolean result = false;

        // bcs = new BaiduBCS(new BCSCredentials(id, secret), HOST);
        // bcs.setDefaultEncoding(ENC);
        //
        // ListBucketRequest listBucketRequest = new ListBucketRequest();
        // ArrayList<BucketSummary> summaries = (ArrayList<BucketSummary>) bcs
        // .listBucket(listBucketRequest).getResult();
        // int size = summaries.size();
        //
        // for (int i = 0; i < size; i++) {
        // if (summaries.get(i).getBucket().equals(bucketName)) {
        // result = true;
        // break;
        // }
        // }
        //
        // if (false == result) {
        // bcs.createBucket(bucketName);
        // summaries = (ArrayList<BucketSummary>) bcs.listBucket(
        // listBucketRequest).getResult();
        // size = summaries.size();
        //
        // for (int i = 0; i < size; i++) {
        // if (summaries.get(i).getBucket().equals(bucketName)) {
        // result = true;
        // break;
        // }
        // }
        // }

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
        OSS = null;
        return true;
    }

    @Override
    public boolean put(String bucketName, String localTarget)
            throws FileNotFoundException {
        boolean result = false;

        // File file = new File(localTarget);
        // if (false == file.exists()) {
        // throw new FileNotFoundException();
        // }
        //
        // String md5 = MyUtils.md5(localTarget);
        // if (null != md5) {
        // BaiduBCSResponse<ObjectMetadata> response = bcs
        // .putObject(new PutObjectRequest(bucketName, OBJECT_PREFIX
        // + file.getName(), file));
        // String tag = response.getResult().getETag();
        // if (null != tag) {
        // result = md5.equals(tag.toUpperCase());
        // }
        // }

        return result;
    }

    @Override
    public boolean put(String localTarget) throws FileNotFoundException {
        boolean result = false;

        if (null != bucketName) {
            result = put(bucketName, localTarget);
        }

        return result;
    }

}
