package com.mgke.kpbrovka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.model.Coordinates;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.model.UserType;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.HotelRoomRepository;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import android.Manifest;
import android.view.View;
import android.widget.Toast;

public class Maps extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    public static boolean isMapInitialized = false;
    private Map map;
    private MapView mapView;
    private InputListener inputListener;
    private PlacemarkMapObject placeMark = null;
    private Hotel currentHotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isMapInitialized){
            MapKitFactory.setApiKey("7a8bcddd-0717-4d1a-a0cb-62cf7170e1b6");
            MapKitFactory.initialize(this);
            isMapInitialized = true;
        }
        setContentView(R.layout.activity_maps);

        String id = getIntent().getStringExtra("HOTELID");
        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        hotelRepository.getHotelById(id).thenAccept(hotel -> {
            currentHotel = hotel;

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                getLocationAndZoom();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        });

        mapView = findViewById(R.id.mapview);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        map = mapView.getMapWindow().getMap();

        inputListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                handleMapTap(map, point);
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                handleMapTap(map, point);
            }
        };
        map.addInputListener(inputListener);

    }

    private void handleMapTap(Map map, Point point) {
        if (placeMark != null){
            map.getMapObjects().remove(placeMark);
        }

        ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.geo4);
        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        currentHotel.coordinates = new Coordinates(point.getLatitude(), point.getLongitude());
        hotelRepository.updateHotel(currentHotel);
        placeMark = map.getMapObjects().addPlacemark(point, imageProvider);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndZoom();
            } else {
                Toast.makeText(this, "Разрешение на доступ к местоположению отклонено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocationAndZoom() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (currentHotel.coordinates == null){
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                Point point = new Point(location.getLatitude(), location.getLongitude());
                map.move(new CameraPosition(point, 15.0f, 0.0f, 0.0f));
            });
        } else {
            Point point = new Point(currentHotel.coordinates.x, currentHotel.coordinates.y);
            ImageProvider imageProvider = ImageProvider.fromResource(this, R.drawable.geo4);
            placeMark = map.getMapObjects().addPlacemark(point, imageProvider);
            map.move(new CameraPosition(point, 15.0f, 0.0f, 0.0f));
        }
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

    public void backFromMaps(View view) {
        Intent intent = new Intent(this, BroHotelEdit.class);
        if (Authentication.user.type == UserType.ADMINISTRATOR) intent.putExtra("HOTEL", currentHotel.id);
        startActivity(intent);
        finish();
    }
}