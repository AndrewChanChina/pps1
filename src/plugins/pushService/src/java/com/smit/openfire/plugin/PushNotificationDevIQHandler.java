package com.smit.openfire.plugin;

import gov.nist.javax.sip.header.TimeStamp;

import java.io.StringReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.packet.IQ;

import com.smit.openfire.plugin.offlinePushIQ.OfflinePushIQ;
import com.smit.openfire.plugin.offlinePushIQ.OfflinePushStore;
import com.smit.openfire.plugin.offlinePushIQ.SmitIQOnlineDeliverer;
import com.smit.openfire.plugin.util.SmitStringUtil;

public class PushNotificationDevIQHandler  extends IQHandler{
	private static final String MODULE_NAME = "SmitPushNotificationDevIQHandler";
	private static final String NAME_SPACE = "smit:iq:dev:notification";
	private IQHandlerInfo mInfo = null;

	public PushNotificationDevIQHandler()
	{
		super(MODULE_NAME);
		System.out.println("RegisterPushIQHandler: CONSTRUTOR");
		mInfo = new IQHandlerInfo("SmitPushNotificationDev", NAME_SPACE);
	}

	@Override
	public IQHandlerInfo getInfo() {
		// TODO Auto-generated method stub
		System.out.println("PushNotificationDevIQHandler: IQHandlerInfo getInfo() ");
		return mInfo;
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		// TODO Auto-generated method stub
		System.out.println("PushNotificationIQHandler: IQ handleIQ(IQ packet)");
		
		//========================================================
		/*
		 * RECEIVED IQ PACKET EXAMPLE
		 * 
		<iq id="1talO-18" type="get" from="test@smitnn/Smack">
		  <server xmlns="smit:iq:dev:notification">
		    <sendTo>true</sendTo>
		    <pushServiceName>widgets push</pushServiceName>
		    <delayWhileIdle>false</delayWhileIdle>
		    <collapseKey>123456789</collapseKey>
		    <title>Click me</title>
		    <ticker>New Message!</ticker>
		    <uri>http://www.smit.com.cn</uri>
		    <message>111</message>
		  </server>
		</iq>
		*
		*
		*/
		
		//INSERT INTO database. 
		

		//parse the received IQ packet.
		String packetStr = packet.toString();
		String sendTo = SmitStringUtil.TwoSubStringMid(packetStr, "<sendTo>", "</sendTo>");
		String pushServiceName = SmitStringUtil.TwoSubStringMid(packetStr, "<pushServiceName>", "</pushServiceName>");
		String delayWhileIdle = SmitStringUtil.TwoSubStringMid(packetStr, "<delayWhileIdle>", "</delayWhileIdle>");
		String collapseKey = SmitStringUtil.TwoSubStringMid(packetStr, "<collapseKey>", "</collapseKey>");
		String title = SmitStringUtil.TwoSubStringMid(packetStr, "<title>", "</title>");
		String ticker = SmitStringUtil.TwoSubStringMid(packetStr, "<ticker>", "</ticker>");
		String uri = SmitStringUtil.TwoSubStringMid(packetStr, "<uri>", "</uri>");
		String message = SmitStringUtil.TwoSubStringMid(packetStr, "<message>", "</message>");
		
		//Generate new IQ which will sent to user.
		IQ IQSendToUser = new IQ();
		IQSendToUser.setFrom("admin@smit/SMIT"); //iq111.setTo("a@smit/spark");
		IQSendToUser.setTo("test@smit/SMIT");
		Element childElementCopy22 = IQSendToUser.getElement();
		Namespace ns22 = new Namespace("", "smit:iq:notification");
		Element openimsElement22 = childElementCopy22.addElement("openims", ns22.getURI());
		//openimsElement22.addElement("pushID").addText(pushId);
		openimsElement22.addElement("title").addText(title);
		openimsElement22.addElement("uri").addText(uri);
		openimsElement22.addElement("message").addText(message);
		
		long timestamp = System.currentTimeMillis();
		openimsElement22.addElement("time").addText(Long.toString(timestamp));
		
		SessionManager sessionManager = SessionManager.getInstance();
		Collection<ClientSession> sessions = sessionManager.getSessions();
		Iterator<ClientSession> it = sessions.iterator();
		String pushId = null;
		for( ; it.hasNext();)
		{	
			XMPPServer xmppServer = XMPPServer.getInstance();
			ClientSession clientSession = it.next();
			String sessionAddr = clientSession.getAddress().toString();
			if(sessionAddr.equals(packet.getFrom().toString()))
			{
				//the session is where the packet from.
				continue;
			}
			
			//judge if the user registered this kind of push service.
			try{
				pushId = IDRegistrationDBManipulator.queryID(pushServiceName, sessionAddr);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(pushId != null && pushId != "")
			{
				IQSendToUser.setTo(sessionAddr);
				openimsElement22.addElement("pushID").addText(pushId);
				xmppServer.getIQRouter().route(IQSendToUser);
			}
			else
			{
				//DO NOT ADD PUSHID.
				continue;
			}
		}

		if(delayWhileIdle.equals("true"))
		{
			OfflinePushStore instance = OfflinePushStore.instance();
			OfflinePushIQ iqIsExsit = instance.queryPushIQ(pushId);
			if(iqIsExsit != null)
			{
				//We delete the previous one first
				instance.deletePushIQ(collapseKey);
			}
			instance.addOfflinePush(packet, collapseKey );
		}
	
		return null;
	}
}
