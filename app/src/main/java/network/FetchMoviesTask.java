package network;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.baxamoosa.popularmovies.Constants;
import com.baxamoosa.popularmovies.PopularMovies;
import com.baxamoosa.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import adapter.MovieAdapter;
import model.Movie;
import timber.log.Timber;

public class FetchMoviesTask extends AsyncTask<Void, Void, Movie[]> {

    private final String TAG = FetchMoviesTask.class.getSimpleName();
    private Context context; // this is not being used and can be removed
    private View rootView;
    private String sort;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public FetchMoviesTask(Context c, View v) {
        context = c;
        rootView = v;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Timber.v(TAG + "inside onPreExecute");
        super.onPreExecute();

        Resources res = PopularMovies.getAppContext().getResources();

        // Getting sort order preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(PopularMovies.getAppContext());
        String sort_type = sharedPref.getString(res.getString(R.string.prefs_sorting_key), res.getString(R.string.prefs_sorting_default));

        Timber.v(TAG + " sort_type = " + sort_type);
        if (sort_type.equals(res.getString(R.string.most_popular))) {
            sort = "popularity.desc";
        } else if (sort_type.equals(res.getString(R.string.highest_rated))) {
            sort = "vote_count.desc";
        }
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        // check to see if the phone is in portrait or landscape
        switch (context.getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                //Toast.makeText(this.context, "screen is in portrait", Toast.LENGTH_LONG).show();
                mLayoutManager = new GridLayoutManager(PopularMovies.getAppContext(), 2);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                //Toast.makeText(this.context, "screen is in landscape", Toast.LENGTH_LONG).show();
                mLayoutManager = new GridLayoutManager(PopularMovies.getAppContext(), 4);
                break;
        }
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

        try {
            // Construct the URL for the themoviedb.org API
            final String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, sort)
                    .appendQueryParameter(API_KEY, Constants.api_key)
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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(simpleDateFormat.parse(movie.getString("release_date")));
                Timber.v(TAG + " " + "Year :" + " " + c.get(Calendar.YEAR));
                movies[i].setDate(String.valueOf(c.get(Calendar.YEAR)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

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