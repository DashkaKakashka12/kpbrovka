package com.mgke.kpbrovka.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mgke.kpbrovka.R;

import java.util.List;

public class FacilitiesAdapter extends RecyclerView.Adapter<FacilitiesAdapter.FacilityViewHolder> {
    private List<String> facilities;

    public FacilitiesAdapter(List<String> facilities) {
        this.facilities = facilities;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_facility, parent, false);
        return new FacilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        holder.bind(facilities.get(position));
    }

    @Override
    public int getItemCount() {
        return facilities.size();
    }

    static class FacilityViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public FacilityViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.facility_text);
        }

        public void bind(String facility) {
            textView.setText(facility);
        }
    }
}
