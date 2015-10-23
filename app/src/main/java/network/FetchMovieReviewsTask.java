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

import model.MovieReviews;
import timber.log.Timber;

public class FetchMovieReviewsTask extends AsyncTask<Void, Void, MovieReviews[]> {

    private final String TAG = FetchMovieReviewsTask.class.getSimpleName();
    private String id;

    public FetchMovieReviewsTask(String movieID) {
        Timber.v(TAG + " inside FetchMovieReviewsTask constructor");
        id = movieID;
        Timber.v(TAG + " id : " + id);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(MovieReviews[] movieReviewses) {
        super.onPostExecute(movieReviewses);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected MovieReviews[] doInBackground(Void... params) {
        Timber.v(TAG + "inside doInBackground");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewsJsonStr = null;

        try {
            // Construct the URL for the themoviedb.org API based on http://api.themoviedb.org/3/movie/id/videos
            final String BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/reviews";
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
            reviewsJsonStr = buffer.toString();
            Timber.v(TAG + reviewsJsonStr);
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
            return getReviewsFromJson(reviewsJsonStr);
        } catch (JSONException e) {
            Timber.e(TAG + e.getMessage() + e);
            e.printStackTrace();
        }
        return null;
    }

    private MovieReviews[] getReviewsFromJson(String reviewsJsonStr) throws JSONException {
        Timber.v(TAG + "inside getReviewsFromJson " + reviewsJsonStr);

        final String reviewsJson = "results";
        Timber.v(TAG + " " + reviewsJson);

        // Create JSONObject from results string
        JSONObject reviewsJsonObj = new JSONObject(reviewsJsonStr);

        // Create JSONArray from JSONObject
        JSONArray reviewsArray = reviewsJsonObj.getJSONArray(reviewsJson);

        Timber.v(TAG + " " + "JSONObject reviewsArray size = " + reviewsArray.length());

        // Create Movie objects array
        MovieReviews[] reviews = new MovieReviews[reviewsArray.length()];
        Timber.v(TAG + " " + "reviewsArray.length()" + " " + reviewsArray.length());

        for (int i = 0; i < reviewsArray.length(); i++) {
            Timber.v(TAG + "i = " + i);
            // Get JSON object representing a single movie
            JSONObject reviewsArrayJSONObject = reviewsArray.getJSONObject(i);

            reviews[i] = new MovieReviews();

            // get id
            Timber.v(TAG + " id :" + " " + reviewsArrayJSONObject.getString("id"));
            reviews[i].setId(reviewsArrayJSONObject.getString("id"));

            // get author
            Timber.v(TAG + " author :" + " " + reviewsArrayJSONObject.getString("author"));
            reviews[i].setAuthor(reviewsArrayJSONObject.getString("author"));

            // get id
            Timber.v(TAG + " content :" + " " + reviewsArrayJSONObject.getString("content"));
            reviews[i].setContent(reviewsArrayJSONObject.getString("content"));

        }
        Timber.v(TAG + " " + "JSONObject reviews size = " + reviews.length);
        return reviews;
    }
}
