package com.jovision.bean;

public class WebUrl {
	private String demoUrl;// (视频广场url)
	private int demoSwitch;// (视频广场开关 0屏蔽 1开启)
	private String custUrl;// (我要装监控url)
	private int custSwitch;// (我要装监控开关 0屏蔽 1开启)
	private String statUrl;// (云视通指数url)
	private int statSwitch;// (云视通指数开关 0屏蔽 1开启)
	private String bbsUrl;// (论坛url)
	private int bbsSwitch;// (论坛开关 0屏蔽 1开启)
	private String gcsUrl;// (我是工程商url)
	private int gcsSwitch;// (我是工程商开关 0屏蔽 1开启)

	public String getDemoUrl() {
		return demoUrl;
	}

	public void setDemoUrl(String demoUrl) {
		this.demoUrl = demoUrl;
	}

	public String getCustUrl() {
		return custUrl;
	}

	public int getDemoSwitch() {
		return demoSwitch;
	}

	public void setDemoSwitch(int demoSwitch) {
		this.demoSwitch = demoSwitch;
	}

	public int getCustSwitch() {
		return custSwitch;
	}

	public void setCustSwitch(int custSwitch) {
		this.custSwitch = custSwitch;
	}

	public int getStatSwitch() {
		return statSwitch;
	}

	public void setStatSwitch(int statSwitch) {
		this.statSwitch = statSwitch;
	}

	public int getBbsSwitch() {
		return bbsSwitch;
	}

	public void setBbsSwitch(int bbsSwitch) {
		this.bbsSwitch = bbsSwitch;
	}

	public int getGcsSwitch() {
		return gcsSwitch;
	}

	public void setGcsSwitch(int gcsSwitch) {
		this.gcsSwitch = gcsSwitch;
	}

	public void setCustUrl(String custUrl) {
		this.custUrl = custUrl;
	}

	public String getStatUrl() {
		return statUrl;
	}

	public void setStatUrl(String statUrl) {
		this.statUrl = statUrl;
	}

	public String getBbsUrl() {
		return bbsUrl;
	}

	public void setBbsUrl(String bbsUrl) {
		this.bbsUrl = bbsUrl;
	}

	public String getGcsUrl() {
		return gcsUrl;
	}

	public void setGcsUrl(String gcsUrl) {
		this.gcsUrl = gcsUrl;
	}

}
