package com.example.promemory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Reminder { //class for the reminders

    private int id;
    private String title;       //title of the reminder
    private String text;        //description of the reminder
    private String deadline;    //deadline for the notification
    private String hour;
    private String createdOn;   //date of the creation
    private int favourite;      //flag for the management of favourite reminders
    private int completed;      //flag for the management of an expired reminder

    //constructors
    public Reminder(){}
    public Reminder(String titolo, String testo, String scadenza, String ora, int preferito) {
        super();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        this.title = titolo;
        this.text = testo;
        this.deadline = scadenza;
        this.hour = ora;
        this.createdOn = dateFormat.format((date));
        this.favourite = preferito;
        this.completed = 0;
    }

    //getters & setters
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getText() {
        return text;
    }
    public String getDeadline() {
        return deadline;
    }
    public String getHour() {
        return hour;
    }
    public String getCreatedOn() {
        return createdOn;
    }
    public int getFavourite() {
        return favourite;
    }
    public int getCompleted() { return completed; }

    public void setId(int id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public void setHour(String hour) { this.hour = hour; }
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }
    public void setCompleted(int completed) { this.completed = completed; }

    @Override
    public String toString() {
        if(completed == 0)
            return title + "\nExpires: " + deadline;
        else
            return title + "         Completed\nExpired: " + deadline;
    }
}
