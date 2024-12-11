package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    }

    public void registrationUser(View view) {
        EditText nameEdit = findViewById(R.id.NameEditReg);
        EditText passEdit = findViewById(R.id.PassEditReg);
        EditText passEdit2 = findViewById(R.id.PassEditReg2);

        String name = nameEdit.getText().toString().trim();
        String password = passEdit.getText().toString();
        String passwordConfirm = passEdit2.getText().toString();

        if (name.length() < 5 || name.length() > 20) {
            nameEdit.setError("Имя должно содержать от 5 до 20 символов.");
            return;
        }

        if (!isValidPassword(password)) {
            passEdit.setError("Пароль должен содержать минимум 5 символов, одну букву и одну цифру.");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            passEdit2.setError("Пароли не совпадают.");
            return;
        }

        userRepository.nameMatchingCheck(name).thenAccept(b -> {
           if (!b){
               User user = new User();
               user.name = name;
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