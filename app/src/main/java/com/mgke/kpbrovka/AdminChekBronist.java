package com.mgke.kpbrovka;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.model.User;
import com.mgke.kpbrovka.model.UserType;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.UserRepository;

public class AdminChekBronist extends AppCompatActivity {
    private UserRepository userRepository;
    private HotelRepository hotelRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chek_bronist);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userRepository = new UserRepository(db);
        hotelRepository = new HotelRepository(db);
    }

    public void eddBro(View view) {
        // Создаем AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_admin_edd_bro, null);
        builder.setView(customView);
        builder.setTitle("Добавление брониста")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User user = new User();
                        EditText name = customView.findViewById(R.id.name);
                        EditText password = customView.findViewById(R.id.password);
                        EditText hotelName = customView.findViewById(R.id.hotel_name);
                        EditText email = customView.findViewById(R.id.email);

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
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}