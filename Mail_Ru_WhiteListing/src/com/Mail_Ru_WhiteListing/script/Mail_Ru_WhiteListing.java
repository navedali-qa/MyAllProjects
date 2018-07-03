package com.Mail_Ru_WhiteListing.script;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.openqa.selenium.By;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;

public class Mail_Ru_WhiteListing 
{
	static WebDriver driver;
	static int allMail=0;
	static int count=0;
	static String value1="";
	static String value2="";
	static String result="";
	static ArrayList<String> ar = null;
	static String csvEmail=null;
	static String spreadsheetId = "1PGcj_SfLN1G_XQT7jklAvaCBVZba-tMuG0Qk-z4eG94";
	static Sheets service=null;

	public static void main(String[] args) throws IOException
	{
		service = GoogleSheetAPI.getSheetsService();
		String range = "Accounts!A2:D";
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();
		int rowCount=GoogleSheetAPI.getRows(range);

		while(true)
		{
			String[] proxy = getProxy();

			int l=Integer.parseInt(readAndWriteFile("AccountCounter.txt"));
			String result="Yes";
			ar = new ArrayList<String>();
			try
			{
				int k=1;

				for (@SuppressWarnings("rawtypes") List row : values) 
				{
					if(k==l)
					{
						if(row.get(2).toString().toLowerCase().contains("test"))
						{
							//System.out.println("==========================");
							System.out.println("Running script for : "+row.get(0).toString()+"\t"+ row.get(1).toString()+"\t"+row.get(2).toString());
							System.out.println("==========================");
							result=manageMail(proxy, row.get(0).toString(), row.get(1).toString());
							csvEmail=row.get(1).toString();
							List<String> data = new ArrayList<String>();
							data.add(result);
							System.out.println("GOT Result : "+result);
							GoogleSheetAPI.writeData(data,k+1);
							k=0;
							break;
						}
					}
					k++;
				}
				//generateCsvFile(csvEmail,ar);
				if(driver !=null) {driver.quit();}

			}
			catch(Exception e) 
			{
				System.out.println("rese");
				if(driver !=null) {driver.quit();}
				e.printStackTrace();
			}
			if(l>(rowCount+20))
			{
				break;
			}
		}
	}

	private static String manageMail(String[] proxy, String username, String password)
	{
		try
		{
			driver=invokeBrowser(proxy);

			while(driver==null)
			{
				driver=invokeBrowser(proxy);
			}

			String loginStatus = login(username,password);
			System.out.println(loginStatus);
			if(loginStatus.contains("Phone verification message displayed"))
			{
				return "Phone verification message displayed";
			}
			else
			{
				updateDetails(username);
				logOut();
				return "Yes";
			}
		}
		catch(Exception e) {return "No";}
	}

	private static void updateDetails(String email) throws Exception 
	{
		Thread.sleep(2000);
		boolean open = true;
		if(isElementDisplay(Xpaths.unreadMail))
		{
			if(open)
			{
				click(Xpaths.unreadMail);
				open = false;
			}
			Thread.sleep(10000);
			int writeIndex=Integer.parseInt(Logcounter())-1;
			while(true)
			{
				List<Object> data1 = new ArrayList<Object>();
				data1.add(getText(Xpaths.emailTime));
				data1.add(getText(Xpaths.subject));
				data1.add(getAttributeValue(Xpaths.senderEmail, "data-contact-informer-email"));
				data1.add(email);
				click(Xpaths.flagEmail);
				driver.findElement(By.tagName("body")).sendKeys(Keys.DELETE);
				List<List<Object>> data = new ArrayList<List<Object>>();
				data.add(data1);
				GoogleSheetAPI.updateBunch("Logs", writeIndex, "D", data);
				writeIndex++;
				Thread.sleep(10000);
				if(driver.getCurrentUrl().contains("/inbox"))
				{break;}
			}
		}
	}

	public static String[] getProxy() throws IOException
	{
		service = GoogleSheetAPI.getSheetsService();
		String range = "Proxies!A2:A";
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();
		String[] domains = new String[values.size()];
		int i=0;
		for (@SuppressWarnings("rawtypes") List row : values) 
		{
			domains[i]=row.get(0).toString();
			i++;
		}
		return domains ;
	}

	public static WebDriver invokeBrowser(String[] proxyString) throws InterruptedException
	{
		Boolean result=true;
		ArrayList<Integer> list = new ArrayList<Integer>(proxyString.length);
		for(int i = 0; i <= proxyString.length; i++)
		{
			list.add(i);
		}

		Random rand = new Random();

		int index = rand.nextInt(list.size());
		int l=list.remove(index).intValue();

		ChromeOptions options = new ChromeOptions();

		options.addArguments("--proxy-server="+proxyString[l]);

		options.addArguments("disable-infobars");
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver(options);

		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();

		driver.navigate().to("https://mail.ru/");
		driver.getTitle();
		if(driver.getPageSource().contains("This site can’t be reached"))
		{
			driver.quit();
			driver=null;
		}

		waitForTitle("Mail.Ru: почта, поиск в интернете, новости, игры");

		if(result)
		{
			ArrayList<String> data = new ArrayList<String>();
			data.add("Working fine");
			GoogleSheetAPI.UpdateProxyStatus(data, l);
			data.remove(0);
		}

		return driver;
	}

