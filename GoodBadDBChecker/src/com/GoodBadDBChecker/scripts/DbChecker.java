package com.GoodBadDBChecker.scripts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

public class DbChecker 
{
	static String goodDBFileName = "goodDBFile";
	static String badDBFileName = "badDBFile";

	static String username="username";
	static String password ="password";

	static ArrayList<String> badDataFinal = null;
	static ArrayList<String> goodDataFinal = null;
	static String masterCSVFile="master.csv";

	public static void main(String as[]) throws Exception
	{
		try
		{
			masterCSVFile=as[0];
			goodDBFileName = as[1];
			badDBFileName = as[2];
			username=as[3];
			password=as[4];
		}
		catch(Exception e){System.out.println("Using default values : "+masterCSVFile+"\t"+goodDBFileName+"\t"+badDBFileName+"\t"+username+"\t"+password);}
		
		badDataFinal = new ArrayList<String>();
		goodDataFinal = new ArrayList<String>();

		Reader emailReader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/"+masterCSVFile));
		CSVReader allEmails = new CSVReaderBuilder(emailReader).withSkipLines(1).build();
		String[] email;
		int columnCount=0;
		boolean gotCount=true;
		boolean headerAdded = true;

		if(headerAdded)
		{
			addHeader();
			/*goodDataFinal.add("ID");
			goodDataFinal.add(",");
			goodDataFinal.add("NAME");
			goodDataFinal.add(",");
			goodDataFinal.add("EMAIL");
			goodDataFinal.add(",");
			goodDataFinal.add("PHONE");
			goodDataFinal.add(",");
			goodDataFinal.add("FAN COUNT");
			goodDataFinal.add(",");
			goodDataFinal.add("WEBSITE");
			goodDataFinal.add(",");
			goodDataFinal.add("LOCATION");
			goodDataFinal.add(",");
			goodDataFinal.add("GENDER");
			goodDataFinal.add(",");
			goodDataFinal.add("USERNAME");
			goodDataFinal.add(",");
			goodDataFinal.add("MOBILE PHONE");
			goodDataFinal.add(",");
			goodDataFinal.add("BIRTHDAY");
			goodDataFinal.add(",");
			goodDataFinal.add("antiSpamCheck");
			goodDataFinal.add(",");
			goodDataFinal.add("Gmail");
			goodDataFinal.add(",");
			goodDataFinal.add("City");
			goodDataFinal.add(",");
			goodDataFinal.add("Language"); 
			goodDataFinal.add(",");
			goodDataFinal.add("Twiter");
			goodDataFinal.add(",");
			*/
			goodDataFinal.add("\n");

			badDataFinal.add("EMAIL");
			badDataFinal.add(",");
			badDataFinal.add("REASON");
			badDataFinal.add(",");
			badDataFinal.add("\n");
			headerAdded=false;
		}
		int count =0;
		while ((email = allEmails.readNext()) != null)
		{
			ArrayList<String> badData = new ArrayList<String>();
			ArrayList<String> goodData = new ArrayList<String>();

			String antiSpamCheckResultVal = "";
			String verifyCityResultVal = "";
			String verifyEnglishResultVal = "";
			String verifyTwiterResultVal = "";
			String verifyGmailResultVal = "";

			if(gotCount)
			{
				for(;;)
				{
					try
					{
						@SuppressWarnings("unused")
						String val = email[columnCount];
						columnCount++;
					}
					catch(Exception e)
					{break;}
				}
				gotCount=false;
			}

			try
			{
				count++;
				System.out.println(count +" - Working on : "+email[0]);
				String antiSpamCheckResult = antiSpamCheck(email[0]);
				if(antiSpamCheckResult.toLowerCase().contains("green"))
				{
					antiSpamCheckResultVal="green";

					String verifyGmailResult = verifyGmail(email[0]);

					if(verifyGmailResult.toLowerCase().contains("email exists"))
					{
						verifyGmailResultVal="Email Exists";

						String verifyCityResult = verifyCity(email[0]);
						String[] val = verifyCityResult.split(",");
						verifyCityResultVal=val[2];
						verifyEnglishResultVal = val[4].trim();
						String verifyTwiterResult = verifyTwiter(email[0]);
						if(verifyTwiterResult.toLowerCase().contains("twityes"))
						{
							verifyTwiterResultVal = "twitterYes";
						}
						else
						{
							verifyTwiterResultVal = "twitterNo";
						}
						goodData = addDataToList(columnCount,email,goodData);
						goodData.add(antiSpamCheckResultVal);
						goodData.add(",");
						goodData.add(verifyGmailResultVal);
						goodData.add(",");
						goodData.add(verifyCityResultVal);
						goodData.add(",");
						goodData.add(verifyEnglishResultVal);
						goodData.add(",");
						goodData.add(verifyTwiterResultVal);
						goodData.add("\n");
					}
					else
					{
						badData.add(email[0]);
						badData.add(",");
						badData.add("Email not exist");
						badData.add(",");
						badData.add("\n");
					}
				}
				else
					if(!antiSpamCheckResult.toLowerCase().contains(email[0].toLowerCase()))
					{
						badData.add(email[0]);
						badData.add(",");
						badData.add("Unknown");
						badData.add(",");
						badData.add("\n");
					}
				else
				{
					String[] val = antiSpamCheckResult.split(",");
					badData.add(email[0]);
					badData.add(",");
					badData.add(val[1]);
					badData.add("\n");
				}
			}
			catch(Exception e) {}
			badDataFinal = addToList(badDataFinal, badData);
			goodDataFinal = addToList(goodDataFinal, goodData);

			generateCsvFile(email,goodDataFinal,goodDBFileName);
			generateCsvFile(email,badDataFinal,badDBFileName);
		}
	}

