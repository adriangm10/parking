package com.lksnext.parkingagarcia.view.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.lksnext.parkingagarcia.R;
import com.lksnext.parkingagarcia.domain.Hour;
import com.lksnext.parkingagarcia.domain.Place;
import com.lksnext.parkingagarcia.domain.Reservation;
import com.lksnext.parkingagarcia.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainFragment extends Fragment {
    Integer year, month, dayOfMonth, startHour, endHour, startMinute, endMinute;

    MainViewModel mainViewModel;

    Place[][] parking = {
            {new Place(1, Place.Type.ELECTRIC), new Place(2, Place.Type.ELECTRIC), new Place(3, Place.Type.ELECTRIC), new Place(4, Place.Type.ELECTRIC), new Place(5, Place.Type.ELECTRIC), new Place(6, Place.Type.ELECTRIC), new Place(7, Place.Type.ELECTRIC), new Place(8, Place.Type.ELECTRIC), new Place(9, Place.Type.ELECTRIC), new Place(10, Place.Type.ELECTRIC)},
            {new Place(11, Place.Type.DISABLED), new Place(12, Place.Type.NORMAL), new Place(13, Place.Type.NORMAL), new Place(14, Place.Type.NORMAL), new Place(15, Place.Type.NORMAL), new Place(16, Place.Type.NORMAL), new Place(17, Place.Type.NORMAL), new Place(18, Place.Type.NORMAL), new Place(19, Place.Type.NORMAL), new Place(20, Place.Type.DISABLED)},
            {new Place(21, Place.Type.DISABLED), new Place(22, Place.Type.NORMAL), new Place(23, Place.Type.NORMAL), new Place(24, Place.Type.NORMAL), new Place(25, Place.Type.NORMAL), new Place(26, Place.Type.NORMAL), new Place(27, Place.Type.NORMAL), new Place(28, Place.Type.NORMAL), new Place(29, Place.Type.NORMAL), new Place(30, Place.Type.DISABLED)},
            {new Place(31, Place.Type.DISABLED), new Place(32, Place.Type.NORMAL), new Place(33, Place.Type.NORMAL), new Place(34, Place.Type.NORMAL), new Place(35, Place.Type.NORMAL), new Place(36, Place.Type.NORMAL), new Place(37, Place.Type.NORMAL), new Place(38, Place.Type.NORMAL), new Place(39, Place.Type.NORMAL), new Place(40, Place.Type.DISABLED)},
            {new Place(41, Place.Type.DISABLED), new Place(42, Place.Type.NORMAL), new Place(43, Place.Type.NORMAL), new Place(44, Place.Type.NORMAL), new Place(45, Place.Type.NORMAL), new Place(46, Place.Type.NORMAL), new Place(47, Place.Type.NORMAL), new Place(48, Place.Type.NORMAL), new Place(49, Place.Type.NORMAL), new Place(50, Place.Type.DISABLED)},
            {new Place(51, Place.Type.DISABLED), new Place(52, Place.Type.NORMAL), new Place(53, Place.Type.NORMAL), new Place(54, Place.Type.NORMAL), new Place(55, Place.Type.NORMAL), new Place(56, Place.Type.NORMAL), new Place(57, Place.Type.NORMAL), new Place(58, Place.Type.NORMAL), new Place(59, Place.Type.NORMAL), new Place(60, Place.Type.DISABLED)},
            {new Place(61, Place.Type.DISABLED), new Place(62, Place.Type.NORMAL), new Place(63, Place.Type.NORMAL), new Place(64, Place.Type.NORMAL), new Place(65, Place.Type.NORMAL), new Place(66, Place.Type.NORMAL), new Place(67, Place.Type.NORMAL), new Place(68, Place.Type.NORMAL), new Place(69, Place.Type.NORMAL), new Place(70, Place.Type.DISABLED)},
            {new Place(71, Place.Type.DISABLED), new Place(72, Place.Type.NORMAL), new Place(73, Place.Type.NORMAL), new Place(74, Place.Type.NORMAL), new Place(75, Place.Type.NORMAL), new Place(76, Place.Type.NORMAL), new Place(77, Place.Type.NORMAL), new Place(78, Place.Type.NORMAL), new Place(79, Place.Type.NORMAL), new Place(80, Place.Type.DISABLED)},
            {new Place(81, Place.Type.DISABLED), new Place(82, Place.Type.NORMAL), new Place(83, Place.Type.NORMAL), new Place(84, Place.Type.NORMAL), new Place(85, Place.Type.NORMAL), new Place(86, Place.Type.NORMAL), new Place(87, Place.Type.NORMAL), new Place(88, Place.Type.NORMAL), new Place(89, Place.Type.NORMAL), new Place(90, Place.Type.DISABLED)},
            {new Place(91, Place.Type.MOTORCYCLE), new Place(92, Place.Type.MOTORCYCLE), new Place(93, Place.Type.MOTORCYCLE), new Place(94, Place.Type.MOTORCYCLE), new Place(95, Place.Type.MOTORCYCLE), new Place(96, Place.Type.MOTORCYCLE), new Place(97, Place.Type.MOTORCYCLE), new Place(98, Place.Type.MOTORCYCLE), new Place(99, Place.Type.MOTORCYCLE), new Place(100, Place.Type.MOTORCYCLE)},
    };

    Place selectedPlace;
    MaterialButton selectedButton;
    View view;

    public MainFragment() {
        // Es necesario un constructor vacio
    }

    private int hourDiff(int startHour, int endHour) {
        if (endHour < startHour) {
            return 24 - startHour + endHour;
        } else {
            return endHour - startHour;
        }
    }

    private boolean checkNulls() {
        boolean ret = false;
        if (selectedPlace == null) {
            Toast.makeText(getContext(), "Select a place", Toast.LENGTH_SHORT).show();
            ret = true;
        }
        if (year == null || month == null || dayOfMonth == null) {
            ((EditText) view.findViewById(R.id.dateText)).setError("Select a date");
            ret = true;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, dayOfMonth);
            if (cal.before(Calendar.getInstance())) {
                ((EditText) view.findViewById(R.id.dateText)).setError("Select a future date");
                ret = true;
            } else {
                ((EditText) view.findViewById(R.id.dateText)).setError(null);
            }
        }
        if (startHour == null) {
            ((EditText) view.findViewById(R.id.startTimeText)).setError("Select a start time");
            ret = true;
        } else {
            ((EditText) view.findViewById(R.id.startTimeText)).setError(null);
        }
        if (endHour == null) {
            ((EditText) view.findViewById(R.id.endTimeText)).setError("Select an end time");
            ret = true;
        } else {
            ((EditText) view.findViewById(R.id.endTimeText)).setError(null);
        }
        return ret;
    }

    private Pair<Date, Date> getDates() {
        if (year == null || month == null || dayOfMonth == null || startHour == null || endHour == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, dayOfMonth, startHour, startMinute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();

        if (endHour < startHour) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        cal.set(Calendar.HOUR_OF_DAY, endHour);
        cal.set(Calendar.MINUTE, endMinute);
        Date endDate = cal.getTime();

        return new Pair<>(startDate, endDate);
    }

    private void resetFields() {
        selectedButton.setStrokeColor(ColorStateList.valueOf(getResources().getColor(com.google.android.material.R.color.material_grey_300, null)));
        selectedPlace = null;
        selectedButton = null;
        year = null;
        month = null;
        dayOfMonth = null;
        startHour = null;
        endHour = null;
        startMinute = null;
        endMinute = null;
        EditText t = view.findViewById(R.id.dateText);
        t.setText(null);
        t = view.findViewById(R.id.startTimeText);
        t.setText(null);
        t = view.findViewById(R.id.endTimeText);
        t.setText(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        GridLayout glParking = view.findViewById(R.id.glParking);
        glParking.setRowCount(parking.length);
        glParking.setColumnCount(parking[0].length);

        for (Place[] row : parking) {
            for (Place place : row) {
                MaterialButton btn = new MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
                btn.setText(String.valueOf(place.getId()));
                switch (place.getType()) {
                    case ELECTRIC:
                        btn.setIconResource(R.drawable.ic_electric);
                        break;
                    case NORMAL:
                        btn.setIconResource(R.drawable.ic_normal);
                        break;
                    case DISABLED:
                        btn.setIconResource(R.drawable.ic_disabled);
                        break;
                    case MOTORCYCLE:
                        btn.setIconResource(R.drawable.ic_motorcycle);
                        break;
                }
                btn.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
                btn.setIconSize(60);
                btn.setId((int) place.getId());
                btn.setOnClickListener(v -> {
                    if (selectedButton != null) {
                        selectedButton.setStrokeColor(ColorStateList.valueOf(getResources().getColor(com.google.android.material.R.color.material_grey_300, null)));
                    }
                    btn.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.orange, null)));
                    selectedButton = btn;
                    selectedPlace = place;
                });
                glParking.addView(btn);
            }
        }

        EditText dateText = view.findViewById(R.id.dateText);
        dateText.setOnClickListener(v -> {
            CalendarConstraints.DateValidator min = DateValidatorPointForward.from(MaterialDatePicker.todayInUtcMilliseconds());
            CalendarConstraints.DateValidator max = DateValidatorPointBackward.before(MaterialDatePicker.todayInUtcMilliseconds() + 1000 * 60 * 60 * 24 * 7);
            ArrayList<CalendarConstraints.DateValidator> listValidators = new ArrayList<>();
            listValidators.add(min);
            listValidators.add(max);
            CalendarConstraints.DateValidator validators = CompositeDateValidator.allOf(listValidators);

            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder
                    .datePicker()
                    .setTitleText("Select a Date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(validators).build())
                    .build();

            picker.addOnPositiveButtonClickListener(selection -> {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(selection);
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH) + 1;
                dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                dateText.setText(dayOfMonth + "/" + month + "/" + year);
                if (startHour != null && endHour != null) {
                    mainViewModel.loadReservationsForDate(dayOfMonth + "/" + month + "/" + year);
                }
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        EditText startTimeText = view.findViewById(R.id.startTimeText);
        EditText endTimeText = view.findViewById(R.id.endTimeText);

        startTimeText.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Select Start Time")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build();

            picker.addOnPositiveButtonClickListener(view1 -> {
                startHour = picker.getHour();
                startMinute = picker.getMinute();

                startTimeText.setText(String.format("%02d:%02d", startHour, startMinute));
                endTimeText.setEnabled(true);
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        endTimeText.setOnClickListener(v -> {
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(startHour)
                    .setMinute(startMinute)
                    .setTitleText("Select End Time")
                    .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                    .build();

            picker.addOnPositiveButtonClickListener(view1 -> {
                int newEndHour = picker.getHour();
                int newEndMinute = picker.getMinute();

                if (newEndHour == startHour && newEndMinute <= startMinute) {
                    endTimeText.setError("End time must be after start time");
                    Toast.makeText(getContext(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (hourDiff(startHour, newEndHour) > 9 || (newEndHour - startHour == 9 && newEndMinute > startMinute)) {
                    endTimeText.setError("The maximum duration is 8 hours");
                    Toast.makeText(getContext(), "The maximum duration is 8 hours", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (month != null && dayOfMonth != null && year != null) {
                    mainViewModel.loadReservationsForDate(dayOfMonth + "/" + month + "/" + year);
                }

                endHour = newEndHour;
                endMinute = newEndMinute;
                endTimeText.setError(null);
                endTimeText.setText(String.format("%02d:%02d", endHour, endMinute));
            });

            picker.show(getParentFragmentManager(), picker.toString());
        });

        MaterialButton reserveButton = view.findViewById(R.id.btnReserve);
        reserveButton.setOnClickListener(v -> {
            if (checkNulls()) return;

            String date = dayOfMonth + "/" + month + "/" + year;
            String id = selectedPlace.getId() + "-" + date + "-" + startHour + "-" + endHour;
            Pair<Date, Date> dates = getDates();

            mainViewModel.reserve(date, id, selectedPlace, dates.first, dates.second);
            resetFields();
        });

        mainViewModel.getError().observe(getViewLifecycleOwner(), error ->
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );

        mainViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success ->
            Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show()
        );

        mainViewModel.getReservationsForDate().observe(getViewLifecycleOwner(), reservations -> {
            Pair<Date, Date> dates = getDates();
            for (Reservation r : reservations) {
                Log.d("MainFragment", "Checking reservation " + r.getId() + " startTime: " + r.getHour().getStartTime() + " endTime: " + r.getHour().getEndTime() + " startDate: " + dates.first.getTime() + " endDate: " + dates.second.getTime());
                if (r.getHour().isOverlapping(new Hour(dates.first, dates.second))) {
                    Log.d("MainFragment", "Disabling place " + (int) r.getPlace().getId());
                    MaterialButton btn = view.findViewById((int) r.getPlace().getId());
                    Log.d("MainFragment", "Button: " + btn.getText());
                    btn.setEnabled(false);
                } else {
                    view.findViewById((int) r.getPlace().getId()).setEnabled(true);
                }
            }
        });

        return view;
    }
}
