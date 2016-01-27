package com.tudalex.fingerprint.xml;

import java.util.ArrayList;

/**
 * Created by tudalex on 25/01/16.
 */
public class PolicySet {
    public ArrayList<Policy> policies = new ArrayList<>();
    public ArrayList<ContextProvider> contextProviders = new ArrayList<>();
    public ArrayList<Context> contexts = new ArrayList<>();
    @Override
    public String toString() {
        return "Policy set: " + policies.toString();
    }
}
