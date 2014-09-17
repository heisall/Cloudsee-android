package com.jovision.bean;

import java.util.ArrayList;

/**
 * 主界面新品
 * 
 * @author Administrator
 * 
 */
public class NewProduct {
	public int newProSort = 0;
	public String newProDescription = "";
	public int newProLanguage = 0;
	public String newProImgUrl = "";
	public int newProOwner = 0;
	public String newProName = "";
	public int newProOID = 0;
	public int newProSortIndex = 0;
	public String CloudSeeNo = "";
	public String UserName = "";
	public String UserPwd = "";
	public int ChannelNum = 0;

	// 存放新品图片
	public ArrayList<NewProduct> imageList = new ArrayList<NewProduct>();

}
