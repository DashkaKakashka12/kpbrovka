package com.mgke.kpbrovka;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.model.User;
import com.mgke.kpbrovka.repository.UserRepository;

public class BroProfileEdit extends AppCompatActivity {

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    public UserRepository userRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bro_profile_edit);

        setValue();
        userRepository = new UserRepository(FirebaseFirestore.getInstance());

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            ImageView photo = findViewById(R.id.photo);
                            Glide.with(this).load(imageUri).apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()).into(photo);

                            String userId = Authentication.user.id;
                            CloudinaryUploader uploader = new CloudinaryUploader(this);
                            uploader.uploadImage(imageUri, userId, new CloudinaryUploader.UploadCallback() {
                                @Override
                                public void onUploadComplete(String imageUrl) {
                                    if (imageUrl != null) {
                                        Authentication.user.photo = imageUrl;
                                        userRepository.updateUser(Authentication.user);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void setValue (){
        ImageView photo = findViewById(R.id.photo);
        TextView name = findViewById(R.id.name);
        TextView pass = findViewById(R.id.password);
        TextView email = findViewById(R.id.email);


        Glide.with(this).load(Authentication.user.photo).apply(new RequestOptions()
                .centerCrop()
                .circleCrop()).into(photo);

        name.setText(Authentication.user.name);
        pass.setText(Authentication.user.pass);
        email.setText(Authentication.user.email);

    }
    public void broEditName(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_edit_name, null);
        EditText name = customView.findViewById(R.id.name);
        name.setText(Authentication.user.name);

        builder.setView(customView);
        builder.setTitle("Имя")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = name.getText().toString();


                        if (newName.isEmpty()) {
                            Toast.makeText(view.getContext(), "Имя не может быть пустым.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Authentication.user.name = newName;
                        userRepository.updateUser(Authentication.user);
                        TextView nameUser = findViewById(R.id.name);
                        nameUser.setText(Authentication.user.name);
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
    public void broEditPassword(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_edit_password, null);
        EditText pass = customView.findViewById(R.id.password1);
        pass.setText(Authentication.user.pass);

        builder.setView(customView);
        builder.setTitle("Пароль")
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPassword = pass.getText().toString();

                        if (newPassword.length() < 5 || !newPassword.matches(".*[a-zA-Zа-яА-ЯЁё].*")) {
                            Toast.makeText(view.getContext(), "Пароль должен содержать минимум 5 символов и хотя бы одну букву.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Authentication.user.pass = newPassword;
                        userRepository.updateUser(Authentication.user);
                        TextView namePass = findViewById(R.id.password);
                        namePass.setText(Authentication.user.pass);
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

    public void broEditEmail(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_bro_edit_email, null);
        EditText email = customView.findViewById(R.id.editEmail1);
        email.setText(Authentication.user.email);

        builder.setView(customView);
        builder.setTitle("Email")
                .setPositiveButton("ОК", null)
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();


        dialog.setOnShowListener(dialogInterface -> {
            Button buttonOk = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            buttonOk.setOnClickListener(v -> {
                String newEmail = email.getText().toString();

                String validationMessage = validateEmail(newEmail);
                if (validationMessage == null) {
                    Authentication.user.email = newEmail;
                    userRepository.updateUser(Authentication.user);
                    TextView emailUser = findViewById(R.id.email);
                    emailUser.setText(Authentication.user.email);
                    dialog.dismiss();
                } else {

                    Toast.makeText(this, validationMessage, Toast.LENGTH_LONG).show();
                }
            });
        });

        dialog.show();
    }


    private String validateEmail(String email) {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.isEmpty()) {
            return "Email не должен быть пустым.";
        }

        if (!email.matches(emailPattern)) {
            return "Введите корректный email.";
        }

        return null; // Email прошел все проверки
    }

    public void exit (View b){
        Intent a = new Intent(this, LoginActivity.class);
        startActivity(a);
        finish();
    }

    public void back (View b){
        Intent a = new Intent(this, BroHotelEdit.class);
        startActivity(a);
        finish();
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

