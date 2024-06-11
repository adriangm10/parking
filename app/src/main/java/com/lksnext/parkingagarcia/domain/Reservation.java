package com.lksnext.parkingagarcia.domain;

import java.util.Objects;

public class Reservation {
    String date, user, id;

    Place place;

    Hour hour;

    public Reservation() {

    }

    public Reservation(String date, String id, Place place, Hour hour) {
        this.date = date;
        this.place = place;
        this.hour = hour;
        this.id = id;
    }

    public Reservation(String date, String user, String id, Place place, Hour hour) {
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

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Hour getHour() {
        return hour;
    }

    public void setHour(Hour hour) {
        this.hour = hour;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
