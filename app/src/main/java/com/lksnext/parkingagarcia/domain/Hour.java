package com.lksnext.parkingagarcia.domain;

public class Hour {

    long horaInicio;
    long horaFin;

    public Hour() {

    }

    public Hour(long horaInicio, long horaFin) {
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public long getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(long horaInicio) {
        this.horaInicio = horaInicio;
    }

    public long getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(long horaFin) {
        this.horaFin = horaFin;
    }
}
