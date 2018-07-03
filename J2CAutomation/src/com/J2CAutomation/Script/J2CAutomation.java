package com.J2CAutomation.Script;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

import org.dom4j.Document;

public class J2CAutomation
{
	static String baseUrl = "https://mailingwhale.com/ATM-API/api/";
	public static Map<String, String> listBody;
	public static Map<String, String> campaignBody;
	public static Map<String, String> headers;

	public static Reader jobReader = null;
	public static Reader subscriberReader = null;
	public static CSVReader allSubscriber = null;
	public static CSVReader alljob = null;

	static String downloadedFolder = "";
	private static Document doc;
	static String id = "3568";
	static String password="jxZ0qazORSf8sFmF";

	public static void main(String as[]) throws Exception
	{
		//Generate XML
		downloadedFolder = System.getProperty("user.dir")+"\\Results\\";

		testFiles();

		executeTest(id,password);

		createCampaign();

	}

	//Methods for generating XML
	
	public static void testFiles()
	{
		if(!(new File(downloadedFolder).exists()))
		{
			new File(downloadedFolder).mkdir();
		}
		else
		{
			new File(downloadedFolder).delete();
			new File(downloadedFolder).mkdir();
		}
		if(!(new File(System.getProperty("user.dir")+"\\Files\\Cities.csv").exists()))
		{
			System.out.println("ERROR!!!!\nPlease create a Cities.csv file under Files folder");
			System.out.println("Cities.CSV file should have \"City\" column");

			if(!(new File(System.getProperty("user.dir")+"\\Files").exists()))
			{
				new File(System.getProperty("user.dir")+"\\Files").mkdir();
			}
			System.exit(0);
		}

		if(!(new File(System.getProperty("user.dir")+"\\Files\\Jobs.csv").exists()))
		{
			System.out.println("ERROR!!!!\nPlease create a jobs.csv file under Files folder");
			System.out.println("Jobs.csv file should have \"job\" column");
			System.exit(0);
		}
	}

	public static void executeTest(String id, String password)
	{
		try
		{
			String url="";
			String urlStartPart = "http://api.jobs2careers.com/api/search.php?id="+id+"&pass="+password+"&q=";
			String urlEndPart="&t3=124&ip=NOIPREQUIRED&useragent=&format=xml&start=0&limit=10";

			Reader cityReader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/Files/cities.csv"));
			CSVReader allCities = new CSVReaderBuilder(cityReader).withSkipLines(1).build();
			String[] nextCity;
			while ((nextCity = allCities.readNext()) != null)
			{
				Reader jobsReader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/Files/jobs.csv"));
				CSVReader alljobs = new CSVReaderBuilder(jobsReader).withSkipLines(1).build();
				String[] nextJob;
				while ((nextJob = alljobs.readNext()) != null)
				{
					String city = nextCity[0].toString();
					String jobType=nextJob[0].toString();
					if(city.contains(" "))
					{
						city=city.replace(" ", "");
					}
					if(jobType.contains(" "))
					{
						jobType=jobType.replace(" ", "");
					}

					if(!(new File(downloadedFolder+jobType).exists()))
					{
						new File(downloadedFolder+jobType).mkdir();
					}
					url=urlStartPart+jobType+"&l="+city+"&link=1&t1="+jobType+"&t2="+city+urlEndPart;
					System.out.println("----------------------------------------------------------\n");
					System.out.println("GENERATED URL : "+url);

					generateXml(new URL(url), new File(downloadedFolder+jobType+"\\"+city+"_"+jobType+".xml"));

					generateHTML(downloadedFolder+jobType+"\\"+city+"_"+jobType+".xml");
				}
			}
		}
		catch(Exception e){	}
	}

	public static void generateHTML(String fileName)
	{
		try
		{
			assignFile(fileName);
			int count = getJobCount(fileName);
			String resultantCode="";
			String title = "",url="",description="";
			for(int i=1;i<=count;i++)
			{
				title =getNodeValue("result.job["+i+"].title");
				url = getNodeValue("result.job["+i+"].url");
				description =getNodeValue("result.job["+i+"].description");
				resultantCode=resultantCode+"<a href=\""+url+"\">"+title+"</a><br>"+description+"<br><br>\n";
			}

			fileName = fileName.replace(".xml", ".html");
			File file = new File(fileName);
			FileWriter fw =null;
			BufferedWriter bw=null;

			file.createNewFile();
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(resultantCode);
			bw.flush();
			bw.close();
			System.out.println("Generated HTML : "+fileName);
			fileName = fileName.replace(".html", ".xml");
			new File(fileName).delete();

		}
		catch(Exception e){}

	}

