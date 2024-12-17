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
import com.mgke.kpbrovka.BroHotelRoomEdit;
import com.mgke.kpbrovka.R;
import com.mgke.kpbrovka.model.HotelRoom;


import java.util.List;

public class BroChooseRoomAdapter extends ArrayAdapter<HotelRoom> {
    private Context context;
    public BroChooseRoomAdapter(Context context, List<HotelRoom> list) {
        super(context, R.layout.item_list_bro_choose_hotel_room, list);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        HotelRoom hotelRoom = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_bro_choose_hotel_room, parent, false);
        }

        TextView name = convertView.findViewById(R.id.editName);
        ImageView photo = convertView.findViewById(R.id.photo);
        ImageView pensil = convertView.findViewById(R.id.pensil);

        name.setText(hotelRoom.name);
        if (hotelRoom.photos != null) {
            Glide.with(convertView)
                    .load(hotelRoom.photos)
                    .apply(new RequestOptions()
                            .override(Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .transform(new RoundedCorners(16))
                    )
                    .into(photo);
        }

        pensil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(context, BroHotelRoomEdit.class);
                a.putExtra("id", hotelRoom.id);
                context.startActivity(a);
                ((Activity)context).finish();
            }
        });

        return convertView;
    }
}
