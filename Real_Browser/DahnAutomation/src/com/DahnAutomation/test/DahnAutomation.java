package com.DahnAutomation.test;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DahnAutomation
{

	static WebDriver driver=null;

	public static void main(String[] args) throws Exception
	{
		Reader recordsReader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/URLs.csv"));
		CSVReader records = new CSVReaderBuilder(recordsReader).withSkipLines(1).build();
		String[] record;
		int index=getRandomIntegerBetweenRange(0, 535);
		int users=0;
		int  usersCount=1;
		try
		{
			usersCount=Integer.parseInt(args[0]);
		}
		catch(Exception e){}
		int count=0;
		while ((record = records.readNext()) != null)
		{
			if(index==count)
			{
				users++;
				manageDahnAutomation(record[0], record[1], record[2],getRandomIntegerBetweenRange(0, 5));
				index=getRandomIntegerBetweenRange(0, 535);
				count=0;
				System.out.println("\n**********************************************************\n");
			}
			if(users==usersCount)
			{
				break;
			}
			count++;
		}
	}

	public static void manageDahnAutomation(String proxy, String url, String userAgent,int clicks) throws Exception
	{
		invokeBrowser(proxy, url, userAgent);
		System.out.println("\n**********************************************************\n");
		System.out.println("Performing actions for : ");
		System.out.println("Proxy : "+proxy);
		System.out.println("URL : "+url);
		System.out.println("UserAgent : "+userAgent);
		System.out.println("Click : "+clicks);
		if(driver!=null)
		{
			if(clicks==0)
			{
				randomClick(getRandomIntegerBetweenRange(1, 120));

			}
			else
			{
				click("//a[contains(@class,'btn_start')]");
				randomClick(getRandomIntegerBetweenRange(1, 120));
				if(clicks==1)
				{
					click("//button[contains(@class,'ico-arrow-right2') and contains(@class,'btn_nav-border')]");
					randomClick(getRandomIntegerBetweenRange(1, 120));
				}
				else
					if(clicks==2)
					{
						for(int i=1;i<=2;i++)
						{
							click("//button[contains(@class,'ico-arrow-right2') and contains(@class,'btn_nav-border')]");
							randomClick(getRandomIntegerBetweenRange(1, 120));
						}
					}
					else
						if(clicks==3)
						{
							int randomSize = getRandomIntegerBetweenRange(3, 4);
							for(int i=1;i<=randomSize;i++)
							{
								click("//button[contains(@class,'ico-arrow-right2') and contains(@class,'btn_nav-border')]");
								randomClick(getRandomIntegerBetweenRange(1, 120));
							}
						}
						else
							if(clicks==3)
							{
								int randomSize = getRandomIntegerBetweenRange(5, 13);
								for(int i=1;i<=randomSize;i++)
								{
									click("//button[contains(@class,'ico-arrow-right2') and contains(@class,'btn_nav-border')]");
									randomClick(getRandomIntegerBetweenRange(1, 120));
								}
							}
			}
			driver.quit();
		}
		else
		{
			driver.quit();
		}
	}

	public static int getRandomIntegerBetweenRange(int min, int max)
	{
		int x = (int)(Math.random()*((max-min)+1))+min;
		return x;
	}

	private static void randomClick(int clickCount) throws InterruptedException
	{
		try
		{
			if(clickCount==1)
			{
				clickCount++;
			}
			for(int i=1;i<=clickCount;i++)
			{
				clickLink(driver.findElements(By.tagName("a")).size());
			}
		}
		catch(Exception e) {}
	}

	private static void clickLink(int size) throws InterruptedException
	{
		try
		{
			if(size>120)
			{
				size=120;
			}
			size=getRandomIntegerBetweenRange(2, size);
			String base = driver.getWindowHandle();
			for(int i=1;i<=size;i++)
			{
				WebElement link = driver.findElement(By.xpath("(//a)["+i+"]"));
				Actions newTab = new Actions(driver);
				newTab.keyDown(Keys.SHIFT).click(link).keyUp(Keys.SHIFT).build().perform();
				Thread.sleep(getRandomIntegerBetweenRange(1, 10)*1000);
				if(driver.getWindowHandles().size()>1)
				{
					Set<String> set = driver.getWindowHandles();
					driver.switchTo().window((String) set.toArray()[1]);
					driver.close();
					driver.switchTo().window(base);
					if(isElementDisplay("//div[@class='str-feedback-dialog']/header/span"))
					{
						click("//div[@class='str-feedback-dialog']/header/span");
					}
				}
			}
		}
		catch(Exception e) {}
	}

	public static WebDriver invokeBrowser(String proxy, String url, String userAgent) throws Exception
	{
		ChromeOptions options = new ChromeOptions();

		options.addArguments("--proxy-server="+proxy);
		options.addArguments("--user-agent="+userAgent);
		options.addArguments("disable-infobars");

		WebDriverManager.chromedriver().setup();

		driver = new ChromeDriver(options);
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		url = url+getRandomIntegerBetweenRange(1000000, 9999999);
		driver.navigate().to(url);
		driver.getTitle();

		return driver;
	}

	public static void click(String locator) throws InterruptedException
	{
		try
		{
			WebElement el = waitForElement(locator, 10);
			el.click();
		}
		catch(Exception exception){}
	}

	public static WebElement waitForElement(String locator, int timeOutSeconds)
	{
		WebElement el=null;

		WebDriverWait  wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
		el = driver.findElement(By.xpath(locator));

		return el;
	}

	public static Boolean isElementDisplay(String locator)
	{
		Boolean result = false;
		try 
		{
			driver.findElement(By.xpath(locator));
			result = true;
		} 
		catch (Exception ex) 
		{
		}
		return result;
	}

}
