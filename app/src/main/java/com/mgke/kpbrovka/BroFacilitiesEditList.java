package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BroFacilitiesEditList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_fatcilities_edit_list);
    }

    public void backToBroHotelEdit (View b){
        Intent a = new Intent(this, BroFacilitiesEdit.class);
        startActivity(a);
        finish();
    }
}
