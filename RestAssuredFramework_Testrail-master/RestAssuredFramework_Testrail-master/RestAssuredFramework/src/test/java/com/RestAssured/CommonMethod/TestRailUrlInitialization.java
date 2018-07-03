package com.RestAssured.CommonMethod;

import static com.jayway.restassured.RestAssured.given;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import com.jayway.restassured.specification.RequestSpecification;

public class TestRailUrlInitialization
{
	PropertyFileReader propertyReader = null;
	public String baseUrl = "";
	
	@BeforeClass
	public void setURL()
	{
		propertyReader = new PropertyFileReader();
		baseUrl = propertyReader.getKey("TestRailbaseURL");
	}
	
	@BeforeMethod
	public RequestSpecification loginInToApplication()
	{
		String username = propertyReader.getKey("username");
		String password = propertyReader.getKey("password");
		return given().
		auth().
		preemptive().
		basic(username, password);
	}

}
