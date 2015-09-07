package com.tudalex.fingerprint.gui;

import android.app.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.tudalex.fingerprint.R;
import com.tudalex.fingerprint.db.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.uk.rushorm.core.RushSearch;

public class FactList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fact_list);
        final ExpandableListView listView = (ExpandableListView) findViewById(R.id.factList);

        HashMap<String, List<String>> facts = new HashMap<>();

        final List<Fact> knownFacts = new RushSearch().find(Fact.class);
        for (Fact fact : knownFacts) {
            if (facts.containsKey(fact.provider)) {
                facts.get(fact.provider).add(fact.name);
            } else {
                final ArrayList<String> factList = new ArrayList<>();
                factList.add(fact.name);
                facts.put(fact.provider, factList);
            }
        }

        listView.setAdapter(new ExpandableListAdapter(getApplicationContext(), new ArrayList<String>(facts.keySet()), facts));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
