package com.mgke.kpbrovka;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class BroHotelEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_hotel_edit);


        NavigationView navigationView = findViewById(R.id.navigationMenu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent a = null;

                if (menuItem.getItemId() == R.id.profile) {
                    a = new Intent(BroHotelEdit.this, BroHotelEdit.class);
                } else if (menuItem.getItemId() == R.id.user_profile) {
                    a = new Intent(BroHotelEdit.this, BroHotelEdit.class);
                } else if (menuItem.getItemId() == R.id.hotel) {
                    a = new Intent(BroHotelEdit.this, BroHotelEdit.class);
                } else if (menuItem.getItemId() == R.id.rooms) {
                    a = new Intent(BroHotelEdit.this, BroChooseHotelRoomForEdit.class);
                } else if (menuItem.getItemId() == R.id.dates) {
                    a = new Intent(BroHotelEdit.this, BroHotelEdit.class);
                }else if (menuItem.getItemId() == R.id.booking_confirmation) {
                    a = new Intent(BroHotelEdit.this, BroHotelEdit.class);
                }else if (menuItem.getItemId() == R.id.cancel_booking) {
                    a = new Intent(BroHotelEdit.this, BroHotelEdit.class);
                }else if (menuItem.getItemId() == R.id.visitor_registration) {
                    a = new Intent(BroHotelEdit.this, BroHotelEdit.class);
                }

                startActivity(a);
                finish();
                return true;
            }
        });

    }



    public void broRenameHotel(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_rename_hotel, null);
        builder.setView(customView);
        builder.setTitle("Название")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void broRenameAddress(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_rename_address, null);
        builder.setView(customView);
        builder.setTitle("Адрес")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void broEditFacilities (View b){
        Intent a = new Intent(this, BroFacilitiesEdit.class);
        startActivity(a);
        finish();
    }


    public void onBurger(View view) {
        DrawerLayout b = findViewById(R.id.bro_hotel_edit);
        b.openDrawer(GravityCompat.START);
    }

    public void editReviews (View b){
        Intent a = new Intent(this, BroReviewsEdit.class);
        startActivity(a);
        finish();
    }

    public void checkReviews (View b){
        Intent a = new Intent(this, BroCheckReviews.class);
        startActivity(a);
        finish();
    }


}