package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.model.Review;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.HotelRoomRepository;
import com.mgke.kpbrovka.repository.ReservationRepository;
import com.mgke.kpbrovka.repository.ReviewRepository;

public class WriteReview extends AppCompatActivity {
    private Review review = new Review();
    private String reservationId;
    private ReviewRepository reviewRepository;
    private HotelRepository hotelRepository;
    private HotelRoomRepository hotelRoomRepository;
    private ReservationRepository reservationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
        reservationRepository = new ReservationRepository(FirebaseFirestore.getInstance());
        reviewRepository = new ReviewRepository(FirebaseFirestore.getInstance());
        String reviewId = getIntent().getStringExtra("reviewId");
        reservationId = getIntent().getStringExtra("reservationId");
        if (reviewId != null) {
            reviewRepository.getReviewById(reviewId).thenAccept(review1 -> {
                review = review1;
                setValue();
            });
        }
        TextView hotelName = findViewById(R.id.hotelName);

        reservationRepository.getReservationById(reservationId).thenAccept(reservation -> {
            hotelRoomRepository.getHotelRoomById(reservation.hotelRoomId).thenAccept(hotelRoom -> {
                hotelRepository.getHotelById(hotelRoom.hotelId).thenAccept(hotel -> {
                    hotelName.setText(hotel.hotelName);
                });
            });
        });
    }

    private void setValue() {

        TextView addReview = findViewById(R.id.addReview);
        RatingBar stars = findViewById(R.id.stars);
        RatingBar stars1 = findViewById(R.id.stars1);
        RatingBar stars2 = findViewById(R.id.stars2);
        RatingBar stars3 = findViewById(R.id.stars3);
        RatingBar stars4 = findViewById(R.id.stars4);
        RatingBar stars5 = findViewById(R.id.stars5);
        EditText textOfReview = findViewById(R.id.textOfReview);

        addReview.setText("Отредактировать отзыв");

        stars1.setRating(review.valueForMoney);
        stars2.setRating(review.comfort);
        stars3.setRating(review.cleanness);
        stars4.setRating(review.staff);
        stars5.setRating(review.facilities);
        stars.setRating(review.stars);

        textOfReview.setText(review.text);
    }

    public void back(View view) {
        Intent intent = new Intent(this, AllReservation.class);
        intent.putExtra("RESERVATION", reservationId);
        startActivity(intent);
        finish();
    }

    public void addReview(View view) {
        RatingBar stars = findViewById(R.id.stars);
        RatingBar stars1 = findViewById(R.id.stars1);
        RatingBar stars2 = findViewById(R.id.stars2);
        RatingBar stars3 = findViewById(R.id.stars3);
        RatingBar stars4 = findViewById(R.id.stars4);
        RatingBar stars5 = findViewById(R.id.stars5);
        EditText textOfReview = findViewById(R.id.textOfReview);

        review.valueForMoney = (int)stars1.getRating();
        review.comfort = (int)stars2.getRating();
        review.cleanness = (int)stars3.getRating();
        review.staff = (int)stars4.getRating();
        review.facilities = (int)stars5.getRating();
        review.stars = (int)stars.getRating();
        review.text = textOfReview.getText().toString();


        review.userId = Authentication.user.id;
        review.dataCreation = Timestamp.now();
        review.reservationId = reservationId;

        if (review.id == null) reviewRepository.addReview(review);
        else reviewRepository.updateReview(review);

        Intent intent = new Intent(this, AllReservation.class);
        intent.putExtra("RESERVATION", reservationId);
        startActivity(intent);
        finish();
    }
}