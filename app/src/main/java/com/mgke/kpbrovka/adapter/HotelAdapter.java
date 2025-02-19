package com.mgke.kpbrovka.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.BroHotelEdit;
import com.mgke.kpbrovka.R;
import com.mgke.kpbrovka.UserHotel;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.model.Like;
import com.mgke.kpbrovka.model.Review;
import com.mgke.kpbrovka.model.UserType;
import com.mgke.kpbrovka.repository.HotelRoomRepository;
import com.mgke.kpbrovka.repository.LikeRepository;
import com.mgke.kpbrovka.repository.ReviewRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {
    private List<Hotel> hotels = new ArrayList<>();
    private Context context;
    private int countOfPeople;
    private Date start;
    private Date end;

    public HotelAdapter(List<Hotel> hotels, Context context, int countOfPeople, Date start, Date end) {
        this.context = context;
        this.countOfPeople = countOfPeople;
        this.start = start;
        this.end = end;

        loadAndSortHotels(hotels);
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_hotels, parent, false);
        return new HotelViewHolder(view, countOfPeople, start, end, context);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        holder.bind(hotels.get(position));
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    private void loadAndSortHotels(List<Hotel> hotelList) {
        HotelRoomRepository hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());

        Map<String, Double> hotelCosts = new HashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Hotel hotel : hotelList) {
            CompletableFuture<Void> future = hotelRoomRepository.getMinCostByHotelId(hotel.id)
                    .thenAccept(cost -> hotelCosts.put(hotel.id, cost));
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(() -> {
            hotelList.sort((h1, h2) -> {
                Double cost1 = hotelCosts.getOrDefault(h1.id, Double.MAX_VALUE);
                Double cost2 = hotelCosts.getOrDefault(h2.id, Double.MAX_VALUE);
                return cost1.compareTo(cost2);
            });

            this.hotels = hotelList;
            notifyDataSetChanged();
        });
    }

    static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView name;
        TextView mark;
        TextView countOfReviews;
        TextView city;
        TextView cost;
        LinearLayout hotelchik;
        ImageView heartIcon;
        private int countOfPeople;
        private Date start;
        private Date end;
        private Context context;
        private boolean isHeartSelected = false;

        public HotelViewHolder(View itemView, int countOfPeople, Date start, Date end, Context context) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            name = itemView.findViewById(R.id.editName);
            mark = itemView.findViewById(R.id.mark);
            countOfReviews = itemView.findViewById(R.id.countOfReviews);
            city = itemView.findViewById(R.id.city);
            cost = itemView.findViewById(R.id.cost);
            hotelchik = itemView.findViewById(R.id.hotelchik);
            heartIcon = itemView.findViewById(R.id.heartIcon);
            this.countOfPeople = countOfPeople;
            this.start = start;
            this.end = end;
            this.context = context;
        }

        public void bind(Hotel hotel) {
            name.setText(hotel.hotelName);
            if (hotel.adress == null) {
                city.setText(hotel.city);
            } else {
                city.setText(hotel.city + ", " + hotel.adress);
            }
            if (hotel.photos != null) {
                Glide.with(itemView)
                        .load(hotel.photos)
                        .apply(new RequestOptions()
                                .override(Target.SIZE_ORIGINAL)
                                .centerCrop()
                                .transform(new RoundedCorners(16))
                        )
                        .into(photo);
            } else photo.setImageResource(R.drawable.hotel_first_photo);

            ReviewRepository reviewRepository = new ReviewRepository(FirebaseFirestore.getInstance());
            reviewRepository.getReviewsByHotelId(hotel.id).thenAccept(list -> {
                countOfReviews.setText(list.size() + " отзывов");
                double averageStars = list.stream().mapToDouble(Review::getStars).average().orElse(0.0);
                String formattedAverage = String.format("%.1f", averageStars);
                mark.setText(formattedAverage);
            });

            HotelRoomRepository hotelRoomRepository = new HotelRoomRepository(FirebaseFirestore.getInstance());
            hotelRoomRepository.getMinCostByHotelId(hotel.id).thenAccept(costValue -> {
                cost.setText(costValue + " BYN");
            });

            if (Authentication.user.type == UserType.ADMINISTRATOR) heartIcon.setVisibility(View.GONE);

            LikeRepository likeRepository = new LikeRepository(FirebaseFirestore.getInstance());
            likeRepository.getLikeByUserId(Authentication.user.id, hotel.id).thenAccept(aBoolean -> {
                if (!aBoolean) {
                    heartIcon.setImageResource(R.drawable.heart);
                } else {
                    heartIcon.setImageResource(R.drawable.red_heart);
                }

                heartIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isHeartSelected) {
                            heartIcon.setImageResource(R.drawable.heart);
                            likeRepository.deleteLikeByUserId(hotel.id, Authentication.user.id);
                        } else {
                            heartIcon.setImageResource(R.drawable.red_heart);
                            likeRepository.addLike(new Like(null, hotel.id, Authentication.user.id));
                        }
                        isHeartSelected = !isHeartSelected;
                    }
                });

            });

            hotelchik.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Authentication.user.type == UserType.ADMINISTRATOR) {
                        Intent intent = new Intent(context, BroHotelEdit.class);
                        intent.putExtra("HOTEL", hotel.id);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    } else {
                        Intent intent = new Intent(context, UserHotel.class);
                        intent.putExtra("HOTELID", hotel.id);
                        intent.putExtra("countOfPeople", countOfPeople);
                        intent.putExtra("START", start);
                        intent.putExtra("END", end);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
