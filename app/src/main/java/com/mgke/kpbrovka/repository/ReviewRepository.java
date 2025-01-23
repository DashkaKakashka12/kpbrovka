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
import com.mgke.kpbrovka.model.Review;
import com.mgke.kpbrovka.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ReviewRepository {

    public FirebaseFirestore db;

    public ReviewRepository(FirebaseFirestore db) {
        this.db = db;
    }


    public String addReview(Review review) {
        String reviewId = db.collection("reviews").document().getId();
        review.id = reviewId;
        db.collection("reviews").document(reviewId).set(review);
        return reviewId;
    }

    public CompletableFuture<List<Review>> getAllReviews() {
        final CompletableFuture<List<Review>> future = new CompletableFuture<>();
        List<Review> reviewList = new ArrayList<>();

        db.collection("reviews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Review review = document.toObject(Review.class);
                                reviewList.add(review);
                            }
                            future.complete(reviewList);
                        }
                    }
                });

        return future;
    }

    public CompletableFuture<Review> getReviewById(String reviewId) {
        final CompletableFuture<Review> future = new CompletableFuture<>();

        db.collection("reviews").document(reviewId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Review review = document.toObject(Review.class);
                                future.complete(review);
                            }
                        }
                    }
                });

        return future;
    }



    public CompletableFuture<List<Review>> getReviewsByHotelId(String hotelId) {
        final CompletableFuture<List<Review>> future = new CompletableFuture<>();
        List<Review> reviewList = new ArrayList<>();
        ReservationRepository reservationRepository = new ReservationRepository(FirebaseFirestore.getInstance());

        reservationRepository.getAllReservationsByHotelId(hotelId)
                .thenAccept(reservations -> {
                    if (!reservations.isEmpty()) {
                        List<String> reservationIds = reservations.stream()
                                .map(reservation -> reservation.id)
                                .collect(Collectors.toList());

                        db.collection("reviews")
                                .whereIn("reservationId", reservationIds)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.exists()) {
                                                Review review = document.toObject(Review.class);
                                                reviewList.add(review);
                                            }
                                        }
                                        future.complete(reviewList);
                                    } else {
                                        future.completeExceptionally(task.getException());
                                    }
                                });
                    } else {
                        future.complete(reviewList);
                    }
                })
                .exceptionally(ex -> {
                    future.completeExceptionally(ex);
                    return null;
                });

        return future;
    }


    public void deleteReview(Review review) {
        db.collection("reviews").document(review.id)
                .delete();
    }

    public void updateReview(Review review) {
        db.collection("reviews").document(review.id)
                .set(review);
    }

    public CompletableFuture<Review> canWriteReview(Reservation reservation, User user) {
        final CompletableFuture<Review> future = new CompletableFuture<>();

        if (new Date().before(reservation.start)) {
            future.complete(null);
            return future;
        }

        db.collection("reviews").whereEqualTo("reservationId", reservation.id)
                .whereEqualTo("userId", user.id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean reviewFound = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                Review review = document.toObject(Review.class);
                                future.complete(review);
                                reviewFound = true;
                                break;
                            }
                        }
                        if (!reviewFound) {
                            future.complete(new Review());
                        }
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });
        return future;
    }

}
