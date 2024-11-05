package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class AdminEditHotel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_hotel);

        NavigationView navigationView = findViewById(R.id.navigationMenu1);
        AdminBurgerMenuSelect navigationListener = new AdminBurgerMenuSelect(this);
        navigationView.setNavigationItemSelectedListener(navigationListener);
    }

    public void onBurger(View view) {
        DrawerLayout b = findViewById(R.id.admin_edit_hotel);
        b.openDrawer(GravityCompat.START);
    }
}