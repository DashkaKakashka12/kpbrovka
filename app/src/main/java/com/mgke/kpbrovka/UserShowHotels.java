package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.HotelAdapter;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.repository.HotelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class UserShowHotels extends AppCompatActivity {
    String param, value;
    int peopleCount;
    List<Hotel> firstHotelList = new ArrayList<>();
    List<Hotel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_show_hotels);

        param = getIntent().getStringExtra("PARAMETR");
        value = getIntent().getStringExtra("VALUE");
        peopleCount = getIntent().getIntExtra("COUNT", 2);

        TextView nameOfCategory = findViewById(R.id.nameOfCategory);
        if (param.equals("nameAndCity")) nameOfCategory.setText(value);
        else nameOfCategory.setText(getCategory(value));
        TextView countOfPeople = findViewById(R.id.count);
        countOfPeople.setText(String.valueOf(peopleCount));

        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        hotelRepository.getHotelsByParametr(param, value).thenAccept(list -> {
            firstHotelList.addAll(list);
            ListView listView = findViewById(R.id.list);
            HotelAdapter adapter = new HotelAdapter(this, list);
            listView.setAdapter(adapter);

        });

        ListView listView = findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UserShowHotels.this, UserHotel.class);
                intent.putExtra("HOTELID", list.get(position).id);
                startActivity(intent);
                finish();
            }
        });

    }

    public void back(View view) {
        Intent intent = new Intent(this, MainUserActivity.class);
        startActivity(intent);
        finish();
    }

    public void openFindDialog(View view) {
        MyBottomSheetDialog myBottomSheetDialog = new MyBottomSheetDialog(this, param, value, (findStr, count) -> {
            ListView listView = findViewById(R.id.list);
            TextView text = findViewById(R.id.notFind);
            text.setVisibility(View.GONE);
            list = firstHotelList.stream().filter(x -> {
                switch (param){
                    case "city":
                        return x.hotelName.toLowerCase().contains(findStr.toLowerCase());
                    case "facility":
                        return x.hotelName.toLowerCase().contains(findStr.toLowerCase()) || x.city.toLowerCase().contains(findStr.toLowerCase());
                    default: return false;
                }
            }).collect(Collectors.toList());
            if (list.size() == 0){
                text.setVisibility(View.VISIBLE);
            }
            HotelAdapter adapter = new HotelAdapter(this, list);
            listView.setAdapter(adapter);


            TextView nameOfCategory = findViewById(R.id.nameOfCategory);
            if (!findStr.equals("")) nameOfCategory.setText(findStr);
            TextView countOfPeople = findViewById(R.id.count);
            countOfPeople.setText(String.valueOf(count));
        });
        myBottomSheetDialog.show();
    }

    static public String getCategory(String str) {
        switch (str){
            case "Бассейн":
                return ("Cиние Оазисы");
            case "Тренажёрный зал":
                return ("Здоровье и комфорт");
            case "Спа-центр":
                return ("Секреты спокойствия");
            case "Ресторан":
                return ("Вкусовая симфония");
        }
        return "Отель в городе " + str;
    }

}