package com.example.promemory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//activity for the addiition of a reminder
public class AddActivity extends AppCompatActivity {

    static MySQLiteHelper database;     //shared database
    static long dateChange = 0;             //variable that saves the new date in a calendar view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Intent i = getIntent();             //getting the intent that launched this activity

        //matches all the objects with the layout's objects
        final EditText titolo = (EditText) findViewById(R.id.txtTitle);
        final EditText testo = (EditText) findViewById(R.id.txtText);
        final CalendarView scadenza = (CalendarView) findViewById(R.id.txtDeadline);
        final Switch preferito = (Switch) findViewById(R.id.switchFavourite);

        scadenza.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {              //listener that saves the change of the date in the calendar view
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {

                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dateChange = c.getTimeInMillis();                                               //this is the new date selected
            }
        });

        Button add = (Button) findViewById(R.id.btnAdd);                                       //button that sends the new reminder to the database
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //taking the info of the new reminder
                String title = titolo.getText().toString();
                String text = testo.getText().toString();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                if(dateChange != 0) scadenza.setDate(dateChange);                               //if you change the date sets the new date selected
                String deadline = dateFormat.format(scadenza.getDate());                        //converts from Date to String
                int favourite = 0;
                if(preferito.isChecked())favourite = 1;     //if the switch is turned on the reminder will be setted as a favourite

                database.addReminder(new Reminder(title, text, deadline, favourite));       //adding the new reminder to the database
                MainActivity.updateList();                                                  //notfies the changes to the list
                finish();                                                                   //closing this activity
            }
        });
    }

    public static void start(Context context, MySQLiteHelper db){       //this function allows you to share a database between two activities without sharing a single info of a reminder
        Intent i = new Intent(context, AddActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        database = db;
    }
}
