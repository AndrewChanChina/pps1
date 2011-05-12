package com.smit.test;

import java.util.List;

import com.smit.database.CustomSessionFactory;
import com.smit.database.DatabaseMan;
import com.smit.vo.Test;

public class AddHibernateTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		//TEST saveOrUpdate
		Test test = new Test();
		test.setId(null);
		test.setTitle("3 title");
		DatabaseMan.saveOrUpdate(test);
		
		
		
		List<Test> testList = (List<Test>)DatabaseMan.select("from Test");
		for(int i=0; i<testList.size(); i++)
		{
			Test t = testList.get(i);
			System.out.println( "ID: " + t.getId()  + ", Title: " + t.getTitle());
		}
		
		DatabaseMan.delete(testList.get(0));
	}

}
