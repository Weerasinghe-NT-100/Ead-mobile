package com.example.etrainbooking.ReservationController;

import java.io.Serializable;

public class Reservation implements Serializable {
    private String Tname,Tnic,Train,Date,Start,Destination;
    private int Tid;

    public Reservation(){

    }

    public Reservation(int tid, String tname, String tnic, String train, String date, String start,String destination) {
        Tid = tid;
        Tname=tname;
        Tnic=tnic;
        Train = train;
        Date = date;
        Start = start;
        Destination = destination;
    }

    public int getTid() {
        return Tid;
    }

    public void setTid(int tid) {
        Tid = tid;
    }

    public String getTname() {
        return Tname;
    }

    public void setTname(String tname) {
        Tname = tname;
    }

    public String getTnic() {
        return Tnic;
    }

    public void setTnic(String tnic) {
        Tnic = tnic;
    }

    public String getTrain() {
        return Train;
    }

    public void setTrain(String train) {
        Train = train;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date =date;
    }

    public String getStart() {
        return Start;
    }

    public void setStart(String start) {
        Start = start;
    }

    public String getDestination() {
        return Destination;
    }

    public void setDestination(String destination) {
        Destination = destination;
    }

}
