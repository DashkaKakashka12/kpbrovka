package com.mgke.kpbrovka.adapter;

import static com.mgke.kpbrovka.BroHotelEdit.formatTimestamp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.AllReservation;
import com.mgke.kpbrovka.R;
import com.mgke.kpbrovka.model.Reservation;
import com.mgke.kpbrovka.model.Review;
import com.mgke.kpbrovka.repository.HotelRoomRepository;
import com.mgke.kpbrovka.repository.ReviewRepository;
import com.mgke.kpbrovka.repository.UserRepository;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import per.wsj.library.AndRatingBar;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    private List<Reservation> reservations;

    public ReservationAdapter(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_mini_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        holder.bind(reservations.get(position));
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        private TextView days;
        private TextView nameAndSurname;
        private TextView dates;
        private TextView nameRoom;
        private HotelRoomRepository hotelRoomRepository;
        private LinearLayout linearLayout;
        public ReservationViewHolder(View itemView) {
            super(itemView);
            this.days = itemView.findViewById(R.id.days);
            this.nameAndSurname = itemView.findViewById(R.id.nameAndSurname);
            this.dates = itemView.findViewById(R.id.dates);
            this.nameRoom = itemView.findViewById(R.id.nameRoom);
            this.linearLayout = itemView.findViewById(R.id.content3);
            hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
        }

        public void bind(Reservation reservation) {

            long differenceInMillis = reservation.end.getTime() - reservation.start.getTime();
            long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis);

            days.setText(String.valueOf(differenceInDays -1 + " ночь"));

            nameAndSurname.setText(reservation.userName + " " + reservation.userSurname);

            Calendar calendarStart = Calendar.getInstance();
            Calendar calendarEnd = Calendar.getInstance();
            calendarStart.setTime(reservation.start);
            calendarEnd.setTime(reservation.end);

            String startDate = String.format("%02d.%02d", calendarStart.get(Calendar.DAY_OF_MONTH), calendarStart.get(Calendar.MONTH) + 1);
            String endDate = String.format("%02d.%02d", calendarEnd.get(Calendar.DAY_OF_MONTH), calendarEnd.get(Calendar.MONTH) + 1);

            dates.setText(startDate + " - " + endDate);
            hotelRoomRepository.getHotelRoomById(reservation.hotelRoomId).thenAccept(hotelRoom -> {
                nameRoom.setText("Номер: " + hotelRoom.name);
            });

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), AllReservation.class);
                    intent.putExtra("RESERVATION", reservation.id);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}