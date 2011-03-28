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
	
	Date mCreationDate = null;
	
	public OfflinePushIQ(Date creationDate, Element element)
	{
		super(element, true);
		this.mCreationDate = creationDate;
	}
	
	public Date getCreationDate()
	{
		return mCreationDate;
	}
}
