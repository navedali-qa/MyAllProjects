package com.Mail_Ru_WhiteListing.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

public class GoogleSheetAPI 
{
	static String spreadsheetId = "1PGcj_SfLN1G_XQT7jklAvaCBVZba-tMuG0Qk-z4eG94";
	private static final String APPLICATION_NAME = "DemoAutomationSheet";

	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.dir")+"/Client_Credentials_Data/", ".credentials/sheets.googleapis.com-java-quickstart");

	private static FileDataStoreFactory DATA_STORE_FACTORY;

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static HttpTransport HTTP_TRANSPORT;

	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

	static 
	{
		try
		{
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		}
		catch (Throwable t) 
		{
			t.printStackTrace();
		}
	}

	public static Credential authorize() throws IOException 
	{
		FileInputStream in=new FileInputStream(System.getProperty("user.dir")+File.separator+"Client_Credentials_Data"+File.separator+"client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline")
				.build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		//System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	public static Sheets getSheetsService() throws IOException
	{
		Credential credential = authorize();
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}

	public List<List<Object>> getSpreadSheetRecords(String spreadsheetId, String range) throws IOException 
	{
		Sheets service = getSheetsService();		
		ValueRange response = service.spreadsheets().values()
				.get(spreadsheetId, range)
				.execute();
		List<List<Object>> values = response.getValues();
		if (values != null && values.size() != 0) 
		{
			return values;
		} else 
		{
			System.out.println("No data found.");
			return null;
		}
	}

	public static int getRows(String range) throws IOException
	{
		ValueRange response = getSheetsService().spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();
		return values.size();
	}

	public static void writeData(List<String> data,int index)
	{
		try
		{
			String writeRange = "Accounts!D"+index;

			List<List<Object>> writeData = new ArrayList<>();
			List<Object> dataRow = new ArrayList<>();
			dataRow.addAll(data);
			writeData.add(dataRow);

			ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
			getSheetsService().spreadsheets().values()
			.update(spreadsheetId, writeRange, vr)
			.setValueInputOption("RAW")
			.execute();
			System.out.println("Record updated...");
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void UpdateProxyStatus(List<String> data,int index)
	{
		try
		{
			String writeRange = "Proxies!B"+index;

			List<List<Object>> writeData = new ArrayList<>();
			List<Object> dataRow = new ArrayList<>();
			dataRow.addAll(data);
			writeData.add(dataRow);
			ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
			getSheetsService().spreadsheets().values()
			.update(spreadsheetId, writeRange, vr)
			.setValueInputOption("RAW")
			.execute();
		} catch (Exception e) 
		{

		}
	}
	public static void updatePassword(List<String> data,int index)
	{
		try
		{
			String writeRange = "Gmail!B"+(index+1);

			List<List<Object>> writeData = new ArrayList<>();
			List<Object> dataRow = new ArrayList<>();
			dataRow.addAll(data);
			writeData.add(dataRow);

			ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
			getSheetsService().spreadsheets().values()
			.update(spreadsheetId, writeRange, vr)
			.setValueInputOption("RAW")
			.execute();
			System.out.println("Password Changedand updated...");
		} catch (Exception e) 
		{

		}
	}

	public static void passwordChanged(List<String> data,int index)
	{
		try
		{
			String writeRange = "Gmail!E"+(index+1);

			List<List<Object>> writeData = new ArrayList<>();
			List<Object> dataRow = new ArrayList<>();
			dataRow.addAll(data);
			writeData.add(dataRow);

			ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
			getSheetsService().spreadsheets().values()
			.update(spreadsheetId, writeRange, vr)
			.setValueInputOption("RAW")
			.execute();
			System.out.println("Password Changedand updated...");
		} catch (Exception e) 
		{

		}
	}

	public static void writeData(List<String> data,String column,int index)
	{
		try
		{
			String writeRange = "Log!"+column+index;

			List<List<Object>> writeData = new ArrayList<>();
			List<Object> dataRow = new ArrayList<>();
			dataRow.addAll(data);
			writeData.add(dataRow);
			ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
			getSheetsService().spreadsheets().values()
			.update(spreadsheetId, writeRange, vr)
			.setValueInputOption("RAW")
			.execute();
		} catch (Exception e) 
		{

		}
	}

	public static int nextAvailableRow() throws IOException
	{
		return getRows("Logs!A:A");
	}
	
	public static void updateBunch(String sheetName,int index,String lastCol,List<List<Object>> getData) throws IOException, InterruptedException
	{
		int max_count=0;
		boolean execute = true;
		while(execute)
		{
			if(max_count==10)
			{
				System.out.println("Error not resolved...");
				break;
			}
			max_count++;
			try
			{
				String range = sheetName+"!A"+index+":"+lastCol;
				List<List<Object>> values = getData;
				List<ValueRange> data = new ArrayList<ValueRange>();
				data.add(new ValueRange()
						.setRange(range)
						.setValues(values));

				BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
						.setValueInputOption("Raw")
						.setData(data);
				getSheetsService().spreadsheets().values().batchUpdate(spreadsheetId, body).execute();

				//BatchUpdateValuesResponse result =	getSheetsService().spreadsheets().values().batchUpdate(spreadsheetId, body).execute();
				//System.out.printf("%d cells updated.", result.getTotalUpdatedCells());
				execute=false;
			} 
			catch(Exception e)
			{
				Thread.sleep(5000);
				System.out.println(e.getMessage()+"\t"+e.getLocalizedMessage());
			}
		}
	}

}