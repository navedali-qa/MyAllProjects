package com.AllProjectInOne.scripts;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;

public class CaptchaResolver
{
	public static String solveCaptcha() throws Exception
	{
		String username = "";
		String password = "";

		Client client = (Client)(new HttpClient(username, password));
		client.isVerbose = true;
		String captchaFile = saveImage("CAPTCHA_IMAGE_XPATH-,-src");
		try
		{
			try 
			{
				System.out.println("Your balance is " + client.getBalance() + " US cents");
			}
			catch (IOException e) 
			{
				return"Failed fetching balance: " + e.toString();
			}

			Captcha captcha = null;
			try 
			{
				captcha = client.decode(new File(captchaFile));
			}
			catch (IOException e) 
			{
				return "Failed uploading CAPTCHA";
			}
			if (null != captcha) 
			{
				return captcha.text;
			}
			else 
			{
				return "Failed solving CAPTCHA";
			}
		} 
		catch (com.DeathByCaptcha.Exception e) 
		{
			return e.toString();
		}
	}

	public static String saveImage(String logoSRC) throws IOException
	{
		URL imageURL = new URL(logoSRC);
		BufferedImage saveImage = ImageIO.read(imageURL);
		if(!new File(System.getProperty("user.dir")+"/captchas").exists())
		{
			new File(System.getProperty("user.dir")+"/captchas").mkdirs();
		}
		String fileName = System.getProperty("user.dir")+"/captchas/"+File.separator+"SecretImage"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())+".png";

		ImageIO.write(saveImage, "png", new File(fileName));

		return fileName;
	}


}
