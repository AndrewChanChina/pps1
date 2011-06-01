package com.smit.openfire.plugin.offlinePushIQ;

import java.util.ArrayList;
import java.util.List;

import org.xmpp.packet.IQ;


public class PushIQ extends IQ {
	 
	private String id;	 
	private String pushID;	 
	private String title;	 
	private String message; 
	private String uri;	 
	private String ticker;
	
	// 这两个是不发送出去的
	private String type;
	private boolean delayWhileIdle;
	private String collapseKey;
	private List<String> users = new ArrayList<String>();
	 
	public static String getElementName(){	    	
		return "openims";	 
	}
    public static String getNamespace(){
    	return "smit:iq:notification";
    }  
	    
	
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
	       buf.append("<").append(getElementName()).append(" xmlns=\"").append(getNamespace()).append("\">");
	       buf.append(getXML());
	       buf.append("</").append(getElementName()).append(">");
     return buf.toString();
	}
	
	private String getXML(){
		StringBuilder buf = new StringBuilder();
		buf.append("<pushID>").append(pushID).append("</pushID>");
		buf.append("<title>").append(title).append("</title>");
		buf.append("<ticker>").append(ticker).append("</ticker>");
		buf.append("<uri>").append(uri).append("</uri>");
		buf.append("<message>").append(message).append("</message>");
		return buf.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPushID() {
		return pushID;
	}
	/**
	 * 对特殊的两种情况做了处理
	 * @param pushID
	 */
	public void setPushID(String pushID){
		this.pushID = pushID;		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public String getIQType() {
		return type;
	}
	public void setIQType(String type) {
		this.pushID = type;
		this.type = type;
	}
	public List<String> getUsers() {
		return users;
	}
	public void setUsers(List<String> users) {
		this.users = users;
	}
	public boolean isDelayWhileIdle() {
		return delayWhileIdle;
	}
	public void setDelayWhileIdle(boolean delayWhileIdle) {
		this.delayWhileIdle = delayWhileIdle;
	}
	public void setDelayWhileIdle(String delay){
		if("false".endsWith(delay)){
			this.delayWhileIdle = false;
		}else if("true".endsWith(delay)){
			this.delayWhileIdle = true;
		}else{
			this.delayWhileIdle = false;
		}
	}
	public String getCollapseKey() {
		return collapseKey;
	}
	public void setCollapseKey(String collapseKey) {
		this.collapseKey = collapseKey;
	}

}
