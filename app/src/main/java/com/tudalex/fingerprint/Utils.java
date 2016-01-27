package com.tudalex.fingerprint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.tudalex.fingerprint.xml.PolicyParser;
import com.tudalex.fingerprint.xml.PolicySet;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import eu.chainfire.libsuperuser.Shell;

public class Utils {
    static List<String[]> readCSV(String path) {
        List<String[]> ret = new ArrayList<>();
        Log.i("CSV", "Accessing file " + path);
        List<String> rows = Shell.SU.run("cat " + path);
        for (String row : rows) {
            Log.i("CSV", row);
            ret.add(row.split(" "));
        }
        return ret;

    }

    private static final String POLICY_URL = "https://dl.dropboxusercontent.com/u/3817012/fingerprint/policy.xml";

    public static String getSignature(Context context, String name) {
        Signature[] sigs = new Signature[0];
        try {
            sigs = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return sigs[0].toCharsString();
    }
    public static void InstallAPK(String filename){
        File file = new File(filename);
        if(file.exists()){
            try {
                String command;
                command = "adb install -r " + filename;
//                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
//                proc.waitFor();
                Shell.SU.run(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static InputStream fetchURL(String url) {
        final HttpClient client = new DefaultHttpClient();
        final HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        InputStream in;
        try {
            response = client.execute(request);
            in = response.getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
            in = new ByteArrayInputStream("".getBytes());
        }


        return in;
    }

    public static void fetchFile(String url, String location) {
        Log.i("UTIL", "Fetching file from url: "+ url + " and stroing it at location " + location);
        try {
            URL u = new URL(url);
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + location));
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }

        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
    }

    public static void installApk(String url) {
        fetchFile(url, "dep.apk");
        InstallAPK(Environment.getExternalStorageDirectory() + "/dep.apk");
    }

    public static PolicySet parsePolicy() {
        PolicyParser pp = new PolicyParser();
        InputStream stream = fetchURL(POLICY_URL);

        try {
            return pp.parse(stream);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}

