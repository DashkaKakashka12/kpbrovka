package com.mgke.kpbrovka;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.util.Range;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.archit.calendardaterangepicker.customviews.CalendarListener;
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Calendar;
import java.util.Date;

public class MyBottomSheetDialog extends BottomSheetDialog {
    private Date start, end;
    public MyBottomSheetDialog(@NonNull Context context, String param, String category, int count, OnClickFind clickFind) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_search_hotel, null);
        setContentView(view);

        TextView nameOfCategory = view.findViewById(R.id.nameOfCategory);
        LinearLayout date = view.findViewById(R.id.date);
        TextView startEnd = view.findViewById(R.id.startEnd);
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


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_date);

                ((DateRangeCalendarView)dialog.findViewById(R.id.calendar)).setCalendarListener(new CalendarListener() {
                    @Override
                    public void onFirstDateSelected(@NonNull Calendar calendar) {

                    }

                    @Override
                    public void onDateRangeSelected(@NonNull Calendar calendar, @NonNull Calendar calendar1) {
                        Calendar currentCalendar = Calendar.getInstance();
                        currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        currentCalendar.set(Calendar.MINUTE, 0);
                        currentCalendar.set(Calendar.SECOND, 0);
                        currentCalendar.set(Calendar.MILLISECOND, 0);

                        Date selectedStart = calendar.getTime();
                        Date selectedEnd = calendar1.getTime();

                        if (selectedStart.before(currentCalendar.getTime()) || selectedEnd.before(currentCalendar.getTime())) {
                            Toast.makeText(context, "Выбор даты должен быть не раньше текущей", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (selectedStart.equals(selectedEnd)) {
                            return;
                        }

                        start = selectedStart;
                        end = selectedEnd;

                        new Handler().postDelayed(() -> {
                            dialog.dismiss();

                            Calendar calendarStart = Calendar.getInstance();
                            Calendar calendarEnd = Calendar.getInstance();
                            calendarStart.setTime(start);
                            calendarEnd.setTime(end);

                            String startDate = String.format("%02d.%02d", calendarStart.get(Calendar.DAY_OF_MONTH), calendarStart.get(Calendar.MONTH) + 1);
                            String endDate = String.format("%02d.%02d", calendarEnd.get(Calendar.DAY_OF_MONTH), calendarEnd.get(Calendar.MONTH) + 1);

                            startEnd.setText(startDate + " - " + endDate);
                        }, 500);
                    }
                });

                dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                dialog.show();
            }
        });

        find.setOnClickListener(v -> {

            if (startEnd.getText().toString().trim().isEmpty() || start == null || end == null) {
                Toast.makeText(context, "Пожалуйста, выберите даты.", Toast.LENGTH_SHORT).show();
                return;
            }
                int x;
                try {
                    x = Integer.parseInt(countOfPeople.getText().toString());
                } catch (NumberFormatException e){
                    x = 2;
                }
                clickFind.onClick(findByHotelOrCity.getText().toString().trim(), x, start, end);
                dismiss();

        });

    }
}
