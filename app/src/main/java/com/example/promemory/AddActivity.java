package com.example.promemory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

//activity for the addiition of a reminder
public class AddActivity extends AppCompatActivity {

    static MySQLiteHelper database;     //shared database
    static long dateChange = 0;         //variable that saves the new date in a calendar view
    static String DeadlineHour = "";         //variable to set the hour on the timePicker dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //setting the back button
        Toolbar reminder = findViewById(R.id.reminderToolbar);
        setSupportActionBar(reminder);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        Button time = findViewById(R.id.btnHour);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if(selectedHour < 10) DeadlineHour = "0" + selectedHour + ":" + selectedMinute;
                        if(selectedMinute < 10) DeadlineHour = selectedHour + ":0" + selectedMinute;
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
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

                Date current = new Date();
                String c = dateFormat.format(current);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(current);
                String hour = calendar.get(Calendar.HOUR_OF_DAY) + ":" +  calendar.get(Calendar.MINUTE);

                if(title.equals("")) {
                    Snackbar.make(findViewById(R.id.add_layout), "You have to put a title", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) { }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }

                else if(compareDate(deadline, c) == 0){
                    Snackbar.make(findViewById(R.id.add_layout), "The expiration date cannot be earlier than the creation date", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) { }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }

                else if(DeadlineHour.equals("")){
                    Snackbar.make(findViewById(R.id.add_layout), "You have to select an hour", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) { }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }

                else if (compareDate(deadline, c) == - 1 && compareHour(DeadlineHour, hour) == 0){
                    Snackbar.make(findViewById(R.id.add_layout), "You can't travel to the past", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) { }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }

                else {
                    database.addReminder(new Reminder(title, text, deadline, DeadlineHour, favourite));       //adding the new reminder to the database
                    MainActivity.updateList();                                                  //notifies the changes to the list
                    finish();                                                                   //closing this activity
                }
            }
        });
    }

    public static void start(Context context, MySQLiteHelper db){       //this function allows you to share a database between two activities without sharing a single info of a reminder
        Intent i = new Intent(context, AddActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        database = db;
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    private int compareDate(String d1, String d2){
        String day1 = d1.substring(0,2);
        String day2 = d2.substring(0,2);
        String month1 = d1.substring(3,5);
        String month2 = d2.substring(3,5);
        String year1 = d1.substring(6,10);
        String year2 = d2.substring(6,10);

        if(Integer.parseInt(year1) < Integer.parseInt(year2))return 0;
        else if (Integer.parseInt(month1) < Integer.parseInt(month2)) return 0;
        else if (Integer.parseInt(day1) < Integer.parseInt(day2)) return 0;
        return 1;
    }

    private int compareHour(String h1, String h2){
        int hour1;
        int minute1;
        int hour2;
        int minute2;

        if (h1.charAt(1) == ':') {
            hour1 = Integer.parseInt(h1.substring(0, 1));
            minute1 = Integer.parseInt(h1.substring(2, 4));
        } else {
            hour1 = Integer.parseInt(h1.substring(0, 2));
            minute1 = Integer.parseInt(h1.substring(3, 5));
        }

        if (h2.charAt(1) == ':') {
            hour2 = Integer.parseInt(h2.substring(0, 1));
            minute2 = Integer.parseInt(h2.substring(2, 4));
        } else {
            hour2 = Integer.parseInt(h2.substring(0, 2));
            minute2 = Integer.parseInt(h2.substring(3, 5));
        }

        if(hour1 < hour2) return 0;
        else if(minute1 <= minute2) return 0;
        return 1;
    }
}
