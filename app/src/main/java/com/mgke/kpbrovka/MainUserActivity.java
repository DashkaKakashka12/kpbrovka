package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.HotelMiniAdapter;
import com.mgke.kpbrovka.repository.HotelRepository;

import java.util.ArrayList;
import java.util.List;


public class MainUserActivity extends AppCompatActivity {

    private LinearLayout myLinearLayout;
    private TextView myTextView;

    int index = 0;

    private List<String> nameCity = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);


        NavigationView navigationView = findViewById(R.id.navigationMenuUser);
        UserBurgerMenuSelect navigationListener = new UserBurgerMenuSelect(this, navigationView);
        navigationView.setNavigationItemSelectedListener(navigationListener);

        RecyclerView recyclerView = findViewById(R.id.listOfHotels1);
        RecyclerView recyclerView2 = findViewById(R.id.listOfHotels2);
        RecyclerView recyclerView3 = findViewById(R.id.listOfHotels3);

        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        hotelRepository.getThreeHotelsByCity().thenAccept(list -> {
            nameCity.add(list.get(0).get(0).city);
            nameCity.add(list.get(1).get(0).city);
            nameCity.add(list.get(2).get(0).city);
            TextView firstCity = findViewById(R.id.firstCityText);
            TextView secondCity = findViewById(R.id.secondCityText);
            TextView thirdCity = findViewById(R.id.thirdCityText);
            firstCity.setText("Отели в городе " + nameCity.get(0));
            secondCity.setText("Отели в городе " + nameCity.get(1));
            thirdCity.setText("Отели в городе " + nameCity.get(2));

            HotelMiniAdapter adapter = new HotelMiniAdapter(list.get(0), this);
            HotelMiniAdapter adapter2 = new HotelMiniAdapter(list.get(1), this);
            HotelMiniAdapter adapter3 = new HotelMiniAdapter(list.get(2), this);
            recyclerView.setAdapter(adapter);
            recyclerView2.setAdapter(adapter2);
            recyclerView3.setAdapter(adapter3);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView3.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        });

        myLinearLayout = findViewById(R.id.find);
        myTextView = findViewById(R.id.findText);

        myLinearLayout.getLayoutParams().width = 100;
        myLinearLayout.setVisibility(View.VISIBLE);

        expandLinearLayout();

    }

    public void onBurger(View view) {
        DrawerLayout b = findViewById(R.id.main_user);
        b.openDrawer(GravityCompat.START);
    }

    private void expandLinearLayout() {
        ValueAnimator animator = ValueAnimator.ofInt(100, 860);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int width = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = myLinearLayout.getLayoutParams();
                layoutParams.width = width;
                myLinearLayout.setLayoutParams(layoutParams);
            }
        });
        animator.setDuration(1000);
        animator.start();

        animator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                myTextView.setVisibility(View.VISIBLE);
                displayText();
            }
        });
    }

    private void displayText() {
        String textToDisplay = "Найти отель";
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < textToDisplay.length()) {
                    myTextView.append(String.valueOf(textToDisplay.charAt(index)));
                    index++;
                    handler.postDelayed(this, 100);
                }
            }
        }, 100);
    }


    public void onShowHotel(View view) {
        Intent intent = new Intent(this, UserShowHotels.class);
        intent.putExtra("PARAMETR", "facility");
        if (view.getTag().equals("1")){
            intent.putExtra("VALUE", "Бассейн");
        } else  if (view.getTag().equals("2")) {
            intent.putExtra("VALUE", "Тренажёрный зал");
        } else  if (view.getTag().equals("3")) {
            intent.putExtra("VALUE", "Спа-центр");
        } else  if (view.getTag().equals("4")) {
            intent.putExtra("VALUE", "Ресторан");
        }

        startActivity(intent);
        finish();
    }

    public void onShowHotelByCity(View view) {
        Intent intent = new Intent(this, UserShowHotels.class);
        intent.putExtra("PARAMETR", "city");
        if (view.getTag().equals("1")){
            intent.putExtra("VALUE", nameCity.get(0));
        } else  if (view.getTag().equals("2")) {
            intent.putExtra("VALUE", nameCity.get(1));
        } else  if (view.getTag().equals("3")) {
            intent.putExtra("VALUE", nameCity.get(2));
        }

        startActivity(intent);
        finish();
    }
}