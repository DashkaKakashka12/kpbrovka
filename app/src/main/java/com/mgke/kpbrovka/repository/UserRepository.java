package com.mgke.kpbrovka.repository;

    import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
    import com.mgke.kpbrovka.model.Hotel;
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

    public String addUser(User user) {
        String userId = db.collection("users").document().getId();
        user.id = userId;
        db.collection("users").document(userId).set(user);
        return userId;
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

    public CompletableFuture<User> getUserByName(String userName) {
        final CompletableFuture<User> future = new CompletableFuture<>();

        db.collection("users").whereEqualTo("name", userName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                future.complete(user);
                            }
                        }
                        future.complete(null);
                    }
                });

        return future;
    }


    public CompletableFuture<List<User>> getUsersByUserType(UserType userType) {
        final CompletableFuture<List<User>> future = new CompletableFuture<>();
        List<User> userList = new ArrayList<>();

        db.collection("users").whereEqualTo("type", userType.name())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }
                        }
                        future.complete(userList);
                    }
                });

        return future;
    }
    public CompletableFuture<Boolean> passwordMatchingCheck(String password) {
        final CompletableFuture<Boolean> future = new CompletableFuture<>();

        db.collection("users").whereEqualTo("pass", password) // Проверяем поле "pass" вместо "name"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                future.complete(true); // Пароль найден, возвращаем true// Прекращаем выполнение, если найден
                            }
                        }
                        future.complete(false); // Пароль не найден
                    }
                });

        return future;
    }


    public void deleteUser(User user) {
        db.collection("users").document(user.id).delete().addOnCompleteListener(task -> {
                    db.collection("hotels").whereEqualTo("userId", user.id).get().addOnCompleteListener(hotelTask -> {
                        for (QueryDocumentSnapshot document : hotelTask.getResult()) {
                            if (document.exists()) {
                                Hotel hotel = document.toObject(Hotel.class);
                                db.collection("hotels").document(hotel.id).delete();
                            }
                        }
                    });
                });
    }

    public void updateUser(User user) {
        db.collection("users").document(user.id)
                .set(user);
    }
}
