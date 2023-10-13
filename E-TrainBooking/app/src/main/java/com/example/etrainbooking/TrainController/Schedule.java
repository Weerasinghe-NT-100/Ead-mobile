package com.example.etrainbooking.TrainController;

import java.io.Serializable;

public class Schedule implements Serializable {

    private String id, start, end, arrivalTime, departureTime, date;
    private boolean isReserved;

    public Schedule(){

    }

    public Schedule(String id, String start, String end, String arrivalTime, String departureTime, String date, boolean isReversed) {
        id = id;
        start=start;
        end=end;
        arrivalTime = arrivalTime;
        departureTime = departureTime;
        date = date;
        isReserved = isReversed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        end=end;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        departureTime = departureTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        date=date;
    }

    public boolean getIsReversed() {
        return isReserved;
    }

    public void setIsReversed(boolean isReversed) {
        isReserved=isReversed;
    }
}
