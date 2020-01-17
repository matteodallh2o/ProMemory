package com.example.promemory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

//activity that shows the details of the selected reminder
public class ReminderActivity extends AppCompatActivity {

    static MySQLiteHelper database;         //shared database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        Intent i = getIntent();             //getting the intent that launched this activity

        final int id = i.getIntExtra("id", 0);      //gets the id of the selected reminder

        //matches all the objects with the layout's objects
        TextView titolo = findViewById(R.id.txtTitle);
        TextView testo = findViewById(R.id.txtText);
        TextView scadenza = findViewById(R.id.txtDeadline);
        TextView creazione = findViewById(R.id.txtCreatedOn);
        Switch preferito = findViewById(R.id.switchFavourite);

        titolo.setText(database.getReminder(id).getTitle());
        testo.setText(database.getReminder(id).getText());
        scadenza.setText(database.getReminder(id).getDeadline());
        creazione.setText(database.getReminder(id).getCreatedOn());
        if(database.getReminder(id).getFavourite() == 1) preferito.setChecked(true);    //sets the info with the attributes of the selected reminder

        Button modify = findViewById(R.id.btnModify);       //button that redirects you to the activity for the reminder's editing
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ModifyActivity.class);  //creation of the intent
                intent.putExtra("id", id);                                           //putting the id in an extra
                ModifyActivity.setDatabase(database);                                      //shares the database with the next activity
                startActivity(intent);                                                     //starts the next activity
                finish();                                                                  //closes this activity
            }
        });
    }

    public static void setDatabase(MySQLiteHelper db){      //this function allows you to share a database between two activities
        database = db;
    }
}
