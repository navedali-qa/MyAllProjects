import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import io.github.bonigarcia.wdm.WebDriverManager;

public class QRCodeReader
{
	WebDriver driver=null;
	
	@Test
	public void qrCodeReader() throws Exception
	{
		System.out.println("\n\nProgram starts at : "+new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(Calendar.getInstance().getTime())+"\n\n");

		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();

		options.addArguments("--disable-infobars");

		driver = new ChromeDriver(options);

		driver.manage().window().maximize();

		driver.get("file://"+System.getProperty("user.dir")+"/DummyQRFile.html");

		URL url = new URL(driver.findElement(By.tagName("img")).getAttribute("src"));

		BufferedImage bufferedImage = ImageIO.read(url);

		LuminanceSource luminanceSource = new BufferedImageLuminanceSource(bufferedImage);

		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(luminanceSource));

		Result result = new MultiFormatReader().decode(binaryBitmap);

		System.out.println("CODE written in the QR CODE : "+result.getText());

		Assert.assertEquals("This is a demo QR code reader code from https://www.the-qrcode-generator.com/", result.getText());

		System.out.println("\n\nProgram ends at : "+new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(Calendar.getInstance().getTime())+"\n\n");
	}

	@AfterMethod
	public void quitDriver()
	{
		driver.quit();
	}

}
