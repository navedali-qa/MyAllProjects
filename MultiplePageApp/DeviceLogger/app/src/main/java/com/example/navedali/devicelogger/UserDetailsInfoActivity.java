package com.example.navedali.devicelogger;

import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.example.navedali.devicelogger.OtherPages.DatabaseMethods;
import com.example.navedali.devicelogger.OtherPages.Variables;

import java.util.Timer;
import java.util.TimerTask;

public class UserDetailsInfoActivity extends AppCompatActivity  implements View.OnClickListener
{
    Variables variables;

    public TextView textView_ProjectInfo;
    public TextView textView_Logged_User;

    private Timer timer;
    private UserDetailsInfoActivity.MyTimerTask myTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        variables = new Variables();

        WifiManager wmgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wmgr.setWifiEnabled(true);

        setContentView(R.layout.activity_user_details_info);
        textView_Logged_User = findViewById(R.id.textView_Logged_User);
        textView_Logged_User.setText(Variables.loggedUserName+"\n\n");
        updateProject();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStart()
    {
        super.onStart();
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
            case R.id.buttonProceed:
                runOnUiThread(new Runnable()
                {
                    @Override
                        public void run()
                        {
                            proceedButtonFunctionality();
                        }});
                break;
        }
    }

    //HELPER METHODS
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

    public void proceedButtonFunctionality()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                myTimerTask = new UserDetailsInfoActivity.MyTimerTask();
                timer = new Timer();
                timer.cancel();
                finish();
                Intent intent = new Intent(getApplicationContext(), LogoutPageActivity.class);
                startActivity(intent);
                Variables.back=true;
            }
        });
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
                    variables.projectName="Project : Other_";
                }
                else
                {
                    variables.projectName = "Project : " + project_Name;
                }

                textView_ProjectInfo = (TextView) findViewById(R.id.textView_ProjectInfo);
                textView_ProjectInfo.setText(variables.projectName);
                variables.getProjectNameResponse = "";
            }});
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
                    myTimerTask = new UserDetailsInfoActivity.MyTimerTask();
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

        Intent notificationIntent = new Intent(this, UserDetailsInfoActivity.class);
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
