package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ProfileUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
    }

    public void findClick(View view) {
        Intent intent = new Intent(this, SavedUserActivity.class);
        startActivity(intent);
        finish();
    }

    public void savedClick(View view) {
        Intent intent = new Intent(this, SavedUserActivity.class);
        startActivity(intent);
        finish();
    }

    public void bookingClick(View view) {
        Intent intent = new Intent(this, BookingUserActivity.class);
        startActivity(intent);
        finish();
    }
}