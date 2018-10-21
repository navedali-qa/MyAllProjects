package com.example.navedali.myapplication;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;

public class ReceiveUninstallNewService extends Service {
    public ReceiveUninstallNewService()
    {
    }
boolean installed= true;
    @Override
    public int onStartCommand(Intent intent1, int flags, int startId)
    {
        onTaskRemoved(intent1);
        File directory = getApplicationContext().getExternalFilesDir(null);
        Toast.makeText(getApplicationContext(),"THIS IS A New SERVICE", Toast.LENGTH_SHORT).show();

///storage/emulated/0/Android
        /*String[] listOfFiles = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS).list();
        String allFile="";
        for(String file : listOfFiles)
        {
            allFile=allFile+"\n"+file;
        }
        Toast.makeText(getApplicationContext(),allFile, Toast.LENGTH_SHORT).show();*/
        /*System.out.println(directory.getAbsolutePath());
        if(installed)
        {


            File file = new File("/storage/emulated/0/Android/Download/abc.apk");
            Uri fileUri = Uri.fromFile(file);
            if (Build.VERSION.SDK_INT >= 24) {
                fileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName(),
                        file);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.setDataAndType(fileUri, "application/vnd.android" + ".package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getApplicationContext().startActivity(intent);
            installed=false;
        }*/

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
