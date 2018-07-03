import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class Email_Social_Checker
{

	static WebDriver driver=null;
	static String SAMPLE_Email_FILE="";
	static DateFormat dateFormat = null;
	static Date date = null;
	static String Email_CSV_Dir ="";

	public static void main(String[] args) throws InterruptedException, IOException
	{
		SAMPLE_Email_FILE=System.getProperty("user.dir")+"/File/Email.csv";

		dateFormat=	new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
		date = new Date();

		ArrayList<String> at = new ArrayList<String>();

		at.add("Email");
		at.add(",");
		at.add("LikedIn");
		at.add(",");
		at.add("Facebook");
		at.add(",");
		at.add("FourSquare");
		at.add(",");
		at.add("pinterest");
		at.add(",");
		at.add("Google Plus");
		at.add(",");
		at.add("Twitter");
		at.add(",");
		at.add("Instagram");
		at.add(",");
		at.add("About Me");
		at.add(",");
		at.add("Flicker");
		at.add(",");
		at.add("Klout");
		at.add(",");
		at.add("\n");
		generateCsvFile(at);
		int proxyIndex=-1;
		while(true)
		{
			int readData = readData();
			proxyIndex++;
			if(proxyIndex>74)
			{
				proxyIndex=0;
			}
			if(readData<(getRowCount()+5))
			{
				ArrayList<String>socialList = new ArrayList<String>();
				try
				{
					String emails="";

					readAndWriteFile();
					emails=getEmailList(readData);

					ChromeOptions options = new ChromeOptions();
					options.addArguments("--incognito");
					options.addArguments("--proxy-server="+getProxy(proxyIndex));
					options.addArguments("--headless");
					driver = new ChromeDriver(options);

					driver.manage().deleteAllCookies();
					driver.manage().window().maximize();

					driver.navigate().to("https://www.manycontacts.com/en/mail-check");

					enterText("emails", emails.substring(0, emails.length()-1));
					click("verify");

					socialList = getSocialList(emails);
					generateCsvFile(socialList);
					driver.quit();

				}
				catch(Exception e)
				{
					e.printStackTrace();
					driver.quit();
				}
			}
			else
			{
				System.out.println("No More redords to process delete AccountCounter file to start from 0");
			}
		}
	}

	public static ArrayList<String> getSocialList(String emails) throws InterruptedException
	{
		ArrayList<String> dummy = new ArrayList<String>();
		Thread.sleep(2000);
		int accountCount = Integer.parseInt(emails.substring((emails.length()-1), (emails.length())));
		for(int in=1;in<=accountCount;in++)
		{
			waitforElementHide("(//i[@class='fa fa-spinner fa-spin'])["+in+"]");
		}
		emails=emails.substring(0, (emails.length()-1));
		String[] emailAddress=emails.split(",",accountCount);
		String linkedln="";
		String facebook="";
		String FourSquare="";
		String pinterest="";
		String googlePlus="";
		String twitter="";
		String instagram="";
		String aboutMe="";
		String flicker="";
		String klout="";
		for(int i=0;i<emailAddress.length;i++)
		{
			emailAddress[i]=emailAddress[i].trim();
			if(!(isElementDisplay("//div[@class='email']/div/span[contains(text(),'"+emailAddress[(i)]+"')]/../i[@style='color: red;']")))
			{
				dummy.add(getText("//div[@class='email']/div/span[contains(text(),'"+emailAddress[(i)]+"')]"));
				dummy.add(",");

				linkedln=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'linkedin')]");
				facebook=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'facebook')]");
				FourSquare=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'foursquare')]");
				pinterest=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'pinterest')]");;
				googlePlus=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'plus.google')]");
				twitter=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'twitter')]");
				instagram=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'insta')]");
				aboutMe=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'about.me')]");
				flicker=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'flickr')]");
				klout=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'klout')]");

				dummy.add(linkedln);
				dummy.add(",");
				dummy.add(facebook);
				dummy.add(",");
				dummy.add(FourSquare);
				dummy.add(",");
				dummy.add(pinterest);
				dummy.add(",");
				dummy.add(googlePlus);
				dummy.add(",");
				dummy.add(twitter);
				dummy.add(",");
				dummy.add(instagram);
				dummy.add(",");
				dummy.add(aboutMe);
				dummy.add(",");
				dummy.add(flicker);
				dummy.add(",");
				dummy.add(klout);
				dummy.add(",");
				dummy.add("\n");
			}
		}
		return dummy;

	}

	public static int getRowCount() throws IOException
	{
		int rowNumber=0;
		Reader reader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/File/Email.csv"));
		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
		@SuppressWarnings("unused")
		String[] nextRecord;
		while ((nextRecord = csvReader.readNext()) != null) 
		{
			rowNumber++;		
		}

		return rowNumber;
	}

	public static String getEmailList(int index) throws IOException
	{
		String email="";
		int count=0;
		for(int k=(index-5);k<index;k++)
		{
			email = email+getEmail(k)+", ";
			count++;
		}
		email=email.substring(0,email.length()-2)+count;
		return email;		
	}

	public static String getEmail(int index) throws IOException
	{
		Reader reader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/File/Email.csv"));
		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
		String[] nextRecord;
		String email="";
		int i=0;
		while ((nextRecord = csvReader.readNext()) != null) 
		{
			if(i==index)
			{
				email = email+nextRecord[2].toString();
				break;
			}
			i++;
		}
		return email;
	}

	public static String getProxy(int index) throws IOException
	{
		Reader reader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/File/Proxy.csv"));
		CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
		String[] nextRecord;
		String proxy="";
		int i=0;
		while ((nextRecord = csvReader.readNext()) != null) 
		{
			if(i==index)
			{
				proxy = nextRecord[0].toString();
				break;
			}
			i++;
		}
		return proxy;
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

	public static void click(String id) throws InterruptedException
	{
		driver.findElement(By.id(id)).click();
	}

	public static void waitforElementHide(String xpath)
	{
		boolean wait=true;
		while(wait)
		{
			try
			{
				Thread.sleep(1000);
				driver.findElement(By.xpath(xpath)).isDisplayed();
			}
			catch(Exception e)
			{
				wait=false;
			}
		}
	}

	public static void enterText(String id,String text) throws InterruptedException
	{
		driver.findElement(By.id(id)).clear();
		driver.findElement(By.id(id)).sendKeys(text);
	}

	public static String getText(String xpath)
	{
		try
		{
			Thread.sleep(200);
			return driver.findElement(By.xpath(xpath)).getText();
		}
		catch(Exception e)
		{
			return "No social profiles found";
		}
	}

	public static String getSocialUrl(String xpath)
	{
		try
		{
			Thread.sleep(200);
			System.out.println(driver.findElement(By.xpath(xpath)).getAttribute("href"));
			return driver.findElement(By.xpath(xpath)).getAttribute("href");
		}
		catch(Exception e)
		{
			return "Social profile not found";
		}
	}

	private static void generateCsvFile(ArrayList<String> list)
	{
		FileWriter writer = null;
		if(list.size()>0)
		{
			try 
			{
				writer = new FileWriter("GeneratedCSV\\GeneratedCSV_"+dateFormat.format(date)+".csv",true);
				//writer.append(list.get(0));
				for(String val : list)
				{ 
					writer.append(val);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else
		{
			System.out.println("No Data to generate file...");
		}
	}

	public static int readData() throws NumberFormatException, IOException
	{
		File file = new File("AccountCounter.txt");
		FileWriter fw =null;
		BufferedWriter bw=null;
		FileReader fr=null;
		BufferedReader br=null;

		if(!file.exists())
		{
			file.createNewFile();
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write("5");
			bw.flush();
			bw.close();
		}
		fr = new FileReader(file);
		br = new BufferedReader(fr);
		int va =Integer.parseInt(br.readLine());
		br.close();
		return va;
	}

	public static String readAndWriteFile() throws IOException
	{
		File file = new File("AccountCounter.txt");
		FileWriter fw =null;
		BufferedWriter bw=null;
		FileReader fr=null;
		BufferedReader br=null;

		if(!file.exists())
		{
			file.createNewFile();
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write("5");
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
		bw.write(String.valueOf((va+5)));
		bw.flush();
		bw.close();

		fr = new FileReader(file);
		br = new BufferedReader(fr);
		String va1 = br.readLine();
		br.close();
		return va1;	
	}

}
