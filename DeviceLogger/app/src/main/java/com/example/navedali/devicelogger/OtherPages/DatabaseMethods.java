package com.example.navedali.devicelogger.OtherPages;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.widget.Toast;

import com.example.navedali.devicelogger.Login_Page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class DatabaseMethods extends AsyncTask<String, Void, String>
{
    static
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    public static String getMethod(String url)
    {
        StringBuffer response = new StringBuffer();
        try {
            URL obj = new URL(url);

            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            response.append("Something wrong!!!");
        }
        return response.toString();
    }

    public String parseJSON(String response, String key)
    {
        try
        {
            JSONObject myResponse = new JSONObject(response.toString());
            return myResponse.getString(key);
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public String parseJSONArray(String response, String key)
    {
        String value = "";
        try
        {
            JSONArray myArray = new JSONArray(response);
            for (int i=0;i<5;i++)
            {
                JSONObject myResponse = new JSONObject(myArray.get(i).toString());
                if(key=="UserName")
                {
                    String test = getMethod(Variables.apiUrl+"/DeviceLoggerAPI/Api/updateRecentUsersName.php?Username="+myResponse.getString(key));
                    test = parseJSON(test,"FirstName")+" "+parseJSON(test,"LastName");
                    value = value+test+" \n";
                }
                else
                {
                    value = value + myResponse.getString(key) + " \n";
                }
            }
        }
        catch(Exception e)
        {
e.printStackTrace();
        }
        return value;
    }

    @Override
    public String doInBackground(String... voids)
    {

        String url = voids[0];
        StringBuffer response = new StringBuffer();
        try {
            URL obj = new URL(url);

            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            response.append("Something wrong!!!");
        }
        return response.toString();
    }
}