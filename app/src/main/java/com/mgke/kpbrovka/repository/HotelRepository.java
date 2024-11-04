package com.mgke.kpbrovka.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mgke.kpbrovka.model.Hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HotelRepository {

    public FirebaseFirestore db;

    public HotelRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public void addHotel(Hotel hotel) {
        db.collection("hotels")
                .add(hotel);
    }

    public CompletableFuture<List<Hotel>> getAllHotels() {
        final CompletableFuture<List<Hotel>> future = new CompletableFuture<>();
        List<Hotel> hotelList = new ArrayList<>();

        db.collection("hotels")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Hotel hotel = document.toObject(Hotel.class);
                                hotelList.add(hotel);
                            }
                            future.complete(hotelList);
                        }
                    }
                });

        return future;
    }

    public CompletableFuture<Hotel> getHotelById(String hotelId) {
        final CompletableFuture<Hotel> future = new CompletableFuture<>();

        db.collection("hotels").document(hotelId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Hotel hotel = document.toObject(Hotel.class);
                                future.complete(hotel);
                            }
                        }
                    }
                });

        return future;
    }

    public void deleteHotel(Hotel hotel) {
        db.collection("hotels").document(hotel.id)
                .delete();
    }

    public void updateHotel(Hotel hotel, Hotel newHotel) {
        db.collection("hotels").document(hotel.id)
                .set(newHotel, SetOptions.merge());
    }
}
