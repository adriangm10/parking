package com.lksnext.parkingagarcia.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingagarcia.data.DataRepository;
import com.lksnext.parkingagarcia.domain.Callback;
import com.lksnext.parkingagarcia.domain.Hour;
import com.lksnext.parkingagarcia.domain.Place;
import com.lksnext.parkingagarcia.domain.Reservation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainViewModel extends ViewModel {
    MutableLiveData<List<Reservation>> userReservations = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<String> error = new MutableLiveData<>();
    MutableLiveData<String> successMessage = new MutableLiveData<>();
    MutableLiveData<List<Reservation>> reservationsForDate = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Reservation>> getUserReservations() {
        return userReservations;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<List<Reservation>> getReservationsForDate() {
        return reservationsForDate;
    }

    public void reserve(String date, String id, Place place, Date startDate, Date endDate) {
        Reservation r = new Reservation(date, id, place, new Hour(startDate, endDate));
        DataRepository.getInstance().reserve(r, new Callback() {
            @Override
            public void onSuccess(Object ... args) {
                userReservations.getValue().add(r);
                userReservations.setValue(userReservations.getValue());
                successMessage.setValue("Reservation successful");
            }

            @Override
            public void onFailure(String message, String errorCode) {
                error.setValue(message);
            }
        });
    }

    public void loadReservationsForDate(String date) {
        DataRepository.getInstance().getReservationsForDate(date, new Callback() {
            @Override
            public void onSuccess(Object ... args) {
                List<Reservation> rs = (List<Reservation>) args[0];
                Log.d("MainViewModel", "Reservations for date: " + rs.size());
                reservationsForDate.setValue(rs);
            }

            @Override
            public void onFailure(String message, String errorCode) {
                error.setValue(message);
            }
        });
    }

    public void loadUserReservations() {
        DataRepository.getInstance().getUserReservations(new Callback() {
            @Override
            public void onSuccess(Object ... args) {
                List<Reservation> rs = (List<Reservation>) args[0];
                Log.d("MainViewModel", "User reservations: " + rs);
                userReservations.setValue(rs);
            }

            @Override
            public void onFailure(String message, String errorCode) {
                error.setValue(message);
            }
        });
    }

    public void cancelUserReservation(Reservation r) {
        DataRepository.getInstance().cancelUserReservation(r, new Callback() {
            @Override
            public void onSuccess(Object ... args) {
                userReservations.getValue().remove(r);
                successMessage.setValue("Reservation canceled");
            }

            @Override
            public void onFailure(String message, String errorCode) {
                error.setValue(message);
            }
        });
    }
}
