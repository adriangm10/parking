package com.lksnext.parkingagarcia.domain;

public class Place {
    public enum Type {
        ELECTRIC, NORMAL, DISABLED, MOTORCYCLE
    }

    long id;
    Type type;

    public Place() {

    }

    public Place(long id, Type type) {
        this.id = id;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
