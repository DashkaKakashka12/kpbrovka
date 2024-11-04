package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BroEditDescriptionHotelRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_edit_description_hotel_room);
    }

    public void backToBroHotelEdit (View b){
        Intent a = new Intent(this, BroEditHotelRoomFacilities.class);
        startActivity(a);
        finish();
    }
}