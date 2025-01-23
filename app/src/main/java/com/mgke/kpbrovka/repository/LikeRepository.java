package com.mgke.kpbrovka.repository;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.model.Like;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LikeRepository {
    public FirebaseFirestore db;

    public LikeRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public String addLike(Like like) {
        String likeId = db.collection("likes").document().getId();
        like.id = likeId;
        db.collection("likes").document(likeId).set(like);
        return likeId;
    }

    public void deleteLike(Like like) {
        db.collection("likes").document(like.id)
                .delete();
    }

    public void updateLike(Like like) {
        db.collection("likes").document(like.id)
                .set(like);
    }

    public CompletableFuture<Boolean> getLikeByUserId(String userId, String hotelId) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();

        db.collection("likes")
                .whereEqualTo("userId", userId)
                .whereEqualTo("hotelId", hotelId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            future.complete(true);
                        } else {
                            future.complete(false);
                        }
                    }
                });

        return future;
    }

    public CompletableFuture<List<Hotel>> getAllLikeHotel(String userId) {
        final CompletableFuture<List<Hotel>> future = new CompletableFuture<>();
        List<CompletableFuture<Hotel>> futureHotelList = new ArrayList<>();
        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());

        db.collection("likes").whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Like like = document.toObject(Like.class);
                            futureHotelList.add(hotelRepository.getHotelById(like.hotelId));
                        }

                        CompletableFuture.allOf(futureHotelList.toArray(new CompletableFuture[0]))
                                .thenApply(v -> {
                                    List<Hotel> hotelList = futureHotelList.stream()
                                            .map(CompletableFuture::join)
                                            .collect(Collectors.toList());
                                    future.complete(hotelList);
                                    return hotelList;
                                });
                    }
                });

        return future;
    }


    public void deleteLikeByUserId(String hotelId, String userId) {
        db.collection("likes")
                .whereEqualTo("hotelId", hotelId)
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("likes")
                                        .document(document.getId())
                                        .delete();
                            }
                        }
                    }
                });
    }

}
