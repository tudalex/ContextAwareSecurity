package com.tudalex.fingerprint;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by tudal_000 on 5/6/2015.
 */
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
}
