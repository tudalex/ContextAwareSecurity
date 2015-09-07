package com.tudalex.fingerprint.db;

import co.uk.rushorm.core.RushObject;

/**
 * Created by tudalex on 21/08/15.
 */
public class Fact extends RushObject {
    public String name;
    public String provider;
    public Boolean active;
    public long version;


    @Override
    public String toString() {
        return name;
    }

    @Override
    public Fact clone() {
        Fact fact = new Fact();
        fact.name = name;
        fact.provider = provider;
        fact.active = active;
        return fact;
    }

    public Fact(){}
}
