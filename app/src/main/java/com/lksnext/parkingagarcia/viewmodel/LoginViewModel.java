package com.lksnext.parkingagarcia.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingagarcia.data.DataRepository;
import com.lksnext.parkingagarcia.domain.Callback;

public class LoginViewModel extends ViewModel {
    MutableLiveData<Boolean> logged = new MutableLiveData<>(null);
    MutableLiveData<String> errorMessage = new MutableLiveData<>();
    MutableLiveData<String> emailError = new MutableLiveData<>();
    MutableLiveData<String> passwordError = new MutableLiveData<>();

    public LiveData<Boolean> isLogged(){
        return logged;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getEmailError() {
        return emailError;
    }

    public LiveData<String> getPasswordError() {
        return passwordError;
    }

    private void setErrorMessage(MutableLiveData<String> fieldError, String value, String errorMsg) {
        if (value.isEmpty()) {
            fieldError.setValue(errorMsg);
        } else {
            fieldError.setValue(null);
        }
    }

    public void loginUser(String email, String password) {
        setErrorMessage(emailError, email, "Email is required");
        setErrorMessage(passwordError, password, "Password is required");
        if (email.isEmpty() || password.isEmpty()) return;
        DataRepository.getInstance().login(email, password, new Callback() {
            @Override
            public void onSuccess() {
                logged.setValue(true);
            }

            @Override
            public void onFailure(String error, String errorCode) {
                logged.setValue(false);
                switch (errorCode) {
                    case "ERROR_INVALID_EMAIL":
                    case "ERROR_USER_NOT_FOUND":
                        emailError.setValue(error);
                        break;
                    case "ERROR_WRONG_PASSWORD":
                        passwordError.setValue(error);
                        break;
                    default:
                        errorMessage.setValue(error);
                        break;
                }
            }
        });
    }
}

