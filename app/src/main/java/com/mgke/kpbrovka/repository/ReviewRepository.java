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
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        db.collection("reviews").whereEqualTo("hotelId", hotelId)
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
                    }
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
}
