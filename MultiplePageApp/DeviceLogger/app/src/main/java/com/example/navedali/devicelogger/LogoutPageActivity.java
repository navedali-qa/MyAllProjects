package com.example.navedali.devicelogger;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
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

public class LogoutPageActivity extends AppCompatActivity implements View.OnClickListener
{
    private View mContentView;
    PolicyManager policyManager;
    private static final int UI_ANIMATION_DELAY = 5;
    private final Handler mHideHandler = new Handler();
    Variables variables;
    TextView textView_Logged_User;

    public EditText editText_confirm_password;
    public TextView textView_ProjectInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        policyManager = new PolicyManager(this);

        variables = new Variables();

        WifiManager wmgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wmgr.setWifiEnabled(true);

        setContentView(R.layout.activity_logout_page);

        editText_confirm_password = (EditText) findViewById(R.id.editText_confirm_password);
        textView_Logged_User = (TextView) findViewById(R.id.textView_Logged_User);
        textView_Logged_User.setText(Variables.loggedUserName+"\n\n");
        editText_confirm_password.setText("");
        updateProject();
    }

    @Override
    protected void onResume()
    {
        editText_confirm_password.setText("");
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        setContentView(R.layout.activity_logout_page);
        editText_confirm_password = (EditText) findViewById(R.id.editText_confirm_password);
        editText_confirm_password.setText("");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.buttonLogout:
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        hideKeypad();
                        startLoginPage();
                    }}.start();
                break;
        }

    }

    public void startLoginPage()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                finish();
                Intent intent = new Intent(getApplicationContext(), LoginPageActivity.class);
                startActivity(intent);
            }});
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
}
