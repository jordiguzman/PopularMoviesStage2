package appkite.jordiguzman.com.polularmoviesstage2.ui;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import appkite.jordiguzman.com.polularmoviesstage2.R;
import appkite.jordiguzman.com.polularmoviesstage2.adapter.ReviewAdapter;
import appkite.jordiguzman.com.polularmoviesstage2.adapter.TrailerAdapter;
import appkite.jordiguzman.com.polularmoviesstage2.data.MovieContract;
import appkite.jordiguzman.com.polularmoviesstage2.model.Movie;
import appkite.jordiguzman.com.polularmoviesstage2.model.Review;
import appkite.jordiguzman.com.polularmoviesstage2.model.Trailer;
import appkite.jordiguzman.com.polularmoviesstage2.utils.MovieJsonUtils;
import appkite.jordiguzman.com.polularmoviesstage2.utils.MovieUrlUtils;

import static appkite.jordiguzman.com.polularmoviesstage2.ui.MainActivity.btn_retry;
import static appkite.jordiguzman.com.polularmoviesstage2.ui.MainActivity.tv_error;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String URL_IMAGE_PATH = "http://image.tmdb.org/t/p/w342";
    public static Movie movieDataReceived;
    private RecyclerView mRecyclerViewReviews;
    private RecyclerView mRecyclerViewTrailers;
    private Review[] mReviews = null;
    private Trailer[] mTrailers = null;
    private TextView tv_error_adapter, tv_error_adapter_trailer;
    public static ArrayList<String> dataDetail = new ArrayList<>();
    private Cursor mCursor= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView iv_poster_detail = findViewById(R.id.iv_poster_detail);
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_plot = findViewById(R.id.tv_plot);
        TextView tv_rating = findViewById(R.id.tv_rating);
        TextView tv_release = findViewById(R.id.tv_release);
        tv_error_adapter = findViewById(R.id.tv_adapter_no_data);
        tv_error_adapter_trailer = findViewById(R.id.tv_adapter_no_data_review);
        FloatingActionButton floatingActionButton = findViewById(R.id.fb_detail);
        floatingActionButton.setSize(40);


        mRecyclerViewReviews = findViewById(R.id.rv_reviews);
        mRecyclerViewReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewTrailers = findViewById(R.id.rv_trailers);
        mRecyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerViewReviews.setHasFixedSize(true);
        mRecyclerViewTrailers.setHasFixedSize(true);


        movieDataReceived = getIntent().getParcelableExtra("sendData");

        String title = movieDataReceived.getmTitle();
        String poster = movieDataReceived.getmMoviePoster();
        String plot = movieDataReceived.getmPlot();
        String rating = movieDataReceived.getmRating();
        String release = movieDataReceived.getmReleaseDate();
        String releaseFinal = release.substring(0, 4);


        Picasso.with(this)
                .load(URL_IMAGE_PATH.concat(poster))
                .into(iv_poster_detail);
        tv_title.setText(title);
        tv_plot.setText(plot);
        tv_rating.setText(rating.concat("/10"));
        tv_release.setText(releaseFinal);

        setTitle(title);
         dataDetail.add(getTitle().toString());
         dataDetail.add(URL_IMAGE_PATH.concat(poster));
         dataDetail.add(plot);
         dataDetail.add(rating);
         dataDetail.add(releaseFinal);




        new MovieFetchTaskReviews().execute("reviews");
        new MoviewFetchTaskTrailer().execute("trailers");

    }


    public void saveData(View view){

        if (!isFavorited()){
            Toast.makeText(this, "This movie is already in favorites", Toast.LENGTH_LONG).show();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_ID, movieDataReceived.getmId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieDataReceived.getmTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE, URL_IMAGE_PATH.concat(movieDataReceived.getmMoviePoster()));
        contentValues.put(MovieContract.MovieEntry.COLUMN_PLOT, movieDataReceived.getmPlot());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieDataReceived.getmRating());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE, movieDataReceived.getmReleaseDate());
        getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);


    }
    public boolean isFavorited(){
        mCursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_ID);
        if (mCursor != null){
            while (mCursor.moveToNext()){
                String idMovies = mCursor.getString(1);
                String idMovieActual = String.valueOf(movieDataReceived.getmId());
                if (idMovies.equals(idMovieActual)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (MainActivity.queryMovie.equals("popular")) {
                    MainActivity.queryMovie = "popular";
                } else if (MainActivity.queryMovie.equals("top_rated")) {
                    MainActivity.queryMovie = "top_rated";
                }
                break;
            case R.id.menu_share:
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String title= getTitle().toString();
                String subTitle= movieDataReceived.getmPlot();
                myIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                myIntent.putExtra(Intent.EXTRA_TEXT, subTitle);
                startActivity(Intent.createChooser(myIntent, "Share using"));

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("StaticFieldLeak")
    private class MoviewFetchTaskTrailer extends AsyncTask<String, Void, Trailer[]> {

        @Override
        protected Trailer[] doInBackground(String... strings) {

            if (MovieUrlUtils.API_KEY.equals("")) {
                MainActivity.errorNetworkApi();
                tv_error.setText(R.string.missing_api_key);
                btn_retry.setVisibility(View.INVISIBLE);
                return null;
            }
            URL trailerUrl = MovieUrlUtils.buildUrlTrailers(String.valueOf(movieDataReceived.getmId()).concat("/"), strings[0]);
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
                tv_error_adapter_trailer.setVisibility(View.VISIBLE);

            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MovieFetchTaskReviews extends AsyncTask<String, Void, Review[]> {



        @Override
        protected Review[] doInBackground(String... strings) {


            if (MovieUrlUtils.API_KEY.equals("")) {
                MainActivity.errorNetworkApi();
                tv_error.setText(R.string.missing_api_key);
                btn_retry.setVisibility(View.INVISIBLE);
                return null;
            }
            URL reviewUrl = MovieUrlUtils.buildUrlReview(String.valueOf(movieDataReceived.getmId()).concat("/"), strings[0]);
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
                tv_error_adapter.setVisibility(View.VISIBLE);

            }

        }
    }


}
