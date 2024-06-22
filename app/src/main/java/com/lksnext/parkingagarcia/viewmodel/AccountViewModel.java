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
    MutableLiveData<Boolean> success = new MutableLiveData<>(false);
    MutableLiveData<FirebaseUser> user = new MutableLiveData<>(null);

    public LiveData<String> getNameError() {
        return nameError;
    }

    public LiveData<String> getEmailError() {
        return emailError;
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
    
    public void updateUser(String username, String email) {
        boolean ret = setEmptyError(nameError, username, "Name is required");
        ret |= setEmptyError(emailError, email, "Email is required");
        if (ret) return;
        DataRepository.getInstance().updateUser(username, email, new Callback() {
            @Override
            public void onSuccess(Object ... args) {
                success.setValue(true);
                successMessage.setValue("User updated");
            }

            @Override
            public void onFailure(String error, String errorCode) {
                success.setValue(false);
                Log.d("AccountViewModel", "Error code: " + errorCode);
                if (error.contains("INVALID_NEW_EMAIL")) {
                    emailError.setValue("Invalid email");
                } else {
                    errorMessage.setValue(error);
                }
            }
        });
    }

    private boolean setEmptyError(MutableLiveData<String> field, String value, String errorMsg) {
        if (value == null || value.isEmpty()) {
            field.setValue(errorMsg);
            return true;
        } else {
            field.setValue(null);
            return false;
        }
    }
}
