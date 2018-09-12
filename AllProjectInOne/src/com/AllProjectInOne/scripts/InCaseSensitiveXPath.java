package com.AllProjectInOne.scripts;

import java.text.SimpleDateFormat;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class InCaseSensitiveXPath
{

	WebDriver driver=null;

	@Test
	public void inCaseSensitiveXPath() throws Exception
	{

		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();

		options.addArguments("--disable-infobars");

		driver = new ChromeDriver(options);

		driver.manage().window().maximize();

		driver.navigate().to("https://www.google.com");

		Thread.sleep(5000);

		System.out.println("\n\nNormal xpath : "+new SimpleDateFormat("dd-MM-yyy HH:mm:ss:SSSSSSSSSS").format(Calendar.getInstance().getTime())+"\n\n");

		driver.findElement(By.xpath("//input[@name='q']")).sendKeys("demo ");

		System.out.println("\n\nNormal xpath end : "+new SimpleDateFormat("dd-MM-yyy HH:mm:ss:SSSSSSSSSS").format(Calendar.getInstance().getTime())+"\n\n");

		driver.findElement(By.name("q")).sendKeys("demo ");

		System.out.println("\n\nID xpath end : "+new SimpleDateFormat("dd-MM-yyy HH:mm:ss:SSSSSSSSSS").format(Calendar.getInstance().getTime())+"\n\n");

		driver.findElement(By.xpath("//input[translate(@name, 'abcdefghijklmnopqrstuvwxyz', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')='Q']")).sendKeys("demo ");

		System.out.println("\n\ntranslate xpath end : "+new SimpleDateFormat("dd-MM-yyy HH:mm:ss:SSSSSSSSSS").format(Calendar.getInstance().getTime())+"\n\n");

	}

	@AfterMethod
	public void quitDriver()
	{
		driver.quit();
	}
}
