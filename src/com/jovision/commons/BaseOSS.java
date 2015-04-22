
package com.jovision.commons;

import java.io.FileNotFoundException;

/**
 * Open Storage Service super class
 * 
 * @author neo
 */
public abstract class BaseOSS {

    protected static final String BAD_OBJECT_SUBFIX = "_";

    public static final String BUCKET_PREFIX = "jcs-";
    public static final String BUCKET_SUBFIX_FORMATTER = "yyyy-MM";

    protected abstract boolean init(String id, String secret, String bucketName);

    protected abstract boolean init(String id, String secret);

    public abstract boolean deinit();

    public abstract boolean put(String bucketName, String localTarget)
            throws FileNotFoundException;

    public abstract boolean put(String localTarget)
            throws FileNotFoundException;

}
