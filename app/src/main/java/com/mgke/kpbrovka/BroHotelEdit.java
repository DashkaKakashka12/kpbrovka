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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mgke.kpbrovka.adapter.FacilitiesAdapter;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.repository.HotelRepository;

import android.Manifest;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import per.wsj.library.AndRatingBar;

public class BroHotelEdit extends AppCompatActivity {

    private HotelRepository hotelRepository;
    private Hotel hotel;
    private FacilitiesAdapter adapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_hotel_edit);

        NavigationView navigationView = findViewById(R.id.navigationMenu);
        BroBurgerMenuSelect navigationListener = new BroBurgerMenuSelect(this, navigationView);
        navigationView.setNavigationItemSelectedListener(navigationListener);

        hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        hotelRepository.getHotelByUserId(Authentication.user.id).thenAccept(hotel -> {
            this.hotel = hotel;
            setUpValue ();
        });


        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            ImageView photo = findViewById(R.id.photo);
                            Glide.with(this)
                                    .load(imageUri)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_rename_hotel, null);
        EditText editText = customView.findViewById(R.id.rename);
        editText.setText(hotel.hotelName);

        builder.setView(customView);
        builder.setTitle("Название")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newHotelName = editText.getText().toString();

                        if (newHotelName.isEmpty() || newHotelName.length() < 10) {
                            Toast.makeText(view.getContext(), "Название не может быть пустым и должно содержать минимум 10 символов.", Toast.LENGTH_LONG).show();
                            return;
                        }


                        hotel.hotelName = newHotelName;
                        hotelRepository.updateHotel(hotel);
                        TextView name = findViewById(R.id.broEditHotelName);
                        name.setText(hotel.hotelName);
                        NavigationView navigationView = findViewById(R.id.navigationMenu);
                        View nav = navigationView.getHeaderView(0);
                        TextView hotelName = nav.findViewById(R.id.subtitle);
                        hotelName.setText("Бронист отеля " + hotel.hotelName);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void broRenameAddress(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_rename_address, null);
        EditText editText = customView.findViewById(R.id.rename);
        editText.setText(hotel.adress);

        builder.setView(customView);
        builder.setTitle("Адрес")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newAddress = editText.getText().toString();

                        if (newAddress.isEmpty() || newAddress.length() < 5) {
                            Toast.makeText(view.getContext(), "Адрес не может быть пустым и должен содержать минимум 5 символов.", Toast.LENGTH_LONG).show();
                            return;
                        }


                        hotel.adress = newAddress;
                        hotelRepository.updateHotel(hotel);
                        TextView adress = findViewById(R.id.broEditAdress);
                        adress.setText(hotel.adress);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void broEditFacilities(View b) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_choose_main_facilities, null);

        CheckBox[] checkBoxes = new CheckBox[]{
                customView.findViewById(R.id.checkBoxSafe),
                customView.findViewById(R.id.checkBoxConditioning),
                customView.findViewById(R.id.checkBoxCard),
                customView.findViewById(R.id.checkBoxSport),
                customView.findViewById(R.id.checkBoxParking),
                customView.findViewById(R.id.checkBoxWifi),
                customView.findViewById(R.id.checkBoxPool)
        };

        String[] facilities = {"Сейф", "Кондиционер", "Оплата картой", "Тренажёрный зал", "Парковка", "Wifi", "Бассейн"};

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

    public void editReviews (View b){
        Intent a = new Intent(this, BroReviewsEdit.class);
        startActivity(a);
        finish();
    }

    public void checkReviews (View b){
        Intent a = new Intent(this, BroCheckReviews.class);
        startActivity(a);
        finish();
    }

    public void setUpValue (){
        TextView name = findViewById(R.id.broEditHotelName);
        RecyclerView facilities = findViewById(R.id.broEditFacilities);
        TextView adress = findViewById(R.id.broEditAdress);
        ImageView photo = findViewById(R.id.photo);
        AndRatingBar stars = findViewById(R.id.stars);
        AndRatingBar stars2 = findViewById(R.id.stars2);
        ImageView userIcon = findViewById(R.id.userIcon);
        ImageView userIcon2 = findViewById(R.id.userIcon2);
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
        Glide.with(this).load(hotel.photos).into(photo);

        adapter = new FacilitiesAdapter(hotel.facilities);
        facilities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        facilities.setAdapter(adapter);
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



}