package com.mgke.kpbrovka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.view.MenuItem;
import android.view.View;


import com.google.android.material.navigation.NavigationView;

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
        } else if (menuItem.getItemId() == R.id.visitor_registration) {
            intent = new Intent(context, BroReservationEdit.class);
        }

        if (intent != null) {
            context.startActivity(intent);
            ((Activity) context).finish();
        }

        return true;
    }
}
