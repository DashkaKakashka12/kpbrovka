package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class BroChooseHotelRoomForEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_choose_hotel_room_for_edit);


        NavigationView navigationView = findViewById(R.id.navigationMenu);
        BroBurgerMenuSelect navigationListener = new BroBurgerMenuSelect(this, navigationView);
        navigationView.setNavigationItemSelectedListener(navigationListener);

    }

    public void broEditHotelRoom (View b){
        Intent a = new Intent(this, BroHotelRoomEdit.class);
        startActivity(a);
        finish();
    }

    public void burger(View view) {
        DrawerLayout b = findViewById(R.id.bro_choose_hotel_room_from_edit);
        b.openDrawer(GravityCompat.START);
    }
}