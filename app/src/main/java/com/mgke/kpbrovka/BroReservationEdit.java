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
import com.mgke.kpbrovka.model.Reservation;
import com.mgke.kpbrovka.model.StatusReservation;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.HotelRoomRepository;
import com.mgke.kpbrovka.repository.ReservationRepository;
import com.mgke.kpbrovka.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BroReservationEdit extends AppCompatActivity {

    private View indicator1, indicator3;
    private List<Reservation> reservationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_reservation_edit);

        NavigationView navigationView = findViewById(R.id.navigationMenu);
        BroBurgerMenuSelect navigationListener = new BroBurgerMenuSelect(this, navigationView);
        navigationView.setNavigationItemSelectedListener(navigationListener);

        ReservationRepository reservationRepository = new ReservationRepository(FirebaseFirestore.getInstance());
        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        HotelRoomRepository hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());

       hotelRepository.getHotelByUserId(Authentication.user.id).thenAccept(hotel -> {
           hotelRoomRepository.getHotelRoomByHotelId(hotel.id).thenAccept(hotelRooms -> {
              reservationRepository.getReservationByHotelRoom(hotelRooms).thenAccept(reservations -> {
                  reservationList.addAll(reservations);
                  ReservationAdapter adapter = new ReservationAdapter(reservationList.stream().filter(x -> {
                      if (x.status == StatusReservation.INPROGRESS) return true;
                      return false;
                  }).collect(Collectors.toList()));
                  RecyclerView recyclerView = findViewById(R.id.rv);
                  recyclerView.setLayoutManager(new LinearLayoutManager(this));
                  recyclerView.setAdapter(adapter);
                  setClick();
              });
           });
       });
    }

    public void onBurger(View view) {
        DrawerLayout b = findViewById(R.id.bro_reservation_edit);
        b.openDrawer(GravityCompat.START);
    }

    private void setClick(){
        indicator1 = findViewById(R.id.indecator1);
        indicator3 = findViewById(R.id.indecator3);

        indicator1.setVisibility(View.VISIBLE);
        indicator3.setVisibility(View.INVISIBLE);

        findViewById(R.id.text1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicator1.setVisibility(View.VISIBLE);
                indicator3.setVisibility(View.INVISIBLE);

                ReservationAdapter adapter = new ReservationAdapter(reservationList.stream().filter(x -> {
                    if (x.status == StatusReservation.INPROGRESS) return true;
                    return false;
                }).collect(Collectors.toList()));
                RecyclerView recyclerView = findViewById(R.id.rv);
                recyclerView.setLayoutManager(new LinearLayoutManager(BroReservationEdit.this));
                recyclerView.setAdapter(adapter);
            }
        });

        findViewById(R.id.text3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicator3.setVisibility(View.VISIBLE);
                indicator1.setVisibility(View.INVISIBLE);

                ReservationAdapter adapter = new ReservationAdapter(reservationList.stream().filter(x -> {
                    if (x.status == StatusReservation.INPROGRESS) return false;
                    return true;
                }).collect(Collectors.toList()));
                RecyclerView recyclerView = findViewById(R.id.rv);
                recyclerView.setLayoutManager(new LinearLayoutManager(BroReservationEdit.this));
                recyclerView.setAdapter(adapter);
            }
        });


    }
}