package com.mgke.kpbrovka;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.StringValue;
import com.mgke.kpbrovka.adapter.FacilitiesAdapter;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.model.Review;
import com.mgke.kpbrovka.model.UserType;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.ReviewRepository;
import com.mgke.kpbrovka.repository.UserRepository;
import com.yalantis.ucrop.UCrop;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import android.Manifest;
import android.widget.Toast;


import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import per.wsj.library.AndRatingBar;

public class BroHotelEdit extends AppCompatActivity {

    private HotelRepository hotelRepository;
    private Hotel hotel;
    private InputListener inputListener;
    private FacilitiesAdapter adapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private ReviewRepository reviewRepository;

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
        setContentView(R.layout.activity_bro_hotel_edit);

        NavigationView navigationView = findViewById(R.id.navigationMenu);
        BroBurgerMenuSelect navigationListener = new BroBurgerMenuSelect(this, navigationView);
        navigationView.setNavigationItemSelectedListener(navigationListener);

        hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        reviewRepository = new ReviewRepository(FirebaseFirestore.getInstance());
        userRepository = new UserRepository(FirebaseFirestore.getInstance());
        mapView = findViewById(R.id.mapsphoto);

        String id = getIntent().getStringExtra("HOTEL");
        if(id == null){
            findViewById(R.id.roomsButton).setVisibility(View.INVISIBLE);
            hotelRepository.getHotelByUserId(Authentication.user.id).thenAccept(hotel -> {
                this.hotel = hotel;
                setUpValue ();
                setMap();
                findViewById(R.id.back).setVisibility(View.GONE);
            });
        } else {
            hotelRepository.getHotelById(id).thenAccept(hotel -> {
                this.hotel = hotel;
                setUpValue ();
                setMap();
                findViewById(R.id.menu).setVisibility(View.GONE);

            });
        }


        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            startCrop(imageUri);
                            ImageView photo = findViewById(R.id.photo);
                            Glide.with(this)
                                    .load(imageUri)
                                    .apply(new RequestOptions()
                                            .override(Target.SIZE_ORIGINAL)
                                            .centerCrop()
                                            .transform(new RoundedCorners(16))
                                    )
                                    .into(photo);

                            String hotelId = hotel.id;
                            CloudinaryUploader uploader = new CloudinaryUploader(this);

