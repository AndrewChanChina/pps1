/*
 * 
 * AUTHOR : Qianfeng Shen
 * 
 * 
 */

package com.smit.openfire.plugin.offlinePushIQ;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.user.User;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.dom4j.Element;

import com.smit.openfire.plugin.IDRegistrationDBManipulator;
import com.smit.openfire.plugin.util.SmitStringUtil;

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
	
	/*
	public class MyTask extends TimerTask{
		private String mUserAccount = "";
		private long mLastPushTime;
	    public MyTask(final String userAccount, final long lastPushTime) {
	    	mUserAccount = userAccount;
	    	mLastPushTime = lastPushTime;
	    }
	   
	    public void run() {
	    	long lastPushTime = mLastPushTime;
	    	String userAccount = mUserAccount;
	    	
	     }
	}*/
	
	public void pushPushIQ(String userAccount, long lastPushTime)
	{
		//Timer timer = new Timer();
		//MyTask task = new MyTask(userAccount, lastPushTime);
		//timer.schedule(task, 800);
		XMPPServer mXMPPServer = XMPPServer.getInstance();
		ArrayList<OfflinePushIQ> array = OfflinePushStore.instance().queryAllPushIQ();
		if(array == null || array.size() == 0)
		{
			return; 
		}
		OfflinePushIQ offlinePushIQ = null;
		int i = 0;
		
		while(i<array.size())
		{
			offlinePushIQ = array.get(i);
			Date creationDate = offlinePushIQ.getCreationDate();
			long time = creationDate.getTime();
			if(lastPushTime > time)
			{
				//Neglect the push IQ
			}
			else
			{
				//Push PushIQ
				String sendTo = offlinePushIQ.getSendTo();
				String collapseKey = offlinePushIQ.getCollapseKey();
				IQ iq = offlinePushIQ.createCopy();
				IQ iqNew = new IQ();
				iq.setID(iqNew.getID());
				iq.setType(Type.result);
				String IQXmlStr = iq.toString();
				String pushServiceName = SmitStringUtil.TwoSubStringMid(IQXmlStr, "<pushServiceName>", "</pushServiceName>");
				String pushId = "";
				try {
					pushId = IDRegistrationDBManipulator.queryID(pushServiceName, userAccount);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Element e = iq.getChildElement();
				e.addElement("pushID").addText(pushId);
				//e.addElement("pushID").addText("URL");
				
				if(sendTo.equalsIgnoreCase("all"))
				{
					
					iq.setTo(userAccount);
					mXMPPServer.getIQRouter().route(iq);
					//SmitIQOnlineDeliverer.instance().deliverToOne(iq);
				}
				else if(sendTo.equalsIgnoreCase(userAccount))
				{
					//IQ iq = offlinePushIQ.createCopy();
					iq.setTo(userAccount);
					mXMPPServer.getIQRouter().route(iq);
					//SmitIQOnlineDeliverer.instance().deliverToOne(iq);
					//OfflinePushStore.instance().deletePushIQ(offlinePushIQ.getId());
					//Collection<ClientSession> sess = SessionManager.getInstance().getSessions(userAccount);
					//Iterator<ClientSession> it = sess.iterator();
					/*
					for( ; it.hasNext();)
					{
						String sessionAddr = it.next().
						if(iqTo.equalsIgnoreCase(sessionAddr))
						{
							iq.setTo(iqTo);
							mXMPPServer.getIQRouter().route(iq);
							break;
						}
					}
					*/
				}
				else
				{
					// Do not deliver this OfflinePushIQ
				}
			}
			i++;
		}
	}
	
}
