package com.lksnext.parkingagarcia.view.fragment;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;

import com.google.android.material.button.MaterialButton;
import com.lksnext.parkingagarcia.Utils;
import com.lksnext.parkingagarcia.R;
import com.lksnext.parkingagarcia.domain.Place;
import com.lksnext.parkingagarcia.domain.Reservation;

public class EditFragment extends DialogFragment {

    private Reservation reservation;
    private View view;
    private MaterialButton selectedButton;

    private Place selectedPlace;

    public EditFragment() {
        // Required empty public constructor
    }

    public static EditFragment newInstance(Reservation reservation) {
        EditFragment fragment = new EditFragment();
        fragment.reservation = reservation;
        fragment.selectedPlace = reservation.getPlace();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit, container, false);

        GridLayout parkingContainer = view.findViewById(R.id.glParking);
        parkingContainer.setRowCount(Utils.getParking().length);
        parkingContainer.setColumnCount(Utils.getParking()[0].length);

        Utils.chargeParking(parkingContainer, v -> {
            MaterialButton btn = (MaterialButton) v;
            if (selectedButton != null) {
                selectedButton.setStrokeColor(ColorStateList.valueOf(getResources().getColor(com.google.android.material.R.color.material_grey_300, null)));
            }
            btn.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.orange, null)));
            selectedButton = btn;
            selectedPlace = Utils.getParking()[(btn.getId() - 1) / 10][(btn.getId() - 1) % 10];
        }, this);

        selectedButton = parkingContainer.findViewById((int) selectedPlace.getId());
        selectedButton.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.orange, null)));

        ((EditText) view.findViewById(R.id.dateText)).setText(reservation.getDate());
        ((EditText) view.findViewById(R.id.startTimeText)).setText(Utils.formatTimeToHHmm(reservation.getHour().getStartTime(), getResources().getConfiguration().getLocales().get(0)));
        ((EditText) view.findViewById(R.id.endTimeText)).setText(Utils.formatTimeToHHmm(reservation.getHour().getEndTime(), getResources().getConfiguration().getLocales().get(0)));

        return view;
    }
}