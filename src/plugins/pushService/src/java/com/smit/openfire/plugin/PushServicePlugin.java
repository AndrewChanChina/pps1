package com.smit.openfire.plugin;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.event.SessionEventDispatcher;
import org.jivesoftware.openfire.event.SessionEventListener;
import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.PresenceEventListener;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.component.ComponentManagerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import com.smit.openfire.plugin.offlinePushIQ.OfflineDateGetter;
import com.smit.openfire.plugin.offlinePushIQ.OfflinePushIQPusher;
import com.smit.openfire.plugin.offlinePushIQ.OfflinePushStore;
import com.smit.openfire.plugin.offlinePushIQ.SmitIQOnlineDeliverer;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;

public class PushServicePlugin implements Plugin, 
										  Component, 
										  PropertyEventListener, 
										  PacketInterceptor,
										  SessionEventListener,
										  PresenceEventListener
{
	
	private String serviceName = "pushService";
	
	private XMPPServer mServer = null;
	private SessionManager sessionManager;
	private ComponentManager componentManager;
	private PluginManager pluginManager;
	private GroupManager groupManager;
    private InterceptorManager interceptorManager;
	private static PushServicePlugin mInstance = null;
	
	public static PushServicePlugin instance()
	{
		return  mInstance;
	}
	
	public PushServicePlugin()
	{
		mInstance = this;
		System.out.println("pushServicePlugin: CONSTRUCTOR");
	}
	
	public void initializePlugin(PluginManager manager, File pluginDirectory)
	{
		System.out.println("pushServicePlugin: initializePlugin()");
		pluginManager = manager;
		mServer = XMPPServer.getInstance();
		sessionManager = SessionManager.getInstance();
		groupManager = GroupManager.getInstance();
        // Register as a component.
        componentManager = ComponentManagerFactory.getComponentManager();
        
        //intercept the packet
        interceptorManager = InterceptorManager.getInstance();
        interceptorManager.addInterceptor(this);
        
        //listen to LOGIN action
        SessionEventDispatcher.addListener(this);
        
        try {
            componentManager.addComponent(serviceName, this);
        }
        catch (Exception e) {
            //componentManager.getLog().error(e);
        	//Log.error(e);
        }
        PropertyEventDispatcher.addListener(this);

        // add IQ handler
        mServer.getIQRouter().addHandler(new RegisterPushIQHandler());
        mServer.getIQRouter().addHandler(new PushNotificationDevIQHandler());
        mServer.getIQRouter().addHandler(new PushNotificationUserIQHandler());
        mServer.getIQRouter().addHandler(new QueyPushIdIQHandler());
        mServer.getIQRouter().addHandler(new QueryUserAccountResourceIQHandler()); 
	}
	
    public void destroyPlugin()
    {
    	System.out.println("pushServicePlugin: destroyPlugin()");
        PropertyEventDispatcher.removeListener(this);
        
        interceptorManager.removeInterceptor(this);
        
        SessionEventDispatcher.removeListener(this);
        
        // Unregister component.
        if (componentManager != null) {
            try {
                componentManager.removeComponent(serviceName);
            }
            catch (Exception e) {
                //Log.error(e);
            }
        }
        componentManager = null;
        pluginManager = null;
        sessionManager = null;
    }
    
    public Component getComponent()
    {
    	return this;
    }

	@Override
	public String getDescription() {
        // Get the description from the plugin.xml file.
        return pluginManager.getDescription(this);
	}

	@Override
	public String getName() {
        // Get the name from the plugin.xml file.
        return pluginManager.getName(this);
	}

	@Override
	public void initialize(JID jid, ComponentManager componentManager)
			throws ComponentException {
		System.out.println("PushServicePlugin: initialize...");
	}

	@Override
	public void processPacket(Packet packet) {
		System.out.println("PushServicePlugin: processPacket...");
		String packetXml = packet.toString();
		System.out.println("PushServicePlugin: processPacket packet xml :" + packetXml);
		if (packet instanceof Presence) {
            Presence presence = (Presence) packet;
            if (presence.isAvailable() || presence.getType() == Presence.Type.unavailable ||
                    presence.getType() == Presence.Type.error) {
                // Store answer of presence probes
                //probedPresence.put(presence.getFrom().toString(), presence);
            	System.out.println("PushServicePlugin: We got a presence packet...");
            }
        }
	}

	@Override
	public void shutdown() {
		System.out.println("PushServicePlugin: shutdown...");
	}

	@Override
	public void start() {
		System.out.println("PushServicePlugin: start...");
	}

	@Override
	public void propertyDeleted(String property, Map<String, Object> params) {
		System.out.println("PushServicePlugin: propertyDeleted...");
	}

	@Override
	public void propertySet(String property, Map<String, Object> params) {
		System.out.println("PushServicePlugin: propertySet...");
		
	}

	@Override
	public void xmlPropertyDeleted(String property, Map<String, Object> params) {
		System.out.println("PushServicePlugin: xmlPropertyDeleted...");
	}

	@Override
	public void xmlPropertySet(String property, Map<String, Object> params) {
		System.out.println("PushServicePlugin: xmlPropertySet...");
	}
	
	////////////////////////////////////////////
    public String getServiceName() {
    	System.out.println("PushServicePlugin: getServiceName...");
        return serviceName;
    }

	@Override
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {

		System.out.println("Plugin Interceptor: " + packet.toString() + "\n");
	}

	@Override
	public void anonymousSessionCreated(Session session) {

		JID address = session.getAddress();
		String userAccount = address.toString();
		System.out.println("========= User Account: " + userAccount  + " anonymous SESSION CREATED ===============");
	}

	@Override
	public void anonymousSessionDestroyed(Session session) {
		
		JID address = session.getAddress();
		String userAccount = address.toString();
		System.out.println("========= User Account: " + userAccount  + " anonymous SESSION DESTROYED ===============");
	}

	@Override
	public void resourceBound(Session session) {
		JID address = session.getAddress();
		String userAccount = address.toString();
		System.out.println("========= User Account: " + userAccount  + "  RESOURCE BOUND ===============");
		
		//Plugin plugin = (PushServicePlugin) XMPPServer.getInstance().getPluginManager().getPlugin("pushService");
		//plugin.
		
		/*
		if(3 != session.getStatus()) //if != STATUS_AUTHENTICATED
		{
			return;
		}
        UserManager userManager = UserManager.getInstance();
        User user = null;
		try {
			user = userManager.getUser(userAccount);
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(user != null)
		{
			long lastOfflineDate = OfflineDateGetter.getOfflineDate(user);
			OfflinePushIQPusher.instance().pushPushIQ(userAccount, lastOfflineDate);
		}
		*/
	}

	@Override
	public void sessionCreated(Session session) {
		//We will query the offline push IQ from the database...
		
		JID address = session.getAddress();
		String userAccount = address.toString();
		System.out.println("========= User Account: " + userAccount  + "  SESSION CREATED ===============");
	}

	@Override
	public void sessionDestroyed(Session session) {
		JID address = session.getAddress();
		String userAccount = address.toString();
		System.out.println("========= User Account: " + userAccount  + "  RSESSION DESTROYED ===============");
	}

	@Override
	public void availableSession(ClientSession session, Presence presence) {
		/*
		JID address = session.getAddress();
		String userAccount = address.toString();
		System.out.println("========= User Account: " + userAccount  + "  AVAILABLE SESSION  ===============");
		
		if(3 != session.getStatus()) //if != STATUS_AUTHENTICATED
		{
			return;
		}
        UserManager userManager = UserManager.getInstance();
        User user = null;
		try {
			user = userManager.getUser(userAccount);
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(user != null)
		{
			long lastOfflineDate = OfflineDateGetter.getOfflineDate(user);
			OfflinePushIQPusher.instance().pushPushIQ(userAccount, lastOfflineDate);
		}
		*/
	}

	@Override
	public void unavailableSession(ClientSession session, Presence presence) {
		System.out.println("=========  Unavailable Session ===============");
	}

	@Override
	public void presenceChanged(ClientSession session, Presence presence) {
		System.out.println("=========  presence Changed ===============");
	}

	@Override
	public void subscribedToPresence(JID subscriberJID, JID authorizerJID) {
		System.out.println("=========  subscribed To Presence ===============");
	}

	@Override
	public void unsubscribedToPresence(JID unsubscriberJID, JID recipientJID) {
		System.out.println("=========  unsubscribed To Presence ===============");
	}
}

