package com.tudalex.fingerprint.db;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushDisableAutodelete;
import co.uk.rushorm.core.annotations.RushIgnore;
import co.uk.rushorm.core.annotations.RushList;

/**
 * Created by tudalex on 21/08/15.
 */
public class Context extends RushObject{
    public String name;
    public boolean active;

    @RushDisableAutodelete
    @RushList(classType = Fact.class)
    public List<Fact> facts;


    @RushList(classType = Rule.class)
    public List<Rule> rules;

    @RushDisableAutodelete
    @RushList(classType = Application.class)
    public List<Application> apps;

    @RushIgnore
    public static void create(String name) {
        Context context = new Context();
        context.name = name;
        context.rules = new ArrayList<>();
        context.apps = new ArrayList<>();
        context.facts = new ArrayList<>();
        for (String permission : XPrivacyHelper.getPermissionClasses()) {
            final Rule rule = new Rule();
            rule.category = permission;
            rule.active = true;
            context.rules.add(rule);
        }
        context.save();
    }

    @Override
    public String toString() {
        return name;
    }

    public Context() {};

}
