package com.baxamoosa.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import model.Movie;
import network.FetchMoviesTask;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements DetailFragment.OnItemClickedListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public static boolean mTwoPane;
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

        if (savedInstanceState != null) {
            Timber.v(TAG + " savedInstanceState != null");
            listOfMovies = (ArrayList<Movie>) savedInstanceState.get("key");
            mSortBy = savedInstanceState.getString("sort_by");
        }

        setContentView(R.layout.activity_item_app_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.frameLayout) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            ConnectivityManager cm =
                    (ConnectivityManager) PopularMovies.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (isConnected) {
                // Timber.v(TAG + " (inside if) isConnected: " + isConnected);
                FetchMoviesTask moviesTask = new FetchMoviesTask(MainActivity.this, this.findViewById(android.R.id.content).getRootView());
                moviesTask.execute();
            } else {
                // Timber.v(TAG + " (inside else) isConnected: " + isConnected);
                Toast.makeText(this, "No network connection.", Toast.LENGTH_LONG).show();
            }
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

    @Override
    public void OnItemClicked(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Timber.v(TAG + " mTwoPane is " + mTwoPane);
            // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_detail_container, new DetailFragment()).commit();

            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("id", "24428");
            intent.putExtra("title", "my title");
            intent.putExtra("poster_thumbnail", "/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg");
            intent.putExtra("release_date", "2012");
            intent.putExtra("rating", "10");
            intent.putExtra("synopsis", "some test here. some test here. some test here. ");

            this.startActivity(intent);
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Timber.v(TAG + " mTwoPane is " + mTwoPane);
        }
    }
}
