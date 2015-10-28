package com.baxamoosa.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import model.Movie;
import network.FetchMoviesTask;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {


    private final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Movie> listOfMovies;
    private String mSortBy;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", listOfMovies);
        outState.putString("sort_by", mSortBy);
        super.onSaveInstanceState(outState);
        Timber.v(TAG + " onSaveInstanceState");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // testing Weather model and WeatherAdapter
        /*setContentView(R.layout.main_listview);

        Weather weather_data[] = new Weather[]
                {
                        new Weather(R.drawable.sample_0, "Cloudy"),
                        new Weather(R.drawable.sample_0, "Showers"),
                        new Weather(R.drawable.sample_0, "Snow"),
                        new Weather(R.drawable.sample_0, "Storm"),
                        new Weather(R.drawable.sample_0, "Sunny")
                };

        WeatherAdapter adapter = new WeatherAdapter(this, R.layout.listview_item_row, weather_data);

        ListView listView1 = (ListView)findViewById(R.id.listView1);

        View header = getLayoutInflater().inflate(R.layout.listview_header_row, null);
        listView1.addHeaderView(header);

        listView1.setAdapter(adapter);*/

        // testing MovieTrailers model and MovieTrailersAdapter
        /*setContentView(R.layout.main_listview);

        Timber.v(TAG + " calling FetchMovieReviewsTask");
        FetchMovieReviewsTask reviewsTask = new FetchMovieReviewsTask(this, "id", this.findViewById(android.R.id.content).getRootView());
        reviewsTask.execute();

        Timber.v(TAG + " calling FetchMovieTrailersTask");
        FetchMovieTrailersTask trailersTask = new FetchMovieTrailersTask(this, "id", this.findViewById(android.R.id.content).getRootView());
        trailersTask.execute();*/

        // TODO apply reviews to this and test to see if it works. Most likely issue is with the dynamic ListViews not getting the right heights

        if (savedInstanceState != null) {
            Timber.v(TAG + " savedInstanceState != null");
            listOfMovies = (ArrayList<Movie>) savedInstanceState.get("key");
            mSortBy = savedInstanceState.getString("sort_by");
        }

        setContentView(R.layout.activity_main);

        Timber.v(TAG + " Activity Created");

        ConnectivityManager cm =
                (ConnectivityManager) PopularMovies.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Timber.v(TAG + " isConnected: " + isConnected);

        if (isConnected) {
            Timber.v(TAG + " (inside if) isConnected: " + isConnected);
            FetchMoviesTask moviesTask = new FetchMoviesTask(MainActivity.this, this.findViewById(android.R.id.content).getRootView());
            moviesTask.execute();
        } else {
            Timber.v(TAG + " (inside else) isConnected: " + isConnected);
            Toast.makeText(this, "No network connection.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // get sort by from share preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sortBy = sharedPref.getString(
                getString(R.string.prefs_sorting_key),
                getString(R.string.prefs_sorting_default));

        if (mSortBy == null || mSortBy.compareTo(sortBy) != 0) {
            mSortBy = sortBy;

            // if back from settings activity, the preference value may be changed
            FetchMoviesTask moviesTask = new FetchMoviesTask(MainActivity.this, this.findViewById(android.R.id.content).getRootView());
            moviesTask.execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
