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
import com.mgke.kpbrovka.model.User;
import com.mgke.kpbrovka.model.UserType;
import com.mgke.kpbrovka.repository.UserRepository;

public class RegistrationActivity extends AppCompatActivity {

    UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userRepository = new UserRepository(FirebaseFirestore.getInstance());

        setupPasswordVisibilityToggle(R.id.password_visibility, R.id.passEditReg);
        setupPasswordVisibilityToggle(R.id.password_visibility2, R.id.passEditReg2);
    }


    private void setupPasswordVisibilityToggle(int toggleButtonId, int passwordFieldId) {
        ImageView togglePasswordButton = findViewById(toggleButtonId);
        EditText passwordField = findViewById(passwordFieldId);

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

    public void registrationUser(View view) {
        EditText nameEdit = findViewById(R.id.NameEditReg);
        EditText userEmail = findViewById(R.id.userEmail);
        EditText passEdit = findViewById(R.id.passEditReg);
        EditText passEdit2 = findViewById(R.id.passEditReg2);

        String name = nameEdit.getText().toString().trim();
        String email = userEmail.getText().toString().trim();
        String password = passEdit.getText().toString();
        String passwordConfirm = passEdit2.getText().toString();


        if (name.length() < 5) {
            nameEdit.setError("Имя должно содержать не менее 5 символов.");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("Введите корректный адрес электронной почты.");
            return;
        }

        if (!isValidPassword(password)) {
            passEdit.setError("Пароль должен содержать не менее 5 символов, хотя бы одну букву и цифру.");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            passEdit2.setError("Пароли не совпадают.");
            return;
        }

        if (email.isEmpty()) {
            userEmail.setError("Введите адрес электронной почты.");
            return;
        }


        userRepository.nameMatchingCheck(name).thenAccept(b -> {
            if (!b) {
                User user = new User();
                user.name = name;
                user.email = email;
                user.pass = password;
                user.type = UserType.USER;
                userRepository.addUser(user);

                Authentication.user = user;

                Intent intent = new Intent(this, MainUserActivity.class);
                startActivity(intent);
                finish();
            } else {
                nameEdit.setError("Такой пользователь уже существует");
            }
        });
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 5 &&
                password.matches(".*[a-zA-Zа-яА-ЯЁё].*") &&
                password.matches(".*\\d.*");
    }

    public void loginUser(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}