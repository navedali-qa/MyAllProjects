package com.example.navedali.devicelogger.OtherPages;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.navedali.devicelogger.Login_Page;

import java.util.TimerTask;

public class StartActivityOnBootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                || intent.getAction().equals(Intent.ACTION_SCREEN_ON)
                || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                || intent.getAction().equals(Intent.FLAG_RECEIVER_FOREGROUND)
                ||intent.getAction().equals(Intent.ACTION_USER_PRESENT)
                ||intent.getAction().equals(Intent.ACTION_USER_UNLOCKED)) {
            start_LoginPage(context);
        }
    }

    // Display Login screen
    private void start_LoginPage(Context context)
    {
        Intent mIntent = new Intent(context, Login_Page.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }

}
