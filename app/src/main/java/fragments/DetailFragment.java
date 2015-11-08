package fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baxamoosa.popularmovies.MoviesActivity;
import com.baxamoosa.popularmovies.R;
import com.squareup.picasso.Picasso;

import data.FavoriteContract;
import network.FetchMovieReviewsTask;
import network.FetchMovieTrailersTask;
import timber.log.Timber;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] DETAIL_COLUMNS = {
            FavoriteContract.FavoritesList.TABLE_NAME + "." + FavoriteContract.FavoritesList.COLUMN_ID,
            FavoriteContract.FavoritesList.COLUMN_TITLE,
            FavoriteContract.FavoritesList.COLUMN_THUMBNAIL,
            FavoriteContract.FavoritesList.COLUMN_SYNOPSIS,
            FavoriteContract.FavoritesList.COLUMN_RATING,
            FavoriteContract.FavoritesList.COLUMN_DATE

    };
    private final String TAG = DetailFragment.class.getSimpleName();
    private Uri mUri;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Timber.v(TAG + " inside onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        final Bundle arguments = getArguments(); // getArguments() for fragment, i.e. tablet mode

        ImageView poster_thumbnail = (ImageView) rootView.findViewById(R.id.poster_thumbnail);

        TextView title = null;
        if (arguments != null) { // tablet
            title = (TextView) rootView.findViewById(R.id.title);
        }
        TextView release_date = (TextView) rootView.findViewById(R.id.release_date);
        TextView rating = (TextView) rootView.findViewById(R.id.rating);
        TextView synopsis = (TextView) rootView.findViewById(R.id.synopsis);
        Button favorite = (Button) rootView.findViewById(R.id.btn_favorite);
        // favorite button is for adding/removing favorites
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // setFavorite(view);
                if (MoviesActivity.mTwoPane) { // tablet
                    Timber.v(TAG + " (MoviesActivity.mTwoPane == true) and calling Fragment with Arguments");
                    // use the arguments from the Fragment to insert into the Content Provider
                    ContentValues[] favoriteValuesArr = new ContentValues[1];
                    // Loop through static array of Flavors, add each to an instance of ContentValues
                    // in the array of ContentValues

                    favoriteValuesArr[1] = new ContentValues();
                    favoriteValuesArr[1].put(FavoriteContract.FavoritesList.COLUMN_ID, arguments.getString("id"));
                    favoriteValuesArr[1].put(FavoriteContract.FavoritesList.COLUMN_TITLE, arguments.getString("title"));
                    favoriteValuesArr[1].put(FavoriteContract.FavoritesList.COLUMN_THUMBNAIL, arguments.getString("poster_thumbnail"));
                    favoriteValuesArr[1].put(FavoriteContract.FavoritesList.COLUMN_SYNOPSIS, arguments.getString("synopsis"));
                    favoriteValuesArr[1].put(FavoriteContract.FavoritesList.COLUMN_RATING, arguments.getString("rating"));
                    favoriteValuesArr[1].put(FavoriteContract.FavoritesList.COLUMN_DATE, arguments.getString("release_date"));

                    // Insert our ContentValues array
                    getActivity().getContentResolver().bulkInsert(FavoriteContract.FavoritesList.CONTENT_URI, favoriteValuesArr);
                } else if (!MoviesActivity.mTwoPane) { // phone
                    Timber.v(TAG + " (MoviesActivity.mTwoPane == false) and calling Activity with Intents");
                    // use the intent extras from the Intent to insert into the Content Provider

                    // use the arguments from the Fragment to insert into the Content Provider
                    ContentValues[] favoriteValuesArr = new ContentValues[1];
                    // Loop through static array of Flavors, add each to an instance of ContentValues
                    // in the array of ContentValues

                    favoriteValuesArr[0] = new ContentValues();
                    favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_ID, getActivity().getIntent().getExtras().getString("id"));
                    favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_TITLE, getActivity().getIntent().getExtras().getString("title"));
                    favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_THUMBNAIL, getActivity().getIntent().getExtras().getString("poster_thumbnail"));
                    favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_SYNOPSIS, getActivity().getIntent().getExtras().getString("synopsis"));
                    favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_RATING, getActivity().getIntent().getExtras().getString("rating"));
                    favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_DATE, getActivity().getIntent().getExtras().getString("release_date"));

                    // Insert our ContentValues array
                    getActivity().getContentResolver().bulkInsert(FavoriteContract.FavoritesList.CONTENT_URI, favoriteValuesArr);
                }
            }
        });


        if (MoviesActivity.mTwoPane && arguments != null) { // tablet, with click
            Timber.v(TAG + " (MoviesActivity.mTwoPane == true && arguments != null)");
            // Use the id to get the trailers
            FetchMovieTrailersTask trailersTask = new FetchMovieTrailersTask(getContext(), arguments.getString("id"), rootView);
            trailersTask.execute();

            // Use the id to get the reviews
            FetchMovieReviewsTask reviewsTask = new FetchMovieReviewsTask(getContext(), arguments.getString("id"), rootView);
            reviewsTask.execute();

            String poster_path = "http://image.tmdb.org/t/p/w500/" + arguments.getString("poster_thumbnail");
            Picasso.with(getActivity()).load(poster_path).into(poster_thumbnail);
            title.setText(arguments.getString("title"));
            release_date.setText(arguments.getString("release_date"));
            rating.setText(arguments.getString("rating"));
            synopsis.setText(arguments.getString("synopsis"));
        } else if (!MoviesActivity.mTwoPane) { // && arguments == null, so phone
            Timber.v(TAG + " else if (MoviesActivity.mTwoPane == false)");
            // Use the id to get the trailers
            FetchMovieTrailersTask trailersTask = new FetchMovieTrailersTask(getContext(), getActivity().getIntent().getExtras().getString("id"), rootView);
            trailersTask.execute();

            // Use the id to get the reviews
            FetchMovieReviewsTask reviewsTask = new FetchMovieReviewsTask(getContext(), getActivity().getIntent().getExtras().getString("id"), rootView);
            reviewsTask.execute();

            String poster_path = "http://image.tmdb.org/t/p/w500/" + getActivity().getIntent().getExtras().getString("poster_thumbnail");
            Picasso.with(getActivity()).load(poster_path).into(poster_thumbnail);
            release_date.setText(getActivity().getIntent().getExtras().getString("release_date"));
            rating.setText(getActivity().getIntent().getExtras().getString("rating"));
            synopsis.setText(getActivity().getIntent().getExtras().getString("synopsis"));
        }
        // Timber.v(TAG + " returning rootView");
        return rootView;
    }

    public void setFavorite(View v) {
        Toast.makeText(getActivity(), "clicked favorite button", Toast.LENGTH_LONG).show();
        // TODO: implement DB actions to store favorites. If the movie is not in favorties, add.
        // If the movie is already in favorites, allow the user the option to remove from favorites
        // movie is not a favorite, so add
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Timber.v(TAG + " inside onCreateLoader(int id, Bundle args)");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.v(TAG + " onLoadFinished(Loader<Cursor> loader, Cursor data)");
        if (data != null && data.moveToFirst()) {
            Timber.v(TAG + " (data != null && data.moveToFirst()");

            /*String poster_path = "http://image.tmdb.org/t/p/w500/" + arguments.getString("poster_thumbnail");
            Picasso.with(getActivity()).load(poster_path).into(poster_thumbnail);
            title.setText(arguments.getString("title"));
            release_date.setText(arguments.getString("release_date"));
            rating.setText(arguments.getString("rating"));
            synopsis.setText(arguments.getString("synopsis"));*/
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Timber.v(TAG + " onLoaderReset(Loader<Cursor> loader)");
    }
}

