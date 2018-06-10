__author__ = 'Naved Ali'

from selenium import webdriver

class ScreenShot(object):

    def __init__(self, driver):
        self.driver = driver

    def getScreenShot(self, path):
        directory = "E:\\Automation\\Python\\Self\\Udemy_Project_Workspace\\SeleniumAutomation\\WebAutomation\\ScreenShots\\"
        self.driver.get_screenshot_as_file(directory+path)