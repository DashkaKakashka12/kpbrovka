package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void registrationUser(View b){
        Intent a = new Intent(this, MainUserActivity.class);
        startActivity(a);
        finish();
    }

    public void loginUser (View b){
        Intent a = new Intent(this, LoginActivity.class);
        startActivity(a);
        finish();
    }
}