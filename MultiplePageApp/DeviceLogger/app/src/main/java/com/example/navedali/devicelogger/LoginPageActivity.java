package com.example.navedali.devicelogger;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.PendingIntent;
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
import android.widget.TextView;

import com.example.navedali.devicelogger.OtherPages.DatabaseMethods;
import com.example.navedali.devicelogger.OtherPages.Variables;

import java.util.Timer;
import java.util.TimerTask;

public class LoginPageActivity extends AppCompatActivity implements View.OnClickListener
{
    private View mContentView;
    PolicyManager policyManager;
    private static final int UI_ANIMATION_DELAY = 5;
    private final Handler mHideHandler = new Handler();
    Variables variables;

    public EditText editText_username;
    public EditText editText_password;
    public TextView textView_ProjectInfo;

    private Timer timer;
    private MyTimerTask myTimerTask;

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

        variables = new Variables();

        WifiManager wmgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wmgr.setWifiEnabled(true);

        setContentView(R.layout.activity_login_page);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        mControlsView = findViewById(R.id.All_Control);

        editText_username = (EditText) findViewById(R.id.editText_username);
        editText_password = (EditText) findViewById(R.id.editText_password);

        editText_username.setText("");
        editText_password.setText("");
        updateUIFirstTime();
        updateProject();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mContentView.setVisibility(View.GONE);

        delayedHide(5);

        editText_username.setText("");
        editText_password.setText("");
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
        editText_username.setText("");
        editText_password.setText("");
    }

    @Override
    protected void onPause()
    {
        timerStart();
        super.onPause();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.buttonLogin:
                runOnUiThread(new Runnable()
                {
                      @Override
                        public void run()
                      {
                            hideKeypad();
                            timer = new Timer();
                            timer.cancel();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), UserDetailsInfoActivity.class);
                            startActivity(intent);
                      }});
                break;
        }
    }

    //HELPER METHODS

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

    public void updateProject()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                String query = "Mobile_Name=" + Build.MODEL.replaceAll(" ","%20")+"&Brand="+Build.BRAND+ "&Mobile_Serial_Number="+ Build.SERIAL+"&Version=Android%20"+Build.VERSION.RELEASE +"&Screen_Size="+getScreenSize()+"%20Inches";
                variables.getProjectNameResponse = getDatabaseMethods().doInBackground(Variables.apiUrl+"/DeviceLoggerAPI/Api/getProjectName.php?"+query);
                String project_Name = getDatabaseMethods().parseJSON(variables.getProjectNameResponse,"Project");
                if(project_Name=="" || project_Name.contains("Something wrong!!!"))
                {
                    Variables.projectName="Project : Other_";
                }
                else
                {
                    Variables.projectName = "Project : " + project_Name;
                }

                textView_ProjectInfo = (TextView) findViewById(R.id.textView_ProjectInfo);
                textView_ProjectInfo.setText(variables.projectName);
                variables.getProjectNameResponse = "";
            }});
    }

    public void updateUIFirstTime()
    {
        runOnUiThread(new Runnable()
        {
            boolean found=false;
            @Override
            public void run()
            {
                variables.updateUIFirstTimeResponse = getDatabaseMethods().doInBackground(Variables.apiUrl+"/DeviceLoggerAPI/Api/updateUIFirstTime.php/isUserLoggedIn/"+Build.SERIAL);
                if (variables.updateUIFirstTimeResponse.contains("User not logged in") || variables.updateUIFirstTimeResponse.contains("Something wrong!!!"))
                {
                    //DO NOTHING...
                }
                else
                {
                    finish();
                }
            }});
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

    public void timerStart()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                if (timer == null)
                {
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

        Intent notificationIntent = new Intent(this, LoginPageActivity.class);
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
