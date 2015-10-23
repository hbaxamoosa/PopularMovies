package network;

import android.net.Uri;
import android.os.AsyncTask;

import com.baxamoosa.popularmovies.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import model.MovieTrailers;
import timber.log.Timber;

public class FetchMovieTrailersTask extends AsyncTask<Void, Void, MovieTrailers[]> {

    private final String TAG = FetchMovieTrailersTask.class.getSimpleName();
    private String id;

    public FetchMovieTrailersTask(String movieID) {
        Timber.v(TAG + " inside FetchMovieTrailersTask constructor");
        id = movieID;
        Timber.v(TAG + " id : " + id);
    }

    @Override
    protected MovieTrailers[] doInBackground(Void... params) {
        Timber.v(TAG + "inside doInBackground");

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
        Timber.v(TAG + "inside getTrailersFromJson " + trailersJsonStr);

        final String trailersJson = "results";
        Timber.v(TAG + " " + trailersJson);

        // Create JSONObject from results string
        JSONObject trailersJsonObj = new JSONObject(trailersJsonStr);

        // Create JSONArray from JSONObject
        JSONArray trailersArray = trailersJsonObj.getJSONArray(trailersJson);

        Timber.v(TAG + " " + "JSONObject trailersArray size = " + trailersArray.length());

        // Create Movie objects array
        MovieTrailers[] trailers = new MovieTrailers[trailersArray.length()];
        Timber.v(TAG + " " + "trailersArray.length()" + " " + trailersArray.length());

        for (int i = 0; i < trailersArray.length(); i++) {
            Timber.v(TAG + "i = " + i);
            // Get JSON object representing a single movie
            JSONObject trailersArrayJSONObject = trailersArray.getJSONObject(i);

            trailers[i] = new MovieTrailers();

            // get id
            Timber.v(TAG + " id :" + " " + trailersArrayJSONObject.getString("id"));
            trailers[i].setId(trailersArrayJSONObject.getString("id"));

            // Get key
            Timber.v(TAG + " key :" + " " + trailersArrayJSONObject.getString("key"));
            trailers[i].setKey(trailersArrayJSONObject.getString("key"));

            // Get name
            Timber.v(TAG + " name :" + " " + trailersArrayJSONObject.getString("name"));
            trailers[i].setName(trailersArrayJSONObject.getString("name"));

            // Get site
            Timber.v(TAG + " site :" + " " + trailersArrayJSONObject.getString("site"));
            trailers[i].setSite(trailersArrayJSONObject.getString("site"));
        }
        Timber.v(TAG + " " + "JSONObject trailers size = " + trailers.length);
        return trailers;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(MovieTrailers[] movieTrailers) {
        super.onPostExecute(movieTrailers);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
