package appkite.jordiguzman.com.polularmoviesstage2.ui;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import appkite.jordiguzman.com.polularmoviesstage2.R;
import appkite.jordiguzman.com.polularmoviesstage2.adapter.ReviewAdapter;
import appkite.jordiguzman.com.polularmoviesstage2.adapter.TrailerAdapter;
import appkite.jordiguzman.com.polularmoviesstage2.data.MovieContract;
import appkite.jordiguzman.com.polularmoviesstage2.databinding.ActivityDetailBinding;
import appkite.jordiguzman.com.polularmoviesstage2.model.Movie;
import appkite.jordiguzman.com.polularmoviesstage2.model.Review;
import appkite.jordiguzman.com.polularmoviesstage2.model.Trailer;
import appkite.jordiguzman.com.polularmoviesstage2.utils.MovieJsonUtils;
import appkite.jordiguzman.com.polularmoviesstage2.utils.MovieUrlUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static appkite.jordiguzman.com.polularmoviesstage2.ui.MainActivity.btn_retry;
import static appkite.jordiguzman.com.polularmoviesstage2.ui.MainActivity.isFavorited;
import static appkite.jordiguzman.com.polularmoviesstage2.ui.MainActivity.mContext;
import static appkite.jordiguzman.com.polularmoviesstage2.ui.MainActivity.tv_error;


public class DetailActivity extends AppCompatActivity  {

    ActivityDetailBinding mBinding;
    @BindView(R.id.rv_reviews)
    RecyclerView mRecyclerViewReviews;
    @BindView(R.id.rv_trailers)
    RecyclerView mRecyclerViewTrailers;
    @BindView(R.id.fb_detail)
    FloatingActionButton fb;

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String URL_IMAGE_PATH = "http://image.tmdb.org/t/p/w342";
    private static final String URL_IMAGE_PATH_LARGE = "http://image.tmdb.org/t/p/w780";
    public static Movie movieDataReceived;
    private Review[] mReviews = null;
    private Trailer[] mTrailers = null;
    public static ArrayList<String[]> arrayListMovies = new ArrayList<>();
    public static ArrayList<String> dataDetail = new ArrayList<>();
    private String title, poster, plot, rating, release, releaseFinal, id;
    public static String [][] movieFav;
    public static boolean fromFavorites;
    int position, idToast;
    private final String URL_YOUTUBE = "http://www.youtube.com/watch?v=";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        ButterKnife.bind(this);

        mRecyclerViewReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewReviews.setHasFixedSize(true);
        mRecyclerViewTrailers.setHasFixedSize(true);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        fromFavorites= bundle.getBoolean("fromFavorites");
        position = bundle.getInt("sendPosition");

        if (!fromFavorites){
            populateVariablesMovies();
        }else {
           populateVariablesMoviesFavorites();
        }

