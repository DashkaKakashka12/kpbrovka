package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.HotelAdapter;
import com.mgke.kpbrovka.repository.HotelRepository;

public class UserShowHotels extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_show_hotels);

        String param = getIntent().getStringExtra("PARAMETR");
        String value = getIntent().getStringExtra("VALUE");

        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        hotelRepository.getHotelsByParametr(param, value).thenAccept(list -> {
            ListView listView = findViewById(R.id.list);
            HotelAdapter adapter = new HotelAdapter(this, list);
            listView.setAdapter(adapter);
        });
    }

    public void back(View view) {
        Intent intent = new Intent(this, MainUserActivity.class);
        startActivity(intent);
        finish();
    }

    public void openFindDialog(View view) {
        MyBottomSheetDialog myBottomSheetDialog = new MyBottomSheetDialog(this);
        myBottomSheetDialog.show();
    }
}