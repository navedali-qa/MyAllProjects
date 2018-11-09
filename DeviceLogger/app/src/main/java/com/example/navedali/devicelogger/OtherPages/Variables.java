package com.example.navedali.devicelogger.OtherPages;

import android.net.wifi.WifiManager;

import com.example.navedali.devicelogger.PolicyManager;

public class Variables
{

    public static String apiUrl = "http://10.148.0.25:8081";
    public static String database="360_logica_mobile_logger";
    public static String updateUIFirstTimeResponse="";
    public static String logged_UserName = "";
    public static String userPassword = "";
    public static String userName="";
    public static String backupAdminName="aa";
    public static String backupUserName="bb";
    public static String addDeviceDetailsApiResponse="";
    public static PolicyManager policyManager;
    public static WifiManager wmgr;

    public static String updateRecentUserApiResponse = "";
    public static String getProjectNameResponse="";

    public static String loginApiResponse = "";
    public static String insertLoginInfoResponse = "";
    public static String updateLoginInfoResponse = "";
}
