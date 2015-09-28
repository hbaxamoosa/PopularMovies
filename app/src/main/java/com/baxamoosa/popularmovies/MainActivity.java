package com.baxamoosa.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import adapter.MovieAdapter;
import model.Movie;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {


    private final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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

        setContentView(R.layout.activity_main);

        Timber.v(TAG + " Activity Created");

        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
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
            FetchMoviesTask moviesTask = new FetchMoviesTask();
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

    public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {

        private final String TAG = FetchMoviesTask.class.getSimpleName();
        private String sort_type;
        private String sort;

        @Override
        protected void onPreExecute() {
            Timber.v(TAG + "inside onPreExecute");
            super.onPreExecute();

            // Getting sort order preference
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String sort_type = sharedPref.getString(getString(R.string.prefs_sorting_key), getString(R.string.prefs_sorting_default));

            Timber.v(TAG + " sort_type = " + sort_type);
            if (sort_type.equals(getString(R.string.most_popular))) {
                sort = "popularity.desc";
            } else if (sort_type.equals(getString(R.string.highest_rated))) {
                sort = "vote_count.desc";
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            super.onPostExecute(movies);
            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new MovieAdapter(movies);
            mRecyclerView.setAdapter(mAdapter);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Movie[] doInBackground(Void... params) {
            Timber.v(TAG + "inside doInBackground");

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String api_key = Constants.api_key;

            try {
                // Construct the URL for the themoviedb.org API
                final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort)
                        .appendQueryParameter(API_KEY, api_key)
                        .build();

                URL url = new URL(builtUri.toString());

                Timber.v(TAG + "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Timber.v(TAG + moviesJsonStr);
            } catch (IOException e) {
                Timber.e(TAG + "Error " + e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Timber.e(TAG + "Error closing stream " + e);
                    }
                }
            }
            try {
                return getMoviesFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Timber.e(TAG + e.getMessage() + e);
                e.printStackTrace();
            }
            return null;
        }

        private Movie[] getMoviesFromJson(String moviesJsonStr) throws JSONException {
            final String moviesJson = "results";
            Timber.v(TAG + moviesJson);

            // Create JSONObject from results string
            JSONObject moviesJsonObj = new JSONObject(moviesJsonStr);

            // Create JSONArray from JSONObject
            JSONArray moviesArray = moviesJsonObj.getJSONArray(moviesJson);

            Timber.v(TAG + " " + "JSONObject movie size = " + moviesArray.length());

            // Create Movie objects array
            Movie[] movies = new Movie[moviesArray.length()];
            Timber.v(TAG + " " + "moviesArray.length()" + " " + moviesArray.length());

            for (int i = 0; i < moviesArray.length(); i++) {
                Timber.v(TAG + "i = " + i);
                // Get JSON object representing a single movie
                JSONObject movie = moviesArray.getJSONObject(i);

                movies[i] = new Movie();
                // Get title
                Timber.v(TAG + " " + "original_title :" + " " + movie.getString("original_title"));
                movies[i].setTitle(movie.optString("original_title").toString());

                // Get synopsis
                Timber.v(TAG + " " + "overview :" + " " + movie.getString("overview"));
                movies[i].setSynopsis(movie.getString("overview"));

                // Get release date
                Timber.v(TAG + " " + "release_date :" + " " + movie.getString("release_date"));
                movies[i].setDate(movie.getString("release_date"));

                // Get poster path
                Timber.v(TAG + " " + "poster_path :" + " " + movie.getString("poster_path"));
                movies[i].setThumbnail(movie.getString("poster_path"));

                // Get rating
                Timber.v(TAG + " " + "vote_average :" + " " + movie.getString("vote_average"));
                movies[i].setRating(movie.getString("vote_average"));
            }

            Timber.v(TAG + " " + "JSONObject movie size = " + movies.length);
            return movies;
        }
    }

    
}
