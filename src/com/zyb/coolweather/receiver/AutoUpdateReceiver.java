package com.zyb.coolweather.receiver;

import com.zyb.coolweather.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent in = new Intent(context,AutoUpdateService.class);
        context.startService(in);
	}

}
