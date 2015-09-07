package com.tudalex.fingerprint.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tudalex.fingerprint.ContextAwareService;

public class BootCompleteIntentReciever extends BroadcastReceiver {
    public BootCompleteIntentReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            ContextAwareService.enforce(context);
        }
    }
}
