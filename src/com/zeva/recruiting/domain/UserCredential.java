package com.zeva.recruiting.domain;

import java.util.Date;

public class UserCredential {
	
	
    private int guid;
    private String username;
    private String password;
    private char activeFlag;
    private Date lastUpdate;
    
    public UserCredential(){};
    
    public UserCredential(String username,String password){
    	this.username = username;
    	this.password = password;

    }
    
	public int getGuid() {
		return guid;
	}
	public void setGuid(int guid) {
		this.guid = guid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public char getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(char activeFlag) {
		this.activeFlag = activeFlag;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
    
}
