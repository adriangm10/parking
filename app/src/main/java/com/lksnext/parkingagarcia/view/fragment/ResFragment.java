package com.lksnext.parkingagarcia.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.lksnext.parkingagarcia.R;

public class ResFragment extends Fragment {
    public ResFragment() {
        // need empty constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservations, container, false);
    }
}