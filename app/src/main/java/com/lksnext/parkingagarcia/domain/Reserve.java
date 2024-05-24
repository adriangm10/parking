package com.lksnext.parkingagarcia.domain;

public class Reserve {

    String date, user, id;

    Place place;

    Hour hour;

    public Reserve() {

    }

    public Reserve(String date, String user, String id, Place place, Hour hour) {
        this.date = date;
        this.user = user;
        this.place = place;
        this.hour = hour;
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Place getPlazaId() {
        return place;
    }

    public void setPlazaId(Place place) {
        this.place = place;
    }

    public Hour getHourStart() {
        return hour;
    }

    public void setHourStart(Hour hour) {
        this.hour = hour;
    }

    public Hour getHourEnd() {
        return hour;
    }

    public void setHourEnd(Hour hour) {
        this.hour = hour;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
