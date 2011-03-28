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
import org.jivesoftware.openfire.session.Session;
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
										  SessionEventListener
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
		// TODO Auto-generated method stub
        // Get the description from the plugin.xml file.
        return pluginManager.getDescription(this);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
        // Get the name from the plugin.xml file.
        return pluginManager.getName(this);
	}

	@Override
	public void initialize(JID jid, ComponentManager componentManager)
			throws ComponentException {
		// TODO Auto-generated method stub
		System.out.println("PushServicePlugin: initialize...");
	}

	@Override
	public void processPacket(Packet packet) {
		// TODO Auto-generated method stub
		System.out.println("PushServicePlugin: processPacket...");
		
		//String toNode = packet.getTo().getNode();
		//boolean targetAll = "all".equals(toNode);
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		System.out.println("PushServicePlugin: shutdown...");
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		System.out.println("PushServicePlugin: start...");
	}

	@Override
	public void propertyDeleted(String property, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertySet(String property, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void xmlPropertyDeleted(String property, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void xmlPropertySet(String property, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}
	
	////////////////////////////////////////////
    public String getServiceName() {
    	System.out.println("PushServicePlugin: getServiceName...");
        return serviceName;
    }
	
    /*
    public boolean sendIntent(String toWho,
			String title,
			String ticker,
			String uri,
			String message,
			boolean bMessage,
			String pushServiceId)
    {
    	String pushId = "PENDINGINTENT";
    	if(bMessage){
    		pushId = pushServiceId;
    	}
		IQ iq111 = new IQ();
		//iq111.setFrom("admin@smit/SMIT"); //iq111.setTo("a@smit/spark");
		iq111.setTo(toWho);
		Element childElementCopy22 = iq111.getElement();
		Namespace ns22 = new Namespace("", "smit:iq:notification");
		Element openimsElement22 = childElementCopy22.addElement("openims", ns22.getURI());
		openimsElement22.addElement("pushID").addText(pushId);
		openimsElement22.addElement("title").addText(title);
		openimsElement22.addElement("uri").addText(uri);
		openimsElement22.addElement("message").addText(message);
		openimsElement22.addElement("ticker").addText(ticker);
		
		OfflinePushStore.instance().addOfflinePush(iq111);
    	SmitIQOnlineDeliverer.instance().deliverToOne(iq111);
    	
    	return true;
    }

    public boolean vibrateDevice(String toWho,String timeLong)
    {
		IQ iq111 = new IQ();
		iq111.setTo(toWho);
		Element childElementCopy22 = iq111.getElement();
		Namespace ns22 = new Namespace("", "smit:iq:notification");
		Element openimsElement22 = childElementCopy22.addElement("openims", ns22.getURI());
		openimsElement22.addElement("pushID").addText("WARNING");
		openimsElement22.addElement("title").addText("vibrate");
		openimsElement22.addElement("uri").addText(timeLong);
		openimsElement22.addElement("message").addText("where's my pad!");
		openimsElement22.addElement("ticker").addText("update");
		
		SmitIQOnlineDeliverer.instance().deliverToOne(iq111);
		
    	return true;
	}
	*/

	@Override
	public void interceptPacket(Packet packet, Session session,
			boolean incoming, boolean processed) throws PacketRejectedException {
		// TODO Auto-generated method stub
		System.out.println("Plugin Interceptor: " + packet.toString() + "\n");
	}

	@Override
	public void anonymousSessionCreated(Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void anonymousSessionDestroyed(Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resourceBound(Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionCreated(Session session) {
		// TODO Auto-generated method stub
		//We will query the offline push IQ from the database...
		System.out.println("======We will query the offline push IQ from the database===");
		JID address = session.getAddress();
		String userAccount = address.toString();
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
			OfflinePushIQPusher.instance().pushPushIQ(user, lastOfflineDate);
		}
	}

	@Override
	public void sessionDestroyed(Session session) {
		// TODO Auto-generated method stub
		
	}
}

