package com.AllProjectInOne.scripts;

import com.automation.remarks.video.annotations.Video;
import com.automation.remarks.video.testng.VideoListener;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(VideoListener.class)
public class VideoRecorderBYVideoRecorderLessSize
{
	//As of now it will only capture video of failed test cases
	
	WebDriver driver =null;
	
	@Test
	@Video
	public void videoRecorderBYVideoRecorderLessSize() throws Exception
	{
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
		
		Assert.assertTrue(false);
	}

	@Test
	@Video
	public void videoShouldHaveNameSecondTest() throws Exception
	{
		Thread.sleep(1000);
		Assert.assertTrue(true);
	}
}