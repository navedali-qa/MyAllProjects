package com.Mail_Ru_WhiteListing.script;

public class Xpaths 
{
	public static String email_field = "//input[@id='mailbox:login']";
	public static String password_field = "//input[@name='password']";
	public static String submitButton = "//label[@id='mailbox:submit']";
	public static String unreadMail = "(//div[contains(@class,'b-datalist__item_unread')]//div[@class='b-datalist__item__subj'])[1]";
	public static String subject = "//div[@class='b-letter__head__subj__text']";
	public static String senderEmail = "(//span[@data-contact-informer-email])[1]";
	public static String emailTime = "//div[@class='b-letter__head__date']";
	public static String flagEmail = "//div[contains(@class,'b-letter__controls__flag')]/div";
	public static String deleteEmail="(//div[@data-name='remove'])[3]";
	public static String baloonPopup = "//*[@class='balloon__icon']";

}
