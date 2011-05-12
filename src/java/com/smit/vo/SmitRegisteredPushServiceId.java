package com.smit.vo;

import java.io.Serializable;
import java.util.Date;

public class SmitRegisteredPushServiceId implements Serializable 
{
	private Integer id;
	private String pushServiceID;
	private String serviceType;
	private String userName;
	private String userAccount;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getPushServiceID() {
		return pushServiceID;
	}
	public void setPushServiceID(String pushServiceID) {
		this.pushServiceID = pushServiceID;
	}
	
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
}
