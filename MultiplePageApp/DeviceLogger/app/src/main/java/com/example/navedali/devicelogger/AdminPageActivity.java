package com.example.navedali.devicelogger;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.navedali.devicelogger.OtherPages.DatabaseMethods;
import com.example.navedali.devicelogger.OtherPages.Variables;

public class AdminPageActivity extends AppCompatActivity   implements View.OnClickListener
{
    PolicyManager policyManager;
    private static final int UI_ANIMATION_DELAY = 5;
    private final Handler mHideHandler = new Handler();
    Variables variables;

    public TextView textView_ProjectInfo;

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mControlsView = findViewById(R.id.admin_Control);

        policyManager = new PolicyManager(this);
        variables = new Variables();

        WifiManager wmgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wmgr.setWifiEnabled(true);

        setContentView(R.layout.activity_admin_page);

        textView_ProjectInfo = (TextView) findViewById(R.id.textView_ProjectInfo);
        textView_ProjectInfo.setText(Variables.projectName);

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
        super.onPause();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.add_device_Info:
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        addDeviceInfo();
                        startLoginPage();
                    }});
                break;
            case R.id.activate_admin:
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        if (!policyManager.isAdminActive()) {
                            Intent activateDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, policyManager.getAdminComponent());
                            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "After activating admin, you will be able to block application uninstallation.");
                            startActivityForResult(activateDeviceAdmin, PolicyManager.DPM_ACTIVATION_REQUEST_CODE);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Admin already activated", Toast.LENGTH_SHORT).show();
                            startLoginPage();
                        }
                    }});
                break;
            case R.id.deactivate_admin:
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        if (policyManager.isAdminActive()) {
                            policyManager.disableAdmin();
                        }
                        startLoginPage();
                    }});
                break;
            case R.id.backButton:
                startLoginPage();
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

    public void addDeviceInfo()
    {
        String query = "Mobile_Name=" + Build.MODEL.replaceAll(" ","%20") +"&Brand="+Build.BRAND+ "&Mobile_Serial_Number="+ Build.SERIAL+"&Version=Android%20"+Build.VERSION.RELEASE +"&Screen_Size="+getScreenSize()+"%20Inches&Project=Other";
        variables.addDeviceDetailsApiResponse = getDatabaseMethods().doInBackground(Variables.apiUrl + "/DeviceLoggerAPI/Api/addDeviceDetails.php?" + query);
        Toast.makeText(AdminPageActivity.this, "Device Info Added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){}

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