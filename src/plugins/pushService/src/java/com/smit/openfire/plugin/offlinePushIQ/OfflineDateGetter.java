package com.smit.openfire.plugin.offlinePushIQ;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.util.Log;

public class OfflineDateGetter {
    private static final String LOAD_OFFLINE_PRESENCE =
        "SELECT offlinePresence, offlineDate FROM ofPresence WHERE username=?";
    
    public static long getOfflineDate(User user)
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String username = user.getUsername();
        long offlineDate = -1;
        //Lock lock = CacheFactory.getLock(username, offlinePresenceCache);
        try {
            //lock.lock();
            //if (!offlinePresenceCache.containsKey(username) || !lastActivityCache.containsKey(username)) {
                con = DbConnectionManager.getConnection();
                pstmt = con.prepareStatement(LOAD_OFFLINE_PRESENCE);
                pstmt.setString(1, username);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    String offlinePresence = DbConnectionManager.getLargeTextField(rs, 1);
                    if (rs.wasNull()) {
                        //offlinePresence = NULL_STRING;
                    }
                    offlineDate = Long.parseLong(rs.getString(2).trim());
                    //offlinePresenceCache.put(username, offlinePresence);
                    //lastActivityCache.put(username, offlineDate);
                    System.out.println("getOfflineDate\n");
                }
                else {
                    //offlinePresenceCache.put(username, NULL_STRING);
                    //lastActivityCache.put(username, NULL_LONG);
                }
        }
        catch(SQLException sqle)
        {
            Log.error(sqle);
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
            //lock.unlock();
        }
    	return offlineDate;
    }
}
