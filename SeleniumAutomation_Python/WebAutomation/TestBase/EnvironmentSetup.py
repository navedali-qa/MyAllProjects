__author__ = 'Naved Ali'

import unittest
import datetime
from selenium import webdriver


class EnvironmentSetup(unittest.TestCase):

# setUp contains the browser setup attribute
    def setUp(self):
        self.driver = webdriver.Chrome('E:\Drivers\chromedriver.exe')
        print("Run started at :" + str(datetime.datetime.now()))
        print("Chrome Environment set up")
        print("--------------------------------------")
        self.driver.implicitly_wait(20)
        self.driver.maximize_window()

# teardown method just to close all the browser instance then quit
    def tearDown(self):
        if (self.driver != None):
            print("--------------------------------")
            print("Test Environment destroyed")
            print("Run completed at :" + str(datetime.datetime.now()))
            self.driver.close()
            self.driver.quit()
