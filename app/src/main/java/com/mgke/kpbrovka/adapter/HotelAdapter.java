package com.mgke.kpbrovka.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.BroHotelRoomEdit;
import com.mgke.kpbrovka.R;
import com.mgke.kpbrovka.model.Hotel;
import com.mgke.kpbrovka.model.HotelRoom;
import com.mgke.kpbrovka.model.Review;
import com.mgke.kpbrovka.repository.HotelRoomRepository;
import com.mgke.kpbrovka.repository.ReviewRepository;


import java.util.List;

public class HotelAdapter extends ArrayAdapter<Hotel> {
    private Context context;
    public HotelAdapter(Context context, List<Hotel> list) {
        super(context, R.layout.item_list_hotels, list);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Hotel hotel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_hotels, parent, false);
        }

        ImageView photo = convertView.findViewById(R.id.photo);
        TextView name = convertView.findViewById(R.id.editName);
        TextView mark = convertView.findViewById(R.id.mark);
        TextView countOfReviews = convertView.findViewById(R.id.countOfReviews);
        TextView city = convertView.findViewById(R.id.city);
        TextView cost = convertView.findViewById(R.id.cost);

        name.setText(hotel.hotelName);
        city.setText(hotel.city);
        if (hotel.photos != null) {
            Glide.with(convertView)
                    .load(hotel.photos)
                    .apply(new RequestOptions()
                            .override(Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .transform(new RoundedCorners(16))
                    )
                    .into(photo);
        }
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


        return convertView;
    }
}
