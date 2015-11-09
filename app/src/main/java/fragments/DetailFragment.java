package fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
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

import com.baxamoosa.popularmovies.MoviesActivity;
import com.baxamoosa.popularmovies.R;
import com.squareup.picasso.Picasso;

import data.FavoriteContract;
import network.FetchMovieReviewsTask;
import network.FetchMovieTrailersTask;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = DetailFragment.class.getSimpleName();
    private boolean favoriteMovie;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (MoviesActivity.mTwoPane && arguments != null) { // tablet, with click
            isFavorite(arguments.getString("id"));
            // Timber.v(TAG + " favoriteMovie is " + favoriteMovie);
        } else if (!MoviesActivity.mTwoPane) { // && arguments == null, so phone
            isFavorite(getActivity().getIntent().getExtras().getString("id"));
            // Timber.v(TAG + " favoriteMovie is " + favoriteMovie);
        }
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

        if (!favoriteMovie) { // this movie is not a favorite
            // Timber.v(TAG + " favoriteMovie == false");
            favorite.setText("Mark as Favorite");
        } else { // this movie is already a favorite
            // Timber.v(TAG + " favoriteMovie == true");
            favorite.setText("Remove Favorite");
        }

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!favoriteMovie) { // this movie is not a favorite
                    Snackbar.make(view, "Movie saved as Favorite", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    if (MoviesActivity.mTwoPane) { // tablet
                        // Timber.v(TAG + " (MoviesActivity.mTwoPane == true) and calling Fragment with Arguments");
                        // use the arguments from the Fragment to insert into the Content Provider
                        ContentValues[] favoriteValuesArr = new ContentValues[1];

                        favoriteValuesArr[0] = new ContentValues();
                        favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_ID, arguments.getString("id"));
                        favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_TITLE, arguments.getString("title"));
                        favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_THUMBNAIL, arguments.getString("poster_thumbnail"));
                        favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_SYNOPSIS, arguments.getString("synopsis"));
                        favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_RATING, arguments.getString("rating"));
                        favoriteValuesArr[0].put(FavoriteContract.FavoritesList.COLUMN_DATE, arguments.getString("release_date"));

                        // Insert our ContentValues array
                        getActivity().getContentResolver().bulkInsert(FavoriteContract.FavoritesList.CONTENT_URI, favoriteValuesArr);
                    } else if (!MoviesActivity.mTwoPane) { // phone
                        // Timber.v(TAG + " (MoviesActivity.mTwoPane == false) and calling Activity with Intents");
                        // use the intent extras from the Intent to insert into the Content Provider

                        // use the arguments from the Fragment to insert into the Content Provider
                        ContentValues[] favoriteValuesArr = new ContentValues[1];

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
                } else { // this movie is already a favorite
                    Snackbar.make(view, "Removed from Favorite", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    ContentResolver resolver = getActivity().getContentResolver();
                    String[] selectionArgs;
                    if (MoviesActivity.mTwoPane) {
                        selectionArgs = new String[]{arguments.getString("id")};
                    } else {
                        selectionArgs = new String[]{getActivity().getIntent().getExtras().getString("id")};
                    }

                    resolver.delete(FavoriteContract.FavoritesList.CONTENT_URI,
                            FavoriteContract.FavoritesList.COLUMN_ID + "= ?",
                            selectionArgs);
                }
            }
        });


        if (MoviesActivity.mTwoPane && arguments != null) { // tablet, with click
            // Timber.v(TAG + " (MoviesActivity.mTwoPane == true && arguments != null)");
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
            isFavorite(arguments.getString("id"));
        } else if (!MoviesActivity.mTwoPane) { // && arguments == null, so phone
            // Timber.v(TAG + " else if (MoviesActivity.mTwoPane == false)");
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
            isFavorite(getActivity().getIntent().getExtras().getString("id"));
        }
        // Timber.v(TAG + " returning rootView");
        return rootView;
    }

    private void isFavorite(String id) {
        // Timber.v(TAG + " inside isFavorite()");
        favoriteMovie = false;

        ContentResolver resolver = getActivity().getContentResolver();
        String[] projection = new String[]{FavoriteContract.FavoritesList.COLUMN_ID};
        // Timber.v(TAG + " projection is " + projection);
        Cursor cursor = resolver.query(FavoriteContract.FavoritesList.CONTENT_URI,
                projection,
                null,
                null,
                null);
        // Timber.v(TAG + " cursor.getCount() is " + cursor.getCount());
        if (cursor.moveToFirst()){
            do {
                String movieID = cursor.getString(0);
                // Timber.v(TAG + " movieID is " + movieID);
                if (movieID.equals(id)){
                    // Timber.v(TAG + " (movieID == id)");
                    favoriteMovie = true;
                }
            } while (cursor.moveToNext());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Timber.v(TAG + " inside onCreateLoader(int id, Bundle args)");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Timber.v(TAG + " onLoadFinished(Loader<Cursor> loader, Cursor data)");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Timber.v(TAG + " onLoaderReset(Loader<Cursor> loader)");
    }
}

