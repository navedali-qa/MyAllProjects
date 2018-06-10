var DriverTestCase = function()
{
	this.setUp = function()
	{
		browser.ignoreSynchronization = true;
		browser.driver.manage().window().maximize();
		browser.get("http://www.medocity.com/");
		//browser.get("http://www.thetestroom.com/jswebapp/");
		//browser.get("http://www.gmail.com/");
		//browser.ignoreSynchronization USED FOR NON_AJAX APPLICATION
	};
};
module.exports = new DriverTestCase();