package com.lksnext.parkingagarcia.domain;

public interface Callback {
    void onSuccess(Object ... args);
    void onFailure(String error, String errorCode);
}
