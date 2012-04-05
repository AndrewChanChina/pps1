package com.smit.test;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;



public class TestSub {
	public static void main(String[] args) throws Exception{
		String str="<iq type=\"set\" from=\"server@smit/Smack\"><pubsub xmlns=\"smit:pubsub:subscribe\">" +
				"<subscribe node=\"http://domain.tld/feed1.xml\" jid=\"test888@smit/Smack\"/>" +
				"<subscribe node=\"http://domain.tld/feed2.xml\" jid=\"test888@smit/Smack\"/>" +
				"<subscribe node=\"http://domain.tld/feed3.xml\" jid=\"test888@smit/Smack\"/>" +
				"</pubsub></iq>";
		
		Document doc = DocumentHelper.parseText(str);
		//IQ result = new SubscribeIQHandler().handleIQ(doc.);
		ConnectionConfiguration cfg = new ConnectionConfiguration("localhost", 5222);
		XMPPConnection xmppconnection = new XMPPConnection(cfg);
		xmppconnection.connect();
		xmppconnection.login("test888", "123456");
		//IQ packet = subpacket();
		IQ packet = unsubpacket();
		packet.setType(IQ.Type.SET);
		xmppconnection.sendPacket(packet);
	}

	private static IQ unsubpacket() {
		IQ packet = new IQ() {
			
			@Override
			public String getChildElementXML() {
				String str="<pubsub xmlns=\"smit:pubsub:subscribe\">" +
				"<unsubscribe node=\"http://domain.tld/feed1.xml\" jid=\"test888@smit/Smack\"/>" +
				"</pubsub>";
		
				return str;
			}
		};
		return packet;
	}

	private static IQ subpacket() {
		IQ packet = new IQ() {
			
			@Override
			public String getChildElementXML() {
				String str="<pubsub xmlns=\"smit:pubsub:subscribe\">" +
				"<subscribe node=\"http://domain.tld/feed1.xml\" jid=\"test888@smit/Smack\"/>" +
				"<subscribe node=\"http://domain.tld/feed1.xml\" jid=\"test888@smit/Smack\"/>" +
				"<subscribe node=\"http://domain.tld/feed1.xml\" jid=\"test888@smit/Smack\"/>" +
				"</pubsub>";
		
				StringBuffer sb = new StringBuffer();
				sb.append(str);
				return sb.toString();
			}
		};
		return packet;
	}
}
