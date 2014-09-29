package com.jovision.bean;


public class DemoBean {
	private String demodevicename;
	private String demoaddress;
	private String democlass;
	private String demotime;
	public DemoBean(String demodevicename,String demoaddress,String democlass,String demotime) {
		this.demodevicename = demodevicename;
		this.demoaddress = demoaddress;
		this.democlass = democlass;
		this.demotime = demotime;
	}
	
	public String getDemodevicename() {
		return demodevicename;
	}
	public void setDemodevicename(String demodevicename) {
		this.demodevicename = demodevicename;
	}
	public String getDemoaddress() {
		return demoaddress;
	}
	public void setDemoaddress(String demoaddress) {
		this.demoaddress = demoaddress;
	}
	public String getDemoclass() {
		return democlass;
	}
	public void setDemoclass(String democlass) {
		this.democlass = democlass;
	}
	public String getDemotime() {
		return demotime;
	}
	public void setDemotime(String demotime) {
		this.demotime = demotime;
	}
}
