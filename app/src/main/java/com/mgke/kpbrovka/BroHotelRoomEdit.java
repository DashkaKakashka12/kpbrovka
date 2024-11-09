package com.mgke.kpbrovka;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mgke.kpbrovka.adapter.FacilitiesAdapter;
import com.mgke.kpbrovka.model.HotelRoom;
import com.mgke.kpbrovka.repository.HotelRoomRepository;

import java.util.ArrayList;
import java.util.List;

public class BroHotelRoomEdit extends AppCompatActivity {
    private HotelRoom hotelRoom;
    private HotelRoomRepository hotelRoomRepository;
    private FacilitiesAdapter adapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_hotel_room_edit);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
        hotelRoomRepository.getHotelRoomById(id).thenAccept(room -> {
            hotelRoom = room;
            setValue();
        });


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
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

                            String hotelRoomId = hotelRoom.id;
                            StorageReference hotelRoomRef = storageReference.child("hotelRooms/" + hotelRoomId + ".jpg");

                            UploadTask uploadTask = hotelRoomRef.putFile(imageUri);
                            uploadTask.addOnSuccessListener(taskSnapshot -> hotelRoomRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                String imageUrlString = downloadUri.toString();

                                hotelRoom.photos = imageUrlString;
                                hotelRoomRepository.updateHotelRoom(hotelRoom);
                            }));
                        }
                    }
                });
    }

    private void setValue() {
        ImageView photo = findViewById(R.id.photo);
        TextView name = findViewById(R.id.name);
        RecyclerView facilities = findViewById(R.id.listFacilities);
        TextView cost1 = findViewById(R.id.cost1);
        TextView cost2 = findViewById(R.id.cost2);
        TextView countOfRooms = findViewById(R.id.countOfRooms);
        TextView description = findViewById(R.id.descriptionText);

        Glide.with(this).load(hotelRoom.photos).into(photo);
        name.setText(hotelRoom.name);
        cost1.setText("Без питания\n" + hotelRoom.costWithout);
        cost2.setText("Включён завтрак\n" + hotelRoom.costWith);
        countOfRooms.setText(String.valueOf(hotelRoom.count));
        description.setText(hotelRoom.description);

        adapter = new FacilitiesAdapter(hotelRoom.facilities);
        facilities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        facilities.setAdapter(adapter);
    }

    public void backToBroChooseHotelRoom (View b){
        Intent a = new Intent(this, BroChooseHotelRoomForEdit.class);
        startActivity(a);
        finish();
    }

    public void broEditFacilitiesOfRoom (View b){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_choose_hotel_room_main_facilities, null);

        CheckBox[] checkBoxes = new CheckBox[]{
                customView.findViewById(R.id.checkBoxIron),
                customView.findViewById(R.id.checkBoxWorkspace),
                customView.findViewById(R.id.checkBoxMiniBar),
                customView.findViewById(R.id.checkBoxSafe),
                customView.findViewById(R.id.checkBoxHairDryer),
                customView.findViewById(R.id.checkBoxFridge),
                customView.findViewById(R.id.checkBoxTowels),
                customView.findViewById(R.id.checkBoxCoffee)
        };

        String[] facilities = {"Утюг", "Рабочее место", "Мини-бар", "Сейф", "Фен", "Холодильник", "Полотенца", "Кофеварка"};

        for (int i = 0; i < facilities.length; i++) {
            checkBoxes[i].setChecked(hotelRoom.facilities.contains(facilities[i]));
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
                        hotelRoom.facilities = selectedFacilities;
                        hotelRoomRepository.updateHotelRoom(hotelRoom);

                        adapter = new FacilitiesAdapter(hotelRoom.facilities);
                        RecyclerView facilitiesView = findViewById(R.id.listFacilities);
                        facilitiesView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                        facilitiesView.setAdapter(adapter);
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

    public void broEditHotelRoomDescription (View b){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_edit_hotel_room_description, null);
        EditText editText = customView.findViewById(R.id.description);
        editText.setText(hotelRoom.description);
        builder.setView(customView);
        builder.setTitle("Описание")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hotelRoom.description = editText.getText().toString();
                        hotelRoomRepository.updateHotelRoom(hotelRoom);
                        TextView description = findViewById(R.id.descriptionText);
                        description.setText(hotelRoom.description);
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


    public void broRenameHotelRoom(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_rename_hotel_room, null);
        EditText editText = customView.findViewById(R.id.nameHotelRoom);
        editText.setText(hotelRoom.name);
        builder.setView(customView);
        builder.setTitle("Название номера")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hotelRoom.name = editText.getText().toString();
                        hotelRoomRepository.updateHotelRoom(hotelRoom);
                        TextView name = findViewById(R.id.name);
                        name.setText(hotelRoom.name);
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

    public void broChangeCountOfRooms (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_change_number_of_rooms, null);
        EditText editText = customView.findViewById(R.id.countOfHotelRooms);
        editText.setText(String.valueOf(hotelRoom.count));
        builder.setView(customView);
        builder.setTitle("Количество номеров")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hotelRoom.count = Integer.valueOf(editText.getText().toString());
                        hotelRoomRepository.updateHotelRoom(hotelRoom);
                        TextView countOfRooms = findViewById(R.id.countOfRooms);
                        countOfRooms.setText(String.valueOf(hotelRoom.count));
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

    public void broChangeCost (View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_change_cost_of_room, null);
        EditText editText1 = customView.findViewById(R.id.cost1);
        editText1.setText(String.valueOf(hotelRoom.costWithout));
        EditText editText2 = customView.findViewById(R.id.cost2);
        editText2.setText(String.valueOf(hotelRoom.costWith));

        builder.setView(customView);
        builder.setTitle("Стоимость номера")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hotelRoom.costWithout = Integer.valueOf(editText1.getText().toString());
                        TextView cost1 = findViewById(R.id.cost1);
                        cost1.setText("Без питания\n" + hotelRoom.costWithout);

                        hotelRoom.costWith = Integer.valueOf(editText2.getText().toString());
                        hotelRoomRepository.updateHotelRoom(hotelRoom);
                        TextView cost2 = findViewById(R.id.cost2);
                        cost2.setText("Включён завтрак\n" + hotelRoom.costWith);

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

    public void onClickOpenGallery(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1);
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