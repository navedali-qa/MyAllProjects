package com.example.navedali.demoapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShutdownReceiver extends BroadcastReceiver
{
    @Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			System.out.println("SHUTDOWN STARTED\n\n");
			LoginPage loginPage = new LoginPage();

			Class.forName("com.mysql.jdbc.Driver");
			Connection con=(Connection) DriverManager.getConnection("jdbc:mysql://"+loginPage.serverUrl+"/"+loginPage.database+"","root","");
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			String query = "UPDATE `login_info` SET `End_Time` = \""+sdf.format(new Date())+"\" WHERE `Mobile_Serial_Number`=\""+ Build.SERIAL+"\" AND `End_Time`=\"LOCKED\"";
			System.out.println(query);
			PreparedStatement preparedStmt = con.prepareStatement(query);
			preparedStmt.executeUpdate();

			con.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(e);
		}
	}
}
