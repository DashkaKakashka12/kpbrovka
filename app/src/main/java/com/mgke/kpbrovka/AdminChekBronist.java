package com.mgke.kpbrovka;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.ListOfBroAdapter;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.model.User;
import com.mgke.kpbrovka.model.UserType;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.UserRepository;

import java.util.List;

public class AdminChekBronist extends AppCompatActivity {
    private UserRepository userRepository;
    private HotelRepository hotelRepository;
    private ListOfBroAdapter listOfBroAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chek_bronist);

        NavigationView navigationView = findViewById(R.id.navigationMenu);
        AdminBurgerMenuSelect navigationListener = new AdminBurgerMenuSelect(this);
        navigationView.setNavigationItemSelectedListener(navigationListener);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userRepository = new UserRepository(db);
        hotelRepository = new HotelRepository(db);

        ListView listView = findViewById(R.id.listOfBro);
        userRepository.getUsersByUserType(UserType.HOTELIER)
                .thenAccept(list -> {
                    userList = list;
                    listOfBroAdapter = new ListOfBroAdapter(this, list);
                    listView.setAdapter(listOfBroAdapter);
                    listOfBroAdapter.notifyDataSetChanged();
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = userList.get(position);
                hotelRepository.getHotelByUserId(user.id)
                        .thenAccept(hotel -> {
                           updateBro(user, hotel);
                        });
            }
        });
    }

    public void updateBro(User user, Hotel hotel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialogBlack);
        View customView = getLayoutInflater().inflate(R.layout.dialog_admin_edd_bro, null);
        EditText name = customView.findViewById(R.id.name);
        EditText password = customView.findViewById(R.id.password);
        EditText hotelName = customView.findViewById(R.id.hotel_name);
        EditText email = customView.findViewById(R.id.email);

        name.setText(user.name);
        password.setText(user.pass);
        hotelName.setText(hotel.hotelName);
        email.setText(user.email);

        builder.setView(customView);
        builder.setTitle("Редактирование отельера")
                .setPositiveButton("ОК", null);
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ImageView togglePasswordButton = customView.findViewById(R.id.password_visibility);
        EditText passwordField = customView.findViewById(R.id.password);

        togglePasswordButton.setOnClickListener(v -> {
            if (passwordField.getInputType() == (InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.icon_not_visible_white);
            } else {
                passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.icon_visible_white);
            }
            passwordField.setSelection(passwordField.getText().length());
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositive.setOnClickListener(v -> {
                if (validateInput(name, password, email)) {
                    user.name = name.getText().toString();
                    user.pass = password.getText().toString();
                    user.email = email.getText().toString();
                    userRepository.updateUser(user);

                    hotel.hotelName = hotelName.getText().toString();
                    hotelRepository.updateHotel(hotel);

                    listOfBroAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    public void eddBro(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialogBlack);
        View customView = getLayoutInflater().inflate(R.layout.dialog_admin_edd_bro, null);
        builder.setView(customView);
        builder.setTitle("Добавление отельера")
                .setPositiveButton("ОК", null);
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ImageView togglePasswordButton = customView.findViewById(R.id.password_visibility);
        EditText passwordField = customView.findViewById(R.id.password);

        togglePasswordButton.setOnClickListener(v -> {
            if (passwordField.getInputType() == (InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.icon_not_visible_white);
            } else {
                passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordButton.setImageResource(R.drawable.icon_visible_white);
            }
            passwordField.setSelection(passwordField.getText().length());
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositive.setOnClickListener(v -> {
                User user = new User();
                EditText name = customView.findViewById(R.id.name);
                EditText password = customView.findViewById(R.id.password);
                EditText hotelName = customView.findViewById(R.id.hotel_name);
                EditText email = customView.findViewById(R.id.email);

                if (validateInput(name, password, email)) {
                    userRepository.nameMatchingCheck(name.getText().toString()).thenAccept(b ->{
                        if (!b){
                            user.name = name.getText().toString();
                            user.pass = password.getText().toString();
                            user.type = UserType.HOTELIER;
                            user.email = email.getText().toString();
                            String userId = userRepository.addUser(user);

                            Hotel hotel = new Hotel();
                            hotel.hotelName = hotelName.getText().toString();
                            hotel.isActive = false;
                            hotel.userId = userId;
                            hotelRepository.addHotel(hotel);

                            user.id = userId;
                            userList.add(user);
                            listOfBroAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } else {
                            name.setError("Такой пользователь уже существует");
                        }
                    });
                }
            });
        });

        dialog.show();
    }

    private boolean validateInput(EditText name, EditText password, EditText email) {
        String nameText = name.getText().toString().trim();
        String passwordText = password.getText().toString();
        String emailText = email.getText().toString().trim();

        if (nameText.length() < 5 ) {
            name.setError("Имя должно содержать не менее 5 символов.");
            return false;
        }

        if (passwordText.length() < 5 ||
                !passwordText.matches(".*[a-zA-Zа-яА-ЯЁё].*") ||
                !passwordText.matches(".*\\d.*")) {
            password.setError("Пароль должен содержать не менее 5 символов, хотя бы одну букву и цифру.");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Введите корректный адрес электронной почты.");
            return false;
        }

        return true;
    }

    public void onBurger(View view) {
        DrawerLayout b = findViewById(R.id.admin_chek_bronist);
        b.openDrawer(GravityCompat.START);
    }
}