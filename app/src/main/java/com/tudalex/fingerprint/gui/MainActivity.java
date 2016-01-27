package com.tudalex.fingerprint.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.tudalex.fingerprint.ContextAwareService;
import com.tudalex.fingerprint.R;
import com.tudalex.fingerprint.UpdateService;
import com.tudalex.fingerprint.db.*;
import com.tudalex.fingerprint.db.Application;

import java.util.List;

import co.uk.rushorm.core.RushSearch;

public class MainActivity extends Activity {

    static String TAG = "Main";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), WifiGui.class));
            return true;
        }
        if (id == R.id.facts_list) {
            startActivity(new Intent(getApplicationContext(), FactList.class));
            return true;
        }

        if (id == R.id.context_list) {
            startActivity(new Intent(getApplicationContext(), ContextList.class));
            return true;
        }

        if (id == R.id.enforce) {
            ContextAwareService.enforce(this);
        }

        if (id == R.id.update_policy) {
            UpdateService.startUpdate(this);
        }

        if (id == R.id.refresh) {
            startService(new Intent("fullrefresh",
                    null,
                    getApplicationContext(),
                    ContextAwareService.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
