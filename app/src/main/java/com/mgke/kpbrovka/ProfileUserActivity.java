package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class ProfileUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        NavigationView navigationView = findViewById(R.id.navigationMenuUser);
        UserBurgerMenuSelect navigationListener = new UserBurgerMenuSelect(this);
        navigationView.setNavigationItemSelectedListener(navigationListener);
    }

    public void onBurger(View view) {
        DrawerLayout b = findViewById(R.id.profile_user);
        b.openDrawer(GravityCompat.START);
    }

}