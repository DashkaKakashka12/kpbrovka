package com.mgke.kpbrovka;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class AdminBurgerMenuSelect implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private NavigationView navigationView;

    public AdminBurgerMenuSelect(Context context, NavigationView navigationView) {
        this.context = context;
        this.navigationView = navigationView;
        setupMenu();
    }

    private void setupMenu() {
        View view = navigationView.getHeaderView(0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileUserActivity.class);
                if (intent != null) {
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        });
    }

    public AdminBurgerMenuSelect(Context context) {
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent a = null;


        if (menuItem.getItemId() == R.id.user_profile) {
            a = new Intent(context, AdminHotelEdit.class);
        } else if (menuItem.getItemId() == R.id.hotel) {
            a = new Intent(context, AdminHotelEdit.class);
        } else if (menuItem.getItemId() == R.id.rooms) {
            a = new Intent(context, AdminHotelEdit.class);
        } else if (menuItem.getItemId() == R.id.dates) {
            a = new Intent(context, AdminHotelEdit.class);
        }else if (menuItem.getItemId() == R.id.bookings) {
            a = new Intent(context, AdminHotelEdit.class);
        }else if (menuItem.getItemId() == R.id.hoteliers) {
            a = new Intent(context, AdminChekBronist.class);
        }else if (menuItem.getItemId() == R.id.visitor_registration) {
            a = new Intent(context, AdminHotelEdit.class);
        }


        if (a != null) {
            context.startActivity(a);
            ((Activity) context).finish();
        }

        return true;
    }
}