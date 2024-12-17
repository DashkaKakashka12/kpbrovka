package com.mgke.kpbrovka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.UserRepository;

public class BroBurgerMenuSelect implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private NavigationView navigationView;

    public BroBurgerMenuSelect(Context context, NavigationView navigationView) {
        this.context = context;
        this.navigationView = navigationView;
        setupMenu();
    }

    private void setupMenu() {
        View view = navigationView.getHeaderView(0);
        TextView name = view.findViewById(R.id.title);
        name.setText(Authentication.user.name);
        ImageView photo = view.findViewById(R.id.icon);

        if (Authentication.user.photo != null) {
            Glide.with(context)
                    .load(Authentication.user.photo)
                    .apply(new RequestOptions()
                            .override(Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .transform(new RoundedCorners(16))
                    )
                    .into(photo);
        }

        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        hotelRepository.getHotelByUserId(Authentication.user.id).thenAccept(hotel -> {
            TextView hotelName = view.findViewById(R.id.subtitle);
            hotelName.setText("Бронист " + hotel.hotelName);
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BroProfileEdit.class);
                if (intent != null) {
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        });


    }



    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent intent = null;

        if (menuItem.getItemId() == R.id.user_profile) {
            intent = new Intent(context, BroProfileEdit.class);
        } else if (menuItem.getItemId() == R.id.hotel) {
            intent = new Intent(context, BroHotelEdit.class);
        } else if (menuItem.getItemId() == R.id.rooms) {
            intent = new Intent(context, BroChooseHotelRoomForEdit.class);
        } else if (menuItem.getItemId() == R.id.dates) {
            intent = new Intent(context, BroReservationEdit.class);
        } else if (menuItem.getItemId() == R.id.bookings) {
            intent = new Intent(context, BroReservationEdit.class);
        }

        if (intent != null) {
            context.startActivity(intent);
            ((Activity) context).finish();

        }

        return true;
    }
}
