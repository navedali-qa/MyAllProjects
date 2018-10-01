package com.example.navedali.devicelogger;

import android.os.Build;
import android.os.StrictMode;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseMethods
{
    Connection connection;
    PreparedStatement preparedStatement;
    Statement statement;
    public DatabaseMethods(String serverUrl,String database)
    {
        try
        {
            if(connection==null)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                StrictMode.setThreadPolicy(policy);

                Class.forName("com.mysql.jdbc.Driver");
                connection = (Connection) DriverManager.getConnection("jdbc:mysql://" + serverUrl + "/" + database, "root", "");
            }
        }
        catch (Exception e)
        {
e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query)
    {
        ResultSet resultSet=null;
        try
        {
            statement = connection.createStatement();
            resultSet = (ResultSet) statement.executeQuery(query);
            statement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void insertUpdateValue(String query)
    {
        try
        {
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
