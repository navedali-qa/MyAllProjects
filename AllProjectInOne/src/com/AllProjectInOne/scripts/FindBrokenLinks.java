package com.AllProjectInOne.scripts;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class FindBrokenLinks
{

	WebDriver driver=null;
	
	@Test
	public void findBrokenLinks()
	{
		
		System.out.println("\n\nProgram starts at : "+new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(Calendar.getInstance().getTime())+"\n\n");

		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();

		options.addArguments("--disable-infobars");

		driver=new ChromeDriver();

		driver.manage().window().maximize();

		driver.get("http://www.google.co.in/");

		List<WebElement> links=driver.findElements(By.tagName("a"));

		System.out.println("Total links are "+links.size());

		for(int i=0;i<links.size();i++)
		{

			WebElement ele= links.get(i);

			String url=ele.getAttribute("href");

			verifyLinkActive(url);

		}

		System.out.println("\n\nProgram ends at : "+new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(Calendar.getInstance().getTime())+"\n\n");
	
	}

	public void verifyLinkActive(String linkUrl)
	{
		try 
		{
			URL url = new URL(linkUrl);

			HttpURLConnection httpURLConnect=(HttpURLConnection)url.openConnection();

			httpURLConnect.setConnectTimeout(3000);

			httpURLConnect.connect();

			if(httpURLConnect.getResponseCode()==200)
			{
				System.out.println(linkUrl+" - "+httpURLConnect.getResponseMessage());
			}
			if(httpURLConnect.getResponseCode()==HttpURLConnection.HTTP_NOT_FOUND)  
			{
				System.out.println(linkUrl+" - "+httpURLConnect.getResponseMessage() + " - "+ HttpURLConnection.HTTP_NOT_FOUND);
			}
		} catch (Exception e) {

		}
	} 

	@AfterMethod
	public void quitDriver()
	{
		driver.quit();
	}

}