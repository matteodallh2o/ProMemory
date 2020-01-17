package com.example.promemory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Switch;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//activity that allows you to edit a reminder
public class ModifyActivity extends AppCompatActivity {

    static MySQLiteHelper database;     //shared database
    static long dateChange = 0;             //variable that saves the new date in a calendar view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        Intent i = getIntent();             //getting the intent that launched this activity

        final int id = i.getIntExtra("id", 0);  //gets the id of the selected reminder

        //matches all the objects with the layout's objects
        final EditText titolo = findViewById(R.id.txtTitle);
        final EditText testo = findViewById(R.id.txtText);
        final CalendarView scadenza = findViewById(R.id.txtDeadline);
        final Switch preferito = findViewById(R.id.switchFavourite);

        if(!database.getReminder(id).getTitle().equals("")) titolo.setText(database.getReminder(id).getTitle());
        if(!database.getReminder(id).getText().equals("")) testo.setText(database.getReminder(id).getText());
        if(!database.getReminder(id).getDeadline().equals("")){
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = dateFormat.parse(database.getReminder(id).getDeadline());
                long d = date.getTime();
                scadenza.setDate(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (database.getReminder(id).getFavourite() == 1) preferito.setChecked(true);           //sets the editable info with the attributes of the selected reminder

        scadenza.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {              //listener that saves the change of the date in the calendar view
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {

                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dateChange = c.getTimeInMillis();                                               //this is the new date selected
            }
        });

        Button modify = findViewById(R.id.btnModify);                   //button that sends the changes to the database
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //taking the new info
                String title = titolo.getText().toString();
                String text = testo.getText().toString();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                if(dateChange != 0) scadenza.setDate(dateChange);                               //if you change the date sets the new date selected
                String deadline = dateFormat.format(scadenza.getDate());                        //converts from Date to String
                int favourite = 0;
                if(preferito.isChecked())favourite = 1; //if the switch is turned on the reminder will be setted as a favourite

                //changing the attributes of the selected reminder
                Reminder newReminder = database.getReminder(id);
                newReminder.setTitle(title);
                newReminder.setText(text);
                newReminder.setDeadline(deadline);
                newReminder.setFavourite(favourite);

                database.updateReminder(newReminder);   //updates the reminder in the database
                MainActivity.updateList();              //notfies the changes to the list
                finish();                               //closing this activity
            }
        });
    }

    public static void setDatabase(MySQLiteHelper db){      //this function allows you to share a database between two activities
        database = db;
    }
}