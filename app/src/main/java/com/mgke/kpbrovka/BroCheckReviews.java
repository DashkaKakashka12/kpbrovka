package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.ReviewsAdapter;
import com.mgke.kpbrovka.model.Review;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.ReviewRepository;

public class BroCheckReviews extends AppCompatActivity {

    private ReviewRepository reviewRepository;

    private HotelRepository hotelRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_check_reviews);
        reviewRepository = new ReviewRepository(FirebaseFirestore.getInstance());
        hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        String id = getIntent().getStringExtra("id");

        ProgressBar progressBar1 = findViewById(R.id.progressBar1);
        ProgressBar progressBar2 = findViewById(R.id.progressBar2);
        ProgressBar progressBar3 = findViewById(R.id.progressBar3);
        ProgressBar progressBar4 = findViewById(R.id.progressBar4);
        ProgressBar progressBar5 = findViewById(R.id.progressBar5);
        TextView int1 = findViewById(R.id.int1);
        TextView int2 = findViewById(R.id.int2);
        TextView int3 = findViewById(R.id.int3);
        TextView int4 = findViewById(R.id.int4);
        TextView int5 = findViewById(R.id.int5);
        TextView countOfReviews = findViewById(R.id.countOfReviews);
        TextView mark = findViewById(R.id.mark);


        RecyclerView recyclerView = findViewById(R.id.listOfReviews);
        reviewRepository.getReviewsByHotelId(id).thenAccept(list -> {
            ReviewsAdapter adapter = new ReviewsAdapter(list);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            if (list.size() == 0){
                return;
            }

            int[] mass = new int[5];
            for (int i = 0; i < list.size(); i++){
                mass[0] += list.get(i).valueForMoney;
                mass[1] += list.get(i).comfort;
                mass[2] += list.get(i).cleanness;
                mass[3] += list.get(i).staff;
                mass[4] += list.get(i).facilities;
            }


            int1.setText(String.valueOf((double) mass[0] / list.size()));
            int2.setText(String.valueOf((double) mass[1] / list.size()));
            int3.setText(String.valueOf((double) mass[2] / list.size()));
            int4.setText(String.valueOf((double) mass[3] / list.size()));
            int5.setText(String.valueOf((double) mass[4] / list.size()));

            progressBar1.setProgress(mass[0] / list.size());
            progressBar2.setProgress(mass[1] / list.size());
            progressBar3.setProgress(mass[2] / list.size());
            progressBar4.setProgress(mass[3] / list.size());
            progressBar5.setProgress(mass[4] / list.size());
        });

        TextView hotelName = findViewById(R.id.hotelName);

        hotelRepository.getHotelById(id).thenAccept(hotel ->{
           hotelName.setText(hotel.hotelName);
            ReviewRepository reviewRepository = new ReviewRepository(FirebaseFirestore.getInstance());
            reviewRepository.getReviewsByHotelId(hotel.id).thenAccept(list -> {
                countOfReviews.setText(list.size() + " отзывов");
                double averageStars = list.stream().mapToDouble(Review::getStars).average().orElse(0.0);
                String formattedAverage = String.format("%.1f", averageStars);
                mark.setText(formattedAverage);
            });
        });


    }

    public void hotelEdit (View b){
        Intent a = new Intent(this, BroHotelEdit.class);
        startActivity(a);
        finish();
    }



}

