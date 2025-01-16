package com.mgke.kpbrovka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.adapter.ReservationAdapter;
import com.mgke.kpbrovka.model.HotelRoom;
import com.mgke.kpbrovka.model.Reservation;
import com.mgke.kpbrovka.model.StatusReservation;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.HotelRoomRepository;
import com.mgke.kpbrovka.repository.ReservationRepository;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AllReservation extends AppCompatActivity {
    private Reservation reservation;
    private HotelRoom hotelRoom;
    private HotelRoomRepository hotelRoomRepository;
    private ReservationRepository reservationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reservation);
        hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
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
        TextView halfCost = findViewById(R.id.halfCost);
        TextView allCost = findViewById(R.id.allCost);
        TextView numberOfCard = findViewById(R.id.numberOfCard);
        TextView dateOfCard = findViewById(R.id.dateOfCard);
        TextView nameOfCard = findViewById(R.id.nameOfCard);
        TextView ccv = findViewById(R.id.ccv);

        TextView cancellation  = findViewById(R.id.cancellation);
        TextView confirm  = findViewById(R.id.confirm);

        if (reservation.status == StatusReservation.REJECTED){
            confirm.setVisibility(View.GONE);
            cancellation.setClickable(false);
            cancellation.setText("Отменено");
        } else if (reservation.status == StatusReservation.CONFIRMED) {
            cancellation.setVisibility(View.GONE);
            confirm.setClickable(false);
            confirm.setText("Подтверждено");
        }

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


        if (reservation.switchPrepayment) {
            halfCost.setText("Внесено: " + (String.valueOf(hotelRoom.costWithout / 2)) + " BYN");
        } else halfCost.setText("Внесено: " + String.valueOf(hotelRoom.costWithout) + " BYN");


        allCost.setText("Стоимость: " + String.valueOf(hotelRoom.costWithout) + " BYN");

        numberOfCard.setText(reservation.numberOfCard);
        dateOfCard.setText(reservation.dateOfCard);
        nameOfCard.setText(reservation.nameOfCard);
        ccv.setText(reservation.ccv);

    }

    public void cancellation(View view) {
        reservation.status = StatusReservation.REJECTED;
        reservationRepository.updateReservation(reservation);
        Intent intent = new Intent(this, BroReservationEdit.class);
        startActivity(intent);
        finish();
    }

    public void confirm(View view) {
        reservation.status = StatusReservation.CONFIRMED;
        reservationRepository.updateReservation(reservation);
        Intent intent = new Intent(this, BroReservationEdit.class);
        startActivity(intent);
        finish();
    }

    public void back(View view) {
        Intent intent = new Intent(this, BroReservationEdit.class);
        startActivity(intent);
        finish();
    }
}