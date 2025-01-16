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
    import android.widget.Switch;
    import android.widget.TextView;

    import com.bumptech.glide.Glide;
    import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
    import com.bumptech.glide.request.RequestOptions;
    import com.bumptech.glide.request.target.Target;
    import com.google.android.material.navigation.NavigationView;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.mgke.kpbrovka.auth.Authentication;
    import com.mgke.kpbrovka.model.Hotel;
    import com.mgke.kpbrovka.model.HotelRoom;
    import com.mgke.kpbrovka.model.Reservation;
    import com.mgke.kpbrovka.model.Review;
    import com.mgke.kpbrovka.model.StatusReservation;
    import com.mgke.kpbrovka.repository.HotelRepository;
    import com.mgke.kpbrovka.repository.HotelRoomRepository;
    import com.mgke.kpbrovka.repository.ReservationRepository;
    import com.mgke.kpbrovka.repository.ReviewRepository;

    import java.util.Calendar;
    import java.util.Date;

    public class UserBooking extends AppCompatActivity {

        private HotelRoom hotelRoom;
        private Hotel hotel;
        private int selectedCarIndex = -1;
        private  int count;
        private Date start, end;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_booking);
            String id = getIntent().getStringExtra("ROOMID");
            count = getIntent().getIntExtra("COUNT", -1);
            start = (Date) getIntent().getSerializableExtra("START");
            end = (Date) getIntent().getSerializableExtra("END");
            HotelRoomRepository hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
            hotelRoomRepository.getHotelRoomById(id).thenAccept(hotelRoom1 -> {
                hotelRoom = hotelRoom1;
                HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
                hotelRepository.getHotelById(hotelRoom.hotelId).thenAccept(hotel1 -> {
                    hotel = hotel1;
                    setupGreyCar();
                    setupCarClickListeners();
                    setValue();
                });
            });
        }

        private void setupGreyCar() {
            ReservationRepository reservationRepository = new ReservationRepository(FirebaseFirestore.getInstance());
            reservationRepository.getAllReservationsByDates(hotelRoom.hotelId, start, end).thenAccept(reservations -> {
                for (Reservation reservation : reservations) {
                    if (reservation.parking != -1){
                        int previousImageViewId = getResources().getIdentifier("car" + reservation.parking +1, "id", getPackageName());
                        ImageView previousCarImageView = findViewById(previousImageViewId);
                        previousCarImageView.setImageResource(R.drawable.car_grey_icon);
                        previousCarImageView.setClickable(false);
                    }
                }
            });
        }

        private void setupCarClickListeners() {
            for (int i = 1; i <= 16; i++) {
                final int index = i - 1;
                int imageViewId = getResources().getIdentifier("car" + i, "id", getPackageName());
                ImageView carImageView = findViewById(imageViewId);
                carImageView.setOnClickListener(view -> {
                    if (carImageView.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.car_grey_icon).getConstantState()) {
                        onCarSelected(index, carImageView);
                    }
                });
            }
        }

        private void onCarSelected(int index, ImageView carImageView) {
            if (selectedCarIndex == index) return;

            if (selectedCarIndex != -1) {
                int previousImageViewId = getResources().getIdentifier("car" + (selectedCarIndex + 1), "id", getPackageName());
                ImageView previousCarImageView = findViewById(previousImageViewId);
                previousCarImageView.setImageResource(R.drawable.car_black_icon);
            }

            carImageView.setImageResource(R.drawable.car_red_icon);
            selectedCarIndex = index;
        }

        private void setValue() {
            TextView nameHotel = findViewById(R.id.nameHotel);
            TextView nameHotel2 = findViewById(R.id.nameHotel2);
            TextView mark = findViewById(R.id.mark);
            TextView countOfReviews = findViewById(R.id.countOfReviews);
            TextView nameRoom = findViewById(R.id.nameRoom);
            TextView typeOfBed = findViewById(R.id.typeOfBed);
            TextView countOfPeople = findViewById(R.id.countOfPeople);
            TextView city = findViewById(R.id.city);
            TextView adress = findViewById(R.id.adress);
            TextView cost = findViewById(R.id.cost);
            ImageView photoOfNumber = findViewById(R.id.photoOfNumber);
            TextView countText = findViewById(R.id.count);
            TextView dates = findViewById(R.id.dates);
            Switch switch1 = findViewById(R.id.switch1);
            Switch switch2 = findViewById(R.id.switch2);

            countText.setText(String.valueOf(count));

            Calendar calendarStart = Calendar.getInstance();
            Calendar calendarEnd = Calendar.getInstance();
            calendarStart.setTime(start);
            calendarEnd.setTime(end);

            String startDate = String.format("%02d.%02d", calendarStart.get(Calendar.DAY_OF_MONTH), calendarStart.get(Calendar.MONTH) + 1);

            String endDate = String.format("%02d.%02d", calendarEnd.get(Calendar.DAY_OF_MONTH), calendarEnd.get(Calendar.MONTH) + 1);

            dates.setText(startDate + " - " + endDate);
            nameHotel.setText(hotel.hotelName);
            nameHotel2.setText(hotel.hotelName);
            nameRoom.setText(hotelRoom.name);
            typeOfBed.setText(hotelRoom.typeOfBed);
            countOfPeople.setText(String.valueOf(hotelRoom.countOfPeople + " основных места"));
            city.setText(hotel.city + ", ");
            adress.setText(hotel.adress);

            cost.setText(switch1.isChecked()
                    ? String.valueOf(hotelRoom.costWithout / 2) + " BYN"
                    : String.valueOf(hotelRoom.costWithout) + " BYN");

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


            ReviewRepository reviewRepository = new ReviewRepository(FirebaseFirestore.getInstance());
            reviewRepository.getReviewsByHotelId(hotel.id).thenAccept(list -> {
                countOfReviews.setText(list.size() + " отзывов");
                double averageStars = list.stream().mapToDouble(Review::getStars).average().orElse(0.0);
                String formattedAverage = String.format("%.1f", averageStars);
                mark.setText(formattedAverage);
            });

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

        }

        public void back(View view) {
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

            EditText name = findViewById(R.id.name);
            EditText surname = findViewById(R.id.surname);
            EditText email = findViewById(R.id.email);
            EditText phone = findViewById(R.id.phone);
            EditText wishes = findViewById(R.id.wishes);

            String emailInput = email.getText().toString().trim();
            String phoneInput = phone.getText().toString().trim();

            if (name.getText().toString().trim().isEmpty()) {
                name.setError("Поля, отмеченные * обязательны для заполнения");
                return;
            }
            if (surname.getText().toString().trim().isEmpty()) {
                surname.setError("Поля, отмеченные * обязательны для заполнения");
                return;
            }
            if (emailInput.isEmpty() || !isValidEmail(emailInput)) {
                email.setError(emailInput.isEmpty() ? "Поля, отмеченные * обязательны для заполнения" : "Введите корректный email");
                return;
            }
            if (phoneInput.isEmpty() || !isValidPhoneNumber(phoneInput)) {
                phone.setError(phoneInput.isEmpty() ? "Поля, отмеченные * обязательны для заполнения" : "Введите корректный номер телефона");
                return;
            }

            builder.setView(customView)
                    .setTitle("Оплата")
                    .setPositiveButton("ОК", null)
                    .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();

            dialog.setOnShowListener(dialogInterface -> {
                Button buttonOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                buttonOk.setOnClickListener(v -> {
                    CheckBox checkBox1 = findViewById(R.id.checkBox1);
                    CheckBox checkBox2 = findViewById(R.id.checkBox2);
                    CheckBox checkBox3 = findViewById(R.id.checkBox3);

                    Switch switch1 = findViewById(R.id.switch1);
                    Switch switch2 = findViewById(R.id.switch2);

                    Reservation reservation = new Reservation();
                    ReservationRepository reservationRepository = new ReservationRepository(FirebaseFirestore.getInstance());
                    reservation.userCountOfPeople = count;
                    reservation.userName = name.getText().toString();
                    reservation.userSurname = surname.getText().toString();
                    reservation.userEmail = email.getText().toString();
                    reservation.userPhone = phone.getText().toString();
                    reservation.wishesForTheNumber = wishes.getText().toString();
                    reservation.numberOfCard = numberOfCard.getText().toString();
                    reservation.dateOfCard = dateOfCard.getText().toString();
                    reservation.nameOfCard = nameOfCard.getText().toString();
                    reservation.ccv = ccv.getText().toString();

                    reservation.switchPrepayment = switch1.isChecked();
                    reservation.switchAllCost = switch2.isChecked();


                    reservation.checkBoxBreakfast = checkBox1.isChecked();
                    reservation.checkBoxLunch = checkBox2.isChecked();
                    reservation.checkBoxDinner = checkBox3.isChecked();

                    reservation.userId = Authentication.user.id;
                    reservation.hotelRoomId = hotelRoom.id;
                    reservation.status = StatusReservation.INPROGRESS;

                    reservation.parking = selectedCarIndex;
                    reservation.start = start;
                    reservation.end = end;

                    reservationRepository.addReservation(reservation);

                    dialog.dismiss();
                    Intent intent = new Intent(this, MainUserActivity.class);
                    startActivity(intent);
                    finish();
                });
            });

            dialog.show();
        }

        private boolean isValidEmail(String email) {
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            return email.matches(emailPattern);
        }

        private boolean isValidPhoneNumber(String phone) {
            // Регулярное выражение: допускаем 10-15 цифр, пробелы, (), + и -
            String phonePattern = "^[+]?([\\d\\s()\\-]{10,15})$";
            return phone.matches(phonePattern);
        }

    }