	public static String login(String email,String password) throws InterruptedException
	{
		enterText(Xpaths.email_field, email);
		enterText(Xpaths.password_field, password);
		click(Xpaths.submitButton);

		waitForTitle("Входящие - Почта Mail.Ru");
		if(driver.getCurrentUrl().contains("/cgi-bin/passremind"))
		{
			return "Phone verification message displayed";
		}

		if(isElementDisplay(Xpaths.baloonPopup))
		{
			click(Xpaths.baloonPopup);
		}
		if(driver.getTitle().contains("Входящие - Почта Mail.Ru"))
		{
			return "Successfull";
		}
		else
		{
			return "UnSuccess";
		}
	}

	public static void logOut() throws InterruptedException
	{
		click("//a[@id='PH_logoutLink']");
		Thread.sleep(3000);
	}

	public static void waitForTitle(String title) throws InterruptedException
	{
		int i=0;
		while(!driver.getTitle().toLowerCase().contains(title.toLowerCase())) 
		{
			Thread.sleep(1000);
			if(i>120)
			{
				break;
			}
			i++;
		}
	}

	public static Boolean isElementDisplay(String locator)
	{
		Boolean result = false;
		try 
		{
			waitForElement(locator);
			driver.findElement(By.xpath(locator));
			result = true;
		} 
		catch (Exception ex) 
		{
			System.out.print(" isElementDisplay("+locator+") ");
		}
		return result;
	}

	public static void enterText(String locator,String text) throws InterruptedException
	{
		try
		{
			waitForElement(locator);
			driver.findElement(By.xpath(locator)).clear();
			driver.findElement(By.xpath(locator)).sendKeys(text);
		}
		catch(Exception e)
		{
			System.out.print(" enterText("+locator+","+text+") ");
		}
	}

	public static void click(String locator) throws InterruptedException
	{
		try
		{
			waitForElement(locator);
			driver.findElement(By.xpath(locator)).click();		
		}
		catch(Exception elementNotInteractableException)
		{
			System.out.print("Click("+locator+") ");

		}
	}

	public static String getText(String locator)
	{
		try 
		{
			waitForElement(locator);
			return driver.findElement(By.xpath(locator)).getText();
		}
		catch(ElementNotVisibleException elementNotVisibleException)
		{
			//System.out.println("NO ACTION PERFORMED...");
			return "";
		}
	}

	public static String getAttributeValue(String locator,String attribute)
	{
		waitForElement(locator);
		return driver.findElement(By.xpath(locator)).getAttribute(attribute);
	}

	public static String readAndWriteFile(String fileName) throws IOException
	{
		File file = new File(fileName);
		FileWriter fw =null;
		BufferedWriter bw=null;
		FileReader fr=null;
		BufferedReader br=null;

		if(!file.exists())
		{
			file.createNewFile();
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write("0");
			bw.flush();
			bw.close();
		}
		fr = new FileReader(file);
		br = new BufferedReader(fr);
		int va =Integer.parseInt(br.readLine());
		br.close();

		file.createNewFile();
		fw = new FileWriter(file);
		bw = new BufferedWriter(fw);
		bw.write(String.valueOf((va+1)));
		bw.flush();
		bw.close();

		fr = new FileReader(file);
		br = new BufferedReader(fr);
		String va1 = br.readLine();
		br.close();
		return va1;	
	}

	public static String Logcounter() throws IOException
	{
		File file = new File("LogCounter.txt");
		FileWriter fw =null;
		BufferedWriter bw=null;
		FileReader fr=null;
		BufferedReader br=null;

		if(!file.exists())
		{
			file.createNewFile();
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(String.valueOf(GoogleSheetAPI.nextAvailableRow()));
			bw.flush();
			bw.close();
		}
		fr = new FileReader(file);
		br = new BufferedReader(fr);
		int va =Integer.parseInt(br.readLine());
		br.close();

		file.createNewFile();
		fw = new FileWriter(file);
		bw = new BufferedWriter(fw);
		if(va>=GoogleSheetAPI.nextAvailableRow())
		{
			va=va+1;
		}

		bw.write(String.valueOf((va+1)));
		bw.flush();
		bw.close();

		fr = new FileReader(file);
		br = new BufferedReader(fr);
		String va1 = br.readLine();
		br.close();
		return va1;	
	}

	public static void waitForElement(String locator)
	{
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(locator))));
	}
}

