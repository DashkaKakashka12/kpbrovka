package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void registrationUser(View view) {
        EditText nameEdit = findViewById(R.id.NameEditReg);
        EditText passEdit = findViewById(R.id.PassEditReg);
        EditText passEdit2 = findViewById(R.id.PassEditReg2);

        String name = nameEdit.getText().toString().trim();
        String password = passEdit.getText().toString();
        String passwordConfirm = passEdit2.getText().toString();

        if (name.length() < 5 || name.length() > 20) {
            Toast.makeText(this, "Имя должно содержать от 5 до 20 символов.", Toast.LENGTH_LONG).show();
            return;
        }


        if (!isValidPassword(password)) {
            Toast.makeText(this, "Пароль должен содержать минимум 5 символов и хотя бы одну букву.", Toast.LENGTH_LONG).show();
            return;
        }


        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Пароли не совпадают.", Toast.LENGTH_LONG).show();
            return;
        }


        Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidPassword(String password) {

        return password.length() >= 5 && password.matches(".*[a-zA-Zа-яА-ЯЁё].*");
    }

    public void loginUser(View view) {

    }
}