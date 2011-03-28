/*
 * 
 * AUTHOR : Qianfeng Shen
 * 
 * 
 */


package com.smit.openfire.plugin.offlinePushIQ;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.container.BasicModule;
import org.jivesoftware.openfire.event.UserEventListener;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.util.FastDateFormat;
import org.jivesoftware.util.JiveConstants;
import org.jivesoftware.util.LocaleUtils;
import org.jivesoftware.util.Log;
import org.jivesoftware.util.StringUtils;
import org.xmpp.packet.IQ;

public class OfflinePushStore extends BasicModule implements UserEventListener
{
	
	private static OfflinePushStore mInstance = null;
	private FastDateFormat dateFormat;
	
	private static String INSERT_PUSH_IQ = "INSERT INTO ofPushIQ (collapseKey, IQText, IQSize, creationDate) " + 
						  "VALUES (?, ?, ?, ?)";
	private static String DELETE_PUSH_IQ = "DELETE FROM ofPushIQ WHERE collapseKey = ?";
	private static String SELECT_PUSH_IQ = "SELECT * FROM ofPushIQ WHERE collapseKey = ?";
	private static String SELECT_ALL_PUSH_IQ = "SELECT * FROM ofPushIQ";
	
    /**
     * Pool of SAX Readers. SAXReader is not thread safe so we need to have a pool of readers.
     */
    private BlockingQueue<SAXReader> xmlReaders = new LinkedBlockingQueue<SAXReader>();
	
	private OfflinePushStore()
	{
		super("Offline Push Store");
		
        SAXReader xmlReader = new SAXReader();
        //xmlReader.setEncoding("UTF-8");
        xmlReaders.add(xmlReader);
        
        dateFormat = FastDateFormat.getInstance(JiveConstants.XMPP_DELAY_DATETIME_FORMAT,
                TimeZone.getTimeZone("UTC"));
	}
	
	public static OfflinePushStore instance()
	{
		if(mInstance == null)
		{
			mInstance = new OfflinePushStore();
		}
		return mInstance;
	}

	@Override
	public void userCreated(User user, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userDeleting(User user, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userModified(User user, Map<String, Object> params) {
		// TODO Auto-generated method stub
		
	}
	
	public void addOfflinePush(IQ pushIQ, String collapseKey)
	{
		/*
		<iq id="ea0nN-5" type="get" from="test@smit/SMIT">
			<openims xmlns="smit:iq:notification"/>       
				<pushID>1234567879</pushID> 
				<title>udate</title>
				<uri>www.baidu.com</uri>
				<message>New message available</message>
			</openims>
		</iq>
		*/
		
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(INSERT_PUSH_IQ);
            pstmt.setString(1, collapseKey);
            pstmt.setString(2, pushIQ.toString());
            pstmt.setInt(3, pushIQ.toString().length());
            Date dateTime = new java.util.Date();
            String date = Long.toString(dateTime.getTime());  //StringUtils.dateToMillis();
            pstmt.setString(4, date);
            pstmt.executeUpdate();
        }
        catch (Exception e)
        {
            Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}
	
	public ArrayList<OfflinePushIQ> queryAllPushIQ()
	{
		ArrayList<OfflinePushIQ> array = new ArrayList<OfflinePushIQ>();
        Connection con = null;
        PreparedStatement pstmt = null;
        SAXReader xmlReader = null;
        OfflinePushIQ offlinePushIQ = null;
        try {
            // Get a sax reader from the pool
        	//xmlReaders.add(new SAXReader());
            xmlReader = xmlReaders.take();
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SELECT_ALL_PUSH_IQ);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
            	String collapseKey = rs.getString(1);
            	String queryIQText = rs.getString(2);
            	int queryIQSize = rs.getInt(3);
            	String queryCreationDate = rs.getString(4);
            	Date creationDate = new Date(Long.parseLong(queryCreationDate.trim()));
            	
            	//IQ recoveredIQ = new IQ();
            	//recoveredIQ.setID(collapseKey);
            	
            	Element element = xmlReader.read(new StringReader(queryIQText)).getRootElement();
            	offlinePushIQ = new OfflinePushIQ(creationDate, element);
            	
            	//add to array...
            	array.add(offlinePushIQ);
            }
            rs.close();
        }
        catch (Exception e)
        {
            Log.error("Error retrieving all offline IQ...", e);
        }
        finally
        {
        	// Return the sax reader to the pool
            if (xmlReader != null) {
                xmlReaders.add(xmlReader);
            }
            DbConnectionManager.closeConnection(pstmt, con);
        }
		return array;
	}
	
	public OfflinePushIQ queryPushIQ(String id)
	{
        Connection con = null;
        PreparedStatement pstmt = null;
        SAXReader xmlReader = null;
        OfflinePushIQ offlinePushIQ = null;
        try {
            // Get a sax reader from the pool
            xmlReader = xmlReaders.take();
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SELECT_PUSH_IQ);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
            {
            	String collapseKey = rs.getString(1);
            	//String queryServiceType = rs.getString(2);
            	String queryIQText = rs.getString(2);
            	int queryIQSize = rs.getInt(3);
            	String queryCreationDate = rs.getString(4);
            	Date creationDate = new Date(Long.parseLong(queryCreationDate.trim()));
            	
            	//IQ recoveredIQ = new IQ();
            	//recoveredIQ.setID(queryId);
            	
            	Element element = xmlReader.read(new StringReader(queryIQText)).getRootElement();
            	offlinePushIQ = new OfflinePushIQ(creationDate, element);
            }
            rs.close();
        }
        catch (Exception e)
        {
            Log.error("Error retrieving offline IQ of username: " + id, e);
        }
        finally
        {
        	// Return the sax reader to the pool
            if (xmlReader != null) {
                xmlReaders.add(xmlReader);
            }
            DbConnectionManager.closeConnection(pstmt, con);
        }
        return offlinePushIQ;
	}
	
	public void deletePushIQ(String collapseKey)
	{
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(DELETE_PUSH_IQ);
            pstmt.setString(1, collapseKey);
            pstmt.executeUpdate();
        }
        catch (Exception e)
        {
            //Log.error(LocaleUtils.getLocalizedString("admin.error"), e);
        	Log.error("ERROR Deleting Push ID : " + collapseKey, e);
        }
        finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}
}
