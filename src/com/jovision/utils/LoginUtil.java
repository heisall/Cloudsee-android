package com.jovision.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jovision.bean.Device;
import com.jovision.bean.NewProduct;
import com.jovision.commons.MyLog;
import com.jovision.commons.Url;

public class LoginUtil {

	// http://localhost:2109/upload.aspx?cmd=GetProduceList&type=1&language=1&category=1
	// 获取所有新品,可滑动图片

	// 3.23获取主界面新品1 中文2 英文
	public static ArrayList<NewProduct> getNewProducts(int language, int size) {

		ArrayList<NewProduct> npList = new ArrayList<NewProduct>();
		StringBuffer sb = new StringBuffer();
		sb.append(Url.UPLOAD_IMAGE_URL + "?");
		sb.append("cmd=GetProduceList&");
		sb.append("type=" + size + "&");// type代表尺寸
		sb.append("Language=" + language + "&");
		sb.append("category=2");// 2代表android

		MyLog.v("getNewProducts*****", sb.toString());
		String result = JSONUtil.getRequest(sb.toString());
		MyLog.v("getNewProducts*****", result);

		/**
		 * [{"Language":1,"ImageUrl":"/Upload/130137668247955868.png","Owner":0,
		 * "Name":"123","OID":3},
		 * {"Language":1,"ImageUrl":"/Upload/130137665768580868.png"
		 * ,"Owner":0,"Name":"中喂D9016","OID":4},
		 * {"Language":1,"ImageUrl":"/Upload/130137691033424618.png"
		 * ,"Owner":0,"Name":"中喂D9017","OID":5}]
		 */
		try {
			JSONArray temArray = new JSONArray(result);
			if (null != temArray && 0 != temArray.length()) {
				for (int i = 0; i < temArray.length(); i++) {
					JSONObject temObj = temArray.getJSONObject(i);
					NewProduct np = new NewProduct();
					np.newProLanguage = temObj.getInt("Language");
					np.newProImgUrl = temObj.getString("ImageUrl");
					np.newProOwner = temObj.getInt("Owner");
					np.newProName = temObj.getString("Name");
					np.newProOID = temObj.getInt("OID");
					npList.add(np);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return npList;

	}

	// http://192.168.14.3:9991/Upload.aspx?cmd=GetSubNewProductByOID&p_oid=1&Language=1&type=2&category=2
	// 3.24获取单个新品的详细信息
	public static NewProduct getNewProductDetail(int npOid, int size,
			int language) {

		NewProduct npDesc = null;
		StringBuffer sb = new StringBuffer();
		sb.append(Url.UPLOAD_IMAGE_URL + "?");
		sb.append("cmd=GetSubNewProductByOID" + "&");
		sb.append("p_oid=" + npOid + "&");
		sb.append("Language=" + language + "&");
		sb.append("type=" + size + "&");// type代表尺寸
		sb.append("category=2");// 2代表android

		MyLog.v("getNewProductPic*****", sb.toString());
		String result = JSONUtil.getRequest(sb.toString());
		MyLog.v("getNewProductPic（Result）*****", result);
		// [{"MobileType":0,"MoblieCategory":0,"Language":1,"ImageUrl":"/UploadFiles/301.png","Sort":1,"Owner":1,"SortIndex":2,"Host":null,"Name":"测试","OID":1},
		// {"MobileType":0,"MoblieCategory":0,"Language":1,"ImageUrl":"/UploadFiles/AC_1_1.png","Sort":1,"Owner":1,"SortIndex":3,"Host":null,"Name":"测试","OID":1}]

		try {

			JSONObject jsonObject = new JSONObject(result);

			if (null != jsonObject) {
				JSONArray newProDescArray = new JSONArray(
						jsonObject.getString("PDesc"));// 获取描述
				if (null != newProDescArray && newProDescArray.length() > 0) {
					JSONObject newProObj = newProDescArray.getJSONObject(0);
					npDesc = new NewProduct();
					npDesc.newProOwner = newProObj.getInt("Owner");
					npDesc.newProDescription = newProObj
							.getString("Description");
					npDesc.CloudSeeNo = newProObj.getString("CloudSeeNo");
					npDesc.UserName = newProObj.getString("UserName");
					npDesc.UserPwd = newProObj.getString("UserPwd");
					npDesc.ChannelNum = newProObj.getInt("ChannelNum");
					npDesc.newProName = newProObj.getString("Name");
					npDesc.newProOID = newProObj.getInt("OID");

					if (null == npDesc.imageList) {
						npDesc.imageList = new ArrayList<NewProduct>();
					}
					if (null != jsonObject.getString("PDisplay")
							&& !"null".equalsIgnoreCase(jsonObject
									.getString("PDisplay"))) {
						JSONArray newProImageArray = new JSONArray(
								jsonObject.getString("PDisplay"));// 获取
																	// 图片信息
						if (null != newProImageArray
								&& newProImageArray.length() > 0) {
							for (int i = 0; i < newProImageArray.length(); i++) {
								JSONObject temObj = newProImageArray
										.getJSONObject(i);
								NewProduct npImg = new NewProduct();
								npImg.newProLanguage = temObj
										.getInt("Language");
								npImg.newProImgUrl = temObj
										.getString("ImageUrl");
								npImg.newProSort = temObj.getInt("Sort");
								npImg.newProOwner = temObj.getInt("Owner");
								npImg.newProSortIndex = temObj
										.getInt("SortIndex");
								npImg.newProName = temObj.getString("Name");
								npImg.newProOID = temObj.getInt("OID");
								npDesc.imageList.add(npImg);
							}
						}
					}

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return npDesc;
	}

	// http://192.168.14.3:9991/Upload.aspx?Cmd=GetProductInfoByOID&p_oid=1&Language=1&type=2&category=2

	//
	// // 3.25获取单个新品的文字信息
	// public static NewProduct getNewProductText(int npOid, int size,int
	// language) {
	// NewProduct np = null;
	// StringBuffer sb = new StringBuffer();
	// sb.append(Url.UPLOAD_IMAGE_URL1 + "?");
	// sb.append("cmd=GetProductInfoByOID" + "&");
	// sb.append("p_oid=" + npOid + "&");
	// sb.append("Language="+language+"&");
	// sb.append("type="+ size + "&");//type代表尺寸
	// sb.append("category=2");//2代表android
	//
	//
	// MyLog.v("getNewProductTEXT*****", sb.toString());
	// String result = JSONUtil.getRequest(sb.toString());
	// MyLog.v("getNewProductTEXT（Result）*****", result);
	// /**
	// * {"Sort":0,
	// * "Description":"中维JVS-C300Q让您有全新的视频监控体验。",
	// * "MobileType":2,
	// * "MoblieCategory":2,
	// * "CloudSeeNo":"A361",
	// * "UserName":"abc",
	// * "UserPwd":"123",
	// * "ChannelNum":1,
	// * "Language":0,
	// * "ImageUrl":"/UploadFiles/AC_2_1.png",
	// * "Owner":0,
	// * "SortIndex":0,
	// * "Host":null,
	// * "Name":null,
	// * "OID":1}
	// *
	// */
	// try {
	// JSONObject temObj = new JSONObject(result);
	// np = new NewProduct();
	// np.newProSort = temObj.getInt("Sort");
	// np.newProDescription = temObj.getString("Description");
	// np.CloudSeeNo = temObj.getString("CloudSeeNo");
	// np.UserName = temObj.getString("UserName");
	// np.UserPwd = temObj.getString("UserPwd");
	// np.ChannelNum = temObj.getInt("ChannelNum");
	// np.newProLanguage = temObj.getInt("Language");
	// np.newProImgUrl = temObj.getString("ImageUrl");
	// np.newProOwner = temObj.getInt("Owner");
	// np.newProSortIndex = temObj.getInt("SortIndex");
	// np.newProName = temObj.getString("Name");
	// np.newProOID = temObj.getInt("OID");
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return np;
	// }

	/**
	 * http://192.168.15.80:81/Upload.aspx?cmd=UploadDeviceInfo&Account=zha&OS=1
	 * &IMEI=1&Language=1 os：3 (android)
	 * 
	 */

	// 根据设备通道id获取通道在设备中的index
	public static int getPointByOID(Device device, int owner) {
		int index = -1;
		if (null != device.pointList && 0 != device.pointList.size()) {
			for (int i = 0; i < device.pointList.size(); i++) {
				if (device.pointList.get(i).pointOID == owner) {
					index = i;
				}
			}
		}
		return index;
	}

	// md5加密
	public static String Md5(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			try {
				md.update(sourceStr.getBytes("gb2312"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block e.printStackTrace();
		}
		// Log.e("sourceStr",result);
		return result;
	}

	// call_id生成算法（向服务器发送请求的时间戳，从1970至今多少秒）
	public static String secondsTime() {
		String str = "";
		long interval = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date currentTime = new java.util.Date(); // 将截取到的时间字符串转化为时间格式的字符串
			java.util.Date beginTime;
			beginTime = sdf.parse("1970-01-01 00:00:00");
			// 默认为毫秒
			interval = (currentTime.getTime() - beginTime.getTime()) / 1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		str = String.valueOf(interval);

		// Log.e("CALL_ID---------------------------", str);
		return str;
	}

	// 正则验证用户注册用户名
	public static boolean checkUserName(String userName) {
		boolean flag = false;
		Pattern pattern = Pattern.compile("^[A-Za-z0-9@_.-]{6,20}$");
		Matcher isUserName = pattern.matcher(userName);
		if (isUserName.matches()) {
			flag = true;
		} else {
			flag = false;
		}
		// MyLog.v("验证用户名：", flag + "");
		return flag;
	}

	// 正则验证用户注册密码
	public static int checkPass(String pass) {
		int res = -1;
		boolean flag = false;
		Pattern pattern = Pattern.compile("^.{6,16}$");
		Matcher isPass = pattern.matcher(pass);
		if (isPass.matches()) {
			flag = true;
			res = 0;
		} else {
			flag = false;
		}
		// MyLog.v("验证密码：", flag + "");

		if (flag) {// 验证长度和字符通过
			if (equalStr(pass)) {// 全是相同的字母和数字
				res = 1;
			} else if (isOrderNumeric(pass)) {// 连续递增数字
				res = 2;
			} else if (isOrderChar(pass)) {// 连续递增字符
				res = 3;
			}
			// else if(isOrderNumeric_(pass)){//连续递减数字
			// res = 3;
			// }
		}
		return res;
	}

	// 不能全是相同的数字或者字母（如：000000、111111、aaaaaa） 全部相同返回true
	public static boolean equalStr(String numOrStr) {
		boolean flag = true;
		char str = numOrStr.charAt(0);
		for (int i = 0; i < numOrStr.length(); i++) {
			if (str != numOrStr.charAt(i)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	// 不能是连续的数字--递增（如：123456、12345678）连续数字返回true
	public static boolean isOrderNumeric(String numOrStr) {
		boolean flag = true;// 如果全是连续数字返回true
		boolean isNumeric = true;// 如果全是数字返回true
		for (int i = 0; i < numOrStr.length(); i++) {
			if (!Character.isDigit(numOrStr.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {// 如果全是数字则执行是否连续数字判断
			for (int i = 0; i < numOrStr.length(); i++) {
				if (i > 0) {// 判断如123456
					int num = Integer.parseInt(numOrStr.charAt(i) + "");
					int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") + 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;
	}

	// 不能是连续的字符--递增（如：abcdef）连续字符返回true
	public static boolean isOrderChar(String numOrStr) {
		boolean flag = true;// 如果全是连续字符返回true
		for (int i = 0; i < numOrStr.length(); i++) {
			if (i > 0) {// 判断如abcdef
				int num = (int) numOrStr.charAt(i);
				int num_ = (int) numOrStr.charAt(i - 1) + 1;
				if (num != num_) {
					flag = false;
					break;
				}
			}
		}

		return flag;
	}

	// 不能是连续的数字--递减（如：987654、876543）连续数字返回true
	public static boolean isOrderNumeric_(String numOrStr) {
		boolean flag = true;// 如果全是连续数字返回true
		boolean isNumeric = true;// 如果全是数字返回true
		for (int i = 0; i < numOrStr.length(); i++) {
			if (!Character.isDigit(numOrStr.charAt(i))) {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {// 如果全是数字则执行是否连续数字判断
			for (int i = 0; i < numOrStr.length(); i++) {
				if (i > 0) {// 判断如654321
					int num = Integer.parseInt(numOrStr.charAt(i) + "");
					int num_ = Integer.parseInt(numOrStr.charAt(i - 1) + "") - 1;
					if (num != num_) {
						flag = false;
						break;
					}
				}
			}
		} else {
			flag = false;
		}
		return flag;

	}

	/**
	 * 验证云通号
	 * 
	 * @param ystEdit
	 * @return
	 */
	public static boolean checkYSTNum(String ystEdit) {
		boolean flag = true;

		try {
			int kkk;
			for (kkk = 0; kkk < ystEdit.length(); kkk++) {
				char c = ystEdit.charAt(kkk);
				if (c <= '9' && c >= '0') {
					break;
				}
			}
			String group = ystEdit.substring(0, kkk);
			String yst = ystEdit.substring(kkk);
			for (int mm = 0; mm < group.length(); mm++) {
				char c = ystEdit.charAt(mm);
				if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {

				} else {
					flag = false;
				}
			}

			for (int i = 0; i < yst.length(); i++) {
				char c = yst.charAt(i);
				if ((c >= '0' && c <= '9')) {

				} else {
					flag = false;
				}
			}
			int ystValue = "".equals(yst) ? 0 : Integer.parseInt(yst);
			if (kkk >= 4 || kkk <= 0 || ystValue <= 0) {
				flag = false;
			}
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}

		return flag;

	}

	/**
	 * 检查ip地址格式是否正确
	 * 
	 * @author suifupeng
	 * @param ipAdress
	 * @return
	 */
	public static boolean checkIPAdress(String ipAddress) {
		if (ipAddress
				.matches("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查端口号格式是否正确
	 * 
	 * @author suifupeng
	 * @param portNum
	 * @return
	 */
	public static boolean checkPortNum(String portNum) {
		if (portNum.length() < 1 || portNum.length() > 5) {
			return false;
		}
		for (int i = 0; i < portNum.length(); i++) {
			char c = portNum.charAt(i);
			if (c > '9' || c < '0') {
				return false;
			}
		}
		if (Integer.valueOf(portNum).intValue() <= 0
				&& Integer.valueOf(portNum).intValue() > 65535) {
			return false;
		}
		return true;
	}

	// 验证昵称
	public static boolean checkNickName(String str) {
		boolean flag = false;
		try {
			byte[] b = str.getBytes("UTF-8");
			str = new String(b, "UTF-8");
			Pattern pattern = Pattern
					.compile("^[A-Za-z0-9_.()\\+\\-\\u4e00-\\u9fa5]{1,20}$");
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches() && 20 >= str.getBytes().length) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (UnsupportedEncodingException e) {
			flag = false;
		}
		return flag;
	}

	// 验证设备用户名
	public static boolean checkDeviceUsername(String str) {
		boolean flag = false;
		try {
			byte[] b = str.getBytes("UTF-8");
			str = new String(b, "UTF-8");
			Pattern pattern = Pattern.compile("^[A-Za-z0-9_\\-]{1,16}$");
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (UnsupportedEncodingException e) {
			flag = false;
		}
		// if(0 < str.length() && 16 > str.length()){
		// flag = true;
		// }

		return flag;

	}

	// 验证设备密码
	public static boolean checkDevicePwd(String str) {
		boolean flag = false;
		// try {
		// byte[] b = str.getBytes("UTF-8");
		// str = new String(b, "UTF-8");
		// Pattern pattern = Pattern.compile("^[A-Za-z0-9_\\-]{0,15}$");
		// Matcher matcher = pattern.matcher(str);
		// if (matcher.matches()) {
		// flag = true;
		// } else {
		// flag = false;
		// }
		// } catch (UnsupportedEncodingException e) {
		// flag = false;
		// }
		if (0 <= str.length() && 16 > str.length()) {
			flag = true;
		}

		return flag;

	}
}
