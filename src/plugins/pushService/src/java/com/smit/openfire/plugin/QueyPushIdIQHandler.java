package com.smit.openfire.plugin;

import java.sql.SQLException;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.xmpp.packet.IQ;

import com.smit.openfire.plugin.util.SmitStringUtil;
import com.smit.vo.SmitRegisteredPushServiceId;

public class QueyPushIdIQHandler extends IQHandler{
	
	private static final String MODULE_NAME = "SmitQueryPushIdIQHandler";
	private static final String NAME_SPACE = "smit:iq:queryPushId";
	private IQHandlerInfo mInfo = null;
	
	public QueyPushIdIQHandler() {
		super(MODULE_NAME);
		// TODO Auto-generated constructor stub
		System.out.println("RegisterPushIQHandler: CONSTRUTOR");
		
		mInfo = new IQHandlerInfo("QueryPushId", NAME_SPACE);
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
		//return null;
		
		String packetStr = packet.toString();
		String pushServiceName = SmitStringUtil.TwoSubStringMid(packetStr, "<pushServiceName>", "</pushServiceName>");
		String userAccount = SmitStringUtil.TwoSubStringMid(packetStr, "<userAccount>", "</userAccount>");
		
		//String pushId = "";
		List<SmitRegisteredPushServiceId> list = null;
		try {
			//pushId = IDRegistrationDBManipulator.queryID(pushServiceName, userAccount);
			list = IDRegistrationDBManipulator.queryID(pushServiceName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IQ reply = IQ.createResultIQ(packet);
		reply.setTo(packet.getFrom());
		Element childElementCopy = reply.getElement();
		Namespace ns = new Namespace("", NAME_SPACE);
		Element openimsElement = childElementCopy.addElement("openims", ns.getURI());
		if(list == null)
		{
			openimsElement.addElement("status").addText("fail");
			openimsElement.addElement("pushServiceName").addText(pushServiceName);
			openimsElement.addElement("userAccount").addText(userAccount);
		}
		else
		{
			openimsElement.addElement("status").addText("success");
			openimsElement.addElement("pushServiceName").addText(pushServiceName);
			openimsElement.addElement("userAccount").addText(userAccount);
			for(int i=0; i<list.size(); i++)
			{
				openimsElement.addElement("pushID").addText(list.get(i).getPushServiceID());
			}
		}
		return reply;
	}
}
