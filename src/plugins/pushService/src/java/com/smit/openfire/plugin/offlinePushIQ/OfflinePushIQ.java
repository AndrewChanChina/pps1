/*
 * 
 * AUTHOR : Qianfeng Shen
 * 
 * 
 */

package com.smit.openfire.plugin.offlinePushIQ;



import java.util.Date;

import org.dom4j.Element;
import org.xmpp.packet.IQ;

public class OfflinePushIQ extends IQ {
	
	Integer mId = null;
	Date mCreationDate = null;
	String mSendTo = "";
	String mCollapseKey;
	
	public OfflinePushIQ(Integer id, Date creationDate, Element element, String sendTo, String collapseKey)
	{
		super(element, true);
		this.mId = id;
		this.mCreationDate = creationDate;
		this.mSendTo = sendTo;
		this.mCollapseKey = collapseKey;
	}
	
	public Integer getId()
	{
		return mId;
	}
	public Date getCreationDate()
	{
		return mCreationDate;
	}
	
	public String getSendTo()
	{
		return mSendTo;
	}
	
	public String getCollapseKey()
	{
		return mCollapseKey;
	}
}
