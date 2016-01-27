package com.tudalex.fingerprint.xml;

/**
 * Created by tudalex on 22/01/16.
 */

public class ContextRule {
    public String name = "";
    public Boolean value = false;

    @Override
    public String toString() {
        return String.format("%s %s", name, value);
    }
}
