package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.model.HotelRoom;
import com.mgke.kpbrovka.model.Review;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.HotelRoomRepository;
import com.mgke.kpbrovka.repository.ReviewRepository;

public class UserBooking extends AppCompatActivity {

    private HotelRoom hotelRoom;
    private Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_booking);
        String id = getIntent().getStringExtra("ROOMID");
        HotelRoomRepository hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
        hotelRoomRepository.getHotelRoomById(id).thenAccept(hotelRoom1 -> {
            hotelRoom = hotelRoom1;
            HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
            hotelRepository.getHotelById(hotelRoom.hotelId).thenAccept(hotel1 -> {
                hotel = hotel1;
                setValue();
            });
        });
    }

    private void setValue() {
        TextView nameHotel = findViewById(R.id.nameHotel);
        TextView nameHotel2 = findViewById(R.id.nameHotel2);
        TextView mark = findViewById(R.id.mark);
        TextView countOfReviews = findViewById(R.id.countOfReviews);
        TextView nameRoom = findViewById(R.id.nameRoom);
        TextView typeOfBed = findViewById(R.id.typeOfBed);
        TextView countOfPeople = findViewById(R.id.countOfPeople);
        TextView city = findViewById(R.id.city);
        TextView adress = findViewById(R.id.adress);
        TextView cost = findViewById(R.id.cost);
        ImageView photoOfNumber = findViewById(R.id.photoOfNumber);


        nameHotel.setText(hotel.hotelName);
        nameHotel2.setText(hotel.hotelName);
        nameRoom.setText(hotelRoom.name);
        typeOfBed.setText(hotelRoom.typeOfBed);
        countOfPeople.setText(String.valueOf(hotelRoom.countOfPeople + " основных места"));
        city.setText(hotel.city + ", ");
        adress.setText(hotel.adress);
        cost.setText(String.valueOf(hotelRoom.costWithout + " BYN"));

        ReviewRepository reviewRepository = new ReviewRepository(FirebaseFirestore.getInstance());
        reviewRepository.getReviewsByHotelId(hotel.id).thenAccept(list -> {
            countOfReviews.setText(list.size() + " отзывов");
            double averageStars = list.stream().mapToDouble(Review::getStars).average().orElse(0.0);
            String formattedAverage = String.format("%.1f", averageStars);
            mark.setText(formattedAverage);
        });

        if (hotelRoom.photos != null) {
            Glide.with(this)
                    .load(hotelRoom.photos)
                    .apply(new RequestOptions()
                            .override(Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .transform(new RoundedCorners(16))
                    )
                    .into(photoOfNumber);
        }

    }

    public void back(View view) {
        finish();
    }
}