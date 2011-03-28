package com.smit.openfire.plugin.util;

public class SmitStringUtil {
	public static String TwoSubStringMid(String str, String sub1, String sub2)
	{
		int i = str.indexOf(sub1);
		int j = str.indexOf(sub2);
		if(i == -1 || j == -1)
		{
			return "";
		}
		int begin = i+sub1.length();
		int end = j;
		if(begin >= end)
		{
			return "";
		}
		String ret = str.substring(begin, end);
		return ret;
	}
}
