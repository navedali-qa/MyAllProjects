package com.example.navedali.devicelogger;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.widget.Toast;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class DatabaseMethods
{
    static
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    Connection connection;
    PreparedStatement preparedStatement;
    Statement statement;
    Context context;
    String serverUrl;
    String database;

    public DatabaseMethods(Context context, String serverUrl, String database)
    {
        this.context=context;
        this.serverUrl=serverUrl;
        this.database = database;
    }

    public Connection getConnection()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try
        {
            this.context = context;
            if(connection==null)
            {
                System.out.println("START TIME : "+sdf.format(new Date()));

                Class.forName("com.mysql.jdbc.Driver");
                Properties prop = new Properties();
                prop.put("connectTimeout","10000");
                String connectionString = "jdbc:mysql://"+serverUrl+"/"+database+"?user=root&password=";
                connection = (Connection) DriverManager.getConnection(connectionString,prop);
                //connection = (Connection) DriverManager.getConnection("jdbc:mysql://" + serverUrl + "/" + database, "root", "",prop);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Database/Intranet is not connected...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            System.out.println("END TIME : "+sdf.format(new Date()));
        }
        return connection;
    }

    public ResultSet executeQuery(String query)
    {
        ResultSet resultSet=null;
        try
        {
            connection = getConnection();
            statement = connection.createStatement();
            resultSet = (ResultSet) statement.executeQuery(query);
           // statement.close();
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Database/Intranet is not connected...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return resultSet;
    }

    public void insertUpdateValue(String query)
    {
        try
        {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.execute();
            preparedStatement.close();
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Database/Intranet is not connected...", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
