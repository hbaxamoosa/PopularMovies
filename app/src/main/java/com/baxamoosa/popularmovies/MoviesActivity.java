package com.baxamoosa.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import fragments.MoviesFragment;
import model.Movie;
import timber.log.Timber;

public class MoviesActivity extends AppCompatActivity implements MoviesFragment.Callback {

    public static boolean mTwoPane;  // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
    private final String TAG = MoviesActivity.class.getSimpleName();
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
        setContentView(R.layout.activity_moviesactivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        ConnectivityManager cm =
                (ConnectivityManager) PopularMovies.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (savedInstanceState != null) {
            // Timber.v(TAG + " savedInstanceState != null");
            listOfMovies = (ArrayList<Movie>) savedInstanceState.get("key");
            mSortBy = savedInstanceState.getString("sort_by");
        }

        if (savedInstanceState == null) {
            if (isConnected) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                MoviesFragment fragment = new MoviesFragment();
                transaction.replace(R.id.container, fragment);
                transaction.commit();

                View movieDetails = findViewById(R.id.fragment_detail_container);
                if (movieDetails != null && movieDetails.getVisibility() == View.VISIBLE) {
                    // The detail container view will be present only in the
                    // large-screen layouts (res/values-large and
                    // res/values-sw600dp). If this view is present, then the
                    // activity should be in two-pane mode.
                    mTwoPane = true;

                    // In two-pane mode, list items should be given the 'activated' state when touched.
                    FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
                    DetailFragment mFragment = new DetailFragment();
                    mTransaction.replace(R.id.fragment_detail_container, mFragment);
                    mTransaction.commit();
                }
            } else {
                // Timber.v(TAG + " (inside else) isConnected: " + isConnected);
                Toast.makeText(this, "No network connection.", Toast.LENGTH_LONG).show();
            }
        }
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
            Timber.v(TAG + " onResume()");
            // if back from settings activity, the preference value may be changed
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            MoviesFragment fragment = new MoviesFragment();
            transaction.replace(R.id.container, fragment);
            transaction.commit();

            View movieDetails = findViewById(R.id.fragment_detail_container);
            if (movieDetails != null && movieDetails.getVisibility() == View.VISIBLE) {
                // The detail container view will be present only in the
                // large-screen layouts (res/values-large and
                // res/values-sw600dp). If this view is present, then the
                // activity should be in two-pane mode.
                mTwoPane = true;

                // In two-pane mode, list items should be given the 'activated' state when touched.
                FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
                DetailFragment mFragment = new DetailFragment();
                mTransaction.replace(R.id.fragment_detail_container, mFragment);
                mTransaction.commit();
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

    /**
     * Callback method from {@link MoviesFragment}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int position, Movie[] mMovies) {
        Timber.v(TAG + " inside onItemSelected(String id) is " + position);
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString("id", mMovies[position].getId());
            arguments.putString("title", mMovies[position].getTitle());
            arguments.putString("poster_thumbnail", mMovies[position].getThumbnail());
            arguments.putString("release_date", mMovies[position].getDate());
            arguments.putString("rating", mMovies[position].getRating());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, fragment)
                    .commit();
        } else { // phone
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("id", mMovies[position].getId());
            intent.putExtra("title", mMovies[position].getTitle());
            intent.putExtra("poster_thumbnail", mMovies[position].getThumbnail());
            intent.putExtra("release_date", mMovies[position].getDate());
            intent.putExtra("rating", mMovies[position].getRating());
            intent.putExtra("synopsis", mMovies[position].getSynopsis());

            this.startActivity(intent);
        }
    }
}
