package com.ShellScript.script;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class HashConverter
{

	static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	static ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
	static Invocable invocable =null;
	
	public static void main(String[] args) throws Exception
	{
		String filePath = System.getProperty("user.dir")+"/Md5File.csv";
		int rows = +totalRow(filePath);
		
		try
		{
			Reader recordsReader = Files.newBufferedReader(Paths.get(filePath));
			CSVReader records = new CSVReaderBuilder(recordsReader).build();
			String[] record;
			int i=0;
			while ((record = records.readNext()) != null)
			{
				System.out.println("Processing Row : "+i+"/"+rows);
				createFile("sha256LowerCase.csv", sha256(record[0])+",");
				i++;
			}

		}
		catch(Exception e) {}
	}

	public static String sha256(String text) throws Exception
	{
		String sha256="";
		try
		{
			scriptEngine.eval(Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/JsFile/sha256.js"), StandardCharsets.UTF_8));
			invocable = (Invocable) scriptEngine;
			sha256 = invocable.invokeFunction("hex_sha256", text).toString();
		}
		catch(Exception e)
		{}

		return sha256;
	}

	public static String md5(String email) throws Exception
	{
		String md5="";
		try
		{
			scriptEngine.eval(Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/JsFile/md5.js"), StandardCharsets.UTF_8));
			invocable = (Invocable) scriptEngine;
			md5 = invocable.invokeFunction("hex_md5", email).toString();
		}
		catch(Exception e)
		{}
		return md5;
	}

	public static String sha1(String email) throws Exception
	{
		String sha1="";
		try
		{
			scriptEngine.eval(Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/JsFile/sha1.js"), StandardCharsets.UTF_8));
			invocable = (Invocable) scriptEngine;
			sha1 = invocable.invokeFunction("hex_sha1", email).toString();
		}
		catch(Exception e)
		{}
		return sha1;
	}

	public static void createFile(String fileName, String text) throws IOException
	{
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter out = null;
		try 
		{
			fw = new FileWriter(fileName, true);
			bw = new BufferedWriter(fw);
			out = new PrintWriter(bw);
			out.println(text);
			out.close();
		}
		catch (IOException e) 
		{
			//exception handling left as an exercise for the reader
		}
		finally 
		{
			if(out != null)
				out.close();
			try 
			{
				if(bw != null)
					bw.close();
			}
			catch (IOException e) 
			{
				//exception handling left as an exercise for the reader
			}
			try 
			{
				if(fw != null)
					fw.close();
			}
			catch (IOException e) 
			{
				//exception handling left as an exercise for the reader
			}
		}
	}

	public static int totalRow(String path)
	{
		int i=0;
		try
		{
			Reader recordsReader = Files.newBufferedReader(Paths.get(path));
			CSVReader records = new CSVReaderBuilder(recordsReader).build();
			@SuppressWarnings("unused")
			String[] record;
			while ((record = records.readNext()) != null)
			{
				i++;
			}

		}
		catch(Exception e) {}
		return i;
	}
}