	public static void addHeader() throws Exception
	{
		Reader emailReader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/"+masterCSVFile));
		CSVReader allEmails = new CSVReaderBuilder(emailReader).withSkipLines(0).build();
		String[] email;
		int columnCount=0;
		while ((email = allEmails.readNext()) != null)
		{
			for(;;)
			{
				try
				{
					@SuppressWarnings("unused")
					String val = email[columnCount];
					columnCount++;
				}
				catch(Exception e)
				{break;}
			}

			for(int i=0;i<columnCount;i++)
			{
				goodDataFinal.add(email[i]);
				goodDataFinal.add(",");
			}
			break;
		}
	}
	
	private static ArrayList<String> addToList(ArrayList<String> dataValue, ArrayList<String> data) 
	{
		for(int i=0;i<data.size();i++)
		{
			dataValue.add(data.get(i));
		}
		return dataValue;
	}

	private static ArrayList<String> addDataToList(int columnCount, String[] email,ArrayList<String> goodData)
	{
		for(int i=0;i<columnCount;i++)
		{
			goodData.add(email[i]);
			goodData.add(",");
		}
		return goodData;
	}

	private static void generateCsvFile(String[] columnValue,ArrayList<String> list, String fileName) throws Exception
	{
		FileWriter writer=null;
		writer = new FileWriter(System.getProperty("user.dir")+"/"+fileName+".csv");

		if(new File(System.getProperty("user.dir")+"/"+fileName+".csv").exists())
		{
			new File(System.getProperty("user.dir")+"/"+fileName+".csv").delete();
			new File(System.getProperty("user.dir")+"/"+fileName+".csv").createNewFile();
		}
		else
		{
			new File(System.getProperty("user.dir")+"/"+fileName+".csv").createNewFile();
		}

		if(list.size()>0)
		{
			try
			{
				for(String val : list)
				{ 
					writer.append(val);
				}
			}
			catch (IOException e) {}
			finally 
			{

			}
		}
		else
		{
			//System.out.println("No Data to generate file...");
		}

		try 
		{
			writer.flush();
			writer.close();
		} catch (IOException e) {}
	}

	public static String antiSpamCheck(String email)
	{
		String antiSpamCheckUrl="http://cryptonote.nl/api/antispamapi.php?email="+email+"&user="+username+"&pass="+password;
		Response res = get(antiSpamCheckUrl);
		return res.asString();
	}

	public static String verifyGmail(String email)
	{
		if(email.toLowerCase().contains("gmail.com") || email.toLowerCase().contains("qq.com") || email.toLowerCase().contains("yandex.ru"))
		{
			String verifyGmailUrl = "http://cryptonote.nl/api/smtpverifyapi.php?email="+email+"&user="+username+"&pass="+password;
			Response res = get(verifyGmailUrl);
			return res.asString();
		}
		else
		{
			return "Email Exists";
		}
	}

	public static String verifyCity(String email) 
	{
		String countryUrl="http://cryptonote.nl/api/countryapi.php?email="+email+"&user="+username+"&pass="+password;
		Response res = get(countryUrl);
		return res.asString();
	}

	public static String verifyTwiter(String email) 
	{
		String twitUrl="http://cryptonote.nl/api/twitapi.php?email="+email+"&user="+username+"&pass="+password;
		Response res = get(twitUrl);
		return res.asString();
	}
}
