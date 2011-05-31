package com.smit.util.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class Log {

	/**
	 * @param args
	 */
	private static Logger logger = Logger.getLogger("root");
	static{
		DOMConfigurator.configure("log4j.xml");
	}
	public Log(){
		
	}
	
	public static void debug(String debug){
		logger.debug(debug);
	}
	
	public static void debug(String message,Throwable throwable){
		logger.debug(message, throwable);
	}
	
	public static void info(String info){
		logger.info(info);
	}
	
	public static void info(String message , Throwable t){
		logger.info(message, t);
	}
	
	public static void warn(String warn){
		logger.warn(warn);
	}
	
	public static void warn(String message,Throwable t){
		logger.warn(message, t);
	}
	public static void error(String error){
		logger.error(error);
	}
	
	public static void error(String message,Throwable t){
		logger.error(message, t);
	}
	
	public static void main(String[] args) {
		DOMConfigurator.configure("log4j.xml");
		logger.info("hello!");
		logger.warn("this is warn test!");
		logger.error("this is error test!");

	}

}
