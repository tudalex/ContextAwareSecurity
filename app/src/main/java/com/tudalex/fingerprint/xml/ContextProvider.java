package com.tudalex.fingerprint.xml;

/**
 * Created by tudalex on 25/01/16.
 */
public class ContextProvider {
    public String id = "";
    public String signature = "";
    public String uri = "";

    @Override
    public String toString() {
        return String.format("Context Provider: i %s s %s u %s", id, signature, uri);
    }
}
