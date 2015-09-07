package com.tudalex.fingerprint.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tudalex.fingerprint.Application;
import com.tudalex.fingerprint.R;

import java.util.List;

import co.uk.rushorm.core.RushSearch;

public class ContextList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context_list);
        ListView listView = (ListView) findViewById(R.id.listView2);
        List<com.tudalex.fingerprint.db.Context> contexts = new RushSearch().orderAsc("name").find(com.tudalex.fingerprint.db.Context.class);
        listView.setAdapter(new ArrayAdapter<com.tudalex.fingerprint.db.Context>(getApplicationContext(), R.layout.status_item, R.id.statu_item_name,  contexts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final com.tudalex.fingerprint.db.Context context = getItem(position);
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.status_item, parent, false);
                }
                TextView tvName = (TextView) convertView.findViewById(R.id.statu_item_name);
                CheckBox cbActive = (CheckBox) convertView.findViewById(R.id.status_item_state);

                tvName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), ContextEditor.class);
                        intent.putExtra("context", context.getId());
                        startActivity(intent);
                    }
                });

                tvName.setLongClickable(true);
                tvName.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        (new AlertDialog.Builder(ContextList.this))
                                .setTitle("Delete "+ context.name + "?")
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ;
                                    }
                                })
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        context.delete();
                                    }
                                }).show();
                        return true;
                    }
                });
                tvName.setText(context.name);

                cbActive.setChecked(context.active);
                cbActive.setEnabled(false);
                return convertView;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_context_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            final EditText name = new EditText(this);
            AlertDialog alertDialog = new AlertDialog.Builder(ContextList.this)
                    .setTitle("New context")
                    .setMessage("")
                    .setView(name)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            com.tudalex.fingerprint.db.Context.create(name.getText().toString());
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
            alertDialog.show();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}
