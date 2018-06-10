package com.ShellScript.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.FileUtils;

public class ShellScript 
{
	public static void main(String[] args) throws Exception
	{
		//File dir = new File("C:\\Users\\Administrator\\OneDrive");
		//performAction("C:\\Users\\Administrator\\Automation_Work\\Automation_Projects\\Automation_Projects\\DemoShell");
		performAction("C:\\Users\\Administrator\\OneDrive");
	}

	public static void performAction(String directory)
	{
		try
		{
			File dir = new File(directory);
			String[] extensions = new String[] { "txt", "csv" };
			List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);
			for (File file : files)
			{
				//System.out.println("Processing File : " + file.getCanonicalPath());
				getFileText(file.getCanonicalPath());
			}
		}
		catch(Exception e) {}
	}

	public static void getFileText(String file) throws Exception
	{
		try
		{
			System.out.println(file);
			String everything="";
			BufferedReader br = new BufferedReader(new FileReader(file));
			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null)
				{
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}
				everything = sb.toString();
			} 
			finally 
			{
				br.close();
			}
			extractRegex(everything);
		}
		catch(Exception e) {}
	}

	public static void extractRegex(String txt) throws Exception
	{
		try
		{
			Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+").matcher(txt);
			while (m.find())
			{
				invokeJsFunction(m.group());
			}
		}
		catch(Exception e) {}
	}

	public static void invokeJsFunction(String email) throws Exception
	{
		try
		{
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine enginemd5 = manager.getEngineByName("JavaScript");
			ScriptEngine enginesha1 = manager.getEngineByName("JavaScript");
			ScriptEngine enginesha256 = manager.getEngineByName("JavaScript");
			// read script file
			enginemd5.eval(Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/JsFile/md5.js"), StandardCharsets.UTF_8));
			enginesha1.eval(Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/JsFile/sha1.js"), StandardCharsets.UTF_8));
			enginesha256.eval(Files.newBufferedReader(Paths.get(System.getProperty("user.dir")+"/JsFile/sha256.js"), StandardCharsets.UTF_8));

			Invocable invmd5 = (Invocable) enginemd5;
			Invocable invsha1 = (Invocable) enginesha1;
			Invocable invsha256 = (Invocable) enginesha256;

			// call function from script file
			System.out.println("\n");
			updateTextFile(email,email+
					","+invmd5.invokeFunction("hex_md5", email)+
					","+invsha1.invokeFunction("hex_sha1", email)+
					","+invsha256.invokeFunction("hex_sha256", email));
		}
		catch(Exception e)
		{}
	}

	public static void updateTextFile(String email,String text) throws Exception
	{
		try
		{
			System.out.println("Upgating HASH for email : "+email);
			String filename= "Output.txt";
			FileWriter fw = new FileWriter(filename,true); //the true will append the new data
			fw.write(text+"\n");//appends the string to the file
			fw.close();
		}
		catch(IOException ioe)
		{
			System.err.println("IOException: " + ioe.getMessage());
		}
	}
}
