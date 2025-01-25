package com.mgke.kpbrovka;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.ReservationAdapter;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.model.HotelRoom;
import com.mgke.kpbrovka.model.Reservation;
import com.mgke.kpbrovka.model.Review;
import com.mgke.kpbrovka.model.StatusReservation;
import com.mgke.kpbrovka.model.UserType;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.HotelRoomRepository;
import com.mgke.kpbrovka.repository.ReservationRepository;
import com.mgke.kpbrovka.repository.ReviewRepository;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AllReservation extends AppCompatActivity {
    private Reservation reservation;
    private HotelRoom hotelRoom;
    private Review review;
    private HotelRoomRepository hotelRoomRepository;
    private ReservationRepository reservationRepository;
    private ReviewRepository reviewRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reservation);
        hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
        reviewRepository = new ReviewRepository(FirebaseFirestore.getInstance());

        String id = getIntent().getStringExtra("RESERVATION");
        reservationRepository = new ReservationRepository(FirebaseFirestore.getInstance());
        reservationRepository.getReservationById(id).thenAccept(reservation -> {
            this.reservation = reservation;
            hotelRoomRepository.getHotelRoomById(reservation.hotelRoomId).thenAccept(hotelRoom -> {
                this.hotelRoom = hotelRoom;
                setValue();
            });
        });
    }

    private void setValue() {
        TextView nameAndSurname = findViewById(R.id.nameAndSurname);
        TextView days = findViewById(R.id.days);
        TextView dates = findViewById(R.id.dates);
        TextView phone = findViewById(R.id.phone);
        TextView email = findViewById(R.id.email);
        ImageView photoOfNumber = findViewById(R.id.photoOfNumber);
        TextView nameRoom = findViewById(R.id.nameRoom);
        TextView countOfPeople = findViewById(R.id.countOfPeople);
        TextView eat = findViewById(R.id.eat);
        TextView wishes = findViewById(R.id.wishes);
        TextView parking = findViewById(R.id.parking);
        TextView cost = findViewById(R.id.cost);
        Switch switch1 = findViewById(R.id.switch1);
        Switch switch2 = findViewById(R.id.switch2);
        TextView cancellation  = findViewById(R.id.cancellation);
        TextView confirm  = findViewById(R.id.confirm);
        TextView writeReview  = findViewById(R.id.writeReview);
        TextView halfCost  = findViewById(R.id.halfCost);
        TextView textRet  = findViewById(R.id.textRet);

        if (reservation.numberOfCard != null) {
            if (reservation.switchAllCost) {
                halfCost.setText("Внесено: " + hotelRoom.costWithout + "BYN");
            } else {
                halfCost.setText("Внесено: " + (hotelRoom.costWithout / 2) + "BYN");
            }
            cancellation.setVisibility(View.VISIBLE);
            cancellation.setOnClickListener(v -> {
                Toast.makeText(this, "Деньги будут возвращены на карту в течение 10 рабочих банковских дней.", Toast.LENGTH_LONG).show();
                textRet.setVisibility(View.VISIBLE);
                cancellation.setText("Отменено");
                confirm.setVisibility(View.GONE);
                reservation.status = StatusReservation.REJECTED;
                reservationRepository.updateReservation(reservation);
            });
        } else {
            cancellation.setVisibility(View.GONE);
        }

        if (Authentication.user.type == UserType.USER) {
            if (reservation.status == StatusReservation.REJECTED) {
                confirm.setVisibility(View.GONE);
                writeReview.setVisibility(View.GONE);
                cancellation.setVisibility(View.VISIBLE);
                cancellation.setClickable(false);
                cancellation.setText("Отменено");
                if (reservation.numberOfCard != null) textRet.setVisibility(View.VISIBLE);
            } else if (reservation.status == StatusReservation.CONFIRMED) {
                confirm.setVisibility(View.VISIBLE);
                confirm.setClickable(false);
                confirm.setText("Подтверждено");
                if (reservation.numberOfCard == null){
                    findViewById(R.id.liner5).setVisibility(View.VISIBLE);
                }
                reviewRepository.canWriteReview(reservation, Authentication.user).thenAccept(review -> {
                    this.review = review;
                    if(review == null) writeReview.setVisibility(View.GONE);
                    else if (review.id != null) {
                        writeReview.setVisibility(View.VISIBLE);
                        writeReview.setText("Редактировать отзыв");
                    };
                });
            } else {
                cancellation.setVisibility(View.VISIBLE);
                writeReview.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
            }
        } else {
            if (reservation.status == StatusReservation.REJECTED) {
                cancellation.setVisibility(View.VISIBLE);
                cancellation.setClickable(false);
                cancellation.setText("Отменено");
            } else if (reservation.status == StatusReservation.CONFIRMED) {
                confirm.setVisibility(View.VISIBLE);
                confirm.setClickable(false);
                confirm.setText("Подтверждено");
            } else {
                confirm.setVisibility(View.VISIBLE);
                cancellation.setVisibility(View.VISIBLE);
            }
        }

        switch1.setChecked(true);

        cost.setText(String.valueOf(hotelRoom.costWithout / 2) + " BYN");

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switch2.setChecked(false);
                cost.setText(String.valueOf(hotelRoom.costWithout / 2) + " BYN");
            } else {
                switch2.setChecked(true);
                cost.setText(String.valueOf(hotelRoom.costWithout) + " BYN");
            }
        });

        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switch1.setChecked(false);
                cost.setText(String.valueOf(hotelRoom.costWithout) + " BYN");
            } else {
                switch1.setChecked(true);
                cost.setText(String.valueOf(hotelRoom.costWithout / 2) + " BYN");
            }
        });

        nameAndSurname.setText(reservation.userName + " " + reservation.userSurname);

        long differenceInMillis = reservation.end.getTime() - reservation.start.getTime();
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis);
        days.setText(String.valueOf(differenceInDays -1 + " ночь"));

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        calendarStart.setTime(reservation.start);
        calendarEnd.setTime(reservation.end);

        String startDate = String.format("%02d.%02d", calendarStart.get(Calendar.DAY_OF_MONTH), calendarStart.get(Calendar.MONTH) + 1);
        String endDate = String.format("%02d.%02d", calendarEnd.get(Calendar.DAY_OF_MONTH), calendarEnd.get(Calendar.MONTH) + 1);

        dates.setText(startDate + " - " + endDate);

        phone.setText(reservation.userPhone);
        email.setText(reservation.userEmail);

        nameRoom.setText(hotelRoom.name);

        if (hotelRoom.photos != null) {
            Glide.with(this)
                    .load(hotelRoom.photos)
                    .apply(new RequestOptions()
                            .override(Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .transform(new RoundedCorners(16))
                    )
                    .into(photoOfNumber);
        }

        countOfPeople.setText("Для " + String.valueOf(reservation.userCountOfPeople) + " человек");

        StringBuilder eatText = new StringBuilder();
        if (!reservation.checkBoxBreakfast && !reservation.checkBoxLunch && !reservation.checkBoxDinner) {
            eatText.append("Приёмы пищи не нужны");
        }
        if (reservation.checkBoxBreakfast) {
            eatText.append("* Завтрак\n");
        }
        if (reservation.checkBoxLunch) {
            eatText.append("* Обед\n");
        }
        if (reservation.checkBoxDinner) {
            eatText.append("* Ужин");
        }
        eat.setText(eatText.toString());

        if (reservation.wishesForTheNumber.isEmpty()){
            wishes.setText("Пожелания: отсутствуют");
        } else wishes.setText("Пожелания: " + reservation.wishesForTheNumber);

        if (reservation.parking == -1) {
            parking.setText("Место на парковке: не нужно");
        } else parking.setText("Место на парковке: " + reservation.parking);

    }

    public void cancellation(View view) {
        reservation.status = StatusReservation.REJECTED;
        reservationRepository.updateReservation(reservation);
        Intent intent;
        if (Authentication.user.type == UserType.HOTELIER){
            intent = new Intent(this, BroReservationEdit.class);
        } else {
            intent = new Intent(this, UserBookingHistoryActivity.class);
        }
        startActivity(intent);
        finish();
    }

    public void confirm(View view) {
        reservation.status = StatusReservation.CONFIRMED;
        reservationRepository.updateReservation(reservation);
        Intent intent;
        if (Authentication.user.type == UserType.HOTELIER){
            intent = new Intent(this, BroReservationEdit.class);
        } else {
            intent = new Intent(this, UserBookingHistoryActivity.class);
        }
        startActivity(intent);
        finish();
    }

    public void back(View view) {
        Intent intent;
        if (Authentication.user.type == UserType.HOTELIER){
            intent = new Intent(this, BroReservationEdit.class);
        } else {
            intent = new Intent(this, UserBookingHistoryActivity.class);
        }
        startActivity(intent);
        finish();
    }

    public void pay(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_user_pay, null);
        EditText numberOfCard = customView.findViewById(R.id.numberOfCard);
        EditText dateOfCard = customView.findViewById(R.id.dateOfCard);
        EditText nameOfCard = customView.findViewById(R.id.nameOfCard);
        EditText ccv = customView.findViewById(R.id.ccv);
        nameOfCard.setFilters(new InputFilter[] { new InputFilter.AllCaps() });

        numberOfCard.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String cleanString = s.toString().replaceAll("[^\\d]", ""); // Убираем все нецифровые символы
                String formatted = cleanString.replaceAll("(.{4})", "$1 ").trim(); // Форматируем с пробелами

                if (!formatted.equals(current)) {
                    current = formatted;
                    numberOfCard.setText(current);
                    numberOfCard.setSelection(current.length());
                }
            }
        });
        dateOfCard.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String cleanString = s.toString().replaceAll("[^\\d]", "");
                String formatted = cleanString.replaceFirst("(\\d{2})(\\d)", "$1 / $2"); // Форматируем при наличии двух цифр

                if (!formatted.equals(current)) {
                    current = formatted;
                    dateOfCard.setText(current);
                    dateOfCard.setSelection(current.length());
                }
            }
        });

        builder.setView(customView)
                .setTitle("Оплата")
                .setPositiveButton("ОК", null)
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button buttonOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            buttonOk.setOnClickListener(v -> {

                String cardNumber = numberOfCard.getText().toString().trim();
                String cardDate = dateOfCard.getText().toString().trim();
                String cardName = nameOfCard.getText().toString().trim();
                String cardCcv = ccv.getText().toString().trim();

                if (cardNumber.isEmpty() || cardDate.isEmpty() || cardName.isEmpty() || cardCcv.isEmpty()) {
                    Toast.makeText(this, "Все поля обязательны для заполнения", Toast.LENGTH_SHORT).show();
                    return;
                }

                Switch switch1 = findViewById(R.id.switch1);
                Switch switch2 = findViewById(R.id.switch2);

                reservation.numberOfCard = numberOfCard.getText().toString();
                reservation.dateOfCard = dateOfCard.getText().toString();
                reservation.nameOfCard = nameOfCard.getText().toString();
                reservation.ccv = ccv.getText().toString();

                reservation.switchPrepayment = switch1.isChecked();
                reservation.switchAllCost = switch2.isChecked();

                reservationRepository.updateReservation(reservation);

                dialog.dismiss();

                findViewById(R.id.liner5).setVisibility(View.GONE);
                TextView halfCost  = findViewById(R.id.halfCost);
                if (reservation.switchAllCost) halfCost.setText("Внесено: " + hotelRoom.costWithout + " BYN");
                else halfCost.setText("Внесено: " + (hotelRoom.costWithout /2) + "BYN");
                findViewById(R.id.cancellation).setVisibility(View.GONE);
            });
        });

        dialog.show();
    }

    public void writeReview(View view) {
        Intent intent = new Intent(this, WriteReview.class);
        if (review.id != null) intent.putExtra("reviewId", review.id);
        intent.putExtra("reservationId", reservation.id);
        startActivity(intent);
        finish();
    }
}