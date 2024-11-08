package com.mgke.kpbrovka;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BroEditHotelRoomFacilities extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_edit_hotel_room_facilities);
    }

    public void broChooseHotelRoomMainFacilities(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.activity_dialog_bro_choose_hotel_room_main_facilities, null);
        builder.setView(customView);
        builder.setTitle("Основные удобства")
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


    public void broEditFacilitiesList2 (View b){
        Intent a = new Intent(this, BroEditDescriptionHotelRoom.class);
        startActivity(a);
        finish();
    }


    public void backToBroHotelEdit (View b){
        Intent a = new Intent(this, BroHotelRoomEdit.class);
        startActivity(a);
        finish();
    }
}