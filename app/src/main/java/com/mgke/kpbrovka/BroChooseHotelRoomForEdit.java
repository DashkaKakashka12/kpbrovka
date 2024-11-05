package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class BroChooseHotelRoomForEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_choose_hotel_room_for_edit);


        NavigationView navigationView = findViewById(R.id.navigationMenu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent a = null;

                if (menuItem.getItemId() == R.id.profile) {
                    a = new Intent(BroChooseHotelRoomForEdit.this, BroChooseHotelRoomForEdit.class);
                } else if (menuItem.getItemId() == R.id.user_profile) {
                    a = new Intent(BroChooseHotelRoomForEdit.this, BroChooseHotelRoomForEdit.class);
                } else if (menuItem.getItemId() == R.id.hotel) {
                    a = new Intent(BroChooseHotelRoomForEdit.this, BroHotelEdit.class);
                } else if (menuItem.getItemId() == R.id.rooms) {
                    a = new Intent(BroChooseHotelRoomForEdit.this, BroChooseHotelRoomForEdit.class);
                } else if (menuItem.getItemId() == R.id.dates) {
                    a = new Intent(BroChooseHotelRoomForEdit.this, BroChooseHotelRoomForEdit.class);
                }else if (menuItem.getItemId() == R.id.booking_confirmation) {
                    a = new Intent(BroChooseHotelRoomForEdit.this, BroChooseHotelRoomForEdit.class);
                }else if (menuItem.getItemId() == R.id.cancel_booking) {
                    a = new Intent(BroChooseHotelRoomForEdit.this, BroChooseHotelRoomForEdit.class);
                }else if (menuItem.getItemId() == R.id.visitor_registration) {
                    a = new Intent(BroChooseHotelRoomForEdit.this, BroChooseHotelRoomForEdit.class);
                }

                startActivity(a);
                finish();
                return true;
            }
        });

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