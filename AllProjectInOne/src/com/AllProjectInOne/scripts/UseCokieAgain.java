package com.AllProjectInOne.scripts;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class UseCokieAgain
{

	WebDriver driver=null;

	private final String COMMA_DELIMITER = ",";
	private final String NEW_LINE_SEPARATOR = "\n";
	private final String FILE_HEADER = "Name,Value,Domain,Path,Expiry";


	@Test
	public void useCokieAgain() throws Exception
	{

		System.out.println("\n\nProgram starts at : "+new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(Calendar.getInstance().getTime())+"\n\n");

		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();

		options.addArguments("--disable-infobars");

		if(new File("DummyCookie.csv").exists())
		{
			readCSV();
		}
		driver = new ChromeDriver(options);

		driver.manage().window().maximize();

		//driver.manage().deleteAllCookies();

		driver.navigate().to("https://signin.techsmith.com");

		//driver.findElement(By.cssSelector("#SignIn_Email")).sendKeys("demo123321@yopmail.com");

		driver.findElement(By.cssSelector(".primary-button.js-next-button.t-next-button")).click();

		Thread.sleep(10000);

		driver.findElement(By.cssSelector("#signin-password-hidden")).sendKeys("1qaz!QAZ");

		driver.findElement(By.cssSelector(".primary-button.js-signin-button.t-signin-button")).click();

		Thread.sleep(5000);

		Set<Cookie> cookies = driver.manage().getCookies();

		generateCSV(cookies);
	}

	private void generateCSV(Set<Cookie> cookies) 
	{
		System.out.println(cookies);
		Iterator<Cookie> itr = cookies.iterator();
		FileWriter fileWriter = null;
		try
		{
			fileWriter = new FileWriter("DummyCookie.csv");

			fileWriter.append(FILE_HEADER.toString());

			fileWriter.append(NEW_LINE_SEPARATOR);

			while (itr.hasNext())
			{
				Cookie cookie = itr.next();
				fileWriter.append(cookie.getName());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(cookie.getValue());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(cookie.getDomain());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(cookie.getPath());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(String.valueOf(cookie.getExpiry()));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			System.out.println("CSV file was created successfully !!!");

		} 
		catch (Exception e) 
		{
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				fileWriter.flush();
				fileWriter.close();
			}
			catch (IOException e) 
			{
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
	}

	public void readCSV()
	{
		try
		{
			BufferedReader fileReader = null;

			String line = "";

			//Create the file reader
			fileReader = new BufferedReader(new FileReader("DummyCookie.csv"));

			//Read the CSV file header to skip it
			fileReader.readLine();

			while ((line = fileReader.readLine()) != null) 
			{
				//Get all tokens available in line
				String[] tokens = line.split(COMMA_DELIMITER);
				if (tokens.length > 0) 
				{
					DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
					Date date=null;
					if(tokens[4] != null)
					{
						date = (Date)formatter.parse(tokens[4]);
					}
					driver.manage().addCookie(new Cookie(tokens[0], tokens[1], tokens[2], tokens[3],date));
				}
			}
		}
		catch(Exception e)
		{

		}
	}

	@AfterMethod
	public void quitDriver()
	{
		driver.quit();
	}

	//@Test
	public void demo() throws ParseException
	{
		String date = "Sat Jan 05 04:11:29 IST 2019";
		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
		//Date d = (Date)formatter.parse(null);
		Date d = null;
		System.out.println(d);

	}
}
