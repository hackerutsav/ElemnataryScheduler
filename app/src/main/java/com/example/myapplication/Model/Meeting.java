package com.example.myapplication.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Meeting implements Serializable {
    String Agenda;
    String StartTime,EndTime;
    ArrayList<String> Attendees;

    public Meeting(String agenda, String startTime, String endTime,ArrayList<String> attendees) {
        Agenda = agenda;
        StartTime = startTime;
        EndTime = endTime;
        Attendees = attendees;
    }

    public String getAgenda() {
        return Agenda;
    }

    public ArrayList<String> getAttendees() {
        return Attendees;
    }

    public void setAttendees(ArrayList<String> attendees) {
        this.Attendees = attendees;
    }

    public void setAgenda(String agenda) {
        Agenda = agenda;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }
}
