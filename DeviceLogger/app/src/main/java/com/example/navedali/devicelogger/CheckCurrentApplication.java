package com.example.navedali.devicelogger;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import java.util.Iterator;
import java.util.List;

public class CheckCurrentApplication extends Service {
    public CheckCurrentApplication() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        List li = am.getRunningTasks(100);
        Iterator i = li.iterator();
        PackageManager pm = getApplicationContext().getPackageManager();
        while (i.hasNext()) {
            try {
                ActivityManager.RunningTaskInfo info = (ActivityManager.RunningTaskInfo)(i.next());
                String ac = info.baseActivity.getPackageName();
                CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(
                        ac, PackageManager.GET_META_DATA));
                System.out.println("APP : "+ c.toString());
            } catch (Exception e)
            {
                // Name Not FOund Exception
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {


        throw null;
    }
}
