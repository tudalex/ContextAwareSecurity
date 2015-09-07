package com.tudalex.fingerprint.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tudalex.fingerprint.ContextAwareService;

public class PackageReciever extends BroadcastReceiver {
    public PackageReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        Intent intent1 = new Intent(context, ContextAwareService.class);
        intent1.setAction("refresh");
        context.startService(intent1);
    }
}
