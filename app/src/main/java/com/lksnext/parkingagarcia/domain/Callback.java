package com.lksnext.parkingagarcia.domain;

public interface Callback {
    void onSuccess();
    void onFailure(String error, String errorCode);
}
