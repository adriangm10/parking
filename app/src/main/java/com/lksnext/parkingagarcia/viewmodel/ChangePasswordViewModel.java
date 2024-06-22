package com.lksnext.parkingagarcia.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingagarcia.data.DataRepository;
import com.lksnext.parkingagarcia.domain.Callback;

public class ChangePasswordViewModel extends ViewModel {
    MutableLiveData<String> newPasswordError = new MutableLiveData<>();
    MutableLiveData<String> confirmPasswordError = new MutableLiveData<>();
    MutableLiveData<String> errorMessage = new MutableLiveData<>();
    MutableLiveData<Boolean> success = new MutableLiveData<>(false);

    public LiveData<String> getNewPasswordError() {
        return newPasswordError;
    }

    public LiveData<String> getConfirmPasswordError() {
        return confirmPasswordError;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    private boolean setEmptyError(MutableLiveData<String> fieldError, String value, String errorMsg) {
        if (value.isEmpty()) {
            fieldError.setValue(errorMsg);
            return true;
        } else {
            fieldError.setValue(null);
            return false;
        }
    }

    public void changePassword(String newPassword, String confirmPassword) {
        boolean ret = setEmptyError(newPasswordError, newPassword, "New password is required");
        ret |= setEmptyError(confirmPasswordError, confirmPassword, "Confirm password is required");
        if (ret) return;

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordError.setValue("Passwords do not match");
            return;
        }

        DataRepository.getInstance().changePassword(newPassword, new Callback() {
            @Override
            public void onSuccess(Object ... args) {
                success.setValue(true);
            }

            @Override
            public void onFailure(String error, String errorCode) {
                errorMessage.setValue(error);
            }
        });
    }
}
