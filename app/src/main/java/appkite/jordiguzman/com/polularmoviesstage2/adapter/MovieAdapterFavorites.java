package appkite.jordiguzman.com.polularmoviesstage2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import appkite.jordiguzman.com.polularmoviesstage2.R;
import appkite.jordiguzman.com.polularmoviesstage2.ui.MainActivity;


public class MovieAdapterFavorites extends RecyclerView.Adapter<MovieAdapterFavorites.MovieHolder>{

    private Context mContext=null;

    private MovieClickListener mMovieClickListener = null;

    public MovieAdapterFavorites(ArrayList<String> arrayList, Context context, MovieClickListener movieClickListener){
        MainActivity.dataDetail = arrayList;
        mContext = context;
        mMovieClickListener = movieClickListener;

    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_movie_poster, parent, false);

        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {

        Log.e("Position", String.valueOf(position));
        Picasso.with(mContext)
                .load(MainActivity.dataDetail.get(position))
                .into(holder.imageViewHolder);
    }


    @Override
    public int getItemCount() {
        return MainActivity.dataDetail.size();
    }

    public interface MovieClickListener {
        void onClickMovie(int position);
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView imageViewHolder;

        MovieHolder(View itemView) {
            super(itemView);
            imageViewHolder = itemView.findViewById(R.id.iv_list_item_poster);
            imageViewHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickPosition = getAdapterPosition();
            mMovieClickListener.onClickMovie(clickPosition);
        }
    }
}
