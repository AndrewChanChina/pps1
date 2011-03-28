package com.smit.openfire.plugin;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.dom4j.Element;
import org.dom4j.Namespace;
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

public class PushNotificationUserIQHandler  extends IQHandler{
	private static final String MODULE_NAME = "SmitPushNotificationUserIQHandler";
	private static final String NAME_SPACE = "smit:iq:user:notification";
	//private static final String NAME_SPACE = "jabber:iq:register";
	private IQHandlerInfo mInfo = null;
	
	public PushNotificationUserIQHandler()
	{
		super(MODULE_NAME);
		System.out.println("RegisterPushIQHandler: CONSTRUTOR");
		mInfo = new IQHandlerInfo("AssHole2", NAME_SPACE);
	}

	@Override
	public IQHandlerInfo getInfo() {
		// TODO Auto-generated method stub
		System.out.println("PushNotificationIQHandler: IQHandlerInfo getInfo() ");
		return mInfo;
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		// TODO Auto-generated method stub
		//========================================================
		/*
<iq id="78gAV-84" type="get" from="admin@smitnn/Smack">
  <server xmlns="smit:iq:user:notification">
    <type>notification</type>
    <user>test@smit/SMIT</user>
    <delayWhileIdle>false</delayWhileIdle>
    <collapseKey>123456789</collapseKey>
    <title>Click me</title>
    <ticker>New Message!</ticker>
    <uri>http://www.smit.com.cn</uri>
    <message>Good</message>
  </server>
</iq>
		*/
		
		// TODO Auto-generated method stub
		System.out.println("PushNotificationIQHandler: IQ handleIQ(IQ packet)");

		//parse the received IQ packet.
		String packetStr = packet.toString();
		String type = SmitStringUtil.TwoSubStringMid(packetStr, "<type>", "</type>");
		String user = SmitStringUtil.TwoSubStringMid(packetStr, "<user>", "</user>");
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
		openimsElement22.addElement("ticker").addText(ticker);
		
		String pushId = null;
		if(type.equalsIgnoreCase("alert"))
		{
			pushId = "WARNING";
		}
		else if(type.equalsIgnoreCase("notification"))
		{
			pushId = "PENDINGINTENT";
		}
		openimsElement22.addElement("pushID").addText(pushId);
		
		SessionManager sessionManager = SessionManager.getInstance();
		Collection<ClientSession> sessions = sessionManager.getSessions();
		Iterator<ClientSession> it = sessions.iterator();
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

			if(pushId != null && pushId != "")
			{
				IQSendToUser.setTo(sessionAddr);
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
			OfflinePushIQ iqIsExsit = instance.queryPushIQ(collapseKey);
			if(iqIsExsit != null)
			{
				//We do not insert the offline push IQ.
				instance.deletePushIQ(collapseKey);
			}
			instance.addOfflinePush(packet, collapseKey);		
		}
		
		/*
		if(sendTo.equals("true"))
		{
			//Send to all online users.
			SmitIQOnlineDeliverer.instance().broadcast(IQSendToUser);
		}
		else
		{
			//Send to several users.
			
		}
		*/
		
		//SmitIQOnlineDeliverer.instance().deliverToOne(iq111);
		
		return null;
	}
}
