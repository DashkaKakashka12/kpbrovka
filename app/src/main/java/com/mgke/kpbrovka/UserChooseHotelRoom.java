package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.UserHotelRoomAdapter;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.HotelRoomRepository;

import java.util.Calendar;
import java.util.Date;

public class UserChooseHotelRoom extends AppCompatActivity {
    private Date start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_choose_hotel_room);
        int countOfPeople = getIntent().getIntExtra("countOfPeople", -1);
        String id = getIntent().getStringExtra("id");
        start = (Date) getIntent().getSerializableExtra("START");
        end = (Date) getIntent().getSerializableExtra("END");
        TextView dates = findViewById(R.id.dates);

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendarStart.setTime(start);
        calendarEnd.setTime(end);

        String startDate = String.format("%02d.%02d", calendarStart.get(Calendar.DAY_OF_MONTH), calendarStart.get(Calendar.MONTH) + 1);

        String endDate = String.format("%02d.%02d", calendarEnd.get(Calendar.DAY_OF_MONTH), calendarEnd.get(Calendar.MONTH) + 1);

        dates.setText(startDate + " - " + endDate);

        HotelRoomRepository hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
        hotelRoomRepository.getAllHotelRoomsByParametrs(id, countOfPeople, start, end).thenAccept(list ->{
            UserHotelRoomAdapter userHotelRoomAdapter = new UserHotelRoomAdapter(list, countOfPeople, start, end);
            RecyclerView recyclerView = findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(userHotelRoomAdapter);
        });
        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        hotelRepository.getHotelById(id).thenAccept(hotel -> {
           TextView name = findViewById(R.id.name);
           TextView count = findViewById(R.id.count);
           name.setText(hotel.hotelName);
           count.setText(String.valueOf(countOfPeople));

        });
    }


    public void back(View view) {
        finish();
    }
}