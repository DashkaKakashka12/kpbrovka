package com.mgke.kpbrovka;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MyBottomSheetDialog extends BottomSheetDialog {
    public MyBottomSheetDialog(@NonNull Context context, String param, String category, int count, OnClickFind clickFind) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_search_hotel, null);
        setContentView(view);

        TextView nameOfCategory = view.findViewById(R.id.nameOfCategory);
        LinearLayout date = view.findViewById(R.id.date);
        EditText countOfPeople = view.findViewById(R.id.countOfPeople);
        EditText findByHotelOrCity = view.findViewById(R.id.findByHotelOrCity);
        TextView find = view.findViewById(R.id.find);

        countOfPeople.setText(String.valueOf(count));

        if (param.equals("city")) {
            nameOfCategory.setText("Отели в городе " + category);
            findByHotelOrCity.setHint("Отель");
        } else if (param.equals("facility")){
            nameOfCategory.setText(UserShowHotels.getCategory(category));
        } else nameOfCategory.setText(category);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x;
                try {
                    x = Integer.parseInt(countOfPeople.getText().toString());
                } catch (NumberFormatException e){
                    x = 2;
                }
                clickFind.onClick(findByHotelOrCity.getText().toString().trim(), x);
                dismiss();
            }
        });

    }
}
