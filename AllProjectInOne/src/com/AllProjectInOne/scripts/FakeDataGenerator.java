package com.AllProjectInOne.scripts;

import org.testng.annotations.Test;

import fabricator.Contact;
import fabricator.Fabricator;

public class FakeDataGenerator
{

	@Test
	public void fakeDataGenerator()
	{
		
		for(int i=0;i<100; i++)
		{
			Contact contact =  Fabricator.contact();
			String firstName = contact.firstName();
			String lastName = contact.lastName();
			String designation = contact.suffix();
			String address = contact.address();
			String phoneNumber = contact.phoneNumber();
			String birthday = contact.birthday(19);
			String bloodType = contact.bloodType();
			String city = contact.city();
			System.out.println(designation+"\t"+firstName+"\t"+lastName+"\t"+bloodType+"\t"+phoneNumber+"\t"+birthday+"\t"+city+"\t"+address);
		}
	}
}
