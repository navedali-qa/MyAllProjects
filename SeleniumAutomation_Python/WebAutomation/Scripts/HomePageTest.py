__author__ = "Naved Ali"

from WebAutomation.PageHelper.HomePageHelper import HomePageHelper
import unittest
from time import sleep
from WebAutomation.TestBase.EnvironmentSetup import EnvironmentSetup
from WebAutomation.Utilities.Screenshot import ScreenShot
class HomePageTest_Scripts(EnvironmentSetup):

    def test_verifyElementsOnThePage(self):
        driver = self.driver
        ss = ScreenShot(driver)
        self.driver.get("http://newtours.demoaut.com/")
        sleep(4)

        homePageHelper = HomePageHelper(driver)

        try:
            homePageHelper.getLogo().is_displayed()
        except Exception as e:
            print("Exception occurred "+e)
            ss.getScreenShot("demo.jpeg")


    if __name__ == '__main__':
        unittest.main()


