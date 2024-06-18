package com.lksnext.parkingagarcia.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lksnext.parkingagarcia.R;
import com.lksnext.parkingagarcia.view.activity.AccountActivity;
import com.lksnext.parkingagarcia.view.activity.ChangePasswordActivity;
import com.lksnext.parkingagarcia.view.activity.LoginActivity;
import com.lksnext.parkingagarcia.view.activity.MainActivity;
import com.lksnext.parkingagarcia.viewmodel.MainViewModel;

public class ProfileFragment extends Fragment {
    View view;
    MainViewModel mainViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        view.findViewById(R.id.btnLogout).setOnClickListener(v ->
            mainViewModel.logout()
        );

        view.findViewById(R.id.btnChangePassword).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btnAccount).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btnNotifications).setOnClickListener(v ->
            ((MainActivity) getActivity()).getNavController().navigate(R.id.notificationsFragment)
        );

        mainViewModel.getIsLoggedOut().observe(getViewLifecycleOwner(), isLoggedOut -> {
            if (isLoggedOut) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}