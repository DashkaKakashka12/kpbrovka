package com.mgke.kpbrovka.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.model.User;
import com.mgke.kpbrovka.model.UserType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class HotelRepository {

    public FirebaseFirestore db;

    public HotelRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public String addHotel(Hotel hotel) {
        String hotelId = db.collection("hotels").document().getId();
        hotel.id = hotelId;
        db.collection("hotels").document(hotelId).set(hotel);
        return hotelId;
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

    public CompletableFuture<Hotel> getHotelByUserId(String userId) {
        final CompletableFuture<Hotel> future = new CompletableFuture<>();

        db.collection("hotels").whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
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

    public void updateHotel(Hotel hotel) {
        db.collection("hotels").document(hotel.id)
                .set(hotel);
    }

    public CompletableFuture<List<List<Hotel>>> getThreeHotelsByCity() {
        final CompletableFuture<List<List<Hotel>>> future = new CompletableFuture<>();
        Map<String, List<Hotel>> cityHotelMap = new HashMap<>();

        db.collection("hotels")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Hotel hotel = document.toObject(Hotel.class);
                                String city = hotel.city;

                                cityHotelMap.putIfAbsent(city, new ArrayList<>());
                                cityHotelMap.get(city).add(hotel);
                            }

                            List<Map.Entry<String, List<Hotel>>> sortedCities = new ArrayList<>(cityHotelMap.entrySet());
                            sortedCities.sort((entry1, entry2) -> Integer.compare(entry2.getValue().size(), entry1.getValue().size()));

                            List<List<Hotel>> result = new ArrayList<>();
                            for (int i = 0; i < Math.min(3, sortedCities.size()); i++) {
                                result.add(sortedCities.get(i).getValue());
                            }

                            future.complete(result);
                        } else {
                            future.completeExceptionally(task.getException());
                        }
                    }
                });

        return future;
    }

    public CompletableFuture<List<Hotel>> getHotelsByParametr(String parametr, String value, int count, Date start, Date end) {
        final CompletableFuture<List<Hotel>> future = new CompletableFuture<>();
        List<Hotel> hotelList = new ArrayList<>();
        HotelRoomRepository hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        db.collection("hotels")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Hotel hotel = document.toObject(Hotel.class);
                                CompletableFuture<Void> hotelFuture = hotelRoomRepository.getAllHotelRoomsByParametrs(hotel.id, count, start, end)
                                        .thenAccept(hotelRooms -> {
                                            if (!hotelRooms.isEmpty()) {
                                                switch (parametr) {
                                                    case "city":
                                                        if (hotel.city.equalsIgnoreCase(value))
                                                            hotelList.add(hotel);
                                                        break;
                                                    case "facility":
                                                        if (hotel.facilities.contains(value))
                                                            hotelList.add(hotel);
                                                        break;
                                                    case "nameAndCity":
                                                        if (hotel.hotelName.toLowerCase().contains(value.toLowerCase()) || hotel.city.toLowerCase().contains(value.toLowerCase()))
                                                            hotelList.add(hotel);
                                                        break;
                                                }
                                            }
                                        });
                                futures.add(hotelFuture);
                            }

                            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                            allFutures.thenRun(() -> future.complete(hotelList)).exceptionally(ex -> {
                                future.completeExceptionally(ex);
                                return null;
                            });
                        } else {
                            future.completeExceptionally(task.getException());
                        }
                    }
                });

        return future;
    }
}
