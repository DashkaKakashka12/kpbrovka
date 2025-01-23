package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.HotelAdapter;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.repository.LikeRepository;

public class SavedUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_user);

        NavigationView navigationView = findViewById(R.id.navigationMenuUser);
        UserBurgerMenuSelect navigationListener = new UserBurgerMenuSelect(this);
        navigationView.setNavigationItemSelectedListener(navigationListener);
        LikeRepository likeRepository = new LikeRepository(FirebaseFirestore.getInstance());
        likeRepository.getAllLikeHotel(Authentication.user.id).thenAccept(hotelList -> {
            RecyclerView recyclerView = findViewById(R.id.rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            HotelAdapter hotelAdapter = new HotelAdapter(hotelList, this, 2, null, null);
            recyclerView.setAdapter(hotelAdapter);
        });
    }

    public void onBurger(View view) {
        DrawerLayout b = findViewById(R.id.saved_user);
        b.openDrawer(GravityCompat.START);
    }

}