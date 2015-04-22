
package com.jovision.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * 返回JSON 数据的网络请求处理
 */
public class JsonHttpResponseHandler extends AsyncHttpResponseHandler {

    /**
     * Fired when a request returns successfully and contains a json object at
     * the base of the response string. Override to handle in your own code.
     * 
     * @param response the parsed json object found in the server response (if
     *            any)
     */
    public void onSuccess(JSONObject response) {
    }

    /**
     * Fired when a request returns successfully and contains a json array at
     * the base of the response string. Override to handle in your own code.
     * 
     * @param response the parsed json array found in the server response (if
     *            any)
     */
    public void onSuccess(JSONArray response) {
    }

    @Override
    protected void handleSuccessMessage(String responseBody) {
        super.handleSuccessMessage(responseBody);

        try {
            Object jsonResponse = parseResponse(responseBody);
            if (jsonResponse instanceof JSONObject) {
                onSuccess((JSONObject) jsonResponse);
            } else if (jsonResponse instanceof JSONArray) {
                onSuccess((JSONArray) jsonResponse);
            }
        } catch (JSONException e) {
            onFailure(e, responseBody);
        }
    }

    protected Object parseResponse(String responseBody) throws JSONException {
        if (responseBody != null) {
            return new JSONTokener(responseBody).nextValue();
        } else {
            return "";
        }

    }
}
