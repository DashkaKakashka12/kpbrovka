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
import com.mgke.kpbrovka.auth.Authentication;

public class UserBurgerMenuSelect implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private NavigationView navigationView;

    public UserBurgerMenuSelect(Context context, NavigationView navigationView) {
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
                            .circleCrop()
                    )
                    .into(photo);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileEdit.class);
                if (intent != null) {
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        });
    }

    public UserBurgerMenuSelect(Context context) {
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent a = null;

        if (menuItem.getItemId() == R.id.user_profile) {
            a = new Intent(context, UserProfileEdit.class);
        } else if (menuItem.getItemId() == R.id.find) {
            a = new Intent(context, MainUserActivity.class);
        } else if (menuItem.getItemId() == R.id.saved) {
            a = new Intent(context, SavedUserActivity.class);
        } else if (menuItem.getItemId() == R.id.bookings) {
            a = new Intent(context, MainUserActivity.class);
        }


        if (a != null) {
            context.startActivity(a);
            ((Activity) context).finish();
        }

        return true;
    }
}
