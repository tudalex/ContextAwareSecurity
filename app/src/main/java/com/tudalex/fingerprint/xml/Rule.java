package com.tudalex.fingerprint.xml;

/**
 * Created by tudalex on 25/01/16.
 */
public class Rule {
    public String effect = "";
    public String resource = "";
    public String context = "";

    @Override
    public String toString() {
        return "Rule, effect: " + effect + " res: " + resource + " ctx: "+ context;
    }
}
