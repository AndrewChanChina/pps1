/*
 * 
 * AUTHOR : Qianfeng Shen
 * 
 * 
 */

package com.smit.openfire.plugin.offlinePushIQ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.component.ComponentManagerFactory;
import org.xmpp.packet.IQ;

public class SmitIQOnlineDeliverer {
	
	private static XMPPServer mXMPPServer = null;
	private static SmitIQOnlineDeliverer mInstance = null;
	
	private SmitIQOnlineDeliverer()
	{
		mXMPPServer = XMPPServer.getInstance();
		if(mXMPPServer == null)
		{
			System.out.println("SmitIQDeliverer CONSTRUCTOR: XMPPServer instance is null");
		}
	}
	
	public static SmitIQOnlineDeliverer instance()
	{
		if(mInstance == null)
		{
			mInstance = new SmitIQOnlineDeliverer();
		}
		return mInstance;
	}
	
	public void deliverToOne(IQ iq)
	{
		if(mXMPPServer == null)
		{
			return;
		}
		SessionManager sessionManager = SessionManager.getInstance();
		Collection<ClientSession> sessions = sessionManager.getSessions();
		Iterator<ClientSession> it = sessions.iterator();
		String iqTo = iq.getTo().toString();
		for( ; it.hasNext();)
		{
			String sessionAddr = it.next().getAddress().toString();
			if(iqTo.equalsIgnoreCase(sessionAddr))
			{
				iq.setTo(iqTo);
				mXMPPServer.getIQRouter().route(iq);
				break;
			}
		}
	}
	
	public void broadcast(IQ iq)
	{
		if(mXMPPServer == null)
		{
			return;
		}
		SessionManager sessionManager = SessionManager.getInstance();
		Collection<ClientSession> sessions = sessionManager.getSessions();
		Iterator<ClientSession> it = sessions.iterator();
		for( ; it.hasNext();)
		{
			ClientSession clientSession = it.next();
			String sessionAddr = clientSession.getAddress().toString();
			iq.setTo(sessionAddr);
			mXMPPServer.getIQRouter().route(iq);
			break;

		}
	}
	
	public void multicast(IQ iq, ArrayList<String> userAccountList)
	{
		
	}
	
}