	private static void generateXml(URL url, File file)
	{
		try
		{
			FileUtils.copyURLToFile(url, file);
			System.out.println("GENERATED XML : "+file.getAbsolutePath());
		} 
		catch (IOException e){}
	}

	public static void assignFile(String fileName)
	{
		SAXReader reader = new SAXReader();
		try 
		{
			doc = reader.read(fileName);
		} 
		catch (DocumentException e) 
		{
		}
	}

	public static String getNodeValue(String node)
	{
		try
		{
			return doc.selectSingleNode("//" + node.replace('.', '/')).getText();
		}
		catch(Exception e)
		{
			return "";	
		}
	}

	public static int getJobCount(String fileName)
	{
		try
		{
			return doc.getRootElement().selectNodes("job").size();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			return 0;
		}
	}


	//Methods for rest API
	public static void createCampaign() throws Exception
	{

		Reader cityReader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/Files/Cities.csv"));		
		CSVReader allcity = new CSVReaderBuilder(cityReader).withSkipLines(1).build();
		String[] city;
		String list_uid="";

		while ((city = allcity.readNext()) != null)
		{
			subscriberReader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/Files/DomListsSample.csv"));
			allSubscriber = new CSVReaderBuilder(subscriberReader).withSkipLines(1).build();

			list_uid=createList(getCreateListBody());
			String[] subscriber=null;
			System.out.println("List Unique ID : "+list_uid);

			try
			{
				while ((subscriber = allSubscriber.readNext()) != null)
				{
					if((subscriber[3].trim().toLowerCase()).contains((city[0].trim().toLowerCase())))
					{
						String subcriber_uid = addSubscriber(list_uid,subscriber[0],subscriber[1]);
						System.out.println("Added subsriber for "+list_uid+" : "+subcriber_uid);
					}
				}
			}catch(MalformedInputException en) {}
			jobReader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/Files/Jobs.csv"));
			alljob = new CSVReaderBuilder(jobReader).withSkipLines(1).build();
			String[] job;

			Runtime.getRuntime().exec("cmd /c start "+ System.getProperty("user.dir")+"/Jobs2careersXML2HTML.jar");
			Thread.sleep(5000);
			try
			{
				while ((job = alljob.readNext()) != null)
				{
					String campaign_uid = createCampaign(list_uid,job[0],city[0]);
					System.out.println("Created campaign for :"+list_uid+ " : "+campaign_uid);
				}
			}catch(MalformedInputException en) {}
		}

	}

	@SuppressWarnings("deprecation")
	public static String createList(Map<String, String> createListBody)throws Exception
	{
		String list_uid="";
		try
		{
			Response res = given().
					parameters(createListBody).
					when().
					headers(addHeaders()).
					post(baseUrl+"lists_create.php");
			list_uid=res.asString();
			String asd[] = list_uid.split("=>");
			list_uid=asd[3].toString().trim().substring(0,list_uid.indexOf(" ")).replace(")", "").trim();
		}
		catch(Exception e)
		{}
		return list_uid;
	}

	@SuppressWarnings("deprecation")
	public static String addSubscriber(String uniqueId, String email, String firstName)
	{
		String subscriber_uid="";
		try
		{
			Map<String, String> subcriberMap = new HashMap<String,String>();
			subcriberMap.put("unique_id", uniqueId);
			subcriberMap.put("EMAIL", email);
			subcriberMap.put("FNAME", firstName);
			subcriberMap.put("LNAME", "Last Name");

			Response res = given().
					parameters(subcriberMap).
					when().
					headers(addHeaders()).
					post(baseUrl+"createSubscriber.php");
			subscriber_uid=res.asString();
			String asd[] = subscriber_uid.split("\"");
			subscriber_uid=asd[11].toString().trim();
		}
		catch(Exception e)
		{

		}
		return subscriber_uid;
	}

