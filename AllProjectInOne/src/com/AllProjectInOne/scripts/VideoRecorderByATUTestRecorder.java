package com.AllProjectInOne.scripts;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import atu.testrecorder.ATUTestRecorder;
import io.github.bonigarcia.wdm.WebDriverManager;

public class VideoRecorderByATUTestRecorder
{
	WebDriver driver = null;
	ATUTestRecorder recorder = null;

	@Test
	public void videoRecorderByATUTestRecorder() throws Exception 
	{
		DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");

		Date date = new Date();

		if(!new File(System.getProperty("user.dir")+"/Recordings").exists())
		{
			new File(System.getProperty("user.dir")+"/Recordings").mkdirs();
		}
		recorder = new ATUTestRecorder(System.getProperty("user.dir")+"/Recordings/","TestVideo-"+dateFormat.format(date),false);

		//To start video recording.
		recorder.start();  

		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();

		options.addArguments("--disable-infobars");

		driver = new ChromeDriver(options);

		driver.manage().window().maximize();

		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get("http://google.com/");

		driver.manage().window().setSize(new Dimension(400,768));
		Thread.sleep(2000);  

		driver.manage().window().setSize(new Dimension(400,400));
		Thread.sleep(2000);

		driver.manage().window().setSize(new Dimension(1024,400));      
	} 

	@AfterTest
	public void quitDriver() throws Exception 
	{
		driver.quit();

		//To stop video recording.
		recorder.stop();;
	}
}
