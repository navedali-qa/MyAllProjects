var TC_001_Helper = function()
{
	var driverHelper;
	
	var TC_001_Helper = constructor()
	{
		driverHelper = require('../util/DriverHelper.js');
	};
	
	this.clickOnItem = function(locator)
	{
		driverHelper.clickOn(locator);
	};
	
	this.mouseHoverOnItem = function(locator)
	{
		driverHelper.mouseOver(locator);
	};
	
	this.verifyUserRedirectedOnOtherPage = function(string1)
	{
		browser.getCurrentUrl().then(function(text)
		{
			driverHelper.verifyContains(text,string1);
		});
	};
};
module.exports = new TC_001_Helper();