	@SuppressWarnings("deprecation")
	private static String createCampaign(String list_uid, String jobName, String cityName) throws Exception
	{
		String compaign_uid="";
		try
		{
			Response res = given().
					parameters(getCreateCampaignBody(list_uid, jobName, cityName)).
					when().
					headers(addHeaders()).
					post(baseUrl+"createCampaign.php");
			compaign_uid=res.asString();
			String asd[] = compaign_uid.split("=> ");
			compaign_uid=asd[3].toString().trim().substring(0,asd[3].indexOf(" ")).trim();

		}catch(Exception e) {}
		return compaign_uid;
	}

	public static String getListParameter(int index) throws Exception
	{
		Reader createListParameterReader = Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/Files/CreateListParameter.csv"));
		CSVReader allcreateListParameter = new CSVReaderBuilder(createListParameterReader).withSkipLines(1).build();
		String[] createListParameter;

		String parameter="";
		while ((createListParameter = allcreateListParameter.readNext()) != null)
		{
			parameter = createListParameter[index];
		}
		return parameter;
	}

	public static Map<String, String> addHeaders()
	{
		headers =new HashMap<String, String>();

		headers.put("public-key", "559cbb15b7af342f40b5f4ce995608aebb8b2d89");
		headers.put("private-key", "f429575aa17fc445b99e055beab7798e391d8207");
		headers.put("Connection","keep-alive");
		headers.put("Content-Type","application/x-www-form-urlencoded");
		headers.put("Host","mailingwhale.com");

		return headers;
	}

	public static Map<String, String> getCreateListBody() throws Exception
	{
		listBody = new HashMap<String,String>();

		listBody.put("general[name]",getListParameter(0));
		listBody.put("general[description]",getListParameter(1));
		listBody.put("defaults[from_name]",getListParameter(2));
		listBody.put("defaults[from_email]",getListParameter(3));
		listBody.put("defaults[reply_to]",getListParameter(4));
		listBody.put("defaults[subject]",getListParameter(5));
		listBody.put("notifications[subscribe]",getListParameter(6));
		listBody.put("notifications[unsubscribe]",getListParameter(7));
		listBody.put("notifications[subscribe_to]",getListParameter(8));
		listBody.put("notifications[unsubscribe_to]",getListParameter(9));
		listBody.put("company[name]",getListParameter(10));
		listBody.put("company[country]",getListParameter(11));
		listBody.put("company[zone]",getListParameter(12));
		listBody.put("company[address_1]",getListParameter(13));
		listBody.put("company[address_2]",getListParameter(14));
		listBody.put("company[zone_name]",getListParameter(15));
		listBody.put("company[city]",getListParameter(16));
		listBody.put("company[zip_code]",getListParameter(17));
		listBody.put("general[opt_in]",getListParameter(18));

		return listBody;
	}

	public static Map<String, String> getCreateCampaignBody(String list_uid, String jobName, String cityName) throws Exception
	{
		campaignBody = new HashMap<String,String>();

		campaignBody.put("name",jobName+"_"+cityName);
		campaignBody.put("type","regular");
		campaignBody.put("from_name","Job Rescue");
		campaignBody.put("from_email","kim@newlocaltopjobs.com");
		campaignBody.put("subject","Pending Application");
		campaignBody.put("reply_to","kim@newlocaltopjobs.com ");
		campaignBody.put("send_at","2018-05-17 22:00:00");
		campaignBody.put("list_uid",list_uid);
		campaignBody.put("options[url_tracking]","yes");
		campaignBody.put("options[json_feed]","no");
		campaignBody.put("options[xml_feed]","no");
		campaignBody.put("options[plain_text_email]","yes");
		campaignBody.put("options[email_stats]","null");
		campaignBody.put("template[content]",getFileContext(jobName,cityName));
		campaignBody.put("template[inline_css]","no");
		campaignBody.put("template[plain_text]","null");
		campaignBody.put("template[auto_plain_text]","yes");
		campaignBody.put("to_name","Kim");


		campaignBody.put("delivery_servers[]","2");

		return campaignBody;
	}

	private static String getFileContext(String jobName, String cityName) throws IOException
	{
		String outString = new String(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+"/Results/"+cityName+"/"+cityName+"_"+jobName+".html")));

		return outString;
	}
}
