package com.tudalex.fingerprint.db;

import java.util.Comparator;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushIgnore;

/**
 * Created by tudalex on 01/09/15.
 */
public class Rule extends RushObject implements Comparable<Rule>{
    public String category;
    public boolean active;


    @RushIgnore
    @Override
    public int compareTo(Rule rule) {
        return category.compareTo(rule.category);
    }
}
