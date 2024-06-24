package com.lksnext.parkingagarcia.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingagarcia.data.DataRepository;
import com.lksnext.parkingagarcia.domain.Callback;

public class AccountViewModel extends ViewModel {
    MutableLiveData<String> nameError = new MutableLiveData<>();
    MutableLiveData<String> emailError = new MutableLiveData<>();
    MutableLiveData<String> errorMessage = new MutableLiveData<>();
    MutableLiveData<String> successMessage = new MutableLiveData<>();
    MutableLiveData<FirebaseUser> user = new MutableLiveData<>(null);

    public LiveData<String> getNameError() {
        return nameError;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
    }

    public LiveData<String> getEmailError() {
        return emailError;
    }

    public void retrieveUser() {
        DataRepository.getInstance().getUser(new Callback() {
            @Override
            public void onSuccess(Object... args) {
                user.setValue((FirebaseUser) args[0]);
            }

            @Override
            public void onFailure(String error, String errorCode) {
                errorMessage.setValue(error);
            }
        });
    }
    
    public void updateUser(String username, String email) {
        boolean tmp = false;
        if (username == null || username.isEmpty()){
            nameError.setValue("Name is required");
            tmp = true;
        }
        if (email == null || email.isEmpty()){
            nameError.setValue("Email is required");
            tmp = true;
        }
        if (tmp) return;

        DataRepository.getInstance().updateUser(username, email, new Callback() {
            @Override
            public void onSuccess(Object ... args) {
                successMessage.setValue(args[0].toString());
            }

            @Override
            public void onFailure(String error, String errorCode) {
                successMessage.setValue(null);
                Log.d("AccountViewModel", "Error updating user: " + error);
                if (error.contains("INVALID_NEW_EMAIL"))
                    emailError.setValue("Invalid email");
                else
                    errorMessage.setValue(error);
            }
        });
    }
}
