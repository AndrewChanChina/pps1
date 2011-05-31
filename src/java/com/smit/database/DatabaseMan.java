package com.smit.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.smit.util.log4j.Log;

public class DatabaseMan {
	public static List<?> select(String queryString)
	{
		Transaction ts = null;
		List<?> list = null;
		Session session = CustomSessionFactory.getSession();
		try
		{
			ts = session.beginTransaction();
			Query query = session.createQuery(queryString);
			list = query.list();
			ts.commit();
		}
		catch(Exception e)
		{
			if(ts != null)
			{
				ts.rollback();
			}
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		finally
		{
			CustomSessionFactory.closeSession();
		}
		return list;
	}
	
	public static boolean saveOrUpdate(Object object)
	{
		Transaction ts = null;
		Session session = CustomSessionFactory.getSession();
		try
		{
			ts = session.beginTransaction();
			session.saveOrUpdate(object);
			ts.commit();
		}
		catch(Exception e)
		{
			if(ts != null)
			{
				ts.rollback();
			}
			//e.printStackTrace();
			System.err.println(e.getMessage());
			CustomSessionFactory.closeSession();
			return false;
		}
		finally
		{
			CustomSessionFactory.closeSession();
		}
		return true;
	}
	
	public static Object get(Class cls, Serializable ser)
	{
		Session session = CustomSessionFactory.getSession();
		Object o = session.get(cls, ser);
		return o;
	}
	
	public static void delete(Object object)
	{
		Transaction ts = null;
		Session session = CustomSessionFactory.getSession();
		if(session == null)
		{
			return;
		}
		
		try
		{
			ts = session.beginTransaction();
			session.delete(object);
			ts.commit();
		}
		catch(Exception e)
		{
			if(ts != null)
			{
				ts.rollback();
			}
			//e.printStackTrace();
			Log.info("hello");
			System.err.println(e.getMessage());
		}
		finally
		{
			CustomSessionFactory.closeSession();
		}
	}

}