                            uploader.uploadImage(imageUri, hotelId, new CloudinaryUploader.UploadCallback() {
                                @Override
                                public void onUploadComplete(String imageUrl) {
                                    if (imageUrl != null) {
                                        hotel.photos = imageUrl;
                                        hotelRepository.updateHotel(hotel);
                                    }
                                }
                            });
                        }
                    }
                });

    }

    private void setMap() {
        Map map = mapView.getMap();

        map.setRotateGesturesEnabled(false);
        map.setZoomGesturesEnabled(false);
        map.setScrollGesturesEnabled(false);
        map.setTiltGesturesEnabled(false);

        if (hotel.coordinates != null){

            Point point = new Point(hotel.coordinates.x, hotel.coordinates.y);
            map.move(new CameraPosition(point, 15.0f, 0.0f, 0.0f));

            ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.geo4);
            placeMark = map.getMapObjects().addPlacemark(point, imageProvider);
        }
        inputListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                map();
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                map();
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


    public void broRenameHotel(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialogBlack);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_rename_hotel, null);
        EditText editText = customView.findViewById(R.id.rename);
        editText.setText(hotel.hotelName);

        builder.setView(customView)
                .setTitle("Название")
                .setPositiveButton("ОК", null)
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button buttonOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            buttonOk.setOnClickListener(v -> {
                String newHotelName = editText.getText().toString().trim();
                editText.setError(null);

                if (newHotelName.isEmpty() || newHotelName.length() < 4) {
                    editText.setError("Название не может быть пустым и должно содержать минимум 4 символа.");
                    return;
                }

                hotel.hotelName = newHotelName;
                hotelRepository.updateHotel(hotel);
                TextView name = findViewById(R.id.broEditHotelName);
                name.setText(hotel.hotelName);
                NavigationView navigationView = findViewById(R.id.navigationMenu);
                View nav = navigationView.getHeaderView(0);
                TextView hotelName = nav.findViewById(R.id.subtitle);
                hotelName.setText("Отельер отеля " + hotel.hotelName);
                dialog.dismiss();
            });
        });

        dialog.show();
    }


    public void broRenameAddress(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialogBlack);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_rename_address, null);
        EditText editText = customView.findViewById(R.id.renameAdress);
        EditText editCity = customView.findViewById(R.id.renameCity);
        editText.setText(hotel.adress);
        editCity.setText(hotel.city);

        builder.setView(customView)
                .setTitle("Адрес")
                .setPositiveButton("ОК", null)
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button buttonOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            buttonOk.setOnClickListener(v -> {
                String newAddress = editText.getText().toString().trim();
                String newCity = editCity.getText().toString().trim();
                editText.setError(null);
                editCity.setError(null);

                if (newAddress.isEmpty() || newAddress.length() < 5) {
                    editText.setError("Адрес не может быть пустым и должен содержать минимум 5 символов.");
                    return;
                }
                if (newCity.isEmpty() ) {
                    editText.setError("Город не может быть пустым");
                    return;
                }

                hotel.adress = newAddress;
                hotel.city = newCity;
                hotel.isActive = true;
                hotelRepository.updateHotel(hotel);
                TextView adress = findViewById(R.id.broEditAdress);
                TextView city = findViewById(R.id.broEditCity);
                adress.setText(hotel.adress);
                city.setText(hotel.city);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    public void broEditFacilities(View b) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_choose_main_facilities, null);


        CheckBox[] checkBoxes = new CheckBox[]{
                customView.findViewById(R.id.checkBoxRestauran),
                customView.findViewById(R.id.checkBoxConditioning),
                customView.findViewById(R.id.checkBoxCard),
                customView.findViewById(R.id.checkBoxSport),
                customView.findViewById(R.id.checkBoxSpa),
                customView.findViewById(R.id.checkBoxWifi),
                customView.findViewById(R.id.checkBoxPool)
        };

        String[] facilities = {"Ресторан", "Кондиционер", "Оплата картой", "Тренажёрный зал", "Спа-центр", "Wifi", "Бассейн"};


        for (int i = 0; i < facilities.length; i++) {
            checkBoxes[i].setChecked(hotel.facilities.contains(facilities[i]));
        }

        builder.setView(customView);
        builder.setTitle("Удобства")
                .setPositiveButton("ОК", (dialog, which) -> {
                    List<String> selectedFacilities = new ArrayList<>();
                    for (int i = 0; i < checkBoxes.length; i++) {
                        if (checkBoxes[i].isChecked()) {
                            selectedFacilities.add(facilities[i]);
                        }
                    }
                    hotel.facilities = selectedFacilities;
                    hotelRepository.updateHotel(hotel);

                    adapter = new FacilitiesAdapter(hotel.facilities);
                    RecyclerView facilitiesView = findViewById(R.id.broEditFacilities);
                    facilitiesView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    facilitiesView.setAdapter(adapter);
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void onBurger(View view) {
        DrawerLayout b = findViewById(R.id.bro_hotel_edit);
        b.openDrawer(GravityCompat.START);
    }


    public void checkReviews (View b){
        Intent a = new Intent(this, BroCheckReviews.class);
        a.putExtra("id", hotel.id);
        startActivity(a);
        if (Authentication.user.type == UserType.ADMINISTRATOR) finish();
    }


    public void setUpValue() {
        TextView name = findViewById(R.id.broEditHotelName);
        RecyclerView facilities = findViewById(R.id.broEditFacilities);
        TextView adress = findViewById(R.id.broEditAdress);
        TextView city = findViewById(R.id.broEditCity);
        ImageView photo = findViewById(R.id.photo);
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

        name.setText(hotel.hotelName);
        adress.setText(hotel.adress);
        city.setText(hotel.city);

        if (hotel.photos != null) {
            Glide.with(this)
                    .load(hotel.photos)
                    .apply(new RequestOptions()
                            .override(Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .transform(new RoundedCorners(16))
                    )
                    .into(photo);
        }

        adapter = new FacilitiesAdapter(hotel.facilities);
        facilities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        facilities.setAdapter(adapter);

        reviewRepository.getReviewsByHotelId(hotel.id).thenAccept(list -> {
            if (list.size() >= 2) {
                findViewById(R.id.review1).setVisibility(View.VISIBLE);
                findViewById(R.id.review2).setVisibility(View.VISIBLE);
                Review review1 = list.get(0);
                Review review2 = list.get(1);

                stars.setRating(review1.stars);
                stars2.setRating(review2.stars);

                textOfReview.setText(review1.text);
                textOfReview2.setText(review2.text);

                userRepository.getUserById(review1.userId).thenAccept(user -> {
                    if (user.photo != null) {
                        Glide.with(this).load(user.photo).apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()).into(userIcon);
                    }

                    nameAndData.setText(user.name + "\n" + formatTimestamp(review1.dataCreation));
                });

                userRepository.getUserById(review2.userId).thenAccept(user -> {
                    if (user.photo != null) {
                        Glide.with(this).load(user.photo).apply(new RequestOptions()
                                .centerCrop()
                                .circleCrop()).into(userIcon2);
                    }

                    nameAndData2.setText(user.name + "\n" + formatTimestamp(review2.dataCreation));
                });
            }

            int[] mass = new int[5];
            for (int i = 0; i < list.size(); i++) {
                mass[0] += list.get(i).valueForMoney;
                mass[1] += list.get(i).comfort;
                mass[2] += list.get(i).cleanness;
                mass[3] += list.get(i).staff;
                mass[4] += list.get(i).facilities;
            }
            if (list.size() != 0 ) {

                DecimalFormat decimalFormat = new DecimalFormat("#.##");

                int1.setText(decimalFormat.format((double) mass[0] / list.size()));
                int2.setText(decimalFormat.format((double) mass[1] / list.size()));
                int3.setText(decimalFormat.format((double) mass[2] / list.size()));
                int4.setText(decimalFormat.format((double) mass[3] / list.size()));
                int5.setText(decimalFormat.format((double) mass[4] / list.size()));
            }

                progressBar1.setProgress((int) Math.round((double) mass[0] / list.size()));
                progressBar2.setProgress((int) Math.round((double) mass[1] / list.size()));
                progressBar3.setProgress((int) Math.round((double) mass[2] / list.size()));
                progressBar4.setProgress((int) Math.round((double) mass[3] / list.size()));
                progressBar5.setProgress((int) Math.round((double) mass[4] / list.size()));
        });
    }


    public static String formatTimestamp(Timestamp timestamp) {
        Date date = timestamp.toDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        return dateFormat.format(date);
    }

    public void onClickOpenGallery(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void startCrop(Uri sourceUri) {
        if (sourceUri != null) {
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));

            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            options.setCompressionQuality(90);

            UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(4, 3)
                    .withMaxResultSize(1080, 1080)
                    .withOptions(options)
                    .start(this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                ImageView photo = findViewById(R.id.photo);

                Glide.with(this)
                        .load(resultUri)
                        .apply(new RequestOptions()
                                .override(Target.SIZE_ORIGINAL)
                                .centerCrop()
                                .transform(new RoundedCorners(16))
                        )
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(photo);

                String hotelId = hotel.id;
                CloudinaryUploader uploader = new CloudinaryUploader(this);

                uploader.uploadImage(resultUri, hotelId, new CloudinaryUploader.UploadCallback() {
                    @Override
                    public void onUploadComplete(String imageUrl) {
                        if (imageUrl != null) {
                            hotel.photos = imageUrl;
                            hotelRepository.updateHotel(hotel);
                        }
                    }
                });
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(this, "Ошибка обрезки: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
    }

    public void back (View view){
        Intent intent = new Intent(this, AdminHotelEdit.class);
        startActivity(intent);
        finish();
    }

    public void checkRooms(View view) {
        Intent intent = new Intent(this, BroChooseHotelRoomForEdit.class);
        intent.putExtra("HOTELID", hotel.id);
        startActivity(intent);
        finish();
    }

    public void map() {
        Intent intent = new Intent(this, Maps.class);
        intent.putExtra("HOTELID", hotel.id);
        startActivity(intent);
        finish();
    }
}