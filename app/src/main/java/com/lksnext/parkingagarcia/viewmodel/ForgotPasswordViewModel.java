package com.lksnext.parkingagarcia.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingagarcia.data.DataRepository;
import com.lksnext.parkingagarcia.domain.Callback;

public class ForgotPasswordViewModel extends ViewModel {
    MutableLiveData<String> emailError = new MutableLiveData<>();
    MutableLiveData<String> errorMessage = new MutableLiveData<>();
    MutableLiveData<Boolean> emailSent = new MutableLiveData<>(false);

    public LiveData<String> getEmailError() {
        return emailError;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getEmailSent() {
        return emailSent;
    }

    public void sendEmail(String email) {
        if (email.isEmpty()) {
            emailError.setValue("Email is required");
            return;
        }

        DataRepository.getInstance().sendEmail(email, new Callback() {
            @Override
            public void onSuccess(Object ...args) {
                errorMessage.setValue("Email sent");
                emailSent.setValue(true);
            }

            @Override
            public void onFailure(String error, String errorCode) {
                switch (errorCode) {
                    case "ERROR_INVALID_EMAIL":
                    case "ERROR_USER_NOT_FOUND":
                        emailError.setValue(error);
                        break;
                    default:
                        errorMessage.setValue(error);
                }
            }
        });
    }
}
