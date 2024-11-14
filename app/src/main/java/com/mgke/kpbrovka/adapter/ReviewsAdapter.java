package com.mgke.kpbrovka.adapter;

import static com.mgke.kpbrovka.BroHotelEdit.formatTimestamp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mgke.kpbrovka.R;
import com.mgke.kpbrovka.model.Review;
import com.mgke.kpbrovka.repository.ReviewRepository;
import com.mgke.kpbrovka.repository.UserRepository;

import java.util.List;

import per.wsj.library.AndRatingBar;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private List<Review> reviews;
    private UserRepository userRepository;
    private ReviewRepository reviewRepository;

    public ReviewsAdapter(List<Review> reviews) {
        this.reviews = reviews;
        this.userRepository = new UserRepository(FirebaseFirestore.getInstance());
        this.reviewRepository = new ReviewRepository(FirebaseFirestore.getInstance());

    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_reviews, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(reviews.get(position), this);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView nameAndData, text;
        private ImageView delete, photo;
        private AndRatingBar stars;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            nameAndData = itemView.findViewById(R.id.nameAndData);
            text = itemView.findViewById(R.id.text);
            delete = itemView.findViewById(R.id.delete);
            photo = itemView.findViewById(R.id.photo);
            stars = itemView.findViewById(R.id.stars);
        }

        public void bind(Review review, ReviewsAdapter adapter) {
            stars.setNumStars(review.stars);
            text.setText(review.text);

            adapter.userRepository.getUserById(review.userId).thenAccept(user -> {
                if (user.photo != null) {
                    Glide.with(itemView).load(user.photo).apply(new RequestOptions()
                            .centerCrop()
                            .circleCrop()).into(photo);
                }
                nameAndData.setText(user.name + "\n" + formatTimestamp(review.dataCreation));
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.reviewRepository.deleteReview(review);
                    adapter.reviews.remove(getAdapterPosition());
                    adapter.notifyItemRemoved(getAdapterPosition());
                }
            });
        }
    }
}