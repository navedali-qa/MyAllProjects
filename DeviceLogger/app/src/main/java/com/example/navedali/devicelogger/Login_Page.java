package com.example.navedali.devicelogger;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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

    //Custom Variable
    private Timer timer;
    private  MyTimerTask myTimerTask;
    PolicyManager policyManager;

    public EditText editText_username;
    public EditText editText_password;
    public Button buttonLogin;
    public Button buttonProceed;
    public Button buttonLogout;

    //table details
    public TableLayout tableLayout_Mobile_Details;
    public TextView table_Name;
    public TextView table_Model;
    public TextView table_Version;
    public TextView table_S_no;
    public TextView table_IMEI_nu;
    public TextView table_start_Time;

    //Time Text View
    public TextView textView_Logged_User;

    //Logout process
    public TextView textView_confirm_password;
    public EditText editText_confirm_password;

    //FRAMES
    public LinearLayout fullscreen_content_login_controls_horizontal;
    public LinearLayout fullscreen_content_info_controls_horizontal;
    public LinearLayout fullscreen_content_logout_controls_horizontal;
    public LinearLayout fullscreen_content_admin_controls_horizontal;

    //DATABASE VARIABLES:
    Connection con;
    Statement statement;
    ResultSet resultSet;
    //String serverUrl="192.168.0.105:3306";
    String serverUrl="192.168.14.148:3306";
    String database="360_logica_mobile_logger";
    String userName="";
    String logged_UserName="";
    String userPassword="";
    String build_SERIAL="";
    PreparedStatement preparedStatement;

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
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonProceed = (Button) findViewById(R.id.buttonProceed);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        //table details
        tableLayout_Mobile_Details = (TableLayout) findViewById(R.id.tableLayout_Mobile_Details);
        table_Name = (TextView) findViewById(R.id.table_Name);
        table_Model = (TextView) findViewById(R.id.table_Model);
        table_Version = (TextView) findViewById(R.id.table_Version);
        table_S_no = (TextView) findViewById(R.id.table_S_no);
        table_IMEI_nu = (TextView) findViewById(R.id.table_IMEI_nu);
        table_start_Time = (TextView) findViewById(R.id.table_start_Time);
        textView_Logged_User = (TextView) findViewById(R.id.textView_Logged_User);

        //Logout process
        textView_confirm_password = (TextView) findViewById(R.id.textView_confirm_password);
        editText_confirm_password = (EditText) findViewById(R.id.editText_confirm_password);

        //FRAMES
        fullscreen_content_login_controls_horizontal = (LinearLayout) findViewById(R.id.fullscreen_content_login_controls_horizontal);
        fullscreen_content_info_controls_horizontal = (LinearLayout) findViewById(R.id.fullscreen_content_info_controls_horizontal);
        fullscreen_content_logout_controls_horizontal = (LinearLayout) findViewById(R.id.fullscreen_content_logout_controls_horizontal);
        fullscreen_content_admin_controls_horizontal = (LinearLayout) findViewById(R.id.fullscreen_content_admin_controls_horizontal);

        boolean found=false;
        System.out.println("WORKING");
        System.out.println("SELECT * FROM login_info WHERE End_Time='LOCKED' AND Mobile_Serial_Number='" + Build.SERIAL + "'");
        try
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

            Class.forName("com.mysql.jdbc.Driver");
            con = (Connection) DriverManager.getConnection("jdbc:mysql://" + serverUrl + "/" + database, "root", "");

            statement = con.createStatement();
            resultSet = (ResultSet) statement.executeQuery("SELECT * FROM login_info WHERE End_Time='LOCKED' AND Mobile_Serial_Number='" + Build.SERIAL + "'");
            while (resultSet.next())
            {
                userName = resultSet.getString(2);
                found=true;
                break;
            }

            if(found)
            {
                resultSet = (ResultSet) statement.executeQuery("SELECT * FROM users WHERE Username='" + userName + "'");
                while (resultSet.next())
                {
                    logged_UserName = resultSet.getString(2) + " " + resultSet.getString(3);
                    userPassword = resultSet.getString(5);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if(found)
        {
            textView_Logged_User.setText("Logged in User : " + logged_UserName+"\n\n");
            fullscreen_content_logout_controls_horizontal.setVisibility(View.VISIBLE);
        }
        else
        {
            fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
        }
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

    /*@SuppressLint("InlinedApi")
    private void show()
    {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }*/

    private void delayedHide(int delayMillis)
    {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
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
                if(timer==null)
                {
                    myTimerTask = new MyTimerTask();
                    timer = new Timer();
                    timer.schedule(myTimerTask, 0);
                }
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
                if(timer==null)
                {
                    myTimerTask = new MyTimerTask();
                    timer = new Timer();
                    timer.schedule(myTimerTask, 0);
                }
                break;
            case R.id.buttonLogin:
                loginToAppFunctionality();
                if(timer==null)
                {
                    myTimerTask = new MyTimerTask();
                    timer = new Timer();
                    timer.schedule(myTimerTask, 0);
                }
                break;

            case R.id.buttonProceed:
                proceedButtonFunctionality();
                if(timer==null)
                {
                    myTimerTask = new MyTimerTask();
                    timer = new Timer();
                    timer.cancel();
                }
                break;

            case R.id.buttonLogout:
                logoutButtonFunctionality();
                break;
        }
    }

    public void loginToAppFunctionality()
    {
        boolean loginAdmin = false;
        boolean loginUser = false;
            if(editText_username.getText().toString().equals("aa") && editText_password.getText().toString().equals("aa"))
            {
                loginAdmin = true;
            }
            else
            if(editText_username.getText().toString().equals("bb") && editText_password.getText().toString().equals("bb"))
            {
                userPassword="bb";
                loginUser = true;
            }
            else
            if(editText_username.getText().toString().equals("") || editText_password.getText().toString().equals(""))
            {
                Toast.makeText(Login_Page.this, "Enter username/password", Toast.LENGTH_SHORT).show();
                editText_username.setText("");
                editText_password.setText("");
            }
            else
            {
                try
                {
                    Class.forName("com.mysql.jdbc.Driver");
                    System.out.println("SERVER : " + "jdbc:mysql://" + serverUrl + "/" + database);
                    con = (Connection) DriverManager.getConnection("jdbc:mysql://" + serverUrl + "/" + database, "root", "");

                    statement = con.createStatement();

                    resultSet = (ResultSet) statement.executeQuery("SELECT * FROM Admin WHERE Admin_UserName='" + editText_username.getText().toString().trim() + "' AND Admin_Password='" + editText_password.getText().toString() + "'");
                    while (resultSet.next()) {
                        if (editText_password.getText().toString().equals(resultSet.getString(3))) {
                            loginAdmin = true;
                        }
                        break;
                    }
                    if (!loginAdmin) {
                        resultSet = (ResultSet) statement.executeQuery("SELECT * FROM users WHERE Username='" + editText_username.getText().toString() + "' AND Password='" + editText_password.getText().toString() + "'");
                        while (resultSet.next()) {
                            if (editText_password.getText().toString().equals(resultSet.getString(5))) {
                                logged_UserName = resultSet.getString(2) + " " + resultSet.getString(3);
                                userName = resultSet.getString(4);
                                userPassword = resultSet.getString(5);
                                loginUser = true;
                            }
                            break;
                        }
                    }
                }
                catch(Exception e)
                {
                    Toast.makeText(Login_Page.this, "No internet connection!", Toast.LENGTH_SHORT).show();
                }

                if (loginAdmin)
                {
                    fullscreen_content_login_controls_horizontal.setVisibility(View.GONE);
                    fullscreen_content_admin_controls_horizontal.setVisibility(View.VISIBLE);
                    timer = new Timer();
                    timer.cancel();
                }
                else
                if (loginUser)
                {
                    System.out.println("DEVICE DETAILS :\n"
                            + "\nBRAND : " + Build.BRAND
                            + "\nMODEL : " + Build.MODEL
                            + "\nVERIONS SDK_INT : " + Build.VERSION.SDK_INT
                            + "\nVERIONS PRODUCT : " + Build.PRODUCT
                            + "\nSERIAL : " + Build.SERIAL
                            + "\nID : " + Build.VERSION.RELEASE
                            + "\nMANUFACTURER : " + Build.MANUFACTURER);

                    table_Name.setText(Build.BRAND);
                    table_Model.setText(Build.MODEL);
                    table_Version.setText(Build.VERSION.RELEASE);
                    build_SERIAL = Build.SERIAL;
                    table_S_no.setText(build_SERIAL);
                    table_IMEI_nu.setText("XXXX");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    table_start_Time.setText(sdf.format(new Date()));
                    build_SERIAL=Build.SERIAL;
                    fullscreen_content_login_controls_horizontal.setVisibility(View.GONE);
                    fullscreen_content_info_controls_horizontal.setVisibility(View.VISIBLE);
                    try
                    {
                        sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        con=(Connection) DriverManager.getConnection("jdbc:mysql://"+serverUrl+"/"+database,"root","");
                        String query = "INSERT INTO Login_Info (UserName, Mobile_Serial_Number, Start_Time, End_Time, Brand, Mobile_Name, Version) VALUES ('"+userName+"', '"+Build.SERIAL+"', '"+sdf.format(new Date())+"', 'LOCKED', '"+Build.BRAND+"', '"+Build.MODEL+"', '"+Build.VERSION.RELEASE+"')";
                        preparedStatement = con.prepareStatement(query);

                        preparedStatement.execute();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(Login_Page.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    editText_username.setText("");
                    editText_password.setText("");
                }
            }
        }

    public void proceedButtonFunctionality()
    {
        textView_Logged_User.setText("Logged in User : " + logged_UserName+"\n\n");
        fullscreen_content_info_controls_horizontal.setVisibility(View.GONE);
        fullscreen_content_logout_controls_horizontal.setVisibility(View.VISIBLE);
        editText_confirm_password.setText("");
        myTimerTask = new MyTimerTask();
        timer = new Timer();
        timer.cancel();
        moveTaskToBack(true);

    }

    public void logoutButtonFunctionality()
    {
        if (editText_confirm_password.getText().toString().equals(userPassword))
        {
            myTimerTask = new MyTimerTask();
            timer = new Timer();
            timer.schedule(myTimerTask, 0);
            editText_username.setText("");
            editText_password.setText("");
            editText_confirm_password.setText("");
            fullscreen_content_logout_controls_horizontal.setVisibility(View.GONE);
            fullscreen_content_login_controls_horizontal.setVisibility(View.VISIBLE);
            try
            {
                Class.forName("com.mysql.jdbc.Driver");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                String query = "UPDATE `login_info` SET `End_Time` = \"" + sdf.format(new Date()) + "\" WHERE `Mobile_Serial_Number`=\"" + Build.SERIAL + "\" AND `UserName`=\"" + userName + "\" AND `End_Time`=\"LOCKED\"";
                System.out.println(query);
                PreparedStatement preparedStmt = con.prepareStatement(query);
                preparedStmt.executeUpdate();

                con.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println(e);
            }
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
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        if (timer == null)
        {
            myTimerTask = new MyTimerTask();
            timer = new Timer();
            timer.schedule(myTimerTask, 10, 10);
        }

        super.onPause();
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

    @Override
    public void onBackPressed(){}

    class MyTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            bringApplicationToFront();
        }
    }
}
