package com.example.navedali.demoapplication;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.app.ActivityManager;
import android.net.wifi.WifiManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class LoginPage extends AppCompatActivity
{
    int day = 0;
    int hour = 0;
    int min = 0;
    int sec = -1;

    Thread t;
    ActivityManager activityManager;
    private static final boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 100;

    private static final int UI_ANIMATION_DELAY = 300;

    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_page);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        WifiManager wmgr = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wmgr.setWifiEnabled(true);
        activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        final TextView textView_username_text = (TextView) findViewById(R.id.textView_username_text);
        final EditText editText_username = (EditText) findViewById(R.id.editText_username);
        final TextView textView_password_text = (TextView) findViewById(R.id.textView_password_text);
        final EditText editText_password = (EditText) findViewById(R.id.editText_password);
        final TextView textView_Mobile_Details = (TextView) findViewById(R.id.textView_Mobile_Details);
        final Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        final Button buttonProceed = (Button) findViewById(R.id.buttonProceed);
        final Button buttonLogout = (Button) findViewById(R.id.buttonLogout);

        //table details
        final TableLayout tableLayout_Mobile_Details = (TableLayout)findViewById(R.id.tableLayout_Mobile_Details);
        final TextView table_Name = (TextView)findViewById(R.id.table_Name);
        final TextView table_Model = (TextView)findViewById(R.id.table_Model);
        final TextView table_Version = (TextView)findViewById(R.id.table_Version);
        final TextView table_S_no = (TextView)findViewById(R.id.table_S_no);
        final TextView table_IMEI_nu = (TextView)findViewById(R.id.table_IMEI_nu);
        final TextView table_start_Time = (TextView)findViewById(R.id.table_start_Time);

        //Time Text View
        final TextView textView_Time = (TextView)findViewById(R.id.textView_Time);


        //Logout process
        final TextView textView_confirm_password = (TextView) findViewById(R.id.textView_confirm_password);
        final EditText editText_confirm_password = (EditText) findViewById(R.id.editText_confirm_password);

        buttonLogin.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                System.out.println("USER DETAILS :\n"+editText_username.getText()+"\n"+editText_password.getText());
                if (editText_username.getText().toString().equals("a")&& editText_password.getText().toString().equals("a"))
                {
                    textView_username_text.setVisibility(View.GONE);
                    editText_username.setVisibility(View.GONE);
                    textView_password_text.setVisibility(View.GONE);
                    editText_password.setVisibility(View.GONE);
                    buttonLogin.setVisibility(View.GONE);
                    textView_Mobile_Details.setVisibility(View.VISIBLE);
                    buttonProceed.setVisibility(View.VISIBLE);
                    tableLayout_Mobile_Details.setVisibility(View.VISIBLE);
                    textView_Time.setText("HH : MM : SS");
                    //Toast.makeText(LoginPage.this, editText_username.getText(), Toast.LENGTH_SHORT).show();
                    System.out.println("DEVICE DETAILS :\n"
                                        +"\nBRAND : "+Build.BRAND
                                        +"\nMODEL : "+Build.MODEL
                                        +"\nVERIONS SDK_INT : "+Build.VERSION.SDK_INT
                                        +"\nVERIONS PRODUCT : "+Build.PRODUCT
                                        +"\nSERIAL : "+Build.SERIAL
                                        +"\nID : "+Build.VERSION.RELEASE
                                        +"\nMANUFACTURER : "+Build.MANUFACTURER);

                    table_Name.setText(Build.BRAND);
                    table_Model.setText(Build.MODEL);
                    table_Version.setText(Build.VERSION.RELEASE);
                    table_S_no.setText(Build.SERIAL);
                    table_IMEI_nu.setText("XXXX");
                    table_start_Time.setText(new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(Calendar.getInstance().getTime()));
                }
                else
                {
                    Toast.makeText(LoginPage.this,"Invalid credentials",Toast.LENGTH_SHORT).show();
                    editText_username.setText("");
                    editText_password.setText("");
                }
            }
        });

        buttonProceed.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                day = 0;
                hour = 0;
                min = 0;
                sec = -1;

                textView_Time.setVisibility(View.VISIBLE);
                buttonProceed.setVisibility(View.GONE);
                textView_Mobile_Details.setVisibility(View.GONE);
                tableLayout_Mobile_Details.setVisibility(View.GONE);
                buttonLogout.setVisibility(View.VISIBLE);
                textView_confirm_password.setVisibility(View.VISIBLE);
                editText_confirm_password.setVisibility(View.VISIBLE);
                table_Name.setText("XXXXX");
                table_Model.setText("XXXXX");
                table_Version.setText("XXXXX");
                table_S_no.setText("XXXXX");
                table_IMEI_nu.setText("XXXX");
                editText_username.setText("");
                editText_password.setText("");
                //Toast.makeText(LoginPage.this, editText_password.getText(), Toast.LENGTH_SHORT).show();

                t = new Thread()
                {

                    @Override
                    public void run()
                    {
                        try
                        {
                            while (!isInterrupted())
                            {
                                Thread.sleep(1000);
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        sec++;
                                        if (sec == 59) {
                                            sec = 0;
                                            min++;
                                        }
                                        if (min == 59)
                                        {
                                            min = 0;
                                            hour++;
                                        }
                                        if(hour == 24)
                                        {
                                            day++;
                                        }
                                        if(day>0)
                                        {
                                            textView_Time.setText(String.valueOf(day)+"D : "+String.valueOf(hour) + "H : " + String.valueOf(min) + "M : " + String.valueOf(sec)+"S");
                                        }
                                        else
                                        {
                                            textView_Time.setText(String.valueOf(hour) + "H : " + String.valueOf(min) + "M : " + String.valueOf(sec)+"S");
                                        }
                                    }
                                });
                            }
                        } catch (InterruptedException e) { }
                    }
                };
                t.start();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(editText_confirm_password.getText().toString().equals("a"))
                {
                    textView_Time.setVisibility(View.GONE);
                    buttonLogout.setVisibility(View.GONE);
                    buttonLogin.setVisibility(View.VISIBLE);
                    editText_password.setVisibility(View.VISIBLE);
                    textView_password_text.setVisibility(View.VISIBLE);
                    editText_username.setVisibility(View.VISIBLE);
                    textView_username_text.setVisibility(View.VISIBLE);
                    textView_confirm_password.setVisibility(View.GONE);
                    editText_confirm_password.setVisibility(View.GONE);
                    table_Name.setText("XXXXX");
                    table_Model.setText("XXXXX");
                    table_Version.setText("XXXXX");
                    table_S_no.setText("XXXXX");
                    table_IMEI_nu.setText("XXXX");
                    editText_username.setText("");
                    editText_password.setText("");
                    textView_Time.setText("HH : MM : SS");
                    day = 0;
                    hour = 0;
                    min = 0;
                    sec = -1;
                    t.interrupt();
                    editText_confirm_password.setText("");
                }
                else
                    if(editText_confirm_password.getText().toString().trim().equals(""))
                    {
                        Toast.makeText(LoginPage.this, "Please enter password to logout", Toast.LENGTH_SHORT).show();
                        editText_confirm_password.setText("");
                    }
                else
                {
                    Toast.makeText(LoginPage.this, "Please enter correct password to logout", Toast.LENGTH_SHORT).show();
                    editText_confirm_password.setText("");
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    protected boolean enabled = true;

    public void enable(boolean b)
    {
        enabled = b;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        return enabled ?
                super.dispatchTouchEvent(ev) :
                true;
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide()
    {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis)
    {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    //DISABLE ANDROID BUTTONS

    @Override
    public void onBackPressed()
    {
            Toast.makeText(LoginPage.this, "Button Disabled", Toast.LENGTH_SHORT).show();
            return;
    }

    @Override
    protected void onPause()
    {
            Toast.makeText(LoginPage.this, "Button Disabled", Toast.LENGTH_SHORT).show();
            super.onPause();
            activityManager.moveTaskToFront(getTaskId(), 0);
    }
}
