package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;


public class DialogBroChooseMainFacilities extends AppCompatActivity {

    private CheckBox checkBoxWifi;
    private CheckBox checkBoxPool;
    private CheckBox checkBoxParking;
    private CheckBox checkBoxConditioning;
    private CheckBox checkBoxSport;
    private CheckBox checkBoxCard;
    private CheckBox checkBoxSafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_bro_choose_main_facilities);

        checkBoxWifi = findViewById(R.id.checkBoxWifi);
        checkBoxPool = findViewById(R.id.checkBoxPool);
        checkBoxParking = findViewById(R.id.checkBoxParking);
        checkBoxConditioning = findViewById(R.id.checkBoxConditioning);
        checkBoxSport = findViewById(R.id.checkBoxSport);
        checkBoxCard = findViewById(R.id.checkBoxCard);
        checkBoxSafe = findViewById(R.id.checkBoxSafe);

        checkBoxWifi.setOnClickListener(new CheckBoxClickListener());
        checkBoxPool.setOnClickListener(new CheckBoxClickListener());
        checkBoxParking.setOnClickListener(new CheckBoxClickListener());
        checkBoxConditioning.setOnClickListener(new CheckBoxClickListener());
        checkBoxSport.setOnClickListener(new CheckBoxClickListener());
        checkBoxCard.setOnClickListener(new CheckBoxClickListener());
        checkBoxSafe.setOnClickListener(new CheckBoxClickListener());
    }

    private class CheckBoxClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox) v;

            if (checkBox.isChecked()) {
                return;
            } else {
                return;
            }
        }
    }

}

