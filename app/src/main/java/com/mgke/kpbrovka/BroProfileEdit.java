package com.mgke.kpbrovka;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class BroProfileEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_profile_edit);


    }

    public void broEditName(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_edit_name, null);
        builder.setView(customView);
        builder.setTitle("Имя")
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

    public void broEditPassword(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_edit_password, null);
        builder.setView(customView);
        builder.setTitle("Имя")
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

    public void broEditEmail(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_edit_email, null);
        builder.setView(customView);
        builder.setTitle("Имя")
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

    public void exit (View b){
        Intent a = new Intent(this, LoginActivity.class);
        startActivity(a);
        finish();
    }


    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int menuId = menuItem.getItemId();

        if (menuId == R.id.profile) {
            Intent profileIntent = new Intent(this, BroProfileEdit.class);
            startActivity(profileIntent);
        } else if (menuId == R.id.hotel) {
            Intent userProfileIntent = new Intent(this, BroHotelEdit.class);
            startActivity(userProfileIntent);
        } if (menuId == R.id.rooms) {
            Intent profileIntent = new Intent(this, BroChooseHotelRoomForEdit.class);
            startActivity(profileIntent);
        } else if (menuId == R.id.dates) {
            Intent userProfileIntent = new Intent(this, BroReservationEdit.class);
            startActivity(userProfileIntent);
        } if (menuId == R.id.bookings) {
            Intent profileIntent = new Intent(this, BroReservationEdit.class);
            startActivity(profileIntent);
        } else if (menuId == R.id.visitor_registration) {
            Intent userProfileIntent = new Intent(this, BroReservationEdit.class);
            startActivity(userProfileIntent);
        }
        else {
            return false;
        }
        return true;
    }
}

