package com.example.navedali.devicelogger;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SampleDeviceAdminReceiver extends DeviceAdminReceiver
{
	@Override
	public void onDisabled(Context context, Intent intent)
	{
		Toast.makeText(context, "Admin disabled", Toast.LENGTH_SHORT).show();
		super.onDisabled(context, intent);
	}

	@Override
	public void onEnabled(Context context, Intent intent)
	{
		Toast.makeText(context, "Admin activated", Toast.LENGTH_SHORT).show();
		super.onEnabled(context, intent);
	}

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent)
	{
		Toast.makeText(context, "disable dpm request", Toast.LENGTH_SHORT)
				.show();
		return super.onDisableRequested(context, intent);
	}
}