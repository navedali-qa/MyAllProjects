package com.RestAssured.API;

import org.testng.annotations.Test;

import com.RestAssured.APIBody.PostBody;
import com.RestAssured.CommonMethod.UrlInitialization;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.RestAssured.*;

public class APITesting extends UrlInitialization
{
	//Get method
	@Test
	public void apiTest_01()
	{
		Response response = given().
				when().
				get(baseUrl+"/posts");
		System.out.println("Respone Body : \n"+response.asString());
		//Assert.assertTrue(response.asString().contains("json-server"));
	}
	
	//GET method with parameter
	//@Test
	public void apiTest_02()
	{
		Response response = given().
				param("id", "1").
				param("id","2").
				when().
				get(baseUrl+"/posts");
		System.out.println("Status code : "+response.getStatusCode());
		System.out.println("Content type : "+response.getContentType());
		System.out.println("Respone Body : \n"+response.asString());
	}
	
	//POST method
	//@Test
	public void apiTest_03()
	{
		PostBody postBody = new PostBody();
		postBody.setId("3");
		postBody.setTitle("Naved Body Title");
		postBody.setAuthor("Demo");
		
		Response response = given().
				body(postBody).
				when().
				contentType(ContentType.JSON).
				post(baseUrl+"/posts");
		
		System.out.println(response.getStatusCode());
	}
	
	//DELETE method
	//@Test
	public void apiTest_04()
	{
		Response response = given().
				when().
				contentType(ContentType.JSON).
				delete(baseUrl+"/posts/1");
		System.out.println("Status code : "+response.getStatusCode());
		System.out.println("Content type : "+response.getContentType());
		System.out.println("Respone Body : \n"+response.asString());
	}
	
	//Return statusCode
	@Test
	public void apiTest_05()
	{
	Response response=	given().
		when().
		get(baseUrl+"/posts");
	System.out.println(response);
	}
}
