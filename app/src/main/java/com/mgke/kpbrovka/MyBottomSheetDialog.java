package com.mgke.kpbrovka;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MyBottomSheetDialog extends BottomSheetDialog {
    public MyBottomSheetDialog(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_search_hotel, null);
        setContentView(view);

    }
}
