package com.lksnext.parkingagarcia.view.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.lksnext.parkingagarcia.Utils;
import com.lksnext.parkingagarcia.R;
import com.lksnext.parkingagarcia.domain.EndReservationReminderReceiver;
import com.lksnext.parkingagarcia.domain.Hour;
import com.lksnext.parkingagarcia.domain.Place;
import com.lksnext.parkingagarcia.domain.Reservation;
import com.lksnext.parkingagarcia.domain.StartReservationReminderReceiver;
import com.lksnext.parkingagarcia.viewmodel.MainViewModel;

import java.util.Calendar;
import java.util.Date;

public class MainFragment extends Fragment {
    Integer year, month, dayOfMonth, startHour, endHour, startMinute, endMinute;

    MainViewModel mainViewModel;

    Place selectedPlace;
    MaterialButton selectedButton;
    View view;

    private static final String TAG = "MainFragment";

    public MainFragment() {
        // Es necesario un constructor vacio
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

    private void scheduleStartReservationReminder(long startTimeInMillis, String id) {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), StartReservationReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTimeInMillis - 1000 * 60 * 30, pendingIntent);
    }

    private void scheduleEndReservationReminder(long endTimeInMillis, String id) {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), EndReservationReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endTimeInMillis - 1000 * 60 * 15, pendingIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        if (mainViewModel.getDate().getValue() != null) {
            ((EditText) view.findViewById(R.id.dateText)).setText(mainViewModel.getDate().getValue());
            String[] date = mainViewModel.getDate().getValue().split("/");
            if (date.length == 3) {
                this.year = Integer.parseInt(date[2]);
                this.month = Integer.parseInt(date[1]);
                this.dayOfMonth = Integer.parseInt(date[0]);
            }
        }
        if (mainViewModel.getStartTime().getValue() != null) {
            ((EditText) view.findViewById(R.id.startTimeText)).setText(mainViewModel.getStartTime().getValue());
            String[] time = mainViewModel.getStartTime().getValue().split(":");
            if (time.length == 2) {
                view.findViewById(R.id.endTimeText).setEnabled(true);
                this.startMinute = Integer.parseInt(time[1]);
                this.startHour = Integer.parseInt(time[0]);
            }
        }
        if (mainViewModel.getEndTime().getValue() != null) {
            ((EditText) view.findViewById(R.id.endTimeText)).setText(mainViewModel.getEndTime().getValue());
            String[] time = mainViewModel.getEndTime().getValue().split(":");
            if (time.length == 2) {
                this.endMinute = Integer.parseInt(time[1]);
                this.endHour = Integer.parseInt(time[0]);
            }
        }

        GridLayout glParking = view.findViewById(R.id.glParking);
        glParking.setRowCount(Utils.getParking().length);
        glParking.setColumnCount(Utils.getParking()[0].length);

        Utils.chargeParking(glParking, v -> {
            MaterialButton btn = (MaterialButton) v;
            if (selectedButton != null) {
                selectedButton.setStrokeColor(ColorStateList.valueOf(getResources().getColor(com.google.android.material.R.color.material_grey_300, null)));
            }
            btn.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.orange, null)));
            selectedButton = btn;
            selectedPlace = Utils.getParking()[(btn.getId() - 1) / 10][(btn.getId() - 1) % 10];
        }, this);

        if (mainViewModel.getSelectedPlace().getValue() != null) {
            MaterialButton btn = view.findViewById(mainViewModel.getSelectedPlace().getValue().intValue());
            btn.performClick();
        }

        EditText dateText = view.findViewById(R.id.dateText);
        dateText.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = Utils.datePickerDialog();

            picker.addOnPositiveButtonClickListener(selection -> {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(selection);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

                if (startHour != null) {
                    cal.set(year, month - 1, dayOfMonth, startHour, startMinute);
                    if (cal.before(Calendar.getInstance())) {
                        dateText.setError("Select a future date");
                        Toast.makeText(getContext(), "Select a future date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                this.year = year;
                this.month = month;
                this.dayOfMonth = dayOfMonth;
                dateText.setText(dayOfMonth + "/" + month + "/" + year);
                dateText.setError(null);
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
                int startHour = picker.getHour();
                int startMinute = picker.getMinute();

                if (year != null && month != null && dayOfMonth != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month - 1, dayOfMonth, startHour, startMinute);
                    if (cal.before(Calendar.getInstance())) {
                        startTimeText.setError("Select a future time");
                        Toast.makeText(getContext(), "Select a future time", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        startTimeText.setError(null);
                    }
                }

                this.startHour = startHour;
                this.startMinute = startMinute;

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
                if (Utils.hourDiff(startHour, newEndHour) > 9 || (newEndHour - startHour == 9 && newEndMinute > startMinute)) {
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
            String id = selectedPlace.getId() + "-" + date + "-" + startTimeText.getText() + "-" + endTimeText.getText();
            Pair<Date, Date> dates = Utils.getDates(year, month, dayOfMonth, startHour, startMinute, endHour, endMinute);

            mainViewModel.reserve(date, id, selectedPlace, dates.first, dates.second);
            scheduleStartReservationReminder(dates.first.getTime(), id);
            scheduleEndReservationReminder(dates.second.getTime(), id);
            resetFields();
        });

        mainViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                mainViewModel.getError().setValue(null);
            }
        });

        mainViewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                Toast.makeText(getContext(), success, Toast.LENGTH_SHORT).show();
                mainViewModel.getSuccessMessage().setValue(null);
            }
        });

        mainViewModel.getReservationsForDate().observe(getViewLifecycleOwner(), reservations -> {
            if (reservations == null) return;
            Pair<Date, Date> dates = Utils.getDates(year, month, dayOfMonth, startHour, startMinute, endHour, endMinute);
            if (dates == null) return;
            for (Reservation r : reservations) {
                Log.d(TAG, "Checking reservation " + r.getId() + " startTime: " + r.getHour().getStartTime() + " endTime: " + r.getHour().getEndTime() + " startDate: " + dates.first.getTime() + " endDate: " + dates.second.getTime());
                if (r.getHour().isOverlapping(new Hour(dates.first, dates.second))) {
                    Log.d(TAG, "Disabling place " + (int) r.getPlace().getId());
                    MaterialButton btn = view.findViewById((int) r.getPlace().getId());
                    Log.d(TAG, "Button: " + btn.getText());
                    btn.setEnabled(false);
                    if (selectedButton != null && btn.getId() == selectedButton.getId()) {
                        selectedButton.setStrokeColor(ColorStateList.valueOf(getResources().getColor(com.google.android.material.R.color.material_grey_300, null)));
                        selectedButton = null;
                        selectedPlace = null;
                    }
                } else {
                    view.findViewById((int) r.getPlace().getId()).setEnabled(true);
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainViewModel.setDate(((EditText) view.findViewById(R.id.dateText)).getText().toString());
        mainViewModel.setEndTime(((EditText) view.findViewById(R.id.endTimeText)).getText().toString());
        mainViewModel.setStartTime(((EditText) view.findViewById(R.id.startTimeText)).getText().toString());
        mainViewModel.setSelectedPlace(selectedPlace != null ? selectedPlace.getId() : null);
    }
}
