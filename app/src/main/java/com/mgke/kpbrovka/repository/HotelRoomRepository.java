package com.mgke.kpbrovka.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mgke.kpbrovka.model.HotelRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HotelRoomRepository {

    public FirebaseFirestore db;

    public HotelRoomRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public void addHotelRoom(HotelRoom hotelRoom) {
        db.collection("hotelRooms")
                .add(hotelRoom);
    }

    public CompletableFuture<List<HotelRoom>> getAllHotelRooms() {
        final CompletableFuture<List<HotelRoom>> future = new CompletableFuture<>();
        List<HotelRoom> hotelRoomList = new ArrayList<>();

        db.collection("hotelRooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HotelRoom hotelRoom = document.toObject(HotelRoom.class);
                                hotelRoomList.add(hotelRoom);
                            }
                            future.complete(hotelRoomList);
                        }
                    }
                });

        return future;
    }

    public CompletableFuture<HotelRoom> getHotelRoomById(String hotelRoomId) {
        final CompletableFuture<HotelRoom> future = new CompletableFuture<>();

        db.collection("hotelRooms").document(hotelRoomId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                HotelRoom hotelRoom = document.toObject(HotelRoom.class);
                                future.complete(hotelRoom);
                            }
                        }
                    }
                });

        return future;
    }

    public void deleteHotelRoom(HotelRoom hotelRoom) {
        db.collection("hotelRooms").document(hotelRoom.id)
                .delete();
    }

    public void updateHotelRoom(HotelRoom hotelRoom, HotelRoom newHotelRoom) {
        db.collection("hotelRooms").document(hotelRoom.id)
                .set(newHotelRoom, SetOptions.merge());
    }
}