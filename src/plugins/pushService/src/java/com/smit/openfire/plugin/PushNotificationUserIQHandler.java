package com.smit.openfire.plugin;

import java.awt.List;
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
import com.smit.openfire.plugin.offlinePushIQ.PushIQ;
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
		System.out.println("PushNotificationUserIQHandler: IQHandlerInfo getInfo() ");
		return mInfo;
	}

	/**
	 * 处理push过来的IQ的信息
	 * 主要是对消息进行分发
	 * @date 2011-5-17 13:32:04
	 * @author ANDREW
	 * 
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
	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException 
	{
		PushIQ pushIQ = new PushIQ();
		Element root = packet.getChildElement();
		for (Iterator iter = root.elementIterator(); iter.hasNext(); ) {
			Element element = (Element) iter.next();
			String name = element.getName();
			String text = element.getText();
			if(name.endsWith("type")){
				pushIQ.setIQType(text);
			}
			if(name.endsWith("user")){
				pushIQ.getUsers().add(text);
			}
			if(name.endsWith("delayWhileIdle")){
				pushIQ.setDelayWhileIdle(text);
			}
			if(name.endsWith("collapseKey")){
				pushIQ.setCollapseKey(text);
			}
			if(name.endsWith("title")){
				pushIQ.setTitle(text);
			}
			if(name.endsWith("ticker")){
				pushIQ.setTicker(text);
			}
			if(name.endsWith("uri")){
				pushIQ.setUri(text);
			}
			if(name.endsWith("message")){
				pushIQ.setMessage(text);
			}
		}
		
		//Generate new IQ which will sent to user.
		IQ IQSendToUser = new IQ();
		IQSendToUser.setFrom(packet.getFrom().toString());
		//IQSendToUser.setTo("test@smit/SMIT");
		Element childElementCopy22 = IQSendToUser.getElement();
		Namespace ns22 = new Namespace("", "smit:iq:notification");
		Element openimsElement22 = childElementCopy22.addElement("openims", ns22.getURI());
		openimsElement22.addElement("pushID").addText(pushIQ.getPushID());
		openimsElement22.addElement("title").addText(pushIQ.getTitle());
		openimsElement22.addElement("uri").addText(pushIQ.getUri());
		openimsElement22.addElement("message").addText(pushIQ.getMessage());
		openimsElement22.addElement("ticker").addText(pushIQ.getTicker());	
		long timestamp = System.currentTimeMillis();
		openimsElement22.addElement("time").addText(Long.toString(timestamp));
		
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

			if(pushIQ.getPushID() != null && pushIQ.getPushID() != "")
			{
				if(pushIQ.getUsers().contains((Object)sessionAddr)){
					IQSendToUser.setTo(sessionAddr);
					xmppServer.getIQRouter().route(IQSendToUser);
				}
			}
			else
			{
				//DO NOT ADD PUSHID.
				continue;
			}
		}

		/*
		if(pushIQ.isDelayWhileIdle() == true)
		{
			OfflinePushStore instance = OfflinePushStore.instance();
			OfflinePushIQ iqIsExsit = instance.queryPushIQ(pushIQ.getCollapseKey(), "ALL");
			if(iqIsExsit != null)
			{
				//We do not insert the offline push IQ.
				instance.deletePushIQ(pushIQ.getCollapseKey());
			}
			instance.addOfflinePush(packet, pushIQ.getCollapseKey(), "ALL" );
		}
		*/

		return null;
	}
}
