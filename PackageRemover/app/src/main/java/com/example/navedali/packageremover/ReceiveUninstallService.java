package com.example.navedali.packageremover;

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
       Resources appR = getApplicationContext().getResources();
       System.out.println("APP : "+appR.getText(appR.getIdentifier("app_name","string",getApplicationContext().getPackageName())));
        //Toast.makeText(getApplicationContext(),"THIS IS A SERVICE", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY;
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
