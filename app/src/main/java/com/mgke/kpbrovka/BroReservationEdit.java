package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import android.os.Bundle;
import android.view.View;

import com.google.android.material.navigation.NavigationView;


public class BroReservationEdit extends AppCompatActivity {

    private View indicator1, indicator2, indicator3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_reservation_edit);

        NavigationView navigationView = findViewById(R.id.navigationMenu);
        BroBurgerMenuSelect navigationListener = new BroBurgerMenuSelect(this, navigationView);
        navigationView.setNavigationItemSelectedListener(navigationListener);

        indicator1 = findViewById(R.id.indecator1);
        indicator2 = findViewById(R.id.indecator2);
        indicator3 = findViewById(R.id.indecator3);

        indicator1.setVisibility(View.VISIBLE);
        indicator2.setVisibility(View.INVISIBLE);
        indicator3.setVisibility(View.INVISIBLE);

        findViewById(R.id.text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicator1.setVisibility(View.VISIBLE);
                indicator2.setVisibility(View.INVISIBLE);
                indicator3.setVisibility(View.INVISIBLE);
            }
        });

        findViewById(R.id.text2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicator2.setVisibility(View.VISIBLE);
                indicator1.setVisibility(View.INVISIBLE);
                indicator3.setVisibility(View.INVISIBLE);
            }
        });

        findViewById(R.id.text3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicator3.setVisibility(View.VISIBLE);
                indicator2.setVisibility(View.INVISIBLE);
                indicator1.setVisibility(View.INVISIBLE);
            }
        });

    }



    public void onBurger(View view) {
        DrawerLayout b = findViewById(R.id.bro_reservation_edit);
        b.openDrawer(GravityCompat.START);
    }
}