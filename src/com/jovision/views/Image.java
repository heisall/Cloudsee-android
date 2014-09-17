package com.jovision.views;

import java.io.Serializable;

/**
 * 通道图片对象
 * 
 * @author Administrator
 * 
 */
public class Image implements Serializable {
	// public int imageIndex = 0;//图片在list中的位置方便更新
	public String imageUrl = "";
	public int imageSort = 0;
	public int imageOwner = 0;
	public String imageDescribe = "";
	public int imageLanguage = 0;
	public String imageName = "";
	public int imageOID = 0;
	public Long imageTimeSpan;
}
