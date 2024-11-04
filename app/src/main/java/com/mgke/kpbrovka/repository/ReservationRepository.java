package com.mgke.kpbrovka.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mgke.kpbrovka.model.Reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReservationRepository {
    public FirebaseFirestore db;

    public ReservationRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public void addReservation(Reservation reservation) {
        db.collection("Reservations")
                .add(reservation);
    }

    public CompletableFuture<List<Reservation>> getAllReservations() {
        final CompletableFuture<List<Reservation>> future = new CompletableFuture<>();
        List<Reservation> reservationList = new ArrayList<>();

        db.collection("reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Reservation reservation = document.toObject(Reservation.class);
                                reservationList.add(reservation);
                            }
                            future.complete(reservationList);
                        }
                    }
                });

        return future;
    }

    public CompletableFuture<Reservation> getReservationById(String reservationId) {
        final CompletableFuture<Reservation> future = new CompletableFuture<>();

        db.collection("reservations").document(reservationId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Reservation reservation = document.toObject(Reservation.class);
                                future.complete(reservation);
                            }
                        }
                    }
                });

        return future;
    }

    public void deleteReservation(Reservation reservation) {
        db.collection("reservations").document(reservation.id)
                .delete();
    }

    public void updateReservation(Reservation reservation, Reservation newReservation) {
        db.collection("reservations").document(reservation.id)
                .set(newReservation, SetOptions.merge());
    }
}
