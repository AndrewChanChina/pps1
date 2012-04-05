package com.smit.pubsub;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;

import com.smit.database.DatabaseMan;
import com.smit.vo.Subscriber;

public class SubscribeIQHandler extends IQHandler{

	private static final String NAME_SPACE = "smit:pubsub:subscribe";
	private static final String MODULE_NAME = "PubsubSubscribeHandler";
	private IQHandlerInfo info;
	private XMPPServer server;
	private SessionManager sessionManager;
	public SubscribeIQHandler() {
		super(MODULE_NAME);
		info = new IQHandlerInfo("query", NAME_SPACE);
		server = XMPPServer.getInstance();
		sessionManager = server.getSessionManager();
	}

	@Override
	public IQ handleIQ(IQ packet) throws UnauthorizedException {
		System.out.println("pubsubplugin subscribe/unsubscribe iq:"+packet.toXML());
		IQ reply = null;
		String nameSpace = packet.getChildElement().getNamespaceURI();
		if(nameSpace.equals(NAME_SPACE)){
			if(packet.getType()==IQ.Type.set){
				processSubscribe(packet);
			}
		}
		return null;
	}

	private void processSubscribe(IQ packet) {
		IQ reply = IQ.createResultIQ(packet);
		Element pubsub = reply.setChildElement("pubsub",NAME_SPACE);
		Element root = packet.getChildElement();
		try{
			List<Element> elelists  = root.elements("subscribe");
			List<Element> unsubs = root.elements("unsubscribe");
			if(elelists.size()>0){
				processSub(pubsub, elelists,packet.getFrom());
			}else if(unsubs.size()>0){
				processUnsub(pubsub,unsubs);
			}
			System.out.println(reply.toXML());
			
		}catch (Exception e){
			e.printStackTrace();
			reply.setType(IQ.Type.error);
		}
		ClientSession session = sessionManager.getSession(packet.getFrom());
		if(session!=null){
			session.process(reply);
		}
	}

	private void processUnsub(Element pubsub, List<Element> unsubs) {
		for(Element e:unsubs){
			String jid = e.attributeValue("jid");
			String node = e.attributeValue("node");
			DatabaseMan.deleteSub(jid, node);
		}
		pubsub.addAttribute("code", "200");
	}

	private void processSub(Element pubsub, List<Element> elelists,JID from) {
		for(Element e:elelists){
			String node = e.attributeValue("node");
			String jid = e.attributeValue("jid");
			Subscriber subscriber = new Subscriber(from.toString(),node);
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			subscriber.setSub_time(formater.format(new Date()));
			DatabaseMan.saveOrUpdate(subscriber);
			Element subscription = pubsub.addElement("subscription");
			subscription.addAttribute("jid", jid);
			subscription.addAttribute("node", node);
			if(DatabaseMan.saveOrUpdate(subscriber)){
				subscription.addAttribute("subscription", "subscribed");
				subscription.addAttribute("code", "200");
			}else{
				subscription.addAttribute("subscription", "unsubscribed");
				subscription.addAttribute("code", "500");
			}
		}
	}

	@Override
	public IQHandlerInfo getInfo() {
		return info;
	}

}
