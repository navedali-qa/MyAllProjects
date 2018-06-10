var DriverHelper = function()
{
	this.byLocator = function(locator)
	{
		var result = null;
		
		if (locator.startsWith("//"))
		{
            result = by.xpath(locator);
        }
		else if (locator.startsWith("css=")) 
		{
            result = by.cssSelector(locator.replace("css=", ""));
        }
		else if (locator.startsWith("#")) 
		{
            result = by.name(locator.replace("#", ""));
        }
		else if (locator.startsWith(".")) 
		{
            result = by.name(locator.replace(".", ""));
        }
		else if (locator.startsWith("link=")) 
		{
            result = by.linkText(locator.replace("link=", ""));
        }
        else if(locator.startsWith("xpath"))
		{
            by.xpath(locator);
        }
        else 
		{
            result = by.id(locator);
        }
        return result;
	};
	
	this.wait = function(ms)
	{
		browser.sleep(ms);
	};
	
	this.waitForElementEnabled = function(locator,timeout)
	{
		for(var i=0;i<timeout;i++)
		{
			if(element(this.byLocator(locator)).isEnabled())
			{
				break;
			}
			else
			{
				wait(1000);
			}
		}
	};
	
	this.waitForElementNotEnabled = function(locator,timeout)
	{
		for(var i=0;i<timeout;i++)
		{
			if(!element(this.byLocator(locator)).isEnabled())
			{
				break;
			}
			else
			{
				wait(1000);
			}
		}
	};
	
	this.waitForElementVisible = function(locator,timeout)
	{
		for(var i=0;i<timeout;i++)
		{
			if(element(this.byLocator(locator)).isDisplayed())
			{
				break;
			}
			else
			{
				wait(1000);
			}
		}
	};
	
	this.waitForElementNotVisible = function(locator,timeout)
	{
		for(var i=0;i<timeout;i++)
		{
			if(!element(this.byLocator(locator)).isDisplayed())
			{
				break;
			}
			else
			{
				wait(1000);
			}
		}
	};
	
	this.clickOn = function(locator)
	{
		this.waitForElementEnabled(locator,5);
		this.waitForElementVisible(locator,5);
		element(this.byLocator(locator)).click();
	};
	
	this.verifyContains = function(string1,string2)
	{
		expect(string1).toContain(string2);
	};
	
	this.clearTextField = function()
	{
		this.waitForElementEnabled(locator,5);
		this.waitForElementVisible(locator,5);
		element(this.byLocator(locator)).clear();
	};
	
	this.sendKeys = function(locator,stringText)
	{
		this.waitForElementEnabled(locator,5);
		this.waitForElementVisible(locator,5);
		element(this.byLocator(locator)).sendKeys(stringText);
	};
	
	this.uploadFile = function(locator,filePath,fileName)
	{
		//var absolutePath = path.resolve(_dirname,filetoUpload)
		this.waitForElementEnabled(locator,5);
		element(this.byLocator(locator)).sendKeys(filePath+"\\"+fileName);
	}
	
	this.acceptAlert = function()
	{
		browser.switchTo().alert().accept();
	}
	
	this.mouseOver = function(locator)
	{
		this.waitForElementEnabled(locator,5);
		this.waitForElementVisible(locator,5);
		var element1 = element(this.byLocator(locator));
		browser.actions().mouseMove(element1).perform();
	};
};
module.exports = new DriverHelper();