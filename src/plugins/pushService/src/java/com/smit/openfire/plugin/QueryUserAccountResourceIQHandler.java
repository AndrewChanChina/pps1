package com.smit.openfire.plugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.session.ClientSession;
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
			        String userAccAndRes = "";
					try {
						userAccAndRes = userAccount+"/"+res.getResource();
						user = userManager.getUser(userAccAndRes);
					} catch (UserNotFoundException e) {
						e.printStackTrace();
					}
					PresenceManager presenceMan = XMPPServer.getInstance().getPresenceManager();
					Collection<Presence> ps = presenceMan.getPresences(user.getName()); 
					boolean isContains = false;
					for(Presence p1 : ps)
					{
						String node = p1.getFrom().getNode();
						String domain = p1.getFrom().getDomain();
						String resc = p1.getFrom().getResource();
						String acc = node + "@" + domain + "/" +resc;
						if(acc.equalsIgnoreCase(userAccAndRes))
						{
							isContains = true;
						}
					}
					if(isContains)
					{
						openimsElement.addElement("presence").addText("true");
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
			isSuccess = UserAccountResourceDBManipulator.insertResource(userAccount, resource, deviceName, deviceId, -1);
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
			String acc = packet.getFrom().getNode() + "@" + packet.getFrom().getDomain();
			String resc = packet.getFrom().getResource();
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
				
				//long lastOfflineDate = OfflineDateGetter.getOfflineDate(user);
				//OfflinePushIQPusher.instance().pushPushIQ(userAccountAndResource, lastOfflineDate);
				long lastPushTime= -1;
				try {
					lastPushTime = UserAccountResourceDBManipulator.getLastPushTime(acc, resc);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				OfflinePushIQPusher.instance().pushPushIQ(userAccountAndResource, lastPushTime);
				try {
					UserAccountResourceDBManipulator.setLastPushTime(acc, resc, System.currentTimeMillis());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return reply;

	}
	
}
