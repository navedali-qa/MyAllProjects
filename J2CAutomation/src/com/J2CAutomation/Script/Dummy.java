package com.J2CAutomation.Script;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class Dummy {

	public static void main(String[] args) throws Exception
	{
		Runtime.getRuntime().exec("cmd /c start "+ System.getProperty("user.dir")+"/Jobs2careersXML2HTML.jar");
		Thread.sleep(5000);
	FileUtils.deleteDirectory(new File(System.getProperty("user.dir")+"/Results"));

	}

}
