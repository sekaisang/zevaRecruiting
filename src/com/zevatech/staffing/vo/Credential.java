package com.zevatech.staffing.vo;

public class Credential {

	private String systemID;
	private String userID;
	private String password;
	
	public Credential() {
	}
	
	public Credential(String systemID, String userID, String password) {
		this.systemID = systemID;
		this.userID = userID;
		this.password = password;
	}
	
	public String getSystemID() {
		return systemID;
	}
	public void setSystemID(String systemID) {
		this.systemID = systemID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
