package com.example.navedali.demoapplication;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class PersistService extends Service
{
    private static final int INTERVAL = 2; // poll every 3 secs
    private static final String YOUR_APP_PACKAGE_NAME = "com.example.navedali.demoapplication";

    private static boolean stopTask;
    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate()
    {
        super.onCreate();

        stopTask = false;

        // Optional: Screen Always On Mode!
        // Screen will never switch off this way
        mWakeLock = null;

        // Start your (polling) task
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                // If you wish to stop the task/polling
                if (stopTask){
                    this.cancel();
                }
                ActivityManager activityManager;
                activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

                // The first in the list of RunningTasks is always the foreground task.
                ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
                String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();

                // Check foreground app: If it is not in the foreground... bring it!
                if (!foregroundTaskPackageName.equals(YOUR_APP_PACKAGE_NAME)){
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(YOUR_APP_PACKAGE_NAME);
                    startActivity(LaunchIntent);
                }
            }
        };
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(task, 0,INTERVAL);
    }

    @Override
    public void onDestroy(){
        stopTask = true;
        if (mWakeLock != null)
            mWakeLock.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
