package com.mgke.kpbrovka;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.ReviewRepository;
import com.mgke.kpbrovka.repository.UserRepository;

import android.Manifest;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import per.wsj.library.AndRatingBar;

public class BroHotelEdit extends AppCompatActivity {

    private HotelRepository hotelRepository;
    private Hotel hotel;
    private FacilitiesAdapter adapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private ReviewRepository reviewRepository;

    private UserRepository userRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_hotel_edit);

        NavigationView navigationView = findViewById(R.id.navigationMenu);
        BroBurgerMenuSelect navigationListener = new BroBurgerMenuSelect(this, navigationView);
        navigationView.setNavigationItemSelectedListener(navigationListener);

        hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        reviewRepository = new ReviewRepository(FirebaseFirestore.getInstance());
        userRepository = new UserRepository(FirebaseFirestore.getInstance());

        String id = getIntent().getStringExtra("HOTEL");
        if(id == null){
            findViewById(R.id.roomsButton).setVisibility(View.INVISIBLE);
            hotelRepository.getHotelByUserId(Authentication.user.id).thenAccept(hotel -> {
                this.hotel = hotel;
                setUpValue ();
                findViewById(R.id.back).setVisibility(View.GONE);
            });
        } else {
            hotelRepository.getHotelById(id).thenAccept(hotel -> {
                this.hotel = hotel;
                setUpValue ();
                findViewById(R.id.menu).setVisibility(View.GONE);

            });
        }



        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
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
        finish();
    }


    public void setUpValue (){
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
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
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
}