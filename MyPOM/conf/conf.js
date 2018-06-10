// An example configuration file.
var Jasmine2HtmlReporter = require('../libs/node_modules/protractor-jasmine2-html-reporter');
var today = new Date(),
	timeStamp = today.getMonth() + 1 + '-' + today.getDate() + '-' + today.getFullYear() + '-' + today.getHours() + '-' + today.getMinutes();
exports.config = 
{
	directConnect: true,
	//seleniumAddress: 'http://localhost:4444/wd/hub',
	
	//Capabilities to be passed to the webdriver instance.
	capabilities: 
	{
		'browserName': 'chrome'
		//'browserName': 'firefox',
	},
	
	//multiCapabilities:[
	//	{
	//		'browserName': 'chrome'
	//	},
	//	{
	//		'browserName':'firefox'
	//	}
	//],

	// Framework to use. Jasmine is recommended.
	framework: 'jasmine2',

	// Spec patterns are relative to the current working directory when
	specs:['../testscript/*.js'],
	
	onPrepare:function()
	{
		jasmine.getEnv().addReporter(new Jasmine2HtmlReporter(
		{
			'savePath': '../report/',
			'screenshotsFolder': 'Images',
			'fileName': 'Medocity_Report_'+timeStamp,
			'takeScreenshots': true,
			'takeScreenshotsOnlyOnFailures': false,
			'cleanDestination': true,
		})
		);
	},

	// Options to be passed to Jasmine.
	jasmineNodeOpts: 
	{
		// onComplete will be called just before the driver quits.
		onComplete: null,
		// If true, display spec names.
		isVerbose: false,
		// If true, print colors to the terminal.
		showColors: true,
		// If true, include stack traces in failures.
		includeStackTrace: true,
		defaultTimeoutInterval: 80000
	}
};