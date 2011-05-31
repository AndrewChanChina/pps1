/*
 * 
 * AUTHOR : Qianfeng Shen
 * 
 * 
 */

package com.smit.openfire.plugin.offlinePushIQ;

import java.util.ArrayList;
import java.util.Date;

import org.jivesoftware.openfire.user.User;
import org.xmpp.packet.IQ;

public class OfflinePushIQPusher {
	
	private static OfflinePushIQPusher mInstance = null;
	
	public static OfflinePushIQPusher instance()
	{
		if(mInstance == null)
		{
			mInstance = new OfflinePushIQPusher();
		}
		return mInstance;
	}
	
	public void pushPushIQ(String userAccount, long userLastOfflineDate)
	{
		ArrayList<OfflinePushIQ> array = OfflinePushStore.instance().queryAllPushIQ();
		if(array == null || array.size() == 0)
		{
			return; 
		}
		OfflinePushIQ offlinePushIQ = null;
		int i = 0;
		for(offlinePushIQ = array.get(0); i<array.size(); i++)
		{
			Date creationDate = offlinePushIQ.getCreationDate();
			long time = creationDate.getTime();
			if(userLastOfflineDate > time)
			{
				//Neglect the push IQ
			}
			else
			{
				//Push PushIQ
				IQ iq = offlinePushIQ.createCopy();
				iq.setTo(userAccount);
				SmitIQOnlineDeliverer.instance().deliverToOne(iq);
			}
			
		}
	}
	
}
