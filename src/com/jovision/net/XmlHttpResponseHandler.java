
package com.jovision.net;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 接口返回xml 数据网络请求处理
 * 
 * @author wby
 */
public class XmlHttpResponseHandler extends AsyncHttpResponseHandler {
    /**
     * Fired when a request returns successfully and contains a json object at
     * the base of the response string. Override to handle in your own code.
     * 
     * @param response the parsed inputstream found in the server response (if
     *            any)
     */
    public void onSuccess(InputStream response) {
    }

    @Override
    protected void handleSuccessMessage(String responseBody) {
        super.handleSuccessMessage(responseBody);
        if (responseBody != null && responseBody.length() > 0) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    responseBody.getBytes());
            onSuccess(byteArrayInputStream);
        } else {
            onFailure(new Throwable("data error"), "data error");
        }
    }
}
