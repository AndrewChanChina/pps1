package com.smit.openfire.plugin;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.smit.database.DatabaseMan;
import com.smit.vo.SmitRegisteredPushServiceId;
import com.smit.vo.UserAccountResource;

public class UserAccountResourceDBManipulator {


    public UserAccountResourceDBManipulator()
    {
    }

    public static void insertResource(final String userAccount,
    									final String resource ,
    									final String deviceName,
    									final String deviceId)
    {
    	UserAccountResource itemToSave = new UserAccountResource();
    	itemToSave.setId(null);
    	itemToSave.setUserAccount(userAccount);
    	itemToSave.setResource(resource);
    	itemToSave.setDeviceName(deviceName);
    	itemToSave.setDeviceId(deviceId);
    	DatabaseMan.saveOrUpdate(itemToSave);
    }

    public static boolean deleteResource(final String userAccount, 
									     final String deviceName, 
									     final String deviceId) throws SQLException 
    {
    	String selectSQL = "from smitUserAccountResource WHERE " + 
							"userAccount = '" + userAccount + "' AND " +
							"deviceName = '" + deviceName + "' AND " +
							"deviceId = '" + deviceId + "'";
    	List<UserAccountResource> list= (List<UserAccountResource>)DatabaseMan.select(selectSQL);
    	if(list == null || list.size() <= 0)
    	{
    		return false;
    	}
    	for(int i=0; i<list.size(); i++)
    	{
    		DatabaseMan.delete(list.get(i));
    	}
    	return true;
    }
      
    public static String queryResource(final String userAccount, 
    								   final String deviceName, 
    								   final String deviceId) 
    									throws SQLException 
    {
    	String retResource = "";
    	String selectSQL = "from smitUserAccountResource WHERE " + 
    						"userAccount = '" + userAccount + "' AND " +
    						"deviceName = '" + deviceName + "' AND " +
    						"deviceId = '" + deviceId + "'";
    	List<UserAccountResource> list= (List<UserAccountResource>)DatabaseMan.select(selectSQL);
    	if(list == null || list.size() <= 0)
    	{
    		return retResource;
    	}
    	for(int i=0; i<list.size(); i++)
    	{
    		retResource = list.get(i).getResource();
    	}
    	return retResource;
    }
}
