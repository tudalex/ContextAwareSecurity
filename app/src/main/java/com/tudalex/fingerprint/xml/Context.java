package com.tudalex.fingerprint.xml;

import java.util.ArrayList;

/**
 * Created by tudalex on 25/01/16.
 */
public class Context {
    public String name = "";
    public ArrayList<ContextRule> rules = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("Ctx %s %s", name, rules.toString());
    }
}
