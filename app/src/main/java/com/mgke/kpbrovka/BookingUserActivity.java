package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BookingUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_user);
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

    public void profileUserClick(View view) {
        Intent intent = new Intent(this, ProfileUserActivity.class);
        startActivity(intent);
        finish();
    }
}