package com.tudalex.fingerprint;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.tudalex.fingerprint.db.Application;
import com.tudalex.fingerprint.db.Fact;
import com.tudalex.fingerprint.db.Rule;
import com.tudalex.fingerprint.db.XPrivacyHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.uk.rushorm.android.AndroidInitializeConfig;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ContextAwareService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String UPDATE_ACTION = "com.tudalex.fingerprint.UPDATE_FACTS";


    private static boolean bootstrapped = false;
    private static final String TAG = "ContextAwareService";
    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method

    //Perioada de timp

    // Locatie, timp
    // locatie = gps, wifi,
    // broadcast reciever
    // alert manager pt timp

    public static void enforce(Context context) {
        Intent intent = new Intent(context, ContextAwareService.class);
        intent.setAction("enforce");
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY | super.onStartCommand(intent, flags, startId);
    }

    public static void updateContext(Context context, String name, Boolean status) {
        Intent intent = new Intent(context, ContextAwareService.class);
        intent.setAction("update");
        intent.putExtra("name", name);
        intent.putExtra("active", status);
        context.startService(intent);
    }

    public static void publishFact(Context context, String name, Boolean status) {
        Log.d(TAG, "publishing fact " + name);
        Intent intent = new Intent(context, ContextAwareService.class);
        intent.setAction("fact");
        intent.putExtra("name", name);
        intent.putExtra("active", status);
        Log.d(TAG, intent.toString());
        context.startService(intent);
    }

    @Override
    public void onCreate() {
//        refreshFactDB();
//        refreshAppDB();
        Log.d(TAG, "Service created");
        super.onCreate();
    }

    public ContextAwareService() {
        super("ContextAwareService");
    }

    private void refreshFactDB() {


        sendBroadcast(new Intent(UPDATE_ACTION));
    }

    private void fullRefresh() {
        for (Application app : new RushSearch().find(Application.class)) {
            app.delete();
        }
        for (Fact fact : new RushSearch().find(Fact.class)) {
            fact.delete();
        }

        refreshFactDB();
        refreshAppDB();

    }
    private void refreshAppDB() {
        PackageManager pm = getApplicationContext().getPackageManager();
        final int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        for (ApplicationInfo ai : pm.getInstalledApplications(PackageManager.GET_META_DATA)) {
            //Log.d(TAG, ai.toString());
            if ((ai.flags & mask) != 0)
                continue;

            final long existingApps = new RushSearch().whereEqual("uid", ai.uid).count(Application.class);
            Log.d(TAG, String.format("Existing apps for uid %d: %d", ai.uid, existingApps));

            if ( existingApps != 0)
                continue;
            Application app = new Application();
            app.name = ai.name;
            app.pack = ai.packageName;
            app.uid = ai.uid;
            app.save();
        }
    }

    private void propagateConstraints(Fact fact) {
        boolean modifiedContexts = false;
        for (com.tudalex.fingerprint.db.Context context : new RushSearch().find(com.tudalex.fingerprint.db.Context.class)) {

            if (context.facts != null ) {
                boolean value = true;
                for (Fact fact1 : context.facts)
                    value = value && fact1.active;
                if (value != context.active) {
                    context.active = value;
                    context.save();
                    modifiedContexts = true;
                }
                Log.d(TAG, String.format("Context %s state %b", context.name, context.active));
            }
        }
        if (modifiedContexts)
            enforceConstraints();

    }

    private void enforceConstraints() {
        Log.d(TAG, "Enforcing constraints");
        final List<com.tudalex.fingerprint.db.Context> contexts = new RushSearch().find(com.tudalex.fingerprint.db.Context.class);
        XPrivacyHelper.reset();
        for (com.tudalex.fingerprint.db.Context context : contexts) {
            if (!context.active)
                continue;
            for (Rule rule : context.rules) {
                for (Application app: context.apps) {
                    XPrivacyHelper.setAppClassPermission(app.uid, rule.category, rule.active);
                }
            }
        }
        flushCache();
    }



    // TODO: Move to XPrivacyHelper class
    private void flushCache() {
        Log.d(TAG, "Cache flushed");
        Intent intent = new Intent();
        intent.setAction("biz.bokhorst.xprivacy.action.FLUSH");
        startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d(TAG, intent.toString());
        if (intent != null && intent.getAction() != null) {
            Log.d(TAG, "Intent "+ intent.toString());
            final String action = intent.getAction();
            switch (action) {
                case "update":
                    Fact fact1 = new RushSearch()
                            .whereEqual("name", intent.getStringExtra("name"))
                            .and()
                            .whereEqual("provider", intent.getComponent().getPackageName())
                            .findSingle(Fact.class);

                    // Creating the fact if we don't know about it
                    if (fact1 == null) {
                        fact1 = new Fact();
                        fact1.provider = intent.getComponent().getPackageName();
                        fact1.name = intent.getStringExtra("name");
                    }
                    fact1.active = intent.getBooleanExtra("active", false);
                    fact1.save();
                    Log.d(TAG, "action: " + action + " " + intent.getStringExtra("name") + " " + intent.getBooleanExtra("active", false));
                    propagateConstraints(fact1);
                    break;
                case "fullrefresh":
                    fullRefresh();
                    break;
                case "refresh":
                    refreshFactDB();
                    refreshAppDB();
                    break;
                case "enforce":
                    refreshFactDB();
                    refreshAppDB();
                    enforceConstraints();
                    break;
                case "fact":
                    if (new RushSearch()
                            .whereEqual("name", intent.getStringExtra("name"))
                            .and()
                            .whereEqual("provider", intent.getComponent().getPackageName())
                            .count(Fact.class) > 0)
                        break;
                    Fact fact = new Fact();
                    fact.name = intent.getStringExtra("name");
                    fact.provider = intent.getComponent().getPackageName();
                    fact.active = intent.getBooleanExtra("active", false);
                    fact.save();
                    break;
            }
        }
    }



    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed");
        super.onDestroy();
    }
}
