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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mgke.kpbrovka.R;
import com.mgke.kpbrovka.UserBooking;
import com.mgke.kpbrovka.model.HotelRoom;

import java.util.Date;
import java.util.List;

public class UserHotelRoomAdapter extends RecyclerView.Adapter<UserHotelRoomAdapter.UserHotelRoomViewHolder> {
    private List<HotelRoom> hotelRoom;
    private  int count;
    private  Date start, end;
    public UserHotelRoomAdapter(List<HotelRoom> hotelRoom, int count, Date start, Date end) {
        this.hotelRoom = hotelRoom;
        this.count = count;
        this.start = start;
        this.end = end;
    }

    @NonNull
    @Override
    public UserHotelRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_user_rooms, parent, false);
        return new UserHotelRoomViewHolder(view, count, start, end);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHotelRoomViewHolder holder, int position) {
        holder.bind(hotelRoom.get(position));
    }

    @Override
    public int getItemCount() {
        return hotelRoom.size();
    }

    static class UserHotelRoomViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView name;
        TextView typeOfBed;
        TextView countOfPeople;
        RecyclerView facilities;
        TextView descriptionText;
        TextView costWithout;
        LinearLayout description;
        LinearLayout choose;
        private  int count;
        private  Date start, end;


        public UserHotelRoomViewHolder(View itemView, int count, Date start, Date end) {
            super(itemView);

            photo = itemView.findViewById(R.id.photo);
            name = itemView.findViewById(R.id.name);
            typeOfBed = itemView.findViewById(R.id.typeOfBed);
            countOfPeople = itemView.findViewById(R.id.countOfPeople);
            facilities = itemView.findViewById(R.id.listFacilities);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            costWithout = itemView.findViewById(R.id.costWithout);
            description = itemView.findViewById(R.id.description);
            choose = itemView.findViewById(R.id.choose);
            this.count = count;
            this.start = start;
            this.end = end;
        }

        public void bind(HotelRoom hotelRoom) {
            name.setText(hotelRoom.name);
            typeOfBed.setText(hotelRoom.typeOfBed);
            countOfPeople.setText(hotelRoom.countOfPeople + " основных места");
            descriptionText.setText(hotelRoom.description);
            costWithout.setText(hotelRoom.costWithout + " BYN");

            if (hotelRoom.photos != null) {
                Glide.with(itemView.getContext())
                        .load(hotelRoom.photos)
                        .apply(new RequestOptions()
                                .override(Target.SIZE_ORIGINAL)
                                .centerCrop()
                                .transform(new RoundedCorners(16))
                        )
                        .into(photo);
            }

            FacilitiesAdapter adapter = new FacilitiesAdapter(hotelRoom.facilities);
            facilities.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            facilities.setAdapter(adapter);

            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView descriptionText = itemView.findViewById(R.id.descriptionText);

                    if (descriptionText.getVisibility() == View.GONE) {
                        descriptionText.setVisibility(View.VISIBLE);
                    } else {
                        descriptionText.setVisibility(View.GONE);
                    }
                }
            });

            choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), UserBooking.class);
                    intent.putExtra("ROOMID", hotelRoom.id);
                    intent.putExtra("COUNT", count);
                    intent.putExtra("START", start);
                    intent.putExtra("END", end);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
