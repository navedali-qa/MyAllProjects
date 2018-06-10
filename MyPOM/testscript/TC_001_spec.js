describe("Medo City POC",function()
{
	var locatorReader;
	var driverTestCase;
	var driverHelper;
	var tc_001_Helper;
	
	var TC_001 = constructor()
	{
		locatorReader = require('../Locators/LocatorReader.js');
		driverTestCase = require('../util/DriverTestCase.js');
		tc_001_Helper = require('../Helper/TC_001_Helper.js');
		locatorReader.setLocatorFile('TC_001_Locator.json');
		driverTestCase.setUp();
	};
	
	beforeAll(function () 
	{
		console.log('beforeAll');
	});
	
	beforeEach(function () 
	{
		console.log('beforeEach');
		browser.waitForAngular();
		console.log('WaitforAng');
	});

	it('Should be able to open the Senior living page',function()
	{
			var locator = locatorReader.searchLocator("Solutions");
			tc_001_Helper.mouseHoverOnItem(locator);

			console.log("Should be able to open the Senior living page");
			//console.log('\nTest spec: ' + __filename + '\n');
			
			locator = locatorReader.searchLocator("SeniorLiving");
			tc_001_Helper.clickOnItem(locator);
			
			tc_001_Helper.verifyUserRedirectedOnOtherPage("headingSeniorliving");
			expect(true).toBe(true);
	});
	
	afterEach(function () 
	{
		console.log('afterEach');
	});
});