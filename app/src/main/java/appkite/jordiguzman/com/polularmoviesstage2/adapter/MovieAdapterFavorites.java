package appkite.jordiguzman.com.polularmoviesstage2.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import appkite.jordiguzman.com.polularmoviesstage2.R;
import appkite.jordiguzman.com.polularmoviesstage2.data.MovieContract;


public class MovieAdapterFavorites extends RecyclerView.Adapter<MovieAdapterFavorites.MovieHolder>{

    private Context mContext=null;
    private Cursor mCursor;
    private MovieClickListener mMovieClickListener = null;

    public MovieAdapterFavorites(Cursor cursor, Context context, MovieClickListener movieClickListener){
        mCursor = cursor;
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

        mCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_IMAGE);

        if (mCursor != null){
            int sizeCursor= mCursor.getCount();
            ArrayList<String> listMovies = new ArrayList<>();
            mCursor.moveToFirst();
            for (int i=0; i< sizeCursor;i++){
                listMovies.add(mCursor.getString(3));
                mCursor.moveToNext();
            }
            Picasso.with(mContext)
                            .load(listMovies.get(position))
                            .fit()
                            .into(holder.imageViewHolder);

        }
        assert mCursor != null;
        mCursor.close();

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
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
