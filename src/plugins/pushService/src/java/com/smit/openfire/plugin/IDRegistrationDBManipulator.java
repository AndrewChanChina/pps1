package com.smit.openfire.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.Log;

public class IDRegistrationDBManipulator {
	private static final String INSERT_ID = 
		"INSERT INTO ofRegisteredIDTable(pushServiceID, serviceType, userName, userAccount)" +
        " VALUES (?,?,?,?)";
    private static final String DELETE_ID =
        "DELETE FROM ofRegisteredIDTable WHERE pushServiceID=? AND userAccount=?";
    private static final String QUERY_ID =
        "SELECT pushServiceID FROM ofRegisteredIDTable WHERE serviceType=? AND userAccount=?";

    public IDRegistrationDBManipulator()
    {
    	
    }

    //return the inserted "ID" indicates success
    //return null indicates failure
    public static String insertID(final String pushServiceName,final String userName ,final String userAccount) throws SQLException 
    {
    	String idStr = generateID();
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
    	
    	return idStr;
    }

    //return the deleted "ID" indicates success
  //return null indicates failure
    public static boolean deleteID(final String pushServiceName, 
    								final String userAccount) throws SQLException 
    {        
    	Connection con = null;
	    PreparedStatement pstmt = null;
	    boolean abortTransaction = false;
	    try {
	        con = DbConnectionManager.getTransactionConnection();
	        pstmt = con.prepareStatement(DELETE_ID);
	        pstmt.setString(1, pushServiceName);
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
    }
    
    private static String generateID()
    {
    	String idStr = "";

    	Date d = new Date();

    	//long date2 = d.getTime();
    	long year = d.getYear() + 1900;
    	long month = d.getMonth()+1;
    	long date = d.getDate();
    	long hours = d.getHours();
    	long minutes = d.getMinutes();
    	long seconds = d.getSeconds();
    	
    	//String datesss = d.toGMTString();
    	//String daterrr = d.toLocaleString();
    	
    	Random rand = new Random();
    	String end = Integer.toString(Math.abs(rand.nextInt()%10000));
    	
    	String dateStr2 = Long.toString(year) + Long.toString(month) + Long.toString(date) +
    					  Long.toString(hours) + Long.toString(minutes) + Long.toString(seconds) +
    					  end;
    	idStr = dateStr2;
    	return idStr;
    }
    
    public static String queryID(final String pushServiceName, final String userAccount) throws SQLException 
    {
    	String retID = "";
    	Connection con = null;
    	PreparedStatement pstmt = null;
    	boolean abortTransaction = false;
    	try {
	    	con = DbConnectionManager.getConnection();
	    	pstmt = con.prepareStatement(QUERY_ID);
	        pstmt.setString(1, pushServiceName);
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
    }
}
