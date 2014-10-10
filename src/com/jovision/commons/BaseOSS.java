package com.jovision.commons;

import java.io.FileNotFoundException;
import java.io.IOException;

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

	public abstract boolean get(String bucketName, String remoteTarget,
			String localFolder) throws IOException;

	public abstract boolean get(String remoteTarget, String localFolder)
			throws IOException;

	public abstract boolean pull(String bucketName, String localFolder)
			throws IOException;

	public abstract boolean pull(String localFolder) throws IOException;

	public abstract boolean drop(String bucketName);

	public abstract boolean drop();

	public abstract boolean clean();

}
