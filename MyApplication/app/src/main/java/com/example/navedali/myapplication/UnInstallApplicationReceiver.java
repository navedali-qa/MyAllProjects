package com.example.navedali.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class UnInstallApplicationReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String packageName = intent.getData().getEncodedSchemeSpecificPart();
        Toast.makeText(context,"APPLICATION UNINSTALL   "+packageName,Toast.LENGTH_SHORT).show();
    }
}
