package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BroChooseHotelRoomForEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_choose_hotel_room_for_edit);
    }

    public void backToBroHotelEdit (View b){
        Intent a = new Intent(this, BroFacilitiesEdit.class);
        startActivity(a);
        finish();
    }

    public void broEditHotelRoom (View b){
        Intent a = new Intent(this, BroHotelRoomEdit.class);
        startActivity(a);
        finish();
    }
}