import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class FullPageScreenshot 
{

	WebDriver driver=null;

	@Test
	public void testScreenshot() throws IOException
	{

		System.out.println("\n\nProgram starts at : "+new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(Calendar.getInstance().getTime())+"\n\n");

		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();

		options.addArguments("--disable-infobars");

		driver=new ChromeDriver();

		driver.manage().window().maximize();

		driver.get("https://www.softwaretestingmaterial.com/how-to-capture-full-page-screenshot-using-selenium-webdriver/");

		//Normal Screenshot
		TakesScreenshot scrshot = ((TakesScreenshot)driver);
		File srcshot = scrshot.getScreenshotAs(OutputType.FILE);
		if(!new File(System.getProperty("user.dir")+"/Screenshot/").exists())
		{
			new File(System.getProperty("user.dir")+"/Screenshot/").mkdirs();
		}
		String file = System.getProperty("user.dir")+"/Screenshot/Normal_Screenshot_"+new SimpleDateFormat("HH_mm_ss_dd_MM_yyyy").format(Calendar.getInstance().getTime())+".png";
		FileUtils.copyFile(srcshot, new File(file));

		//FullPage screenshot
		Screenshot fpScreenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
		file = System.getProperty("user.dir")+"/Screenshot/FullPage_Screenshot_"+new SimpleDateFormat("HH_mm_ss_dd_MM_yyyy").format(Calendar.getInstance().getTime())+".png";
		ImageIO.write(fpScreenshot.getImage(),"PNG", new File(file));
	}

	@AfterMethod
	public void quitDriver()
	{
		driver.quit();
	}
}
