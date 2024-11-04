package com.mgke.kpbrovka.repository;

    import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
    import com.google.firebase.firestore.SetOptions;
    import com.mgke.kpbrovka.model.Hotel;
    import com.mgke.kpbrovka.model.HotelRoom;
    import com.mgke.kpbrovka.model.NearbyAttraction;
    import com.mgke.kpbrovka.model.Reservation;
    import com.mgke.kpbrovka.model.Review;
    import com.mgke.kpbrovka.model.StatusReservation;
    import com.mgke.kpbrovka.model.User;
    import com.mgke.kpbrovka.model.UserType;

    import java.util.ArrayList;
import java.util.List;
    import java.util.concurrent.CompletableFuture;

public class UserRepository {
    public FirebaseFirestore db;

    public UserRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public void addUser(User user) {
        db.collection("users")
                .add(user);
    }

    public CompletableFuture<List<User>> getAllUsers() {
        final CompletableFuture<List<User>> future = new CompletableFuture<>();
        List<User> userList = new ArrayList<>();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                            future.complete(userList);
                        }
                    }
                });

        return future;
    }

    public CompletableFuture<User> getUserById(String userId) {
        final CompletableFuture<User> future = new CompletableFuture<>();

        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                future.complete(user);
                            }
                        }
                    }
                });

        return future;
    }

    public void deleteUser(User user) {
        db.collection("users").document(user.id)
                .delete();
    }

    public void updateUser(User user, User newUser) {
        db.collection("users").document(user.id)
                .set(newUser, SetOptions.merge());
    }
}
