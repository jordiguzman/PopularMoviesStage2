package appkite.jordiguzman.com.polularmoviesstage2.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import appkite.jordiguzman.com.polularmoviesstage2.R;
import appkite.jordiguzman.com.polularmoviesstage2.model.Review;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder>  {

    private Review[] mReview;
    private TextView tv_author_adapter;
    private TextView tv_review_adapter;



    public ReviewAdapter(Review[] review) {
        this.mReview = review;

    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_review, parent, false);

        return new ReviewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        String author= mReview[position].getmAuthor();
        String review= mReview[position].getmContent();
        tv_author_adapter.setText(author);
        tv_review_adapter.setText(review);

    }

    @Override
    public int getItemCount() {

        return mReview.length;
    }

    class ReviewHolder extends RecyclerView.ViewHolder {

        ReviewHolder(View itemView) {
            super(itemView);
            tv_author_adapter = itemView.findViewById(R.id.tv_author);
            tv_review_adapter = itemView.findViewById(R.id.tv_review_adapter);
        }
    }
}
