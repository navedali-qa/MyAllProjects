package com.RestAssured.CommonMethod;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertyFileReader
{
	String path =  getLocalPath();

	public String getKey(String key)
	{ 
		String value = "";    	
		try
		{  
			Properties prop = new Properties();	          
			File f = new File(path + File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"com"+File.separator+"RestAssured"+File.separator+"config"+File.separator+"application.properties");	         
			if(f.exists())
			{
				prop.load(new FileInputStream(f));
				value = prop.getProperty(key); 		                 
			}
		}
		catch(Exception e)
		{  
			System.out.println("Failed to read from application.properties file.");  
		}
		return value;
	} 

	public String getLocalPath()
	{		
		String path =" ";		
		File file = new File("");
		String absolutePathOfFirstFile = file.getAbsolutePath();
		path = absolutePathOfFirstFile.replaceAll("\\\\+", "/");		
		return path;
	} 
}
