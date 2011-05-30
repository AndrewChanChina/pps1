package com.smit.openfire.plugin;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.cenqua.shaj.log.Log;
import com.smit.database.DatabaseMan;
import com.smit.vo.SmitRegisteredPushServiceId;
import com.smit.vo.SmitUserAccountResource;

public class UserAccountResourceDBManipulator {


    public UserAccountResourceDBManipulator()
    {
    }

    /**
     * �����Դʱ������deviceID������
     * @date 2011-5-17 10:32:12
     * @author ANDREW
     */
    public static boolean insertResource(final String userAccount,
    									final String resource ,
    									final String deviceName,
    									final String deviceId)
    {    
    	SmitUserAccountResource itemToSave = new SmitUserAccountResource();
    	// Ϊ��֤ͬһ���豸ID��ֻ����Դ
    	String selectSQL = "from SmitUserAccountResource WHERE " + 
		"userAccount = '" + userAccount + "' AND " +
		"deviceId = '" + deviceId + "'";
    	List<SmitUserAccountResource> list= (List<SmitUserAccountResource>)DatabaseMan.select(selectSQL);
    	if(list!=null){
    		if(list.size()==1)
    			itemToSave = list.get(0);
    		if(list.size()>1){
    			//TODO �쳣��
    		}    			
    	}   
    	itemToSave.setUserAccount(userAccount);
    	itemToSave.setResource(resource);
    	itemToSave.setDeviceName(deviceName);
    	itemToSave.setDeviceId(deviceId);
    	boolean ret = DatabaseMan.saveOrUpdate(itemToSave);
    	return ret;
    }

    public static boolean deleteResource(final String userAccount, 
									     final String deviceName, 
									     final String deviceId) throws SQLException 
    {
    	String selectSQL = "from SmitUserAccountResource WHERE " + 
							"userAccount = '" + userAccount + "' AND " +
							"deviceName = '" + deviceName + "' AND " +
							"deviceId = '" + deviceId + "'";
    	List<SmitUserAccountResource> list= (List<SmitUserAccountResource>)DatabaseMan.select(selectSQL);
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
    	String selectSQL = "from SmitUserAccountResource WHERE " + 
    						"userAccount = '" + userAccount + "' AND " +
    						"deviceName = '" + deviceName + "' AND " +
    						"deviceId = '" + deviceId + "'";
    	List<SmitUserAccountResource> list= (List<SmitUserAccountResource>)DatabaseMan.select(selectSQL);
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
    
	public static List<SmitUserAccountResource> queryResource(final String userAccount) throws SQLException {
		String retResource = "";
		String selectSQL = "from SmitUserAccountResource WHERE "
				+ "userAccount = '" + userAccount + "'";
		List<SmitUserAccountResource> list = (List<SmitUserAccountResource>) DatabaseMan
				.select(selectSQL);
		return list;
	}
}
