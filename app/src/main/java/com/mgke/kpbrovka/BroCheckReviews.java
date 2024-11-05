package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;

public class BroCheckReviews extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_check_reviews);
    }

    public void hotelEdit(View view) {
        DrawerLayout b = findViewById(R.id.bro_hotel_edit);
        b.openDrawer(GravityCompat.START);
    }
}