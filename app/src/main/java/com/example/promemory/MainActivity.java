package com.example.promemory;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static ArrayAdapter<String> adapter;                //adapter for the list view
    static List<String> reminders = new ArrayList<>();  //list for the management of the reminders' view
    static MySQLiteHelper db = null;                    //reminders database
    static Switch showFavourites;                       //switch that shows only favourite reminders
    static boolean show;                                //bool for the management of the favourite reminders at the start

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //startActivity(new Intent(MainActivity.this, SplashActivity.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new MySQLiteHelper(this);      //creation of the database

        FloatingActionButton fab = findViewById(R.id.fab);          //floating button for a reminder addition
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddActivity.start(getApplicationContext(), db);
            }
        });

        ListView list = (ListView) findViewById(R.id.listReminders);    //matching the list object with the layout's ListView
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reminders);   //setting the adapter
        list.setAdapter(adapter);       //matching the list with the adapter
        showFavourites = findViewById(R.id.switchFavourite);            //matching the switch object with the layout's Switch
        if(!show) {                                                     //if show is false at the start
            updateList();                                               //shows directly all the reminders
            showFavourites.setChecked(false);                           //sets the switch off
        }
        else{                                                           //if show is true at the start
            favouriteList();                                            //shows directly all favourite reminders
            showFavourites.setChecked(true);                            //sets the switch on
        }
        //updateList();

        showFavourites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {        //this function manages the change of switch's state (on/off)
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(showFavourites.isChecked()){                                                         //if the switch turns on
                    favouriteList();                                                                    //shows favourite reminders
                    show = true;                                                                        //sets shows true for the next time that the app will be launched
                }
                else {                                                                                  //if the switch turns off
                    updateList();                                                                       //shows all reminders
                    show = false;                                                                       //sets shows false for the next time that the app will be launched
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {                             //this function manages the click on a single item of the list
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent cont = new Intent(MainActivity.this, ReminderActivity.class);     //creation of an Intent
                cont.putExtra("id", db.getAllReminders().get(position).getId());                 //putting the id of the selected reminder in an extra
                startActivity(cont);                                                                   //starting the activity that shows the details of the reminder
                ReminderActivity.setDatabase(db);                                                      //this function shares the database of this activity with the launched activity
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {                                 //with this function you can hold the reminder in order to delete it
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                new AlertDialog.Builder(MainActivity.this)                             //creation of the alert dialog
                        .setTitle("Delete reminder")
                        .setMessage("Are you sure to delete the reminder?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() { //positive answer
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //System.out.println(i);
                                int id = db.getAllReminders().get(position).getId();
                                Reminder newReminder = db.getReminder(id);                     //match a reminder object with the selected reminder
                                db.deleteReminder(newReminder);                                //deletes the reminder from the database
                                updateList();                                                  //notifies the changes
                            }
                        })
                        .setNegativeButton("NO",null)   //negative answer
                        .show();    //shows the alert dialog
                return true;
            }
        });
    }

    public static void updateList(){ //function that shows all the reminders
        reminders.clear();
        for(Reminder r : db.getAllReminders()) reminders.add(r.getTitle() + "\nExpires: " + r.getDeadline()); //shows only the title and the deadline
        adapter.notifyDataSetChanged();     //notifies the changes
    }

    public static void favouriteList(){ //function that shows favourite reminders
        reminders.clear();
        for(Reminder r : db.getAllReminders()){
            if(r.getFavourite() == 1) reminders.add(r.getTitle() + "\nExpires: " + r.getDeadline());        //adds the info only if it is a favourite
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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