package com.mgke.kpbrovka.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mgke.kpbrovka.model.NearbyAttraction;
import com.mgke.kpbrovka.model.Reservation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ReservationRepository {
    public FirebaseFirestore db;

    public ReservationRepository(FirebaseFirestore db) {
        this.db = db;
    }


    public String addReservation(Reservation reservation) {
        String reservationId = db.collection("Reservations").document().getId();
        reservation.id = reservationId;
        db.collection("Reservations").document(reservationId).set(reservation);
        return reservationId;
    }

    public CompletableFuture<List<Reservation>> getAllReservations() {
        final CompletableFuture<List<Reservation>> future = new CompletableFuture<>();
        List<Reservation> reservationList = new ArrayList<>();

        db.collection("Reservations")
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

    public CompletableFuture<List<Reservation>> getAllReservationsByHotelRoomId(String id) {
        final CompletableFuture<List<Reservation>> future = new CompletableFuture<>();
        List<Reservation> reservationList = new ArrayList<>();

        db.collection("Reservations")
                .whereEqualTo("hotelRoomId", id)
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

    public CompletableFuture<List<Reservation>> getAllReservationsByDates(String id, Date startDate, Date endDate) {
        final CompletableFuture<List<Reservation>> future = new CompletableFuture<>();
        List<CompletableFuture<Reservation>> futures = new ArrayList<>();
        HotelRoomRepository hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());

        db.collection("Reservations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Reservation reservation = document.toObject(Reservation.class);
                                if (startDate.before(reservation.end) && endDate.after(reservation.start)) {
                                    CompletableFuture<Reservation> hotelRoomFuture = hotelRoomRepository.getHotelRoomById(reservation.hotelRoomId)
                                            .thenApply(hotelRoom -> {
                                                if (hotelRoom.hotelId.equals(id)) {
                                                    return reservation;
                                                }
                                                return null;
                                            });
                                    futures.add(hotelRoomFuture);
                                }
                            }

                            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                    .thenApply(v -> {

                                        List<Reservation> reservationList = futures.stream()
                                                .map(CompletableFuture::join)
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toList());
                                        return reservationList;
                                    })
                                    .thenAccept(future::complete)
                                    .exceptionally(ex -> {
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

    public CompletableFuture<Reservation> getReservationById(String reservationId) {
        final CompletableFuture<Reservation> future = new CompletableFuture<>();

        db.collection("Reservations").document(reservationId)
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
        db.collection("Reservations").document(reservation.id)
                .delete();
    }

    public void updateReservation(Reservation reservation) {
        db.collection("Reservations").document(reservation.id)
                .set(reservation);
    }
}
