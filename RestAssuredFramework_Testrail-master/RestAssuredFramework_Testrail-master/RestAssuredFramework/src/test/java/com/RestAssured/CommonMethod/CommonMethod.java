package com.RestAssured.CommonMethod;

import java.util.Map;

import org.testng.Assert;

import com.jayway.restassured.response.Response;

public class CommonMethod extends CommonMethodParent
{

	public int receiveStatusCode(Response response)
	{
		return response.getStatusCode();
	}
	
	public String receiveContentType(Response response)
	{
		return response.getContentType();
	}
	
	public Map<String, String> receiveCookies(Response response)
	{
		 return response.getCookies(); 
	}
	
	public String receiveBody(Response response)
	{
		return response.getBody().asString();
	}
	
	public void verifyBodyContains(Response response, String text)
	{
		Assert.assertTrue(isTextPresentInBody(response,text));
	}
}
