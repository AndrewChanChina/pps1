package com.smit.openfire.plugin;

import java.sql.SQLException;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.xmpp.packet.IQ;

import com.smit.openfire.plugin.util.SmitStringUtil;

public class QueryUserAccountResourceIQHandler extends IQHandler{

	private static final String MODULE_NAME = "SmitQueryUserAccountResourceIQHandler";
	private static final String NAME_SPACE = "smit:iq:queryUserAccountResource";
	private IQHandlerInfo mInfo = null;
	
	public QueryUserAccountResourceIQHandler() {
		super(MODULE_NAME);
		// TODO Auto-generated constructor stub
		System.out.println("RegisterPushIQHandler: CONSTRUTOR");
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
		
		String resource = "";
		try {
			resource = UserAccountResourceDBManipulator.queryResource( userAccount, deviceName, deviceId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IQ reply = IQ.createResultIQ(packet);
		reply.setTo(packet.getFrom());
		//reply.set
		
		Element childElementCopy = reply.getElement();
		Namespace ns = new Namespace("", NAME_SPACE);
		Element openimsElement = childElementCopy.addElement("openims", ns.getURI());
		openimsElement.addElement("resource").addText(resource);;
		
		return reply;
	}
	
}
