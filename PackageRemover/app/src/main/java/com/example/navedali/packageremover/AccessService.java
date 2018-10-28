package com.example.navedali.packageremover;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;

public class AccessService extends AccessibilityService {
    public AccessService() {
    }

    @Override
    public void onServiceConnected()
    {
        super.onServiceConnected();

        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        if(Build.VERSION.SDK_INT >=16)
        {
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        }
        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        System.out.println("ABC - " + event.getPackageName() + " -- " + event.getClassName());
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {
                ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
                ActivityInfo activityInfo = tryGetActivity(componentName);
                boolean isActivity = activityInfo != null;
                if (isActivity) {
                    System.out.println("CURRENT ACTIVITY : " + componentName.flattenToShortString());
                }
            }
        }
    }

    private ActivityInfo tryGetActivity(ComponentName name)
        {
            try {
                return getPackageManager().getActivityInfo(name,0);
            }
            catch (PackageManager.NameNotFoundException e)
            {
                return null;
            }
        }

    @Override
    public void onInterrupt() {

    }
}
