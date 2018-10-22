package com.example.navedali.devicelogger;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navedali.devicelogger.OtherPages.DatabaseMethods;
import com.example.navedali.devicelogger.OtherPages.PolicyManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Login_Page extends AppCompatActivity implements View.OnClickListener
{
    //START SERVICE:
    //am startservice -n com.example.navedali.myapplication/.ReceiveUninstallService
    private static final int UI_ANIMATION_DELAY = 5;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    //DATABASE VARIABLES:
    //String serverUrl="192.168.57.1:3306";
    //String serverUrl="10.148.1.66:3306";
    //static String apiUrl = "http://192.168.57.1:8081";
    static String apiUrl = "http://10.148.1.66:8080";
    String database="360_logica_mobile_logger";
    String userName="";
    String logged_UserName="";
    String userPassword="";
    String build_SERIAL="";
    String backupAdminName="aa";
    String backupUserName="bb";

    String updateUIFirstTimeResponse = "";
    String loginApiResponse = "";
    String insertLoginInfoResponse = "";
    String addDeviceDetailsApiResponse = "";
    String getProjectNameResponse = "";
    String updateLoginInfoResponse = "";
    String updateRecentUserApiResponse = "";

    //Custom Variable
    private Timer timer;
    private MyTimerTask myTimerTask;
    PolicyManager policyManager;

    //Text Box
    public EditText editText_username;
    public EditText editText_password;
    public EditText editText_confirm_password;

    public TextView textView_Logged_User;
    public TextView textView_Logged_User1;
    public TextView textView_ProjectInfo;

    //Frame
    public LinearLayout fullscreen_content_login_controls_horizontal;
    public LinearLayout fullscreen_content_info_controls_horizontal;
    public LinearLayout fullscreen_content_logout_controls_horizontal;
    public LinearLayout fullscreen_content_admin_controls_horizontal;

    //Table
    public TableLayout tableLayout_LoggedIn_Details;

    private final Runnable mHidePart2Runnable = new Runnable()
    {
        @SuppressLint("InlinedApi")
        @Override
        public void run()
        {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private View mControlsView;

    private final Runnable mShowPart2Runnable = new Runnable()
    {
        @Override
        public void run()
        {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private boolean mVisible;

    private final Runnable mHideRunnable = new Runnable()
    {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        policyManager = new PolicyManager(this);

        WifiManager wmgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wmgr.setWifiEnabled(true);

        setContentView(R.layout.activity_login_page);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        mControlsView = findViewById(R.id.All_Control);

        editText_username = (EditText) findViewById(R.id.editText_username);
        editText_password = (EditText) findViewById(R.id.editText_password);

        //table details
        tableLayout_LoggedIn_Details = (TableLayout) findViewById(R.id.tableLayout_LoggedIn_Details);

        textView_Logged_User = (TextView) findViewById(R.id.textView_Logged_User);
        textView_Logged_User1 = (TextView) findViewById(R.id.textView_Logged_User1);
        textView_ProjectInfo = (TextView) findViewById(R.id.textView_ProjectInfo);
        //Logout process
        editText_confirm_password = (EditText) findViewById(R.id.editText_confirm_password);

        //FRAMES
        fullscreen_content_login_controls_horizontal = (LinearLayout) findViewById(R.id.fullscreen_content_login_controls_horizontal);
        fullscreen_content_info_controls_horizontal = (LinearLayout) findViewById(R.id.fullscreen_content_info_controls_horizontal);
        fullscreen_content_logout_controls_horizontal = (LinearLayout) findViewById(R.id.fullscreen_content_logout_controls_horizontal);
        fullscreen_content_admin_controls_horizontal = (LinearLayout) findViewById(R.id.fullscreen_content_admin_controls_horizontal);

        updateUIFirstTime();
        resetFields();
    }

    public void updateUIFirstTime()
    {
        runOnUiThread(new Runnable()
        {
            boolean found=false;
            @Override
            public void run()
            {
                updateUIFirstTimeResponse = getDatabaseMethods().doInBackground(apiUrl+"/DeviceLoggerAPI/Api/updateUIFirstTime.php/isUserLoggedIn/"+Build.SERIAL);
                if (updateUIFirstTimeResponse.contains("User not logged in") || updateUIFirstTimeResponse.contains("Something wrong!!!"))
                {
                    fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
                }
                else
                {
                    logged_UserName = getDatabaseMethods().parseJSON(updateUIFirstTimeResponse,"FirstName")+" "+getDatabaseMethods().parseJSON(updateUIFirstTimeResponse,"LastName");
                    userPassword = getDatabaseMethods().parseJSON(updateUIFirstTimeResponse,"Password");
                    userName = getDatabaseMethods().parseJSON(updateUIFirstTimeResponse,"Username");
                    textView_Logged_User.setText("Logged in User : " + logged_UserName + "\n\n");
                    textView_Logged_User1.setText("Logged in User : " + logged_UserName + "\n");
                    fullscreen_content_logout_controls_horizontal.setVisibility(View.VISIBLE);
                    if (fullscreen_content_logout_controls_horizontal.getVisibility() == View.VISIBLE)
                    {
                        Timer timer = new Timer();
                        timer.cancel();
                        timer = null;
                    }
                }
                updateProject();
                updateUIFirstTimeResponse="";
            }});
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        delayedHide(5);
    }

    private void toggle()
    {
        if (mVisible) {
            hide();
        } else {
            // show();
        }
    }

    private void hide()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.VISIBLE);
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis)
    {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        resetFields();
    }

    @Override
    public void onClick(View v)
    {
        LinearLayout fullscreen_content_login_controls_horizontal = (LinearLayout) findViewById(R.id.fullscreen_content_login_controls_horizontal);
        LinearLayout fullscreen_content_admin_controls_horizontal = (LinearLayout) findViewById(R.id.fullscreen_content_admin_controls_horizontal);
        final EditText editText_username = (EditText) findViewById(R.id.editText_username);
        final EditText editText_password = (EditText) findViewById(R.id.editText_password);

        switch (v.getId())
        {
            case R.id.add_device_Info:
                    addDeviceInfo();
                    updateProject();
                break;
            case R.id.activate_admin:
                if (!policyManager.isAdminActive())
                {
                    timer = new Timer();
                    timer.cancel();
                    Intent activateDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, policyManager.getAdminComponent());
                    activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "After activating admin, you will be able to block application uninstallation.");
                    startActivityForResult(activateDeviceAdmin, PolicyManager.DPM_ACTIVATION_REQUEST_CODE);
                }
                updateProject();
                editText_username.setText("");
                editText_password.setText("");
                fullscreen_content_admin_controls_horizontal.setVisibility(View.GONE);
                fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
                timerStart();
                break;
            case R.id.deactivate_admin:
                if (policyManager.isAdminActive())
                {
                    policyManager.disableAdmin();
                }
                updateProject();
                editText_username.setText("");
                editText_password.setText("");
                fullscreen_content_admin_controls_horizontal.setVisibility(View.GONE);
                fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
                timerStart();
                break;
            case R.id.buttonLogin:
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        hideKeypad();
                        loginToAppFunctionality();
                        timerStart();
                    }});
                break;

            case R.id.buttonProceed:
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        proceedButtonFunctionality(true);
                        myTimerTask = new MyTimerTask();
                        timer = new Timer();
                        timer.cancel();
                        resetFields();
                        updateProject();
                    }});
                break;

            case R.id.buttonLogout:
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        hideKeypad();
                        logoutButtonFunctionality();
                        updateProject();
                    }});
                break;
        }
    }

    public void loginToAppFunctionality()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                final boolean[] loginAdmin = {false};
                final boolean[] loginUser = {false};
                final boolean[] networkConnected = {true};

                if(editText_username.getText().toString().equals("") || editText_password.getText().toString().equals(""))
                {
                    Toast.makeText(Login_Page.this, "Enter username/password", Toast.LENGTH_SHORT).show();
                    editText_username.setText("");
                    editText_password.setText("");
                    networkConnected[0] =false;
                }
                else
                if(editText_username.getText().toString().equals(backupAdminName) && editText_password.getText().toString().equals(backupAdminName))
                {
                    loginAdmin[0] = true;
                }
                else
                if(editText_username.getText().toString().equals(backupUserName) && editText_password.getText().toString().equals(backupUserName))
                {
                    userName=backupUserName;
                    userPassword=backupUserName;
                    logged_UserName = "Dummy User";
                    loginUser[0] = true;
                    networkConnected[0] =false;
                }
                else
                {
                    logged_UserName="";
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            try
                            {
                                loginApiResponse = getDatabaseMethods().doInBackground(apiUrl + "/DeviceLoggerAPI/Api/adminLogin.php?username=" + editText_username.getText().toString().trim() + "&password=" + editText_password.getText().toString());

                                if (!loginApiResponse.contains("Login credentials are wrong. Please try again!") && !loginApiResponse.contains("Something wrong!!!"))
                                {
                                        loginAdmin[0] = true;
                                }
                                if(!loginApiResponse.contains("Something wrong!!!"))
                                {
                                    if (!loginAdmin[0]) {
                                        loginApiResponse = getDatabaseMethods().doInBackground(apiUrl + "/DeviceLoggerAPI/Api/login.php?username=" + editText_username.getText().toString().trim() + "&password=" + editText_password.getText().toString() + "&project=" + textView_ProjectInfo.getText().toString().replace("Project : ","").trim());
                                        System.out.println("RESPONSE : " + loginApiResponse);
                                        if (loginApiResponse.contains("Login credentials are wrong. Please try again!") || loginApiResponse.contains("Something wrong!!!")) {
                                            loginUser[0] = false;
                                        } else {
                                            logged_UserName = getDatabaseMethods().parseJSON(loginApiResponse, "FirstName") + " " + getDatabaseMethods().parseJSON(loginApiResponse, "LastName");
                                            userName = getDatabaseMethods().parseJSON(loginApiResponse, "Username");
                                            userPassword = getDatabaseMethods().parseJSON(loginApiResponse, "Password");
                                            loginUser[0] = true;
                                        }
                                    }
                                }
                                else
                                {
                                    Toast.makeText(Login_Page.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                    networkConnected[0] =false;
                                }
                                loginApiResponse="";
                            }
                            catch(Exception e)
                            {
                                Toast.makeText(Login_Page.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                                networkConnected[0] =false;
                            }
                        }});
                }
                if (loginAdmin[0])
                {
                    fullscreen_content_login_controls_horizontal.setVisibility(View.GONE);
                    fullscreen_content_admin_controls_horizontal.setVisibility(View.VISIBLE);
                    timer = new Timer();
                    timer.cancel();
                }
                else
                if (loginUser[0])
                {
                    updateProject();
                    updateRecentlyUserTable();

                    System.out.println("DEVICE DETAILS :\n"
                            + "\nBRAND : " + Build.BRAND
                            + "\nMODEL : " + Build.MODEL
                            + "\nVERIONS SDK_INT : " + Build.VERSION.SDK_INT
                            + "\nVERIONS PRODUCT : " + Build.PRODUCT
                            + "\nSERIAL : " + Build.SERIAL
                            + "\nID : " + Build.VERSION.RELEASE
                            + "\nMANUFACTURER : " + Build.MANUFACTURER
                            + "\n Screen size in inches : "+getScreenSize());

                    build_SERIAL = Build.SERIAL;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    build_SERIAL=Build.SERIAL;
                    textView_Logged_User1.setText("Logged in User : " + logged_UserName+"\n");
                    fullscreen_content_login_controls_horizontal.setVisibility(View.GONE);
                    fullscreen_content_info_controls_horizontal.setVisibility(View.VISIBLE);

                    if(!userName.equals(backupUserName))
                    {
                        sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String query = "UserName=" + userName + "&Mobile_Serial_Number="+ Build.SERIAL +"&Start_Time=" + sdf.format(new Date()).replaceAll(" ","%20")+ "&End_Time=LOCKED&Brand="+Build.BRAND+"&Mobile_Name=" + Build.MODEL.replaceAll(" ","%20") + "&Version=Android%20"+Build.VERSION.RELEASE +"&Screen_Size="+getScreenSize()+"%20Inches";
                        insertLoginInfoResponse = getDatabaseMethods().doInBackground(apiUrl + "/DeviceLoggerAPI/Api/insertLoginInfo.php?" + query);
                        System.out.println("API : "+apiUrl + "/DeviceLoggerAPI/Api/insertLoginInfo.php?" + query);
                    }
                }
                else
                {
                    if(networkConnected[0])
                    {
                        Toast.makeText(Login_Page.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                    resetFields();
                }
            }
        });
    }

    public void proceedButtonFunctionality(final boolean hide)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                textView_Logged_User.setText("Logged in User : " + logged_UserName+"\n\n");
                textView_Logged_User1.setText("Logged in User : " + logged_UserName+"\n");
                fullscreen_content_info_controls_horizontal.setVisibility(View.GONE);
                fullscreen_content_logout_controls_horizontal.setVisibility(View.VISIBLE);
                resetFields();
                myTimerTask = new MyTimerTask();
                timer = new Timer();
                timer.cancel();
                if(hide) {
                    moveTaskToBack(true);
                }
            }
        });
    }

    public void logoutButtonFunctionality()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (editText_confirm_password.getText().toString().equals(userPassword))
                {
                    myTimerTask = new MyTimerTask();
                    timer = new Timer();
                    timer.schedule(myTimerTask, 0);
                    resetFields();
                    fullscreen_content_logout_controls_horizontal.setVisibility(View.GONE);
                    fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                    sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String query = "End_Time=" + sdf.format(new Date()).replaceAll(" ","%20") + "&Mobile_Serial_Number="+ Build.SERIAL +"&UserName="+userName;
                    updateLoginInfoResponse = getDatabaseMethods().doInBackground(apiUrl + "/DeviceLoggerAPI/Api/updateLoginInfo.php?" + query);
                    userName="";
                }
                else
                if (editText_confirm_password.getText().toString().trim().equals(""))
                {
                    Toast.makeText(Login_Page.this, "Please enter password to logout", Toast.LENGTH_SHORT).show();
                    editText_confirm_password.setText("");
                }
                else
                {
                    Toast.makeText(Login_Page.this, "Please enter correct password to logout", Toast.LENGTH_SHORT).show();
                    editText_confirm_password.setText("");
                }
            }
        });
    }

    public void addDeviceInfo()
    {
        String query = "Mobile_Name=" + Build.MODEL.replaceAll(" ","%20") +"&Brand="+Build.BRAND+ "&Mobile_Serial_Number="+ Build.SERIAL+"&Version=Android%20"+Build.VERSION.RELEASE +"&Screen_Size="+getScreenSize()+"%20Inches&Project=Other";
        addDeviceDetailsApiResponse = getDatabaseMethods().doInBackground(apiUrl + "/DeviceLoggerAPI/Api/addDeviceDetails.php?" + query);
        fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
        editText_username.setText("");
        editText_password.setText("");
        fullscreen_content_admin_controls_horizontal.setVisibility(View.GONE);
        timerStart();
        Toast.makeText(Login_Page.this, "Device Info Added", Toast.LENGTH_SHORT).show();
    }

    //DISABLE ANDROID BUTTONS
    @Override
    protected void onResume()
    {
        mContentView.setVisibility(View.GONE);
        if (timer!=null && fullscreen_content_logout_controls_horizontal.getVisibility()==View.GONE)
        {
            timer.cancel();
            timer = null;
        }

        delayedHide(5);

        resetFields();

        super.onResume();
    }

    @Override
    protected void onPause()
    {
        timerStart();
        super.onPause();
    }

    public void updateRecentlyUserTable()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                String lastUser = " ";
                TextView table_User_Name = (TextView) findViewById(R.id.table_User_Name);
                TextView table_Start_Time1 = (TextView) findViewById(R.id.table_Start_Time1);
                TextView table_End_Time1 = (TextView) findViewById(R.id.table_End_Time1);
                table_User_Name.setText(" ");
                table_Start_Time1.setText(" ");
                table_End_Time1.setText(" ");
                /*try
                {
                    System.out.println("SELECT UserName, Start_Time, End_Time FROM `login_info` WHERE `Mobile_Serial_Number`=\"" + Build.SERIAL + "\" ORDER BY Login_Index DESC LIMIT 5");
                    resultSet = databaseMethods.executeQuery("SELECT UserName, Start_Time, End_Time FROM `login_info` WHERE `Mobile_Serial_Number`=\"" + Build.SERIAL + "\" ORDER BY Login_Index DESC LIMIT 5");
                    while (resultSet.next())
                    {
                        ResultSet resultSet1 = databaseMethods.executeQuery("SELECT * FROM users WHERE Username='" + resultSet.getString(1) + "'");
                        while (resultSet1.next())
                        {
                            //System.out.println(lastUser+"\t"+resultSet.getString(2)+"\t"+resultSet.getString(3));
                            lastUser = resultSet1.getString(2) + " " + resultSet1.getString(3);
                            table_User_Name.setText(table_User_Name.getText() + lastUser + "  \n");
                            table_Start_Time1.setText(table_Start_Time1.getText() + resultSet.getString(2) + "  \n");
                            table_End_Time1.setText(table_End_Time1.getText() + resultSet.getString(3) + "  \n");
                        }
                    }
                    resultSet.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }*/
                String query="Mobile_Serial_Number="+Build.SERIAL;
                updateRecentUserApiResponse = getDatabaseMethods().doInBackground(apiUrl+"/DeviceLoggerAPI/Api/updateRecentUsers.php?"+query);
                String value = getDatabaseMethods().parseJSONArray(updateRecentUserApiResponse,"UserName");
                table_User_Name.setText(value);
                value = getDatabaseMethods().parseJSONArray(updateRecentUserApiResponse,"Start_Time");
                table_Start_Time1.setText(value);
                value = getDatabaseMethods().parseJSONArray(updateRecentUserApiResponse,"End_Time");
                table_End_Time1.setText(value);
            }
        });
    }

    public void hideKeypad()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public String getScreenSize()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);

        screenInches=  (double)Math.round(screenInches * 10) / 10;
        return String.valueOf(screenInches);
    }

    @Override
    public void onBackPressed(){}

    public void resetFields()
    {
        editText_username.setText("");
        editText_password.setText("");
        editText_confirm_password.setText("");
    }

    public void updateProject()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                String query = "Mobile_Name=" + Build.MODEL.replaceAll(" ","%20")+"&Brand="+Build.BRAND+ "&Mobile_Serial_Number="+ Build.SERIAL+"&Version=Android%20"+Build.VERSION.RELEASE +"&Screen_Size="+getScreenSize()+"%20Inches";
                getProjectNameResponse = getDatabaseMethods().doInBackground(apiUrl+"/DeviceLoggerAPI/Api/getProjectName.php?"+query);
                String project_Name = getDatabaseMethods().parseJSON(getProjectNameResponse,"Project");
                if(project_Name=="" || project_Name.contains("Something wrong!!!"))
                {
                    textView_ProjectInfo.setText("Project : Other_");
                }
                else
                {
                    textView_ProjectInfo.setText("Project : " + project_Name);
                }
                getProjectNameResponse = "";
            }});
    }

    public void timerStart()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                if (timer == null && fullscreen_content_logout_controls_horizontal.getVisibility() == View.GONE) {
                    myTimerTask = new MyTimerTask();
                    timer = new Timer();
                    timer.schedule(myTimerTask, 5, 5);
                }
            }});
    }

    private void bringApplicationToFront()
    {
        KeyguardManager myKeyManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (myKeyManager.inKeyguardRestrictedInputMode())
            return;

        Intent notificationIntent = new Intent(this, Login_Page.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public DatabaseMethods getDatabaseMethods()
    {
        final DatabaseMethods[] db = new DatabaseMethods[1];
        runOnUiThread(new Runnable()
        {
        @Override
        public void run()
        {
            db[0] = new DatabaseMethods();
        }});
        return db[0];
    }
    class MyTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            bringApplicationToFront();
        }
    }
}