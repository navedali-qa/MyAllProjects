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
import com.example.navedali.devicelogger.OtherPages.Variables;

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
    String build_SERIAL="";

    //Custom Variable
    private Timer timer;
    private MyTimerTask myTimerTask;

    //UI Fields
    public EditText editText_username;
    public EditText editText_password;
    public EditText editText_confirm_password;
    public TextView textView_Logged_User;
    public TextView textView_ProjectInfo;

    //UI Frame
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

        Variables.policyManager = new PolicyManager(this);

        Variables.wmgr= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Variables.wmgr.setWifiEnabled(true);

        setContentView(R.layout.activity_login_page);

        mContentView = findViewById(R.id.fullscreen_content);
        mControlsView = findViewById(R.id.All_Control);

        editText_username = (EditText) findViewById(R.id.editText_username);
        editText_password = (EditText) findViewById(R.id.editText_password);

        //table details
        tableLayout_LoggedIn_Details = (TableLayout) findViewById(R.id.tableLayout_LoggedIn_Details);

        textView_Logged_User = (TextView) findViewById(R.id.textView_Logged_User);
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
                updateProject();
                Variables.updateUIFirstTimeResponse = getDatabaseMethods().doInBackground(Variables.apiUrl+"/DeviceLoggerAPI/Api/updateUIFirstTime.php/isUserLoggedIn/"+Build.SERIAL);
                if (!Variables.updateUIFirstTimeResponse.contains("\"error\":true") && !Variables.updateUIFirstTimeResponse.contains("Something wrong!!!"))
                {
                    Variables.logged_UserName = getDatabaseMethods().parseJSON(Variables.updateUIFirstTimeResponse,"FirstName")+" "+getDatabaseMethods().parseJSON(Variables.updateUIFirstTimeResponse,"LastName");
                    Variables.userPassword = getDatabaseMethods().parseJSON(Variables.updateUIFirstTimeResponse,"Password");
                    Variables.userName = getDatabaseMethods().parseJSON(Variables.updateUIFirstTimeResponse,"Username");
                    textView_Logged_User.setText("Logged in User : " + Variables.logged_UserName + "\n\n");
                    fullscreen_content_logout_controls_horizontal.setVisibility(View.VISIBLE);
                    if (fullscreen_content_logout_controls_horizontal.getVisibility() == View.VISIBLE)
                    {
                        Timer timer = new Timer();
                        timer.cancel();
                    }
                }
                else
                {
                    fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
                }
                Variables.updateUIFirstTimeResponse="";
            }});
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        delayedHide(5);
    }

    private void hide()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.VISIBLE);

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
        switch (v.getId())
        {
            case R.id.add_device_Info:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addDeviceInfo();
                    }});
                break;

            case R.id.activate_admin:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activateAdmin();
                    }
                });
                break;

            case R.id.deactivate_admin:
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        deactivateAdmin();
                    }});
                break;

            case R.id.buttonLogin:
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        loginToAppFunctionality();
                    }});
                break;

            case R.id.buttonProceed:
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        proceedButtonFunctionality();
                    }});
                break;

            case R.id.buttonLogout:
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        logoutButtonFunctionality();
                    }});
                break;
        }
    }

    public void activateAdmin()
    {
        if (!Variables.policyManager.isAdminActive())
        {
            timer = new Timer();
            timer.cancel();
            Intent activateDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, Variables.policyManager.getAdminComponent());
            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "After activating admin, you will be able to block application uninstallation.");
            startActivityForResult(activateDeviceAdmin, PolicyManager.DPM_ACTIVATION_REQUEST_CODE);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Already have admin privileges",Toast.LENGTH_SHORT).show();
        }
        resetFields();
        fullscreen_content_admin_controls_horizontal.setVisibility(View.GONE);
        fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
        timerStart();
    }

    public void deactivateAdmin()
    {
        myTimerTask = new MyTimerTask();
        timer = new Timer();
        timer.schedule(myTimerTask, 0);
        if (Variables.policyManager.isAdminActive())
        {
            Variables.policyManager.disableAdmin();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Admin privileges already removed",Toast.LENGTH_SHORT).show();
        }
        editText_username.setText("");
        editText_password.setText("");
        fullscreen_content_admin_controls_horizontal.setVisibility(View.GONE);
        fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
    }

    public void loginToAppFunctionality()
    {
        hideKeypad();
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                final boolean[] loginAdmin = {false};
                final boolean[] loginUser = {false};

                if(editText_username.getText().toString().equals(Variables.backupAdminName) && editText_password.getText().toString().equals(Variables.backupAdminName))
                {
                    loginAdmin[0] = true;
                }
                else
                if(editText_username.getText().toString().equals(Variables.backupUserName) && editText_password.getText().toString().equals(Variables.backupUserName))
                {
                    Variables.userName=Variables.backupUserName;
                    Variables.userPassword=Variables.backupUserName;
                    Variables.logged_UserName = "Dummy User";
                    loginUser[0] = true;
                }
                else
                if(editText_username.getText().toString().equals("") || editText_password.getText().toString().equals(""))
                {
                    Toast.makeText(Login_Page.this, "Enter username/password", Toast.LENGTH_SHORT).show();
                    resetFields();
                }
                else
                {
                    //*******************************************
                            //ENTER LOGIN API PART HERE
                    Variables.loginApiResponse=getDatabaseMethods().doInBackground(Variables.apiUrl+"/DeviceLoggerAPI/Api/login.php?username="+editText_username.getText().toString().trim()+"&password="+editText_password.getText().toString().trim()+"&project="+textView_ProjectInfo.getText().toString().replaceAll("Project : ","").trim());
                    if(Variables.loginApiResponse.contains("FirstName"))
                    {
                        if(Variables.loginApiResponse.contains("\"Admin\":1"))
                        {
                            loginAdmin[0]=true;
                        }
                        else
                        if(Variables.loginApiResponse.contains("\"Admin\":0"))
                        {
                            Variables.logged_UserName = getDatabaseMethods().parseJSON(Variables.loginApiResponse, "FirstName") + " " + getDatabaseMethods().parseJSON(Variables.loginApiResponse, "LastName");
                            Variables.userName = getDatabaseMethods().parseJSON(Variables.loginApiResponse, "Username");
                            Variables.userPassword = getDatabaseMethods().parseJSON(Variables.loginApiResponse, "Password");
                            loginUser[0] = true;
                        }
                    }
                    else
                    {
                        Variables.logged_UserName = "";
                        resetFields();
                    }
                    //*******************************************
                }

                if (loginAdmin[0])
                {
                    updateProject();
                    fullscreen_content_login_controls_horizontal.setVisibility(View.GONE);
                    fullscreen_content_admin_controls_horizontal.setVisibility(View.VISIBLE);
                    timer = new Timer();
                    timer.cancel();
                }
                else
                if (loginUser[0])
                {
                    textView_Logged_User.setText("Logged User : " + Variables.logged_UserName+"\n\n");
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
                    fullscreen_content_login_controls_horizontal.setVisibility(View.GONE);
                    fullscreen_content_info_controls_horizontal.setVisibility(View.VISIBLE);

                    if(!Variables.userName.equals(Variables.backupUserName))
                    {
                        sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String query = "UserName=" + Variables.userName + "&Mobile_Serial_Number="+ Build.SERIAL +"&Start_Time=" + sdf.format(new Date()).replaceAll(" ","%20")+ "&End_Time=LOCKED&Brand="+Build.BRAND+"&Mobile_Name=" + Build.MODEL.replaceAll(" ","%20") + "&Version=Android%20"+Build.VERSION.RELEASE +"&Screen_Size="+getScreenSize()+"%20Inches";
                        Variables.insertLoginInfoResponse = getDatabaseMethods().doInBackground(Variables.apiUrl + "/DeviceLoggerAPI/Api/insertLoginInfo.php?" + query);
                    }
                }
                else
                {
                    Toast.makeText(Login_Page.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    resetFields();
                }
            }
        });
        timerStart();
    }

    public void proceedButtonFunctionality()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                fullscreen_content_info_controls_horizontal.setVisibility(View.GONE);
                fullscreen_content_logout_controls_horizontal.setVisibility(View.VISIBLE);
                resetFields();
                myTimerTask = new MyTimerTask();
                timer = new Timer();
                timer.cancel();
                moveTaskToBack(true);
            }
        });
    }

    public void logoutButtonFunctionality()
    {
        hideKeypad();
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (editText_confirm_password.getText().toString().equals(Variables.userPassword))
                {
                    updateProject();
                    myTimerTask = new MyTimerTask();
                    timer = new Timer();
                    timer.schedule(myTimerTask, 0);
                    resetFields();
                    fullscreen_content_logout_controls_horizontal.setVisibility(View.GONE);
                    fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                    sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String query = "End_Time=" + sdf.format(new Date()).replaceAll(" ","%20") + "&Mobile_Serial_Number="+ Build.SERIAL +"&UserName="+Variables.userName;
                    if(!Variables.userPassword.equals(Variables.backupUserName))
                    {
                        Variables.updateLoginInfoResponse = getDatabaseMethods().doInBackground(Variables.apiUrl + "/DeviceLoggerAPI/Api/updateLoginInfo.php?" + query);
                    }
                    Variables.userName="";
                    Variables.logged_UserName="";
                    textView_Logged_User.setText(Variables.logged_UserName);
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
        myTimerTask = new MyTimerTask();
        timer = new Timer();
        timer.schedule(myTimerTask, 0);
        String query = "Device_Type=Android&Mobile_Name=" + Build.MODEL.replaceAll(" ","%20") +"&Brand="+Build.BRAND+ "&Mobile_Serial_Number="+ Build.SERIAL+"&Version=Android%20"+Build.VERSION.RELEASE +"&Screen_Size="+getScreenSize()+"%20Inches&Project=Other";
        Variables.addDeviceDetailsApiResponse = getDatabaseMethods().doInBackground(Variables.apiUrl + "/DeviceLoggerAPI/Api/addDeviceDetails.php?" + query);
        fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
        editText_username.setText("");
        editText_password.setText("");
        fullscreen_content_admin_controls_horizontal.setVisibility(View.GONE);
        if(Variables.addDeviceDetailsApiResponse.contains("New device added successfully"))
        {
            Toast.makeText(Login_Page.this, "Device Info Added", Toast.LENGTH_SHORT).show();
        }
        else
            if(Variables.addDeviceDetailsApiResponse.contains("Device Already Added"))
        {
            Toast.makeText(Login_Page.this, "Device Already Added", Toast.LENGTH_SHORT).show();
        }
        else
            {
                Toast.makeText(Login_Page.this, "Error occured Please contact Admin", Toast.LENGTH_SHORT).show();
            }
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
                String query="Mobile_Serial_Number="+Build.SERIAL;
                Variables.updateRecentUserApiResponse = getDatabaseMethods().doInBackground(Variables.apiUrl+"/DeviceLoggerAPI/Api/updateRecentUsers.php?"+query);
                if(!Variables.updateRecentUserApiResponse.contains("\"error\":true"))
                {
                    String value = getDatabaseMethods().parseJSONArray(Variables.updateRecentUserApiResponse, "UserName");
                    table_User_Name.setText(value);
                    value = getDatabaseMethods().parseJSONArray(Variables.updateRecentUserApiResponse, "Start_Time");
                    table_Start_Time1.setText(value);
                    value = getDatabaseMethods().parseJSONArray(Variables.updateRecentUserApiResponse, "End_Time");
                    table_End_Time1.setText(value);
                }
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
                Variables.getProjectNameResponse = getDatabaseMethods().doInBackground(Variables.apiUrl+"/DeviceLoggerAPI/Api/getProjectName.php?"+query);
                String project_Name = getDatabaseMethods().parseJSON(Variables.getProjectNameResponse,"Project");
                if(project_Name=="" || project_Name.contains("Something wrong!!!"))
                {
                    textView_ProjectInfo.setText("Project : Other_");
                }
                else
                {
                    textView_ProjectInfo.setText("Project : " + project_Name);
                }
                Variables.getProjectNameResponse = "";
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