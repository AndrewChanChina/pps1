package com.smit.openfire.plugin;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.List;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.XMPPPacketReader;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.util.Log;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.packet.IQ;


public class RegisterPushIQHandler extends IQHandler{
	private static final String MODULE_NAME = "SmitRegisterPushServiceIQHandler";
	private static final String NAME_SPACE = "smit:iq:registerPushService";
	private IQHandlerInfo mInfo = null;
	
	public RegisterPushIQHandler()
	{
		super(MODULE_NAME);
		System.out.println("RegisterPushIQHandler: CONSTRUTOR");
		mInfo = new IQHandlerInfo("AssHole", NAME_SPACE);
	}
	
	/*
	* (non-Javadoc)
	* @see org.jivesoftware.openfire.handler.IQHandler#getInfo()
	*/
	@Override
	public IQHandlerInfo getInfo()
	{
		System.out.println("RegisterPushIQHandler: IQHandlerInfo getInfo() ");
		return mInfo;
	}
	
	/*
	* (non-Javadoc)
	* 
	* @see org.jivesoftware.openfire.handler.IQHandler#handleIQ(org.xmpp.packet.IQ)
	*/
	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException
	{
		System.out.println("RegisterPushIQHandler: IQ handleIQ(IQ packet)");
		
		/*
		 * 
		 * WE GOT 
		 * 
		<iq id="gIk2F-5" type="get" from="test@smit/SMIT">
		  <openims xmlns="smit:iq:registerPushService">
		    <userName>yzchen@smit.com.cn</userName>
		    <pushServiceName>pushTest@smit.com.cn</pushServiceName>
		    <regPush>true</regPush>
		  </openims>
		</iq>
		
		
		*
		* WE RETURN
		* 
		  <openims xmlns="smit:iq:registerPushService"/>       
		  	<userName>chenyz@smit</userName>"      
		  	<pushServiceName>widgets push</pushServiceName>               
			<status>true</status> //false indicates failure
			<pushID>123456789</pushID> //return generated ID
		  </openims>

		*
		*/
		
		String USER_NAME = "userName";
		String PUSH_SERVICE_NAME = "pushServiceName";
		String REG_PUSH = "regPush";
		
		String userName = "";
		String pushServiceName = "";
		String regPush = "";
		
		String userAccount = packet.getFrom().toString();
		
		String packetXml = packet.toString();
		XMPPPacketReader reader = new XMPPPacketReader();
		//XmlPullParserFactory factory = null;
		try {
			Element doc = reader.read(new StringReader(packetXml)).getRootElement();
	        if (doc == null) {
	            // No document found.
	            return null;
	        }

			String tagName = doc.getName();
			System.out.println(tagName);
			List<Element> elements = doc.elements();
			for(int i = 0; i<elements.size(); i++)
			{
				Element tag = elements.get(i);
				List<Element> elements2 = tag.elements();
				tagName = tag.getName();
				if( ! tagName.equalsIgnoreCase("openims"))
				{
					continue;
				}
				System.out.println("elements2.size(): " + elements2.size());

				for(int j = 0; j<elements2.size(); j++)
				{
					Element tag2 = elements2.get(j);
					tagName = tag2.getName();
					System.out.println("parse RECV IQ:" + tagName);
					if(tagName.equalsIgnoreCase("username"))
					{
						userName = tag2.getText();
					}
					else if(tagName.equalsIgnoreCase("pushservicename"))
					{
						pushServiceName = tag2.getText();
					}
					else if(tagName.equalsIgnoreCase("regpush"))
					{
						regPush = tag2.getText();
					}
				}
			}
        }
        catch (XmlPullParserException e) {
            Log.error("Error creating a parser factory", e);
        } catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IQ reply = IQ.createResultIQ(packet);
		reply.setTo(packet.getFrom());
		IQ.Type type = packet.getType();

		Element childElementCopy = reply.getElement();
		Namespace ns = new Namespace("", NAME_SPACE);
		Element openimsElement = childElementCopy.addElement("openims", ns.getURI());
		openimsElement.addElement(USER_NAME).addText(userName);;
		openimsElement.addElement(PUSH_SERVICE_NAME).addText(pushServiceName);
		openimsElement.addElement(REG_PUSH).addText(regPush);

		if("true".equalsIgnoreCase(regPush))//register
		{
			String retIDStr = "";
			try
			{
				//INSERT INTO DATABASE
				retIDStr = IDRegistrationDBManipulator.insertID(pushServiceName, userName, userAccount);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(retIDStr != null)
			{
				//register success
				openimsElement.addElement("status").addText("true");		//true or false; false indicates failure
				openimsElement.addElement("pushID").addText(retIDStr);	//123456789 //return generated ID
			}
			else
			{
				//register fail
				openimsElement.addElement("status").addText("false");		//true or false; false indicates failure
				openimsElement.addElement("pushID").addText(retIDStr);	//123456789 //return generated ID
			}
		}
		else if("false".equalsIgnoreCase(regPush))//unregister
		{
			boolean isSuccess = false;
			String id = "";
			try
			{
				//DELETE FROM DATABASE
				id = IDRegistrationDBManipulator.queryID(pushServiceName, userAccount);
				isSuccess = IDRegistrationDBManipulator.deleteID(pushServiceName, userAccount);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(isSuccess)
			{
				//unRegister success
				openimsElement.addElement("status").addText("true");
				openimsElement.addElement("pushID").addText(id);
			}
			else
			{
				//unRegister fail
				openimsElement.addElement("status").addText("false");
				openimsElement.addElement("pushID").addText(id);	
			}

		}
		return reply;
	}
}
