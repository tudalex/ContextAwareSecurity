package com.tudalex.fingerprint.gui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tudalex.fingerprint.R;
import com.tudalex.fingerprint.Settings;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.core.RushCore;
import eu.chainfire.libsuperuser.Shell;


public class WifiGui extends Activity {

    static Boolean isSystemApp = true;
    static String DB_PATH = "/data/system/xprivacy/xprivacy.db";
    private static final String TAG = "Activity";

    String[] makeSystem = {"mount -o remount,rw /system",
            "cp /data/app/com.tudalex.fingerprint*.apk /system/app/com.tudalex.fingerprint.apk",
            "rm /data/app/com.tudalex.fingerprint*",
            "chmod 755 /system/app/com.tudalex.fingerprint*",
            "chown root.root /system/app/com.tudalex.*",
            "mount -o remount,ro /system",
            "reboot"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_fingerprint);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        final List<String> result = Shell.SU.run("ls /data/app/com.tudalex.fingerprint*");

        Log.i(TAG, "" + result.size());

        // Not working always
        isSystemApp = (getApplicationInfo().flags
                & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0;

        if (result.size() != 0)
            isSystemApp = false;
        if (!isSystemApp && false) {
            Shell.SU.run(makeSystem);
        } else {

            Log.i(TAG, Arrays.toString(Shell.SH.run("echo $USER").toArray()));
            Log.i(TAG, Arrays.toString(Shell.SH.run("ls -al /data/system/xprivacy").toArray()));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.network_fingerprint, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    @SuppressLint("NewApi")
    public static class PlaceholderFragment extends Fragment {

        TextView output;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_network_fingerprint, container, false);
            final Button button = (Button) rootView.findViewById(R.id.button);
            final Button save = (Button) rootView.findViewById(R.id.save);
            final EditText homeWifi = (EditText) rootView.findViewById(R.id.editText);


            homeWifi.setText(Settings.getSetting(getActivity().getApplicationContext(), "home"));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    homeWifi.setText(wifiInfo.getSSID().replace("\"", ""));
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Settings.setSetting(getActivity().getApplicationContext(), "home", homeWifi.getText().toString());
                }
            });

            return rootView;
        }
    }
}
