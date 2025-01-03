package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.BroChooseRoomAdapter;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.model.HotelRoom;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.HotelRoomRepository;

public class BroChooseHotelRoomForEdit extends AppCompatActivity {
    private HotelRoomRepository hotelRoomRepository;
    private HotelRepository hotelRepository;
    private Hotel hotel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_choose_hotel_room_for_edit);
        hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
        hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());

        String id = getIntent().getStringExtra("HOTELID");
        if (id == null){
            hotelRepository.getHotelByUserId(Authentication.user.id).thenAccept(userHotel -> {
                hotel = userHotel;
                findViewById(R.id.back).setVisibility(View.GONE);
                hotelRoomRepository.getHotelRoomByHotelId(hotel.id).thenAccept(hotelRooms -> {
                    ListView listView = findViewById(R.id.listOfRooms);
                    BroChooseRoomAdapter adapter = new BroChooseRoomAdapter(this, hotelRooms);
                    listView.setAdapter(adapter);
                });
            });
        } else {
            hotelRepository.getHotelById(id).thenAccept(userHotel -> {
                hotel = userHotel;
                findViewById(R.id.menu).setVisibility(View.GONE);
                hotelRoomRepository.getHotelRoomByHotelId(hotel.id).thenAccept(hotelRooms -> {
                    ListView listView = findViewById(R.id.listOfRooms);
                    BroChooseRoomAdapter adapter = new BroChooseRoomAdapter(this, hotelRooms);
                    listView.setAdapter(adapter);
                });
            });
        }

        NavigationView navigationView = findViewById(R.id.navigationMenu);
        BroBurgerMenuSelect navigationListener = new BroBurgerMenuSelect(this, navigationView);
        navigationView.setNavigationItemSelectedListener(navigationListener);

    }

    public void broEditHotelRoom (View b){
        HotelRoom hotelRoom = new HotelRoom();

        hotelRoom.hotelId = hotel.id;
        String hotelRoomId = hotelRoomRepository.addHotelRoom(hotelRoom);

        Intent a = new Intent(this, BroHotelRoomEdit.class);
        a.putExtra("id", hotelRoomId);
        startActivity(a);
        finish();
    }

    public void burger(View view) {
        DrawerLayout b = findViewById(R.id.bro_choose_hotel_room_from_edit);
        b.openDrawer(GravityCompat.START);
    }

    public void back(View view) {
        Intent a = new Intent(this, BroHotelEdit.class);
        a.putExtra("HOTEL", hotel.id);
        startActivity(a);
        finish();
    }
}