package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class DialogBroChooseHotelRoomMainFacilities extends AppCompatActivity {

    private CheckBox checkBoxIron;
    private CheckBox checkBoxWorkspace;
    private CheckBox checkBoxMiniBar;
    private CheckBox checkBoxSafe;
    private CheckBox checkBoxHairDryer;
    private CheckBox checkBoxFridge;
    private CheckBox checkBoxTowels;
    private CheckBox checkBoxCoffee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_bro_choose_hotel_room_main_facilities);

        checkBoxIron = findViewById(R.id.checkBoxIron);
        checkBoxWorkspace = findViewById(R.id.checkBoxWorkspace);
        checkBoxMiniBar = findViewById(R.id.checkBoxMiniBar);
        checkBoxSafe = findViewById(R.id.checkBoxSafe);
        checkBoxHairDryer = findViewById(R.id.checkBoxHairDryer);
        checkBoxFridge = findViewById(R.id.checkBoxFridge);
        checkBoxTowels = findViewById(R.id.checkBoxTowels);
        checkBoxCoffee = findViewById(R.id.checkBoxCoffee);

        checkBoxIron.setOnClickListener(new DialogBroChooseHotelRoomMainFacilities.CheckBoxClickListener());
        checkBoxWorkspace.setOnClickListener(new DialogBroChooseHotelRoomMainFacilities.CheckBoxClickListener());
        checkBoxMiniBar.setOnClickListener(new DialogBroChooseHotelRoomMainFacilities.CheckBoxClickListener());
        checkBoxSafe.setOnClickListener(new DialogBroChooseHotelRoomMainFacilities.CheckBoxClickListener());
        checkBoxHairDryer.setOnClickListener(new DialogBroChooseHotelRoomMainFacilities.CheckBoxClickListener());
        checkBoxTowels.setOnClickListener(new DialogBroChooseHotelRoomMainFacilities.CheckBoxClickListener());
        checkBoxCoffee.setOnClickListener(new DialogBroChooseHotelRoomMainFacilities.CheckBoxClickListener());
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
