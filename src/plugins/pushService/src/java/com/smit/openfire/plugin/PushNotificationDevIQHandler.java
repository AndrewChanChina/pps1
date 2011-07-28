package com.smit.openfire.plugin;



import java.io.StringReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
import com.smit.openfire.plugin.offlinePushIQ.PushIQ;
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
		<iq id="72xU7-5" type="get" from="server@smitnn/Smack">
		  <server xmlns="smit:iq:dev:notification">
		    <sendTo>false</sendTo>
		    <pushID>20115301755553949</pushID>
		    <pushID>20115301755553949</pushID>
		    <pushID>20115301755553949</pushID>
		    <pushID>20115301755553949</pushID>
		    <pushServiceName>LDduHliC20I881Aik0v8nBkGK7wEtySl</pushServiceName>
		    <delayWhileIdle>true</delayWhileIdle>
		    <collapseKey>sss</collapseKey>
		    <title>theme</title>
		    <ticker>chuandan</ticker>
		    <uri>http://www.baidu.com</uri>
		    <message>sss</message>
		  </server>
		</iq>
		*
		*
		*/
		
		//INSERT INTO database. 
		String sendTo = "";
		List<String> pushIDList = new ArrayList<String>();
		String pushServiceName = "";
		String delayWhileIdle = "";
		String collapseKey = "";
		String title = "";
		String ticker = "";
		String uri = "";
		String message = "";
		
		Element root = packet.getChildElement();
		for (Iterator iter = root.elementIterator(); iter.hasNext(); ) {
			Element element = (Element) iter.next();
			String name = element.getName();
			String text = element.getText();
			if(name.endsWith("sendTo")){
				sendTo = text;
			}
			if(name.endsWith("pushID")){
				pushIDList.add(text);
			}
			if(name.endsWith("pushServiceName")){
				pushServiceName = text;
			}
			if(name.endsWith("delayWhileIdle")){
				delayWhileIdle = text;
			}
			if(name.endsWith("collapseKey")){
				collapseKey = text;
			}
			if(name.endsWith("title")){
				title = text;
			}
			if(name.endsWith("ticker")){
				ticker = text;
			}
			if(name.endsWith("uri")){
				uri = text;
			}
			if(name.endsWith("message")){
				message = text;
			}
		}

		//parse the received IQ packet.

		List<String> userAccountList = new ArrayList<String>();
		for(int i=0; i<pushIDList.size(); i++)
		{
			String generatedPushId = pushIDList.get(i);
			String userAccount = "";
			try {
				userAccount = IDRegistrationDBManipulator.queryAccountByGeneratedPushID(generatedPushId);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if( userAccount != null && ! userAccount.equalsIgnoreCase(""))
			{
				userAccountList.add(userAccount);
			}
		}
		
		//Generate new IQ which will sent to user.
		IQ IQSendToUser = new IQ();
		IQSendToUser.setFrom(packet.getFrom()); //iq111.setTo("a@smit/spark");
		//IQSendToUser.setTo("test@smit/SMIT");
		Element childElementCopy = IQSendToUser.getElement();
		Namespace ns = new Namespace("", "smit:iq:notification");
		Element openimsElement = childElementCopy.addElement("openims", ns.getURI());
		//openimsElement.addElement("pushID").addText(pushId);
		
		openimsElement.addElement("pushServiceName").addText(pushServiceName);
		openimsElement.addElement("title").addText(title);
		openimsElement.addElement("ticker").addText(ticker);
		openimsElement.addElement("uri").addText(uri);
		openimsElement.addElement("message").addText(message);
		openimsElement.addElement("delayWhileIdle").addText(delayWhileIdle);
		openimsElement.addElement("collapseKey").addText(collapseKey);

		long timestamp = System.currentTimeMillis();
		openimsElement.addElement("time").addText(Long.toString(timestamp));
		
		SessionManager sessionManager = SessionManager.getInstance();
		Collection<ClientSession> sessions = sessionManager.getSessions();
		Iterator<ClientSession> it = sessions.iterator();
		String pushId = null;
		
		//IQ tempIQ = IQSendToUser.createCopy();
		
		// add pushIDs to 
		for( ; it.hasNext(); )
		{	
			XMPPServer xmppServer = XMPPServer.getInstance();
			ClientSession clientSession = it.next();
			String sessionAddr = clientSession.getAddress().toString();
			if(sessionAddr.equals(packet.getFrom().toString()))
			{
				//the session is where the packet from.
				continue;
			}
			
			if(sendTo.equalsIgnoreCase("true") || userAccountList.contains(sessionAddr))
			{
				//judge if the user registered this kind of push service.
				try{
					pushId = IDRegistrationDBManipulator.queryID(pushServiceName, sessionAddr);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(pushId != null && pushId != "")
				{
					IQSendToUser.setTo(sessionAddr);
					
					//delete old "pushID" in IQSendToUser
					List<Element> elements = openimsElement.elements();
					for(int i=0; i<elements.size(); i++)
					{
						Element e= elements.get(i);
						String name = e.getName();
						if(name.equalsIgnoreCase("pushID"))
						{
							openimsElement.remove(e);
						}
					}
					//add new "pushID" in IQSendToUser
					openimsElement.addElement("pushID").addText(pushId);
					xmppServer.getIQRouter().route(IQSendToUser);
				}
				else
				{
					//DO NOT ADD PUSHID.
					continue;
				}
				//remove from list;
				userAccountList.remove(sessionAddr);
			}
		}

		//delete old "pushID" in IQSendToUser
		List<Element> elements = openimsElement.elements();
		for(int i=0; i<elements.size(); i++)
		{
			Element e= elements.get(i);
			String name = e.getName();
			if(name.equalsIgnoreCase("pushID"))
			{
				openimsElement.remove(e);
			}
		}
		
		if(delayWhileIdle.equals("true"))
		{
			OfflinePushStore instance = OfflinePushStore.instance();
			if(sendTo.equalsIgnoreCase("true")) //"true" indicates "send to all", "false" indicates "send to several users"
			{
				OfflinePushIQ iqIsExsit = instance.queryPushIQ(collapseKey, "ALL");
				if (iqIsExsit != null) {
					// We delete the previous one first
					instance.deletePushIQ(iqIsExsit.getId());
				}
				instance.addOfflinePush(IQSendToUser, collapseKey, "ALL");
			}
			else
			{
				for(int i=0; i<userAccountList.size(); i++)
				{
					String userAccount = userAccountList.get(i);
					OfflinePushIQ iqIsExsit = instance.queryPushIQ(collapseKey, userAccount);
					if(iqIsExsit != null)
					{
						//We delete the previous one first
						instance.deletePushIQ(iqIsExsit.getId());
					}
					String aPushId = "";
					try {
						aPushId = IDRegistrationDBManipulator.queryID(pushServiceName, userAccount);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					instance.addOfflinePush(IQSendToUser, collapseKey, userAccount );
				}
			}
		}
		return null;
	}
}
