package com.smit.vo;

public class Subscriber {

	private int id;
	private String jid;
	private String feed;
	private String sub_time;
	
	public Subscriber(){
		
	}
	public Subscriber(String jid,String feed){
		this.jid = jid;
		this.feed = feed;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}
	public String getFeed() {
		return feed;
	}
	public void setFeed(String feed) {
		this.feed = feed;
	}
	public String getSub_time() {
		return sub_time;
	}
	public void setSub_time(String sub_time) {
		this.sub_time = sub_time;
	}
	
}
