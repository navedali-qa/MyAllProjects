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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Login_Page extends AppCompatActivity implements View.OnClickListener
{
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 5;
    private static final int UI_ANIMATION_DELAY = 5;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    //DATABASE VARIABLES:
    DatabaseMethods databaseMethods;
    ResultSet resultSet;
    //String serverUrl="192.168.0.103:3306";
    String serverUrl="192.168.0.109:3306";
    //String serverUrl="10.148.1.66:3306";
    String database="360_logica_mobile_logger";
    String userName="";
    String logged_UserName="";
    String userPassword="";
    String build_SERIAL="";
    String backupAdminName="aa";
    String backupUserName="bb";

    //Custom Variable
    private Timer timer;
    private MyTimerTask myTimerTask;
    PolicyManager policyManager;

    //Text Box
    public EditText editText_username;
    public EditText editText_password;
    public EditText editText_confirm_password;

    public TextView textView_Logged_User;
    public  TextView textView_Logged_User_UpdatePassword;

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

        databaseMethods = new DatabaseMethods(Login_Page.this,serverUrl,database);

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
                try {

                    resultSet = databaseMethods.executeQuery("SELECT * FROM login_info WHERE End_Time='LOCKED' AND Mobile_Serial_Number='" + Build.SERIAL + "'");
                    while (resultSet.next()) {
                        userName = resultSet.getString(2);
                        found = true;
                        break;
                    }
                    if (found) {
                        resultSet = databaseMethods.executeQuery("SELECT * FROM users WHERE Username='" + userName + "'");
                        while (resultSet.next()) {
                            logged_UserName = resultSet.getString(2) + " " + resultSet.getString(3);
                            userPassword = resultSet.getString(5);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (found) {
                    textView_Logged_User.setText("Logged in User : " + logged_UserName + "\n\n");
                    fullscreen_content_logout_controls_horizontal.setVisibility(View.VISIBLE);
                    if (fullscreen_content_logout_controls_horizontal.getVisibility() == View.VISIBLE) {
                        Timer timer = new Timer();
                        timer.cancel();
                        timer = null;
                    }
                } else {
                    fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
                }
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
        databaseMethods = new DatabaseMethods(Login_Page.this,serverUrl,database);
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
                fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
                editText_username.setText("");
                editText_password.setText("");
                fullscreen_content_admin_controls_horizontal.setVisibility(View.GONE);
                timerStart();
                break;
            case R.id.deactivate_admin:
                if (policyManager.isAdminActive())
                {
                    policyManager.disableAdmin();
                }
                fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
                editText_username.setText("");
                editText_password.setText("");
                fullscreen_content_admin_controls_horizontal.setVisibility(View.GONE);
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
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            try
                            {
                                resultSet = databaseMethods.executeQuery("SELECT * FROM Admin WHERE Admin_UserName='" + editText_username.getText().toString().trim() + "' AND Admin_Password='" + editText_password.getText().toString() + "'");
                                while (resultSet.next()) {
                                    if (editText_password.getText().toString().equals(resultSet.getString(3))) {
                                        loginAdmin[0] = true;
                                    }
                                    break;
                                }
                                if (!loginAdmin[0]) {
                                    resultSet = databaseMethods.executeQuery("SELECT * FROM users WHERE Username='" + editText_username.getText().toString() + "' AND Password='" + editText_password.getText().toString() + "'");
                                    while (resultSet.next()) {
                                        if (editText_password.getText().toString().equals(resultSet.getString(5))) {
                                            logged_UserName = resultSet.getString(2) + " " + resultSet.getString(3);
                                            userName = resultSet.getString(4);
                                            userPassword = resultSet.getString(5);
                                            loginUser[0] = true;
                                        }
                                        break;
                                    }
                                }
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
                    updateRecentlyUserTable();
                    System.out.println("DEVICE DETAILS :\n"
                            + "\nBRAND : " + Build.BRAND
                            + "\nMODEL : " + Build.MODEL
                            + "\nVERIONS SDK_INT : " + Build.VERSION.SDK_INT
                            + "\nVERIONS PRODUCT : " + Build.PRODUCT
                            + "\nSERIAL : " + Build.SERIAL
                            + "\nID : " + Build.VERSION.RELEASE
                            + "\nMANUFACTURER : " + Build.MANUFACTURER);

                    build_SERIAL = Build.SERIAL;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    build_SERIAL=Build.SERIAL;
                    fullscreen_content_login_controls_horizontal.setVisibility(View.GONE);
                    fullscreen_content_info_controls_horizontal.setVisibility(View.VISIBLE);

                    if(userName!=backupUserName)
                    {
                        sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String query = "INSERT INTO Login_Info (UserName, Mobile_Serial_Number, Start_Time, End_Time, Brand, Mobile_Name, Version) VALUES ('" + userName + "', '" + Build.SERIAL + "', '" + sdf.format(new Date()) + "', 'LOCKED', '" + Build.BRAND + "', '" + Build.MODEL + "', '" + Build.VERSION.RELEASE + "')";
                        databaseMethods.insertUpdateValue(query);
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

                    String query = "UPDATE `login_info` SET `End_Time` = \"" + sdf.format(new Date()) + "\" WHERE `Mobile_Serial_Number`=\"" + Build.SERIAL + "\" AND `UserName`=\"" + userName + "\" AND `End_Time`=\"LOCKED\"";
                    System.out.println(query);
                    databaseMethods.insertUpdateValue(query);
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
                String lastUser = "";
                TextView table_User_Name = (TextView) findViewById(R.id.table_User_Name);
                TextView table_Separator = (TextView) findViewById(R.id.table_Separator);
                TextView table_Start_Time1 = (TextView) findViewById(R.id.table_Start_Time1);
                TextView table_Separator1 = (TextView) findViewById(R.id.table_Separator1);
                TextView table_End_Time1 = (TextView) findViewById(R.id.table_End_Time1);

                table_User_Name.setText("");
                table_Separator.setText("");
                table_Start_Time1.setText("");
                table_Separator1.setText("");
                table_End_Time1.setText("");
                try
                {
                    System.out.println("SELECT UserName, Start_Time, End_Time FROM `login_info` WHERE `Mobile_Serial_Number`=\"" + Build.SERIAL + "\" ORDER BY Login_Index DESC LIMIT 5");
                    resultSet = databaseMethods.executeQuery("SELECT UserName, Start_Time, End_Time FROM `login_info` WHERE `Mobile_Serial_Number`=\"" + Build.SERIAL + "\" ORDER BY Login_Index DESC LIMIT 5");
                    while (resultSet.next())
                    {
                        ResultSet resultSet1 = databaseMethods.executeQuery("SELECT * FROM users WHERE Username='" + resultSet.getString(1) + "'");
                        while (resultSet1.next())
                        {
                            System.out.println(lastUser+"\t"+resultSet.getString(2)+"\t"+resultSet.getString(3));
                            lastUser = resultSet1.getString(2) + " " + resultSet1.getString(3);
                            table_User_Name.setText(table_User_Name.getText() + lastUser + " \t\n ");
                            table_Separator.setText(table_Separator.getText() + " |\t\n ");
                            table_Start_Time1.setText(table_Start_Time1.getText() + resultSet.getString(2) + "\t\n ");
                            table_Separator1.setText(table_Separator1.getText() + " |\t\n ");
                            table_End_Time1.setText(table_End_Time1.getText() + resultSet.getString(3) + "\t\n ");
                        }
                    }
                    resultSet.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
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

    @Override
    public void onBackPressed(){}

    public void resetFields()
    {
        editText_username.setText("");
        editText_password.setText("");
        editText_confirm_password.setText("");
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

    class MyTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            bringApplicationToFront();
        }
    }
}