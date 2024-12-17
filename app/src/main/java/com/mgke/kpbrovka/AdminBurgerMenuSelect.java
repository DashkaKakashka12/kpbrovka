package com.mgke.kpbrovka;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
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
        TextView name = view.findViewById(R.id.titleUser);
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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminProfileEdit.class);
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
            a = new Intent(context, AdminProfileEdit.class);
        } else if (menuItem.getItemId() == R.id.hotel) {
            a = new Intent(context, AdminHotelEdit.class);
        } else if (menuItem.getItemId() == R.id.dates) {
            a = new Intent(context, AdminHotelEdit.class);
        }else if (menuItem.getItemId() == R.id.bookings) {
            a = new Intent(context, AdminHotelEdit.class);
        }else if (menuItem.getItemId() == R.id.hoteliers) {
            a = new Intent(context, AdminChekBronist.class);
        }


        if (a != null) {
            context.startActivity(a);
            ((Activity) context).finish();
        }

        return true;
    }
}