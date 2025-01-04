package com.mgke.kpbrovka;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import com.yandex.mapkit.geometry.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.FacilitiesAdapter;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.model.Review;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.HotelRoomRepository;
import com.mgke.kpbrovka.repository.ReviewRepository;
import com.mgke.kpbrovka.repository.UserRepository;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

import per.wsj.library.AndRatingBar;

public class UserHotel extends AppCompatActivity {

    private Hotel currentHotel;
    private InputListener inputListener;
    private UserRepository userRepository;
    private MapView mapView;
    private PlacemarkMapObject placeMark = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Maps.isMapInitialized){
            MapKitFactory.setApiKey("7a8bcddd-0717-4d1a-a0cb-62cf7170e1b6");
            MapKitFactory.initialize(this);
            Maps.isMapInitialized = true;
        }

        setContentView(R.layout.activity_user_hotel);

        userRepository = new UserRepository(FirebaseFirestore.getInstance());
        mapView = findViewById(R.id.mapsphoto);

        String id = getIntent().getStringExtra("HOTELID");
        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        hotelRepository.getHotelById(id).thenAccept(hotel -> {
            currentHotel = hotel;
            setValue();
            setMap();
        });
    }

    private void setMap() {
        Map map = mapView.getMap();

        map.setRotateGesturesEnabled(false);
        map.setZoomGesturesEnabled(false);
        map.setScrollGesturesEnabled(false);
        map.setTiltGesturesEnabled(false);

        Point point = new Point(currentHotel.coordinates.x, currentHotel.coordinates.y);
        map.move(new CameraPosition(point, 15.0f, 0.0f, 0.0f));

        ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.geo4);
        placeMark = map.getMapObjects().addPlacemark(point, imageProvider);
        inputListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                maps();
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                maps();
            }
        };
        map.addInputListener(inputListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    private void setValue() {
        ImageView photo = findViewById(R.id.photo);
        TextView name = findViewById(R.id.hotelName);
        TextView city = findViewById(R.id.city);
        TextView adress = findViewById(R.id.adress);
        TextView countOfReviews = findViewById(R.id.countOfReviews);
        TextView mark = findViewById(R.id.mark);
        TextView cost = findViewById(R.id.find);

        AndRatingBar stars = findViewById(R.id.stars);
        AndRatingBar stars2 = findViewById(R.id.stars2);
        ImageView userIcon = findViewById(R.id.userIcon);
        ImageView userIcon2 = findViewById(R.id.userIcon2);
        TextView nameAndData = findViewById(R.id.userNameAndData);
        TextView nameAndData2 = findViewById(R.id.userNameAndData2);
        TextView textOfReview = findViewById(R.id.textOfReview);
        TextView textOfReview2 = findViewById(R.id.textOfReview2);
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

        name.setText(currentHotel.hotelName);
        adress.setText(currentHotel.adress);
        city.setText(currentHotel.city + " , ");

        ReviewRepository reviewRepository = new ReviewRepository(FirebaseFirestore.getInstance());
        reviewRepository.getReviewsByHotelId(currentHotel.id).thenAccept(list -> {
            countOfReviews.setText(list.size() + " отзывов");
            double averageStars = list.stream().mapToDouble(Review::getStars).average().orElse(0.0);
            String formattedAverage = String.format("%.1f", averageStars);
            mark.setText(formattedAverage);
        });
        HotelRoomRepository hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
        hotelRoomRepository.getMinCostByHotelId(currentHotel.id).thenAccept(costValue -> {
            cost.setText("Выбрать номера от " + costValue + " BYN");
        });

        FacilitiesAdapter adapter = new FacilitiesAdapter(currentHotel.facilities);
        RecyclerView facilities = findViewById(R.id.facilities);
        facilities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        facilities.setAdapter(adapter);

        reviewRepository.getReviewsByHotelId(currentHotel.id).thenAccept(list -> {
            if (list.size() >= 2){
                findViewById(R.id.review1).setVisibility(View.VISIBLE);
                findViewById(R.id.review2).setVisibility(View.VISIBLE);
            }
            Review review1 = list.get(0);
            Review review2 = list.get(1);

            stars.setNumStars(review1.stars);
            stars2.setNumStars(review2.stars);

            textOfReview.setText(review1.text);
            textOfReview2.setText(review2.text);


            userRepository.getUserById(review1.userId).thenAccept(user -> {
                if (user.photo != null){
                    Glide.with(this).load(user.photo).apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop()).into(userIcon);
                }

                nameAndData.setText(user.name + "\n" + formatTimestamp(review1.dataCreation));
            });
            userRepository.getUserById(review2.userId).thenAccept(user -> {
                if (user.photo != null){
                    Glide.with(this).load(user.photo).apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop()).into(userIcon2);
                }

                nameAndData2.setText(user.name + "\n" + formatTimestamp(review2.dataCreation));
            });


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


        if (currentHotel.photos != null) {
            Glide.with(this)
                    .load(currentHotel.photos)
                    .apply(new RequestOptions()
                            .override(Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .transform(new RoundedCorners(16))
                    )
                    .into(photo);
        }

    }

    public static String formatTimestamp(Timestamp timestamp) {
        Date date = timestamp.toDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        return dateFormat.format(date);
    }

    public void back (View view){
        finish();
    }

    public void maps() {
        String uri = "yandexmaps://maps.yandex.ru/?pt=" + currentHotel.coordinates.y  + "," + currentHotel.coordinates.x + ",pm2rdm";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("ru.yandex.yandexmaps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            String browserUri = "https://yandex.ru/maps/?pt=" + currentHotel.coordinates.y  + "," + currentHotel.coordinates.x + "&z=12&l=map";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(browserUri));
            startActivity(browserIntent);
        }
    }
}