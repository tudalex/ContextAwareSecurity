package com.tudalex.fingerprint.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.tudalex.fingerprint.Application;
import com.tudalex.fingerprint.R;
import com.tudalex.fingerprint.db.Context;
import com.tudalex.fingerprint.db.Fact;
import com.tudalex.fingerprint.db.Rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

import co.uk.rushorm.android.RushAndroid;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;

public class ContextEditor extends Activity {
    private static String TAG = "ContextEditor";
    Context context;

    private Context getCurrentContext() {
        return context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_context_editor);


        //Tabs
        TabHost tabs = (TabHost) findViewById(R.id.tabHost);
        tabs.setup();

        tabs.addTab(tabs.newTabSpec("rules").setContent(R.id.rules).setIndicator("Rules"));
        tabs.addTab(tabs.newTabSpec("apps").setContent(R.id.apps).setIndicator("Apps"));
        tabs.addTab(tabs.newTabSpec("facts").setContent(R.id.facts).setIndicator("facts"));

        update();




    }

    private void update() {
        ListView lvRules = (ListView) findViewById(R.id.rules_list);
        ListView lvApps = (ListView) findViewById(R.id.apps_list);
        ListView lvFacts = (ListView) findViewById(R.id.facts_list);
        context = new RushSearch().whereId(getIntent().getStringExtra("context")).findSingle(Context.class);

        setTitle(context.name);

        Collections.sort(context.rules);
        lvRules.setAdapter(new ArrayAdapter<Rule>(getApplicationContext(), R.layout.status_item, context.rules) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final Rule rule = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.status_item, parent, false);
                }
                TextView tvName = (TextView) convertView.findViewById(R.id.statu_item_name);
                CheckBox cbActive = (CheckBox) convertView.findViewById(R.id.status_item_state);
                cbActive.setOnCheckedChangeListener(null);

                tvName.setText(rule.category);
                cbActive.setChecked(rule.active);

                cbActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Log.d(TAG, rule.category + " " + b);
                        rule.active = b;
                        Log.d(TAG, "" + rule.active);
                        rule.save();
                    }
                });
                return convertView;
            }
        });

        if (context.apps != null)
            lvApps.setAdapter(new ArrayAdapter<com.tudalex.fingerprint.db.Application>(getApplicationContext(), R.layout.list_entry_big, context.apps));
        if (context.facts != null)
            lvFacts.setAdapter(new ArrayAdapter<Fact>(getApplicationContext(), R.layout.status_item, context.facts ) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    final Fact fact = getItem(position);
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.status_item, parent, false);
                    }
                    TextView tvName = (TextView) convertView.findViewById(R.id.statu_item_name);
                    CheckBox cbActive = (CheckBox) convertView.findViewById(R.id.status_item_state);
                    cbActive.setOnCheckedChangeListener(null);

                    tvName.setText(fact.name);
                    cbActive.setChecked(fact.active);
                    cbActive.setEnabled(false);
                    tvName.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            (new AlertDialog.Builder(ContextEditor.this))
                                    .setTitle("Delete " + context.name + "?")
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ;
                                        }
                                    })
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            context.facts.remove(fact.clone());
                                            context.save();
                                            update();
                                        }
                                    }).show();
                            return true;
                        }
                    });
                    return convertView;
                }
            });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_context_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.add_app) {
            final List<com.tudalex.fingerprint.db.Application> apps = new RushSearch()
                    .orderAsc("pack").find(com.tudalex.fingerprint.db.Application.class);

            final AlertDialog alertDialog = new AlertDialog.Builder(ContextEditor.this)
                    .setTitle("Choose app")
                    .setSingleChoiceItems(new ArrayAdapter<com.tudalex.fingerprint.db.Application>(getApplicationContext(), R.layout.list_entry_big, apps), -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (context.apps == null) {
                                context.apps = new ArrayList<com.tudalex.fingerprint.db.Application>();
                            }
                            context.apps.add(apps.get(i).clone());
                            context.save();
                            dialogInterface.dismiss();
                            update();
                        }
                    }).show();

            return true;
        }

        if (id == R.id.add_fact) {
            final List<Fact> facts = new RushSearch().find(Fact.class);
            final AlertDialog alertDialog = new AlertDialog.Builder(ContextEditor.this)
                    .setTitle("Choose fact")
                    .setSingleChoiceItems(new ArrayAdapter<Fact>(getApplicationContext(), R.layout.list_entry_big, facts), -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (context.facts == null) {
                                context.facts = new ArrayList<Fact>();
                            }
                            context.facts.add(facts.get(i));
                            context.save();

                            dialogInterface.dismiss();
                            update();
                        }
                    }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        //context.save();
        super.onPause();
    }
}
