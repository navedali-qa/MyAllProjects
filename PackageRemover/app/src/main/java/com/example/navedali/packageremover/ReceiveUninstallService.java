package com.example.navedali.packageremover;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

public class ReceiveUninstallService extends Service
{
    public ReceiveUninstallService()
    {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        onTaskRemoved(intent);
        getApplicationName();
        //Toast.makeText(getApplicationContext(),"THIS IS A SERVICE", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;
    }

    public void getApplicationName()
    {
        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        am.killBackgroundProcesses("com.android.settings.applications");
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        Intent restartService = new Intent(getApplicationContext(),this.getClass());
        restartService.setPackage(getPackageName());
        startService(restartService);
        super.onTaskRemoved(rootIntent);
    }
}
