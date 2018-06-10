package com.DahnAutomation.test;
import java.util.Random;

public class Test 
{
	public static void main(String as[])
	{
		Random rand = new Random();
		for(int i=0;i<20;i++)
		{
			System.out.println(rand.nextInt((9999999 - 1000000) + 1) + 1000000);
		}
	}
}
