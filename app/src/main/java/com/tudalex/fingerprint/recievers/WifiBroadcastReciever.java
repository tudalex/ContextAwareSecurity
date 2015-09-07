package com.tudalex.fingerprint.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.tudalex.fingerprint.ContextAwareService;
import com.tudalex.fingerprint.Settings;

public class WifiBroadcastReciever extends BroadcastReceiver {

    public static final String HOME = "home";

    public WifiBroadcastReciever() {
    }
    public static String TAG = "WifiReciever";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(TAG, intent.toString());
        final Bundle bundle = intent.getExtras();
        if (intent.getAction().equals(ContextAwareService.UPDATE_ACTION)) {
            ContextAwareService.publishFact(context, "home", false);
            return;
        }

//
//        for (String key : bundle.keySet()) {
//            Object value = bundle.get(key);
//            Log.d(TAG, String.format("%s %s (%s)", key,
//                    value.toString(), value.getClass().getName()));
//        }

        NetworkInfo nwkInfo = (NetworkInfo) bundle.get("networkInfo");
        final String ssid = nwkInfo.getExtraInfo().replace("\"", "");
        Log.d(TAG, ssid);

        Log.d(TAG, "Home setting: " + Settings.getSetting(context, HOME));
        Log.d(TAG, "Compare value: " + Settings.getSetting(context, HOME).equals(ssid));

        if (nwkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
            // Connected to home network
            ContextAwareService.updateContext(context, HOME, Settings.getSetting(context, HOME).equals(ssid));
        } else {
            ContextAwareService.updateContext(context, HOME, false);
        }


        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