        new MovieFetchTaskReviews().execute("reviews");
        new MoviewFetchTaskTrailer().execute("trailers");

    }

    public void populateVariablesMovies(){
        movieDataReceived = getIntent().getParcelableExtra("sendData");
        title = movieDataReceived.getmTitle();
        poster = movieDataReceived.getmMoviePoster();
        plot = movieDataReceived.getmPlot();
        rating = movieDataReceived.getmRating();
        release = movieDataReceived.getmReleaseDate();
        releaseFinal = release.substring(0, 4);
        fb.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite));
        Picasso.with(this)
                .load(URL_IMAGE_PATH.concat(poster))
                .into(mBinding.ivPosterDetail);

        putData();
    }

    public void populateVariablesMoviesFavorites(){
        fb.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete));
        Picasso.with(this)
                .load(movieFav[position][1])
                .into(mBinding.ivPosterDetail);
        title= movieFav[position][0];
        plot= movieFav[position][4];
        rating= movieFav[position][3];
        release = movieFav[position][2];
        releaseFinal = release.substring(0, 4);
        id= movieFav[position][5];
        putData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void toZoom(View view){
        String urlImageLarge;
        if (!fromFavorites){
            urlImageLarge = URL_IMAGE_PATH_LARGE.concat(poster);
        }else {
            urlImageLarge = movieFav[position][1];
        }
        Intent toZoom = new Intent(this, ZoomPoster.class);
        toZoom.putExtra("poster", urlImageLarge);
        toZoom.putExtra("title", title);
        startActivity(toZoom);
    }

    public void putData(){
        mBinding.tvTitle.setText(title);
        mBinding.tvPlot.setText(plot);
        mBinding.tvRating.setText(rating);
        mBinding.tvRelease.setText(releaseFinal);
        setTitle(title);

        dataDetail.add(getTitle().toString());
        dataDetail.add(poster);
        dataDetail.add(plot);
        dataDetail.add(rating);
        dataDetail.add(releaseFinal);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void saveDeleteData(View view){

        if (!isFavorited() && !fromFavorites) {
            idToast = 2;
            toastMessages();
            return;
        }
        if (!isFavorited()){
            idToast=1;
            deleteMovieData();
            if (fromFavorites)fb.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite));
        }else {
            idToast=0;
            saveMovieData();
            if (fromFavorites)fb.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_delete));
        }
    }
    private void toastMessages(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup)findViewById(R.id.toast_container));
        TextView tv_toast = layout.findViewById(R.id.tv_toast);

        switch (idToast){
            case 0:
                tv_toast.setText(getResources().getString(R.string.toast_save));
                break;
            case 1:
                tv_toast.setText(getResources().getString(R.string.toast_delete));
                break;
            case 2:
                tv_toast.setText(getResources().getString(R.string.toast_in_favorites));
                break;

        }
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }



    public boolean isFavorited(){

        Cursor mCursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_ID);
        if (mCursor != null){
            while (mCursor.moveToNext()){
                String idMovies = mCursor.getString(1);
                String idMovieActual;
                if (!fromFavorites){
                     idMovieActual= String.valueOf(movieDataReceived.getmId());
                }else {
                    idMovieActual= movieFav[position][5];
                }
                if (idMovies.equals(idMovieActual)){
                    return false;
                }
            }
        }
        assert mCursor != null;
        mCursor.close();
        return true;
    }

    private void deleteMovieData() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(id)).build();

        contentResolver.delete(uri, null, null);
        MainActivity.dataDetail.clear();
        idToast=1;
        toastMessages();

    }

    private void saveMovieData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_ID, movieDataReceived.getmId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieDataReceived.getmTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE, URL_IMAGE_PATH_LARGE.concat(movieDataReceived.getmMoviePoster()));
        contentValues.put(MovieContract.MovieEntry.COLUMN_PLOT, movieDataReceived.getmPlot());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieDataReceived.getmRating());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, movieDataReceived.getmReleaseDate());
        getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        idToast=0;
        toastMessages();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                switch (MainActivity.queryMovie) {
                    case "popular":
                        MainActivity.queryMovie = "popular";
                        break;
                    case "top_rated":
                        MainActivity.queryMovie = "top_rated";
                        break;
                }
                break;
            case R.id.menu_share:
               Log.e(LOG_TAG, mTrailers[position].getmKey());
                Intent shareMovieVideo = new Intent(Intent.ACTION_SEND);
                shareMovieVideo.setType("text/plain");
                if (!isFavorited){
                    shareMovieVideo.putExtra(Intent.EXTRA_SUBJECT, movieDataReceived.getmTitle());
                    shareMovieVideo.putExtra(Intent.EXTRA_TEXT, movieDataReceived.getmTitle() + " " + URL_YOUTUBE.concat(mTrailers[position].getmKey()));
                }else {
                    shareMovieVideo.putExtra(Intent.EXTRA_SUBJECT, movieFav[position][0]);
                    shareMovieVideo.putExtra(Intent.EXTRA_TEXT, movieFav[position][0] + " " + URL_YOUTUBE.concat(mTrailers[position].getmKey()));
                }
                startActivity(Intent.createChooser(shareMovieVideo, getString(R.string.share_trailer)));

                break;
        }
        return super.onOptionsItemSelected(item);
    }




    @SuppressLint("StaticFieldLeak")
    private class MoviewFetchTaskTrailer extends AsyncTask<String, Void, Trailer[]> {

        URL trailerUrl;
        @Override
        protected Trailer[] doInBackground(String... strings) {

            if (MovieUrlUtils.API_KEY.equals("")) {
                MainActivity.errorNetworkApi();
                tv_error.setText(R.string.missing_api_key);
                tv_error.setTextColor(ContextCompat.getColor(mContext, R.color.secondary_text));
                btn_retry.setVisibility(View.INVISIBLE);
                return null;
            }
            if (!fromFavorites){
                trailerUrl= MovieUrlUtils.buildUrlTrailers(String.valueOf(movieDataReceived.getmId()).concat("/"), strings[0]);
            }else {
                trailerUrl= MovieUrlUtils.buildUrlTrailers(id.concat("/"), strings[0]);

            }
            String trailerResponse;
            try {
                trailerResponse = MovieUrlUtils.getResponseFromHttp(trailerUrl);
                mTrailers = MovieJsonUtils.parseJsonMovieTrailer(trailerResponse);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Problems with trailer", e);
            }

            return mTrailers;
        }

        @Override
        protected void onPostExecute(Trailer[] trailers) {
            if (trailers != null) {
                mTrailers = trailers;
                TrailerAdapter trailerAdapter = new TrailerAdapter(mTrailers, DetailActivity.this);
                mRecyclerViewTrailers.setAdapter(trailerAdapter);

            } else {
                Log.e(LOG_TAG, "Problems with adapter");
            }

            if (isCancelled()) {
                new MoviewFetchTaskTrailer().cancel(true);
            }
            for (Trailer mTrailer : mTrailers) {
                dataDetail.add(mTrailer.getmName());
                dataDetail.add(mTrailer.getmKey());

            }
            assert trailers != null;
            if (trailers.length == 0) {
                mRecyclerViewTrailers.setVisibility(View.INVISIBLE);
                mBinding.tvAdapterNoDataReview.setVisibility(View.VISIBLE);

            }
        }

        @Override
        protected void onCancelled(Trailer[] trailers) {
            super.onCancelled(trailers);
            new MoviewFetchTaskTrailer().cancel(true);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MovieFetchTaskReviews extends AsyncTask<String, Void, Review[]> {



        @Override
        protected Review[] doInBackground(String... strings) {

            URL reviewUrl;
            if (MovieUrlUtils.API_KEY.equals("")) {
                MainActivity.errorNetworkApi();
                tv_error.setText(R.string.missing_api_key);
                tv_error.setTextColor(ContextCompat.getColor(mContext, R.color.secondary_text));
                btn_retry.setVisibility(View.INVISIBLE);
                return null;
            }

            if (!fromFavorites){
                 reviewUrl= MovieUrlUtils.buildUrlReview(String.valueOf(movieDataReceived.getmId()).concat("/"), strings[0]);
            }else {
                reviewUrl= MovieUrlUtils.buildUrlReview(id.concat("/"), strings[0]);

            }
            String reviewResponse;
            try {
                reviewResponse = MovieUrlUtils.getResponseFromHttp(reviewUrl);
                mReviews = MovieJsonUtils.parseJsonMovieReview(reviewResponse);

            } catch (Exception e) {
                Log.e(LOG_TAG, "Problems with review", e);
            }

            return mReviews;
        }

        @Override
        protected void onPostExecute(Review[] reviews) {
            if (reviews != null) {
                mReviews = reviews;
                ReviewAdapter reviewAdapter = new ReviewAdapter(mReviews);
                mRecyclerViewReviews.setAdapter(reviewAdapter);

            } else {
                Log.e(LOG_TAG, "Problems with adapter");
            }
            if (isCancelled()) {
                new MovieFetchTaskReviews().cancel(true);
            }
            for (Review mReview : mReviews){
                dataDetail.add(mReview.getmAuthor());
                dataDetail.add(mReview.getmContent());
            }

            assert reviews != null;
            if (reviews.length == 0) {
                mRecyclerViewReviews.setVisibility(View.INVISIBLE);
                mBinding.tvAdapterNoData.setVisibility(View.VISIBLE);
                mBinding.tvAdapterNoData.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_text));

            }

        }

        @Override
        protected void onCancelled(Review[] reviews) {
            super.onCancelled(reviews);
            new MovieFetchTaskReviews().cancel(true);
        }
    }
}
