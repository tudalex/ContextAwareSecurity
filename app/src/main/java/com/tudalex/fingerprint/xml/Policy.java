package com.tudalex.fingerprint.xml;

import java.util.ArrayList;

/**
 * Created by tudalex on 25/01/16.
 */
public class Policy {
    public String combine = "";
    public String target = "";
    public ArrayList<Rule> rules = new ArrayList<>();


    @Override
    public String toString() {
        return "Policy combine: "+ combine + " target: " + target + "\n" + rules.toString();
    }


}
