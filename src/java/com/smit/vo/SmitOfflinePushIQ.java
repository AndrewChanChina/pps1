package com.smit.vo;

import java.io.Serializable;
import java.util.Date;

public class SmitOfflinePushIQ implements Serializable 
{
	private Integer id;
	private String collapseKey;
	private String IQText;
	private Integer IQSize;
	private Date creationDate;
	private String sendTo;


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getCollapseKey() {
		return collapseKey;
	}
	public void setCollapseKey(String collapseKey) {
		this.collapseKey = collapseKey;
	}
	
	public String getIQText() {
		return IQText;
	}
	public void setIQText(String iQText) {
		IQText = iQText;
	}
	
	public Integer getIQSize() {
		return IQSize;
	}
	public void setIQSize(Integer iQSize) {
		IQSize = iQSize;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getSendTo() {
		return sendTo;
	}
	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}
}
