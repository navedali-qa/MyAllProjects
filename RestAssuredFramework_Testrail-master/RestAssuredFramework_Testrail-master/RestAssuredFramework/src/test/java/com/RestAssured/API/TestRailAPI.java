package com.RestAssured.API;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.RestAssured.CommonMethod.CommonMethod;
import com.RestAssured.CommonMethod.TestRailUrlInitialization;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;

public class TestRailAPI extends TestRailUrlInitialization
{
	int project_id=0;
	int suite_id=0;
	CommonMethod commonMethod = null;
	
	public TestRailAPI()
	{
		commonMethod = new CommonMethod();
	}
	
	//Add project to testrail
	@Test(priority=1)
	public void addProject()
	{
		Response response = loginInToApplication().
				body("{\r\n" + 
						"	\"name\": \"This is a new project\",\r\n" + 
						"	\"announcement\": \"This is the description for the project\",\r\n" + 
						"	\"show_announcement\": true\r\n" + 
						"}").
				when().
				contentType(ContentType.JSON).
				post(baseUrl+"/index.php?/api/v2/add_project");
		Assert.assertEquals(response.getStatusCode(), 200);
	}
	
	//Get all project from testrail
	@Test(priority=2)
	public void getAllProject()
	{
		Response response = loginInToApplication().
				when().
				contentType(ContentType.JSON).
				get(baseUrl+"/index.php?/api/v2/get_projects");
		Assert.assertEquals(response.getStatusCode(), 200);
		project_id = response.then().extract().path("[0].id");
	}

	//Get single project from testRail
	@Test(priority=3)
	public void getSingleProject()
	{

		Response response = loginInToApplication().
				when().
				contentType(ContentType.JSON).
				get(baseUrl+"/index.php?/api/v2/get_project/"+project_id);
		System.out.println(response.asString());
	}
	
	//Update the project
	@Test(priority=4)
	public void updateProject()
	{
		Response response = loginInToApplication().
				body("{\r\n" + 
						"	\"name\": \"This is a Updated project\",\r\n" + 
						"	\"announcement\": \"This is the updated description for the project\",\r\n" + 
						"	\"show_announcement\": true,\r\n" + 
						"	\"is_completed\" : true\r\n"+
						"}").
				when().
				contentType(ContentType.JSON).
				post(baseUrl+"/index.php?/api/v2/update_project/"+project_id);
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertFalse(response.asString().contains("This is a new project"));
	}

	//Add suite to project
	@Test(priority=5)
	public void addSuite()
	{

		Response response = loginInToApplication().
				body("{\r\n" + 
						"	\"name\": \"This is a new test suite\",\r\n" + 
						"	\"description\": \"Use the description to add additional context details\"\r\n" + 
						"}").
				when().
				contentType(ContentType.JSON).
				post(baseUrl+"/index.php?/api/v2/add_suite/13");//+project_id);
		Assert.assertEquals(response.getStatusCode(), 200);
		suite_id = response.then().extract().path("id");
				System.out.println("suite_id : "+suite_id);
	}
	
	//Delete project
	@Test(priority=6)
	public void deleteProject()
	{

		Response response = loginInToApplication().
				when().
				contentType(ContentType.JSON).
				post(baseUrl+"/index.php?/api/v2/delete_project/"+project_id);
		Assert.assertEquals(response.getStatusCode(), 200);
	}
	
	//Try to get details of deleted project
	@Test(priority=7)
	public void getUnknownProject() throws Exception
	{

		Response response = loginInToApplication().
				when().
				contentType(ContentType.JSON).
				get(baseUrl+"/index.php?/api/v2/get_project/"+project_id);
		Assert.assertEquals(response.getStatusCode(), 400);
		commonMethod.verifyBodyContains(response, "error");
	}

	
}
