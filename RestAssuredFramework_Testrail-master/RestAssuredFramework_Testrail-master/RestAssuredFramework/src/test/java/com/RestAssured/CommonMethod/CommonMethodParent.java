package com.RestAssured.CommonMethod;

import com.jayway.restassured.response.Response;

public class CommonMethodParent
{

	public Boolean isTextPresentInBody(Response response,String text)
	{
		if(response.getBody().asString().contains(text))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
