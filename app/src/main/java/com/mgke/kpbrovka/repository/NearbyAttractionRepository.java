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
import com.mgke.kpbrovka.model.NearbyAttraction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NearbyAttractionRepository {
    public FirebaseFirestore db;

    public NearbyAttractionRepository(FirebaseFirestore db) {
        this.db = db;
    }


    public String addNearbyAttraction(NearbyAttraction nearbyAttraction) {
        String nearbyAttractionId = db.collection("NearbyAttraction").document().getId();
        nearbyAttraction.id = nearbyAttractionId;
        db.collection("NearbyAttraction").document(nearbyAttractionId).set(nearbyAttraction);
        return nearbyAttractionId;
    }

    public CompletableFuture<List<NearbyAttraction>> getAllNearbyAttractions() {
        final CompletableFuture<List<NearbyAttraction>> future = new CompletableFuture<>();
        List<NearbyAttraction> nearbyAttractionList = new ArrayList<>();

        db.collection("nearbyAttractions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NearbyAttraction nearbyAttraction = document.toObject(NearbyAttraction.class);
                                nearbyAttractionList.add(nearbyAttraction);
                            }
                            future.complete(nearbyAttractionList);
                        }
                    }
                });

        return future;
    }

    public CompletableFuture<NearbyAttraction> getNearbyAttractionById(String nearbyAttractionId) {
        final CompletableFuture<NearbyAttraction> future = new CompletableFuture<>();

        db.collection("nearbyAttractions").document(nearbyAttractionId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                NearbyAttraction nearbyAttraction = document.toObject(NearbyAttraction.class);
                                future.complete(nearbyAttraction);
                            }
                        }
                    }
                });

        return future;
    }

    public void deleteNearbyAttraction(NearbyAttraction nearbyAttraction) {
        db.collection("nearbyAttractions").document(nearbyAttraction.id)
                .delete();
    }

    public void updateNearbyAttraction(NearbyAttraction nearbyAttraction) {
        db.collection("nearbyAttractions").document(nearbyAttraction.id)
                .set(nearbyAttraction);
    }
}
