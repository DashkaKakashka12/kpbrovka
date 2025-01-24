package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.ReservationAdapter;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.HotelRoomRepository;
import com.mgke.kpbrovka.repository.ReservationRepository;

public class UserBookingHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking_history);

        NavigationView navigationView = findViewById(R.id.navigationMenu1);
        UserBurgerMenuSelect navigationListener = new UserBurgerMenuSelect(this, navigationView);
        navigationView.setNavigationItemSelectedListener(navigationListener);



        ReservationRepository reservationRepository = new ReservationRepository(FirebaseFirestore.getInstance());
        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        HotelRoomRepository hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());

        reservationRepository.getReservationsByUserId(Authentication.user.id).thenAccept(reservations -> {
            ReservationAdapter adapter = new ReservationAdapter(reservations);
            RecyclerView recyclerView = findViewById(R.id.rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        });
    }


    public void onBurger(View view) {
        DrawerLayout b = findViewById(R.id.user_booking_history);
        b.openDrawer(GravityCompat.START);
    }



}