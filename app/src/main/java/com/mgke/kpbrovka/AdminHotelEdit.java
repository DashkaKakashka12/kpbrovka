package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.HotelAdapter;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.repository.HotelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminHotelEdit extends AppCompatActivity {

    private List<Hotel> listOfHotels = new ArrayList<>();
    private List<Hotel> firstListOfHotels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_hotel_edit);

        NavigationView navigationView = findViewById(R.id.navigationMenu1);
        AdminBurgerMenuSelect navigationListener = new AdminBurgerMenuSelect(this, navigationView);
        navigationView.setNavigationItemSelectedListener(navigationListener);

        RecyclerView listView = findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        hotelRepository.getAllHotels().thenAccept(list -> {
            listOfHotels.addAll(list);
            firstListOfHotels.addAll(list);
            HotelAdapter hotelAdapter = new HotelAdapter(listOfHotels, this, -1, null, null);
            listView.setAdapter(hotelAdapter);
        });


        EditText find = findViewById(R.id.findText);
        find.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listOfHotels = firstListOfHotels.stream().filter(hotel -> hotel.hotelName.trim().toLowerCase().contains(s.toString().trim().toLowerCase()) || hotel.city.contains(s.toString().trim().toLowerCase())).collect(Collectors.toList());
                HotelAdapter hotelAdapter = new HotelAdapter(listOfHotels, AdminHotelEdit.this, -1, null, null);
                listView.setAdapter(hotelAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void onBurger(View view) {
        DrawerLayout b = findViewById(R.id.admin_edit_hotel);
        b.openDrawer(GravityCompat.START);
    }
}