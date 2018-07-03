package com.GoodBadDBChecker.scripts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class Dummy {

	static String demo = "admin@pourqwapa.com,roleplay,orange";
	
	public static void main(String as[]) throws IOException
	{
		String[] bj = demo.split(",");
		for(int i = 0;i<bj.length;i++)
		{
			System.out.println(bj[i]);
		}
	}

}
