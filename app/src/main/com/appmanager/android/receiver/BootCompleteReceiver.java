package com.appmanager.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appmanager.android.service.CheckAndInstallService;

/**
 * Created by maimuzo on 2014/08/30.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "start BootCompleteReceiver. this will call CheckAndInstallService.");
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, CheckAndInstallService.class));
        }
    }
}
