package com.smit.openfire.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.Log;

import com.smit.database.DatabaseMan;
import com.smit.vo.SmitRegisteredPushServiceId;

/*
 * CLASS: IDRegistrationDBManipulator
 * DESCRITION: This class is used to manipulate the database "smitRegisteredPushServiceID"
 */
public class IDRegistrationDBManipulator {
	
	/*
	private static final String INSERT_ID = 
		"INSERT INTO smitRegisteredPushServiceID(pushServiceID, serviceType, userName, userAccount)" +
        " VALUES (?,?,?,?)";
    private static final String DELETE_ID =
        "DELETE FROM smitRegisteredPushServiceID WHERE pushServiceID=? AND userAccount=?";
    private static final String QUERY_ID =
        "SELECT pushServiceID FROM smitRegisteredPushServiceID WHERE serviceType=? AND userAccount=?";
    */

    public IDRegistrationDBManipulator()
    {
    }

    /*
     * 
     * FUNCTION: insertID
     * DESCRIPTION: generate an random push id, and insert the push id into database
     * RETURN: return the inserted "ID" indicates success
     */
    //
    //return null indicates failure
    public static String insertID(final String pushServiceName,final String userName ,final String userAccount) throws SQLException 
    {
    	String idStr = generateID();
    	//MODIFY TO HIBERNATE
    	SmitRegisteredPushServiceId pushServiceId = new SmitRegisteredPushServiceId();
    	pushServiceId.setId(null);
    	pushServiceId.setPushServiceID(idStr);
    	pushServiceId.setServiceType(pushServiceName);
    	pushServiceId.setUserName(userName);
    	pushServiceId.setUserAccount(userAccount);
    	DatabaseMan.saveOrUpdate(pushServiceId);
    	//MODIFY TO HIBERNATE
    	
    	/*
    	 * Openfire style database management.
    	 * 
        Connection con = null;
        boolean abortTransaction = false;
        try {
            con = DbConnectionManager.getTransactionConnection();
            PreparedStatement pstmt = con.prepareStatement(INSERT_ID);
            pstmt.setString(1, idStr);
            pstmt.setString(2, pushServiceName);
            pstmt.setString(3, userName);
            pstmt.setString(4, userAccount);
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch(SQLException sqle)
        {
            abortTransaction = true;
            throw sqle;
        }
        finally
        {
            DbConnectionManager.closeTransactionConnection(con, abortTransaction);
        }
    	*/
    	
    	return idStr;
    }

    //return the deleted "ID" indicates success
	//return null indicates failure
    public static boolean deleteID(final String pushServiceType, 
    								final String userAccount) throws SQLException 
    {
    	String selectSQL = "from SmitRegisteredPushServiceId WHERE " + 
							"serviceType = '" + pushServiceType + "' AND " +
							"userAccount = '" + userAccount + "'";
    	List<SmitRegisteredPushServiceId> list= (List<SmitRegisteredPushServiceId>)DatabaseMan.select(selectSQL);
    	if(list == null || list.size() <= 0)
    	{
    		return false;
    	}
    	for(int i=0; i<list.size(); i++)
    	{
    		DatabaseMan.delete(list.get(i));
    	}
    	return true;
    	
    	/*
    	
    	Connection con = null;
	    PreparedStatement pstmt = null;
	    boolean abortTransaction = false;
	    try {
	        con = DbConnectionManager.getTransactionConnection();
	        pstmt = con.prepareStatement(DELETE_ID);
	        pstmt.setString(1, pushServiceType);
	        pstmt.setString(2, userAccount);
	        pstmt.execute();
	    }
	    catch (SQLException sqle) {
	        Log.error(sqle);
	        abortTransaction = true;
	        throw sqle;
	    }
	    finally {
	        DbConnectionManager.closeTransactionConnection(pstmt, con, abortTransaction);
	    }
    	return true;
    	*/
    }
    
    /*
     * FUNCTION: generateID
     * DESCRIPTION: generate a random push ID
     */
    
    private static String generateID()
    {
    	String idStr = "";
    	Date d = new Date();

    	long year = d.getYear() + 1900;
    	long month = d.getMonth()+1;
    	long date = d.getDate();
    	long hours = d.getHours();
    	long minutes = d.getMinutes();
    	long seconds = d.getSeconds();
    	
    	Random rand = new Random();
    	String end = Integer.toString(Math.abs(rand.nextInt()%10000));
    	
    	String dateStr2 = Long.toString(year) + Long.toString(month) + Long.toString(date) +
    					  Long.toString(hours) + Long.toString(minutes) + Long.toString(seconds) +
    					  end;
    	idStr = dateStr2;
    	return idStr;
    }
    
    public static List<SmitRegisteredPushServiceId> queryID(final String pushServiceType) throws SQLException 
    {
    	String selectSQL = "from SmitRegisteredPushServiceId WHERE " + 
    						"serviceType = '" + pushServiceType + "'";
    	List<SmitRegisteredPushServiceId> list= (List<SmitRegisteredPushServiceId>)DatabaseMan.select(selectSQL);
    	return list;
    }
    
    public static String queryAccountByGeneratedPushID(final String pushID) throws SQLException
    {
    	String retAccount = "";
    	String selectSQL = "from SmitRegisteredPushServiceId WHERE " + 
							"pushServiceID = '" + pushID + "'";
    	List<SmitRegisteredPushServiceId> list= (List<SmitRegisteredPushServiceId>)DatabaseMan.select(selectSQL);
    	if(list == null || list.size() <= 0)
    	{
    		return retAccount;
    	}
    	for(int i=0; i<list.size(); i++)
    	{
    		retAccount = list.get(i).getUserAccount();
    	}
    	return retAccount;
    }
    
    public static String queryID(final String pushServiceType, final String userAccount) throws SQLException 
    {
    	String retID = "";
    	
    	//MODIFY TO HIBERNATE
    	String selectSQL = "from SmitRegisteredPushServiceId WHERE " + 
    						"serviceType = '" + pushServiceType + "' AND " +
    						"userAccount = '" + userAccount + "'";
    	List<SmitRegisteredPushServiceId> list= (List<SmitRegisteredPushServiceId>)DatabaseMan.select(selectSQL);
    	if(list == null || list.size() <= 0)
    	{
    		return retID;
    	}
    	for(int i=0; i<list.size(); i++)
    	{
    		retID = list.get(i).getPushServiceID();
    	}
    	return retID;
    	//MODIFY TO HIBERNATE
    	
    	/*
    	 * openfire style database management
    	 * 
    	Connection con = null;
    	PreparedStatement pstmt = null;
    	boolean abortTransaction = false;
    	try {
	    	con = DbConnectionManager.getConnection();
	    	pstmt = con.prepareStatement(QUERY_ID);
	        pstmt.setString(1, pushServiceType);
	        pstmt.setString(2, userAccount);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next())
	        {
	        	retID = rs.getString(1);
	        }
	        rs.close();
	        pstmt.close();
    	}
	    catch (SQLException sqle) {
	        Log.error(sqle);
	        abortTransaction = true;
	        throw sqle;
	    }
	    finally {
	        DbConnectionManager.closeTransactionConnection(pstmt, con, abortTransaction);
	    }
	    return retID;
	    */
	    
    }
}
