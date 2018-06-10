__author__ = 'Naved Ali'

from WebAutomation.Locators.AllLocators import Locators
from selenium.webdriver.common.by import By
from selenium import webdriver


class HomePageHelper:
    def __init__(self, driver):

        self.driver = driver

        # Home page Locators defining
        self.logo = driver.find_element(By.XPATH, Locators.logo)
        self.sign_in = driver.find_element(By.XPATH, Locators.sign_in)
        self.register = driver.find_element(By.XPATH, Locators.register)
        self.support = driver.find_element(By.XPATH, Locators.support)
        self.contact = driver.find_element(By.XPATH, Locators.contact)

    def getlogo(self):
        return self.logo

    def getsignin(self):
        return self.sign_in

    def getregister(self):
        return self.register

    def getsupport(self):
        return self.support

    def getcontact(self):
        return self.contact
