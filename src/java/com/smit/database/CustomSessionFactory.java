package com.smit.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class CustomSessionFactory {
	
	private static Configuration configuration = null;
	private static SessionFactory sessionFactory = null;
	private static ThreadLocal threadLocal = new ThreadLocal();
	private static String configFile = "/hibernate.cfg.xml";

	public static Session getSession()
	{
		if(configuration == null)
		{
			configuration = new Configuration().configure(configFile);
		}
		if(sessionFactory == null)
		{
			sessionFactory = configuration.buildSessionFactory();
		}
		//return sessionFactory.openSession();
		Session s = (Session) threadLocal.get();
		if (s == null)
		{
			s = sessionFactory.openSession();
			threadLocal.set(s);
		}
		return s;
	}
	
	public static void closeSession()
	{
		Session s = (Session) threadLocal.get();
		if (s != null) {
			s.close();
		}
		threadLocal.set(null);
	}
}
