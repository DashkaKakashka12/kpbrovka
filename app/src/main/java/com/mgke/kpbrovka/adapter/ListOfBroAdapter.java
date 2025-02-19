package com.mgke.kpbrovka.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.R;
import com.mgke.kpbrovka.auth.Authentication;
import com.mgke.kpbrovka.model.User;
import com.mgke.kpbrovka.repository.HotelRepository;
import com.mgke.kpbrovka.repository.UserRepository;

import java.util.List;

public class ListOfBroAdapter extends ArrayAdapter<User> {

    public ListOfBroAdapter(Context context, List<User> list) {
        super(context, R.layout.bro_item_list, list);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);
        HotelRepository hotelRepository = new HotelRepository(FirebaseFirestore.getInstance());
        UserRepository userRepository = new UserRepository(FirebaseFirestore.getInstance());

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bro_item_list, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.icon);
        if (user.photo != null && !user.photo.isEmpty()) {
            Glide.with(getContext())
                    .load(user.photo)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop())
                    .into(icon);
        } else {
            Glide.with(getContext())
                    .load(R.drawable.icon_user_grey)
                    .apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop())
                    .into(icon);
        }
        TextView textView = convertView.findViewById(R.id.koresh);
        TextView textView2 = convertView.findViewById(R.id.hot);
        textView.setText(user.name);
        textView.setTag(user.id);

        convertView.findViewById(R.id.delete_bro).setOnClickListener(view -> {
            userRepository.deleteUser(user);
            remove(user);
            notifyDataSetChanged();
        });

        hotelRepository.getHotelByUserId(user.id).thenAccept(hotel -> {
            if (textView.getTag().equals(user.id)) {
                textView.setText(user.name);
                textView2.setText("Отельер " + hotel.hotelName);
            }
        });

        return convertView;
    }
}