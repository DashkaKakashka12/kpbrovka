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
import com.mgke.kpbrovka.model.HotelRoom;
import com.mgke.kpbrovka.model.Reservation;
import com.mgke.kpbrovka.model.User;
import com.mgke.kpbrovka.model.UserType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HotelRoomRepository {

    public FirebaseFirestore db;

    public HotelRoomRepository(FirebaseFirestore db) {
        this.db = db;
    }


    public String addHotelRoom(HotelRoom hotelRoom) {
        String hotelRoomId = db.collection("hotelRooms").document().getId();
        hotelRoom.id = hotelRoomId;
        db.collection("hotelRooms").document(hotelRoomId).set(hotelRoom);
        return hotelRoomId;
    }

    public CompletableFuture<List<HotelRoom>> getHotelRoomByHotelId(String hotelId) {
        final CompletableFuture<List<HotelRoom>> future = new CompletableFuture<>();
        List<HotelRoom> hotelRoomList = new ArrayList<>();

        db.collection("hotelRooms").whereEqualTo("hotelId", hotelId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                HotelRoom hotelRoom = document.toObject(HotelRoom.class);
                                hotelRoomList.add(hotelRoom);
                            }
                        }
                        future.complete(hotelRoomList);
                    }
                });

        return future;
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

    public void updateHotelRoom(HotelRoom hotelRoom) {
        db.collection("hotelRooms").document(hotelRoom.id)
                .set(hotelRoom);
    }

    public CompletableFuture <Double> getMinCostByHotelId(String id) {
        final CompletableFuture<Double> future = new CompletableFuture<>();

        db.collection("hotelRooms")
                .whereEqualTo("hotelId", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double min = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HotelRoom hotelRoom = document.toObject(HotelRoom.class);
                                if (hotelRoom.costWithout < min || min == 0){
                                    min = hotelRoom.costWithout;
                                }
                            }
                            future.complete(min);
                        }
                    }
                });

        return future;
    }



    public CompletableFuture<List<HotelRoom>> getAllHotelRoomsByParametrs(String id, int countOfPeople, Date start, Date end) {
        CompletableFuture<List<HotelRoom>> future = new CompletableFuture<>();
        List<HotelRoom> hotelRoomList = new ArrayList<>();
        ReservationRepository reservationRepository = new ReservationRepository(FirebaseFirestore.getInstance());

        db.collection("hotelRooms")
                .whereEqualTo("hotelId", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<CompletableFuture<Void>> futures = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HotelRoom hotelRoom = document.toObject(HotelRoom.class);
                                if (hotelRoom.countOfPeople >= countOfPeople) {
                                    if (start != null && end != null){
                                        CompletableFuture<Void> reservationFuture = reservationRepository.getAllReservationsByHotelRoomId(hotelRoom.id)
                                                .thenAccept(reservations -> {
                                                    int count = 0;
                                                    for (Reservation reservation : reservations) {
                                                        if (start.before(reservation.end) && end.after(reservation.start)) {
                                                            count++;
                                                        }
                                                    }
                                                    if (count<hotelRoom.count) {
                                                        hotelRoomList.add(hotelRoom);
                                                    }
                                                });
                                        futures.add(reservationFuture);
                                    } else hotelRoomList.add(hotelRoom);
                                }
                            }

                            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                            allOf.thenRun(() -> future.complete(hotelRoomList));
                        } else {
                            future.completeExceptionally(task.getException());
                        }
                    }
                });

        return future;
    }

}
