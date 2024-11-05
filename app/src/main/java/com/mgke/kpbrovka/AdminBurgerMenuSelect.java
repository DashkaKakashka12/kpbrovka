package com.mgke.kpbrovka;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;

public class AdminBurgerMenuSelect implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;

    public AdminBurgerMenuSelect(Context context) {
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent a = null;


        if (menuItem.getItemId() == R.id.profile) {
            a = new Intent(context, AdminEditHotel.class);
        } else if (menuItem.getItemId() == R.id.user_profile) {
            a = new Intent(context, AdminEditHotel.class);
        } else if (menuItem.getItemId() == R.id.hotel) {
            a = new Intent(context, AdminEditHotel.class);
        } else if (menuItem.getItemId() == R.id.rooms) {
            a = new Intent(context, AdminEditHotel.class);
        } else if (menuItem.getItemId() == R.id.dates) {
            a = new Intent(context, AdminEditHotel.class);
        }else if (menuItem.getItemId() == R.id.bookings) {
            a = new Intent(context, AdminEditHotel.class);
        }else if (menuItem.getItemId() == R.id.hoteliers) {
            a = new Intent(context, AdminChekBronist.class);
        }else if (menuItem.getItemId() == R.id.visitor_registration) {
            a = new Intent(context, AdminEditHotel.class);
        }


        if (a != null) {
            context.startActivity(a);
            ((Activity) context).finish();
        }

        return true;
    }
}