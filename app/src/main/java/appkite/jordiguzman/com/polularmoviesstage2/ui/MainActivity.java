package appkite.jordiguzman.com.polularmoviesstage2.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import appkite.jordiguzman.com.polularmoviesstage2.R;
import appkite.jordiguzman.com.polularmoviesstage2.adapter.MovieAdapter;
import appkite.jordiguzman.com.polularmoviesstage2.adapter.MovieAdapterFavorites;
import appkite.jordiguzman.com.polularmoviesstage2.data.MovieContract;
import appkite.jordiguzman.com.polularmoviesstage2.interfaces.AsynTaskCompleteListeningMovie;
import appkite.jordiguzman.com.polularmoviesstage2.model.Movie;
import appkite.jordiguzman.com.polularmoviesstage2.utils.FetchMyDataTask;
import appkite.jordiguzman.com.polularmoviesstage2.utils.MovieUrlUtils;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickListener,
        MovieAdapterFavorites.MovieClickListener  {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String CALLBACK_QUERY = "callbackQuery";
    private static final String CALLBACK_NAMESORT= "callbackNamesort";
    private static final String CALLBACK_FAVORITES = "callbackFavorites";
    public static String queryMovie = "popular";
    private String nameSort = "Popular Movies";
    public static Movie[] mMovie = null;
    private boolean isFavorited;
    private MovieAdapterFavorites movieAdapter;
    public static ArrayList<String> dataDetail = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    public static RecyclerView mRecyclerView;
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;
    @SuppressLint("StaticFieldLeak")
    public static TextView tv_error;
    @SuppressLint("StaticFieldLeak")
    public static Button btn_retry;
    @SuppressLint("StaticFieldLeak")
    public static TextView tv_no_data;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rv_main);
        btn_retry = findViewById(R.id.btn_retry);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);

        progressBar = findViewById(R.id.pb_main);
        tv_error = findViewById(R.id.tv_error);
        tv_no_data = findViewById(R.id.tv_no_data);
        tv_no_data.setVisibility(View.INVISIBLE);

        setTitle(nameSort);
        if (!isOnline()) {
            errorNetworkApi();
            return;
        }


        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(CALLBACK_QUERY) || savedInstanceState.containsKey(CALLBACK_NAMESORT)){
                queryMovie = savedInstanceState.getString(CALLBACK_QUERY);
                nameSort = savedInstanceState.getString(CALLBACK_NAMESORT);
                setTitle(nameSort);
                new FetchMyDataTask(this, new MovieFetchTaskCompleteListener()).execute(queryMovie);
                return;
            }
        }

        new FetchMyDataTask(this, new MovieFetchTaskCompleteListener()).execute(queryMovie);

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String queryMovieSaved = queryMovie;
        String nameSortSaved = nameSort;
        outState.putString(CALLBACK_QUERY, queryMovieSaved);
        outState.putString(CALLBACK_NAMESORT, nameSortSaved);


    }
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isOnline()) return false;
        if (MovieUrlUtils.API_KEY.equals("")) return false;
        int id = item.getItemId();
        switch (id) {
            case R.id.popularity:
                queryMovie = "popular";
                new FetchMyDataTask(this, new MovieFetchTaskCompleteListener()).execute(queryMovie);
                nameSort = "Popular Movies";
                setTitle(nameSort);
                break;
            case R.id.top_rated:
                queryMovie = "top_rated";
                new FetchMyDataTask(this, new MovieFetchTaskCompleteListener()).execute(queryMovie);
                nameSort = "Top Rated Movies";
                setTitle(nameSort);
                break;
            case R.id.favorites:
                nameSort= "Favorites";
                loadFavorites();
                setTitle(nameSort);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadFavorites() {
        dataDetail.clear();

        Cursor mCursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);

        if (mCursor != null){
            while (mCursor.moveToNext()){
                dataDetail.add(mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE)));
            }
        }
        assert mCursor != null;
        mCursor.close();
        mRecyclerView.setVisibility(View.VISIBLE);
        hideProgressAndTextview();

        movieAdapter = new MovieAdapterFavorites(dataDetail, this, MainActivity.this);
        movieAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(movieAdapter);
        if (dataDetail.size()==0){
            tv_no_data.setVisibility(View.VISIBLE);
        }else {
            tv_no_data.setVisibility(View.INVISIBLE);
        }

    }


    public static void errorNetworkApi() {
        progressBar.setVisibility(View.INVISIBLE);
        tv_error.setVisibility(View.VISIBLE);
        btn_retry.setVisibility(View.VISIBLE);
    }

    public static void preExecute(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void clickRetry(View view) {
        if (!isOnline()) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            view.startAnimation(shake);
            return;
        }
        queryMovie = "popular";
        btn_retry.setVisibility(View.INVISIBLE);
        tv_error.setVisibility(View.INVISIBLE);
        new FetchMyDataTask(this, new MovieFetchTaskCompleteListener()).execute(queryMovie);
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClickMovie(int position) {


        if (!isOnline()) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            errorNetworkApi();
            return;
        }

        if (nameSort.equals("Favorites")){
            isFavorited=true;
            Intent intentToDetail = new Intent(this, DetailActivity.class);
            intentToDetail.putExtra("fromFavorites", isFavorited);
            intentToDetail.putExtra("sendPosition", position);
            startActivity(intentToDetail, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            isFavorited=false;
            return;
        }

        Intent intentToDetail = new Intent(this, DetailActivity.class);
        intentToDetail.putExtra("sendData",  mMovie[position]);
        startActivity(intentToDetail, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }


    private void hideProgressAndTextview() {
        progressBar.setVisibility(View.INVISIBLE);
        tv_error.setVisibility(View.INVISIBLE);
    }




    private class MovieFetchTaskCompleteListener implements AsynTaskCompleteListeningMovie {

        @Override
        public void onTaskComplete(Movie[] result) {
            if (result != null) {
                mRecyclerView.setVisibility(View.VISIBLE);
                hideProgressAndTextview();
                mMovie = result;
                MovieAdapter movieAdapter = new MovieAdapter(mMovie, MainActivity.this, MainActivity.this);
                mRecyclerView.setAdapter(movieAdapter);
            }
        }
    }


}
