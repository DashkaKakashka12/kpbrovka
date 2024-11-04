package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SavedUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_user);
    }

    public void findClick(View view) {
        Intent intent = new Intent(this, MainUserActivity.class);
        startActivity(intent);
        finish();
    }

    public void bookingClick(View view) {
        Intent intent = new Intent(this, BookingUserActivity.class);
        startActivity(intent);
        finish();
    }

    public void profileUserClick(View view) {
        Intent intent = new Intent(this, ProfileUserActivity.class);
        startActivity(intent);
        finish();
    }
}