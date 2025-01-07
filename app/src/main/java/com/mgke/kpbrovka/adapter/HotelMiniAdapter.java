package com.mgke.kpbrovka.adapter;
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
import com.mgke.kpbrovka.R;
import com.mgke.kpbrovka.UserHotel;
import com.mgke.kpbrovka.model.Hotel;

import java.util.List;

public class HotelMiniAdapter extends RecyclerView.Adapter<HotelMiniAdapter.HotelMiniViewHolder> {
    private List<Hotel> hotels;
    private Context context;

    public HotelMiniAdapter(List<Hotel> hotels, Context context) {
        this.context = context;
        this.hotels = hotels;
    }

    @NonNull
    @Override
    public HotelMiniViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hotel_mini, parent, false);
        return new HotelMiniViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelMiniViewHolder holder, int position) {
        holder.bind(hotels.get(position));
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    static class HotelMiniViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;
        private LinearLayout linearLayout;
        private Context context;

        public HotelMiniViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            linearLayout = itemView.findViewById(R.id.mini);
            textView = itemView.findViewById(R.id.miniTextOfPhoto);
            imageView = itemView.findViewById(R.id.miniPhotoOfHotel);
        }

        public void bind(Hotel hotel) {
            textView.setText(hotel.hotelName);
            if (hotel.photos != null){
            Glide.with(context)
                    .load(hotel.photos)
                    .apply(new RequestOptions()
                            .override(Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .transform(new RoundedCorners(16))
                    )
                    .into(imageView);
            }
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserHotel.class);
                    intent.putExtra("HOTELID", hotel.id);
                    context.startActivity(intent);
                }
            });
        }
    }
}
