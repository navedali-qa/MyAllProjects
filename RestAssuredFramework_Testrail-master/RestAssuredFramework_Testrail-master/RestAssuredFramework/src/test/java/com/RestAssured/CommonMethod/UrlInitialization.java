package com.RestAssured.CommonMethod;

import org.testng.annotations.BeforeClass;

public class UrlInitialization
{
	PropertyFileReader propertyReader = null;
	public String baseUrl = "";
	
	@BeforeClass
	public void setURL()
	{
		propertyReader = new PropertyFileReader();
		baseUrl = propertyReader.getKey("baseURL");
	}

}
