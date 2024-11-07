package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.repository.UserRepository;

public class LoginActivity extends AppCompatActivity {
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(FirebaseFirestore.getInstance());
    }

    public void registration(View b){
        Intent a = new Intent(this, RegistrationActivity.class);
        startActivity(a);
        finish();
    }

    public void login(View b){
        EditText name = findViewById(R.id.nameEdit);
        EditText password = findViewById(R.id.passEdit);
        String textName = name.getText().toString();
        String textPassword= password.getText().toString();
        userRepository.getUserByName(textName).thenAccept(user -> {
            if (user == null) {
                Toast.makeText(this, "Неверное имя или пароль", Toast.LENGTH_LONG).show();
            } else if (user.pass.equals(textPassword)){
                Authentication.user = user;
                Intent a = new Intent(this, BroHotelEdit.class);
                startActivity(a);
                finish();
            } else Toast.makeText(this, "Неверное имя или пароль", Toast.LENGTH_LONG).show();
        });

    }

    public void admin(View b){
        Intent a = new Intent(this, AdminHotelEdit.class);
        startActivity(a);
        finish();
    }

    public void bronist(View b){
        Intent a = new Intent(this, BroHotelEdit.class);
        startActivity(a);
        finish();
    }

}