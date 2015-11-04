package network;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baxamoosa.popularmovies.Constants;
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

import adapter.MovieTrailersAdapter;
import model.MovieTrailers;
import timber.log.Timber;
import utilities.utils;

public class FetchMovieTrailersTask extends AsyncTask<Void, Void, MovieTrailers[]> {

    private final String TAG = FetchMovieTrailersTask.class.getSimpleName();
    private Context mContext;
    private String id;
    private View rootView;

    public FetchMovieTrailersTask(Context context, String movieID, View view) {
        // Timber.v(TAG + " inside FetchMovieTrailersTask constructor");
        mContext = context;
        id = movieID;
        rootView = view;
        // Timber.v(TAG + " id : " + id);
    }

    @Override
    protected MovieTrailers[] doInBackground(Void... params) {
        // Timber.v(TAG + " MovieTrailers[] inside doInBackground");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String trailersJsonStr = null;

        try {
            // Construct the URL for the themoviedb.org API based on http://api.themoviedb.org/3/movie/id/videos
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/videos";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, Constants.api_key)
                    .build();

            URL url = new URL(builtUri.toString());

            // Timber.v(TAG + "Built URI " + builtUri.toString());

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
            trailersJsonStr = buffer.toString();
            Timber.v(TAG + trailersJsonStr);
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
            return getTrailersFromJson(trailersJsonStr);
        } catch (JSONException e) {
            Timber.e(TAG + e.getMessage() + e);
            e.printStackTrace();
        }
        return null;
    }

    private MovieTrailers[] getTrailersFromJson(String trailersJsonStr) throws JSONException {
        // Timber.v(TAG + " inside getTrailersFromJson " + trailersJsonStr);

        final String trailersJson = "results";
        // Timber.v(TAG + " " + trailersJson);

        // Create JSONObject from results string
        JSONObject trailersJsonObj = new JSONObject(trailersJsonStr);

        // Create JSONArray from JSONObject
        JSONArray trailersArray = trailersJsonObj.getJSONArray(trailersJson);

        // Create Movie objects array
        MovieTrailers[] trailers = new MovieTrailers[trailersArray.length()];
        // Timber.v(TAG + " " + "trailersArray.length()" + " " + trailersArray.length());

        for (int i = 0; i < trailersArray.length(); i++) {
            Timber.v(TAG + " i = " + i);
            // Get JSON object representing a single movie
            JSONObject trailersArrayJSONObject = trailersArray.getJSONObject(i);

            trailers[i] = new MovieTrailers();
            // get id
            // Timber.v(TAG + " id :" + " " + trailersArrayJSONObject.getString("id"));
            trailers[i].setId(trailersArrayJSONObject.getString("id"));

            // Get key
            // Timber.v(TAG + " key :" + " " + trailersArrayJSONObject.getString("key"));
            trailers[i].setKey(trailersArrayJSONObject.getString("key"));

            // Get name
            // Timber.v(TAG + " name :" + " " + trailersArrayJSONObject.getString("name"));
            trailers[i].setName(trailersArrayJSONObject.getString("name"));

            // Get site
            // Timber.v(TAG + " site :" + " " + trailersArrayJSONObject.getString("site"));
            trailers[i].setSite(trailersArrayJSONObject.getString("site"));

            // Get type
            // Timber.v(TAG + " type :" + " " + trailersArrayJSONObject.getString("type"));
            trailers[i].setType(trailersArrayJSONObject.getString("type"));
        }

        // Timber.v(TAG + " trailers array size: " + trailers.length);
        return trailers;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(final MovieTrailers[] movieTrailers) {
        super.onPostExecute(movieTrailers);

        // Timber.v(TAG + " inside onPostExecute(MovieTrailers[] movieTrailers)");
        ListView trailersLV = (ListView) rootView.findViewById(R.id.listview_trailers);
        //ListView trailersLV = (ListView) rootView.findViewById(R.id.LVtrailers);
        MovieTrailersAdapter movieTrailerAdapter = new MovieTrailersAdapter(mContext, R.layout.listview_trailers_item_row, movieTrailers);
        trailersLV.setAdapter(movieTrailerAdapter);
        utils.setListViewHeightBasedOnChildren(trailersLV);

        trailersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Timber.v(TAG + " inside onItemClick(AdapterView<?> parent, View view, int position, long id)");
                Intent trailerIntent = new Intent(Intent.ACTION_VIEW);
                // trailerIntent.setData(Uri.parse("https://www.youtube.com/watch?v=Ut3Hvbvs1bs"));
                String trailerURL = "https://www.youtube.com/watch?v=" + movieTrailers[position].getKey();
                // Timber.v(TAG + " trailerURL: " + trailerURL);
                trailerIntent.setData(Uri.parse("https://www.youtube.com/watch?v=" + movieTrailers[position].getKey()));
                mContext.startActivity(trailerIntent);
            }
        });
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
