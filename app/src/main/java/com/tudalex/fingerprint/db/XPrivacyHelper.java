package com.tudalex.fingerprint.db;

import android.util.Log;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class XPrivacyHelper {
    static String DB_PATH = "/data/system/xprivacy/xprivacy.db";
    static String TAG = "XPrivacyHelper";

    public static List<String[]> runQuery(String query) {
        List<String> results = Shell.SU.run(String.format("sqlite3 %s \"%s\";", DB_PATH, query));
        ArrayList<String[]> data = new ArrayList<>();
        for (String result : results) {
            data.add(result.split("|"));
        }
        return data;
    }

    public static String runQueryString(String query) {
        Log.d(TAG, query);
        List<String> results = Shell.SU.run(String.format("sqlite3 %s \"%s\";", DB_PATH, query));
        if (results != null)
            return Joiner.on("\n").join(results);
        return "";
    }

    public static String getPermissions(int uid) {
        final String response = runQueryString(String.format("select * from restriction where uid = '%d'", uid));
        Log.d(TAG, response);
        return response;
    }

    public static void backup() {

    }

    public static void restore() {

    }



    public static String[] getPermissionClasses() {
        final String response = runQueryString(String.format("select distinct restriction from restriction"));
        return response.split("\n");
    }

    public static void setAppClassPermission(int uid, String category, boolean active) {
        Log.d(TAG, String.format("UID: %d category: %s active: %b", uid, category, active));
        int restriction = 3;

        if (active)
            restriction = 2;

        String response = runQueryString(String.format("update restriction set restricted='%d' where uid= '%d' and method='' and restriction='%s';", restriction, uid, category));

        Log.d(TAG, response);

        // 1- (2 -2) + 2 = 3
        // 1 - (3-2) + 2 = 2

        restriction = 1 - (restriction-2) + 2;

        response = runQueryString(String.format("update restriction set restricted='%d' where uid= '%d' and method!='' and restriction='%s';", restriction, uid, category));
        Log.d(TAG, response);
    }

    public static void reset() {
        String response = runQueryString("update restriction set restricted='2' where method='';");
        Log.d(TAG, response);
        response = runQueryString("update restriction set restricted='3' where method!='';");
        Log.d(TAG, response);
    }

}
