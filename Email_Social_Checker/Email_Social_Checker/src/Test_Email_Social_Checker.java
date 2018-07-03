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
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class Test_Email_Social_Checker
{

	static WebDriver driver=null;
	static String SAMPLE_Email_FILE="";
	static DateFormat dateFormat = null;
	static Date date = null;
	static String Email_CSV_Dir ="";

	public static void main(String[] args) throws InterruptedException, IOException
	{
		SAMPLE_Email_FILE=System.getProperty("user.dir")+"/File/Email.csv";

		addColumnInFile(SAMPLE_Email_FILE);

		dateFormat=	new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
		date = new Date();

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

					driver = new ChromeDriver(options);

					driver.manage().deleteAllCookies();
					driver.manage().window().maximize();

					driver.navigate().to("https://www.manycontacts.com/en/mail-check");

					enterText("emails", emails.substring(0, emails.length()-1));
					click("verify");

					socialList = getSocialList(emails);
					updateCsvFile(SAMPLE_Email_FILE,socialList);
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

	@SuppressWarnings("deprecation")
	public static void updateCSV(String fileToUpdate, String replace,int row, int col) throws IOException 
	{
		try
		{
			File inputFile = new File(fileToUpdate);

			CSVReader reader = new CSVReader(new FileReader(inputFile), ',');
			List<String[]> csvBody = reader.readAll();
			csvBody.get(row)[col] = replace;
			reader.close();

			CSVWriter writer = new CSVWriter(new FileWriter(inputFile), ',');
			writer.writeAll(csvBody);
			writer.flush();
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static String getData(String fileToUpdate,int row,int col)
	{
		try
		{
			File inputFile = new File(fileToUpdate);

			CSVReader reader = new CSVReader(new FileReader(inputFile), ',');
			List<String[]> csvBody = reader.readAll();
			String data=csvBody.get(row)[col];
			reader.close();
			return data.toLowerCase().trim();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "No Match";
		}
	}

	@SuppressWarnings("resource")
	public static void addColumnInFile(String fileToUpdate) throws IOException
	{
		ArrayList<String> updateColumn=null;
		try 
		{
			Reader reader = Files.newBufferedReader(Paths.get(fileToUpdate));
			CSVReader csvReader = new CSVReader(reader);
			updateColumn = new ArrayList<String>();

			String[] nextRecord;
			boolean execute=true;
			while ((nextRecord = csvReader.readNext()) != null) 
			{
				updateColumn.add(nextRecord[0]);
				updateColumn.add(",");
				if(execute)
				{
					updateColumn.add("LikedIn");
					updateColumn.add(",");
					updateColumn.add("Facebook");
					updateColumn.add(",");
					updateColumn.add("FourSquare");
					updateColumn.add(",");
					updateColumn.add("pinterest");
					updateColumn.add(",");
					updateColumn.add("Google Plus");
					updateColumn.add(",");
					execute=false;
				}
				else
				{
					updateColumn.add("-");
					updateColumn.add(",");
					updateColumn.add("-");
					updateColumn.add(",");
					updateColumn.add("-");
					updateColumn.add(",");
					updateColumn.add("-");
					updateColumn.add(",");
					updateColumn.add("-");
					updateColumn.add(",");
				}
				updateColumn.add("\n");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		FileWriter fileWriter= new FileWriter(fileToUpdate);
		for(String column : updateColumn)
		{
			fileWriter.append(column);
		}
		fileWriter.flush();
		fileWriter.close();
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
		for(int i=0;i<emailAddress.length;i++)
		{
			emailAddress[i]=emailAddress[i].trim();
			if(!(isElementDisplay("//div[@class='email']/div/span[contains(text(),'"+emailAddress[(i)]+"')]/../i[@style='color: red;']")))
			{
				dummy.add(getText("//div[@class='email']/div/span[contains(text(),'"+emailAddress[(i)]+"')]"));

				linkedln=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'linkedin')]");
				facebook=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'facebook')]");
				FourSquare=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'foursquare')]");
				pinterest=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'pinterest')]");;
				googlePlus=getSocialUrl("//div[@class='email']/div/span[contains(text(),'"+emailAddress[i]+"')]/../../div[@class='qualify']/span/a[contains(@href,'plus.google')]");

				dummy.add(linkedln);
				dummy.add(facebook);
				dummy.add(FourSquare);
				dummy.add(pinterest);
				dummy.add(googlePlus);
			}
			else
			{
				dummy.add("Account Not Working");
				dummy.add("Account Not Working");
				dummy.add("Account Not Working");
				dummy.add("Account Not Working");
				dummy.add("Account Not Working");
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
				email = email+nextRecord[0].toString();
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
			return driver.findElement(By.xpath(xpath)).getAttribute("href");
		}
		catch(Exception e)
		{
			return "Social profile not found";
		}
	}

	private static void updateCsvFile(String fileToUpdate, ArrayList<String> list) throws IOException
	{
		int index=0;
		if(list.size()>0)
		{
			int available_record = getRowCount();
			for(int i=0;i<available_record;i++)
			{
				//System.out.println(list.get(0)+"\t"+getData(fileToUpdate,i,1));
				if(list.get(index).equals(getData(fileToUpdate,i,0)))
				{
					//updateCSV(fileToUpdate, list.get(0), i, 1);
					updateCSV(fileToUpdate, list.get((index+1)), i, 1);
					updateCSV(fileToUpdate, list.get((index+2)), i, 2);
					updateCSV(fileToUpdate, list.get((index+3)), i, 3);
					updateCSV(fileToUpdate, list.get((index+4)), i, 4);
					updateCSV(fileToUpdate, list.get((index+5)), i, 5);
					index=index+6;
					if(index==30)
					{
						index=24;
					}
				}
			}
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
