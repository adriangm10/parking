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
    MutableLiveData<String> errorMessage = new MutableLiveData<>();
    MutableLiveData<String> successMessage = new MutableLiveData<>();
    MutableLiveData<Boolean> success = new MutableLiveData<>(false);
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

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<FirebaseUser> getUser() {
        return user;
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
    
    public void updateUser(String username) {
        if (username == null || username.isEmpty()){
            nameError.setValue("Name is required");
            return;
        }

        DataRepository.getInstance().updateUser(username, new Callback() {
            @Override
            public void onSuccess(Object ... args) {
                success.setValue(true);
                successMessage.setValue("User updated");
            }

            @Override
            public void onFailure(String error, String errorCode) {
                success.setValue(false);
                Log.d("AccountViewModel", "Error updating user: " + error);
                errorMessage.setValue(error);
            }
        });
    }
}
