package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.model.UserType;
import com.mgke.kpbrovka.repository.UserRepository;

public class LoginActivity extends AppCompatActivity {
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(FirebaseFirestore.getInstance());

        ImageView togglePasswordButton = findViewById(R.id.password_visibility);
        EditText passwordField = findViewById(R.id.passEdit);

        togglePasswordButton.setOnClickListener(v -> {
            if (passwordField.getInputType() == (InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.icon_not_visible_black);
            } else {
                passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.icon_visible_black);
            }
            passwordField.setSelection(passwordField.getText().length());
        });
    }

    public void registration(View b){
        Intent a = new Intent(this, RegistrationActivity.class);
        startActivity(a);
        finish();
    }

    public void login(View b) {
        EditText name = findViewById(R.id.nameEdit);
        EditText password = findViewById(R.id.passEdit);
        String textName = name.getText().toString().trim();
        String textPassword = password.getText().toString();

        if (textName.isEmpty()) {
            name.setError("Имя не должно быть пустым.");
            return;
        }

        if (textPassword.isEmpty()) {
            password.setError("Пароль не должен быть пустым.");
            return;
        }

        userRepository.getUserByName(textName).thenAccept(user -> {
            if (user == null || !user.pass.equals(textPassword)) {
                name.setError("Неверное имя или пароль");
                password.setError("Неверное имя или пароль");
                return;
            }

            Authentication.user = user;
            Intent a;
            if (user.type == UserType.ADMINISTRATOR) {
                a = new Intent(this, AdminHotelEdit.class);
            } else if (user.type == UserType.HOTELIER) {
                a = new Intent(this, BroHotelEdit.class);
            } else {
                a = new Intent(this, MainUserActivity.class);
            }
            startActivity(a);
            finish();
        });
    }

}