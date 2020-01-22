package com.example.promemory;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper implements Serializable {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ReminderDB";

    // Reminders table name
    private static final String TABLE_REMINDERS = "reminders";

    // Reminders Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";
    private static final String KEY_DEADLINE = "deadline";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_CREATEDON = "createdOn";
    private static final String KEY_FAVOURITE = "favourite";

    private static final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_TEXT, KEY_DEADLINE, KEY_HOUR, KEY_CREATEDON, KEY_FAVOURITE};

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create reminder table
        String CREATE_BOOK_TABLE = "CREATE TABLE reminders ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, "+
                "text TEXT," +
                "deadline TEXT," +
                "hour TEXT," +
                "createdOn TEXT," +
                "favourite INTEGER)";

        // create reminders table
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older reminders table if existed
        db.execSQL("DROP TABLE IF EXISTS reminders");

        // create fresh reminders table
        this.onCreate(db);
    }

    public void addReminder(Reminder reminder){
        //for logging
        Log.d("addReminder", reminder.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, reminder.getTitle()); // get title
        values.put(KEY_TEXT, reminder.getText()); // get text
        values.put(KEY_DEADLINE, reminder.getDeadline()); // get deadline
        values.put(KEY_HOUR, reminder.getHour()); // get hour
        values.put(KEY_CREATEDON, reminder.getCreatedOn()); // get created on
        values.put(KEY_FAVOURITE, reminder.getFavourite()); // get favourite flag

        // 3. insert
        db.insert(TABLE_REMINDERS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Reminder getReminder(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_REMINDERS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build reminder object
        Reminder reminder = new Reminder();
        reminder.setId(Integer.parseInt(cursor.getString(0)));
        reminder.setTitle(cursor.getString(1));
        reminder.setText(cursor.getString(2));
        reminder.setDeadline(cursor.getString(3));
        reminder.setHour(cursor.getString(4));
        reminder.setCreatedOn(cursor.getString(5));
        reminder.setFavourite(Integer.parseInt(cursor.getString(6)));

        //log
        Log.d("getReminder("+id+")", reminder.toString());

        // 5. return reminder
        return reminder;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new LinkedList<Reminder>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_REMINDERS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build reminder and add it to list
        Reminder reminder = null;
        if (cursor.moveToFirst()) {
            do {
                reminder = new Reminder();
                reminder.setId(Integer.parseInt(cursor.getString(0)));
                reminder.setTitle(cursor.getString(1));
                reminder.setText(cursor.getString(2));
                reminder.setDeadline(cursor.getString(3));
                reminder.setHour(cursor.getString(4));
                reminder.setCreatedOn(cursor.getString(5));
                reminder.setFavourite(Integer.parseInt(cursor.getString(6)));

                // Add reminder to reminders
                reminders.add(reminder);
            } while (cursor.moveToNext());
        }

        Log.d("getAllReminders()", reminders.toString());

        // return reminders
        return reminders;
    }

    public int updateReminder(Reminder reminder) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("title", reminder.getTitle()); // get title
        values.put("text", reminder.getText()); // get text
        values.put("deadline", reminder.getDeadline()); // get deadline
        values.put("hour", reminder.getHour()); // get hour
        values.put("createdOn", reminder.getCreatedOn()); // get created on
        values.put("favourite", reminder.getFavourite()); // get favourite flag

        // 3. updating row
        int i = db.update(TABLE_REMINDERS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(reminder.getId()) }); //selection args

        // 4. close
        db.close();

        return i;
    }

    public void deleteReminder(Reminder reminder) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_REMINDERS, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(reminder.getId()) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteReminder", reminder.toString());
    }
}