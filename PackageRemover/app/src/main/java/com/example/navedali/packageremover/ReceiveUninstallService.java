package com.example.navedali.packageremover;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class ReceiveUninstallService extends Service
{
    public ReceiveUninstallService()
    {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        onTaskRemoved(intent);
        Toast.makeText(getApplicationContext(),"THIS IS A SERVICE", Toast.LENGTH_SHORT).show();
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
