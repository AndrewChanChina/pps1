package com.smit.pubsub;

import java.io.File;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Packet;

import sun.security.jca.GetInstance;

public class PubSubPlugin implements Plugin,PacketInterceptor{

	private static PubSubPlugin plugin = null;
	private XMPPServer xmpp;
	private PluginManager manager;
	public static PubSubPlugin getInstance(){
		return plugin;
	}
	public PubSubPlugin(){
		plugin = this;
		System.out.println("pubsub plugin construtor!");
	}
	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		//initiallize var
		this.manager = manager;
		xmpp = XMPPServer.getInstance();
		System.out.println("³õÊ¼»¯pubsub²å¼þ£¡");
		
		//add iqhandler
		xmpp.getIQRouter().addHandler(new SubscribeIQHandler() );
		xmpp.getIQRouter().addHandler(new PublishIQHandler());
	}

	@Override
	public void destroyPlugin() {
		System.out.println("destroy  punsun plugin!");
	}
	@Override
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {
		System.out.println("Plugin intercept:"+packet.toXML());
	}

}
