package com.smit.openfire.plugin;

import java.util.Collection;

import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupAlreadyExistsException;
import org.jivesoftware.openfire.group.GroupManager;
import org.jivesoftware.openfire.user.User;
import org.xmpp.packet.JID;

public class SmitGroupManager {
	private static SmitGroupManager mInstance = null;
	
	private SmitGroupManager()
	{

	}
	
	public static SmitGroupManager instance()
	{
		if(mInstance == null)
		{
			mInstance = new SmitGroupManager();
		}
		return mInstance;
	}
	
	public Group createGroup(String name)
	{
		Group group = null;
		try {
			group = GroupManager.getInstance().createGroup(name);
			return group;
		} catch (GroupAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(group != null)
		{
			
		}
		return null;
	}
	
	public Collection<Group> getAllGroups()
	{
		Collection<Group> groups = GroupManager.getInstance().getGroups();
		return groups;
	}
	
	public void addMemberToGroup(String groupName, JID user, boolean isAdmin)
	{
		GroupManager.getInstance().getProvider().addMember(groupName, user, isAdmin);
	}
	
	public Collection<Group> userBelongToWhich(JID user)
	{
		User realuser = new User();
		Collection<Group> groups = GroupManager.getInstance().getGroups(user);
		return groups;
	}
}
