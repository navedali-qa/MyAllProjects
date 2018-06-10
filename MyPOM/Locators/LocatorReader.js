var LocatorReader = function()
{
	var locatorFile;
	this.setLocatorFile = function(file)
	{
		locatorFile = require('../Locators/'+ file +'');
	};
	
	this.searchLocator = function(locator)
	{
		var result;
		locatorFile.forEach(function(data)
		{
			if(data.node==locator)
			{
				result = data.value;
			}
		});
		return result;
	};
};
module.exports = new LocatorReader();