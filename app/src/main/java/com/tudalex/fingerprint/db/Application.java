package com.tudalex.fingerprint.db;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushIgnore;

/**
 * Created by tudalex on 21/08/15.
 */
public class Application extends RushObject {
    public String name;
    public String pack;
    public int uid;

    @RushIgnore
    @Override
    public String toString() {

        return pack;
    }

    @Override
    public Application clone() {
        Application app = new Application();
        app.name = name;
        app.pack = pack;
        app.uid = uid;
        return app;
    }
}
