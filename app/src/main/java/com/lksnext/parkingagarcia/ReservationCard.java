package com.lksnext.parkingagarcia;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ReservationCard extends RelativeLayout {
    private TextView placeText;
    private ImageView placeImage;
    private TextView dateText;
    private TextView hourText;
    private MaterialButton cancelBtn;
    private MaterialButton editBtn;

    public ReservationCard(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.reservation_card, this);
        placeText = findViewById(R.id.placeText);
        placeImage = findViewById(R.id.placeImage);
        dateText = findViewById(R.id.dateText);
        hourText = findViewById(R.id.timeText);
        cancelBtn = findViewById(R.id.cancelButton);
        editBtn = findViewById(R.id.editButton);
    }

    public ReservationCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReservationCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPlaceNumber(long number) {
        placeText.setText("Place " + number);
    }

    public void setPlaceImage(Drawable drawable) {
        placeImage.setImageDrawable(drawable);
    }

    public void setDateText(String date) {
        dateText.setText("Date\n\n" + date);
    }

    public void setHourText(String from, String to) {
        hourText.setText("From\n" + from + "\n\nto\n" + to);
    }

    public void setCancelBtnText(String text) {
        cancelBtn.setText(text);
    }

    public void setEditBtnText(String text) {
        editBtn.setText(text);
    }

    public void setCancelBtnOnClickListener(OnClickListener listener) {
        cancelBtn.setOnClickListener(listener);
    }

    public void setEditBtnOnClickListener(OnClickListener listener) {
        editBtn.setOnClickListener(listener);
    }
}