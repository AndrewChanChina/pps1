package com.smit.openfire.plugin;

import java.sql.SQLException;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;
import org.xmpp.packet.Presence.Show;

import com.smit.openfire.plugin.offlinePushIQ.OfflineDateGetter;
import com.smit.openfire.plugin.offlinePushIQ.OfflinePushIQPusher;
import com.smit.openfire.plugin.util.SmitStringUtil;
import com.smit.vo.SmitUserAccountResource;

public class QueryUserAccountResourceIQHandler extends IQHandler{

	private static final String MODULE_NAME = "SmitQueryUserAccountResourceIQHandler";
	private static final String NAME_SPACE = "smit:iq:queryUserAccountResource";
	private IQHandlerInfo mInfo = null;
	
	public QueryUserAccountResourceIQHandler() {
		super(MODULE_NAME);
		// TODO Auto-generated constructor stub
		System.out.println("SmitQueryUserAccountResourceIQHandler: CONSTRUTOR");
		mInfo = new IQHandlerInfo("QueryUserAccountResource", NAME_SPACE);
	}
	
	@Override
	public IQHandlerInfo getInfo() {
		// TODO Auto-generated method stub
		System.out.println("SmitQueryUserAccountResourceIQHandler: IQHandlerInfo getInfo() ");
		return mInfo;
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		// TODO Auto-generated method stub
		//return null;
		
		String packetStr = packet.toString();
		
		String userAccount = SmitStringUtil.TwoSubStringMid(packetStr, "<userAccount>", "</userAccount>");
		String deviceName = SmitStringUtil.TwoSubStringMid(packetStr, "<deviceName>", "</deviceName>");
		String deviceId = SmitStringUtil.TwoSubStringMid(packetStr, "<deviceId>", "</deviceId>");
		String opCode = SmitStringUtil.TwoSubStringMid(packetStr, "<opCode>", "</opCode>");
		//"resource" use only when opCode="save" and opCode="queryOfflinePush"
		String resource = SmitStringUtil.TwoSubStringMid(packetStr, "<resource>", "</resource>");
		
		IQ reply = IQ.createResultIQ(packet);
		reply.setTo(packet.getFrom());
		Element childElementCopy = reply.getElement();
		Namespace ns = new Namespace("", NAME_SPACE);
		Element openimsElement = childElementCopy.addElement("openims", ns.getURI());
		openimsElement.addElement("userAccount").addText(userAccount);
		openimsElement.addElement("opCode").addText(opCode);
		
		if(opCode.equalsIgnoreCase("query"))
		{
			List<SmitUserAccountResource> list = null;
			try {
				list = UserAccountResourceDBManipulator.queryResource( userAccount);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(list == null)
			{
				openimsElement.addElement("status").addText("fail");
			}
			else
			{
				openimsElement.addElement("status").addText("success");
				for(int i=0; i<list.size(); i++)
				{
					SmitUserAccountResource res = list.get(i);
					openimsElement.addElement("resource").addText(res.getResource());
					openimsElement.addElement("deviceName").addText(res.getDeviceName());
					openimsElement.addElement("deviceId").addText(res.getDeviceId());
					//Check for presence of the userAccount@smitnn/resource
					UserManager userManager = UserManager.getInstance();
			        User user = null;
					try {
						String userAccAndRes = userAccount+"@smit/"+res.getResource();
						user = userManager.getUser(userAccAndRes);
					} catch (UserNotFoundException e) {
						e.printStackTrace();
					}
					PresenceManager presenceMan = XMPPServer.getInstance().getPresenceManager();
					Presence p = presenceMan.getPresence(user);
					// TODO it is ok? CHEN YING ZHONG 2011-6-16 13:32:00
					if(p != null)
					{
						Show s2 = p.getShow();
						if(s2 != null)
						{
							int n = s2.compareTo(Presence.Show.away);
							n = s2.compareTo(Presence.Show.chat);
							n = s2.compareTo(Presence.Show.dnd);
							n = s2.compareTo(Presence.Show.xa);							
							openimsElement.addElement("presence").addText("true");
						}else{							
							openimsElement.addElement("presence").addText("false");
						}
					}
					else
					{
						openimsElement.addElement("presence").addText("false");
					}
				}
			}
		}
		else if(opCode.equalsIgnoreCase("save"))
		{
			boolean isSuccess = false;
			isSuccess = UserAccountResourceDBManipulator.insertResource(userAccount, resource, deviceName, deviceId);
			if(isSuccess)
			{
				openimsElement.addElement("status").addText("success");
			}
			else
			{
				openimsElement.addElement("status").addText("fail");
			}
		}
		else if(opCode.equalsIgnoreCase("queryOfflinePush"))
		{
			String userAccountAndResource = packet.getFrom().toString();
	        UserManager userManager = UserManager.getInstance();
	        User user = null;
			try {
				user = userManager.getUser(userAccountAndResource);
			} catch (UserNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(user != null)
			{
				long lastOfflineDate = OfflineDateGetter.getOfflineDate(user);
				OfflinePushIQPusher.instance().pushPushIQ(userAccountAndResource, lastOfflineDate);
			}
			/*
			boolean isSuccess = false;
			isSuccess = UserAccountResourceDBManipulator.insertResource(userAccount, resource, deviceName, deviceId);
			if(isSuccess)
			{
				openimsElement.addElement("status").addText("success");
			}
			else
			{
				openimsElement.addElement("status").addText("fail");
			}
			*/
		}
		return reply;

	}
	
}
