package appkite.jordiguzman.com.polularmoviesstage2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import appkite.jordiguzman.com.polularmoviesstage2.R;
import appkite.jordiguzman.com.polularmoviesstage2.ui.DetailActivity;


public class MovieAdapterFavorites extends RecyclerView.Adapter<MovieAdapterFavorites.MovieHolder>{

    private Context mContext=null;


    public MovieAdapterFavorites(ArrayList<String[]> arrayList, Context context){
        DetailActivity.arrayListMovies = arrayList;
        mContext = context;


    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_movie_poster, parent, false);

        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {

        Picasso.with(mContext)
                .load(DetailActivity.movieFav[position][1])
                .into(holder.imageViewHolder);

    }


    @Override
    public int getItemCount() {
        return DetailActivity.movieFav.length;
    }



    class MovieHolder extends RecyclerView.ViewHolder{

        final ImageView imageViewHolder;

        MovieHolder(View itemView) {
            super(itemView);
            imageViewHolder = itemView.findViewById(R.id.iv_list_item_poster);

        }

    }
}
