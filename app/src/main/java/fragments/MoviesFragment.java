package fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baxamoosa.popularmovies.MoviesActivity;
import com.baxamoosa.popularmovies.PopularMovies;
import com.baxamoosa.popularmovies.R;

import adapter.MovieAdapter;
import data.FavoriteContract;
import model.Movie;
import network.FetchMoviesTask;

public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int COL_ID = 0;
    public static final int COL_THUMBNAIL = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_SYNOPSIS = 3;
    public static final int COL_RATING = 4;
    public static final int COL_DATE = 5;
    private static final int CURSOR_LOADER_ID = 0;
    public static Movie[] mMovies;
    public static RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private final String TAG = MoviesFragment.class.getSimpleName();
    private RecyclerView.LayoutManager mLayoutManager;
    private Resources res;
    private SharedPreferences sharedPref;
    private String sort_type;
    private Cursor mFavoriteCursor;

    public MoviesFragment() {
        res = PopularMovies.getAppContext().getResources();

        // Getting sort order preference
        sharedPref = PreferenceManager.getDefaultSharedPreferences(PopularMovies.getAppContext());
        sort_type = sharedPref.getString(res.getString(R.string.prefs_sorting_key), res.getString(R.string.prefs_sorting_default));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!sort_type.equals(res.getString(R.string.favorites))) { // not favorites
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Timber.v(TAG + " inside onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");

        final View rootView = inflater.inflate(R.layout.activity_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        if (!MoviesActivity.mTwoPane && getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // check to see if the phone is in portrait for phone
            mLayoutManager = new GridLayoutManager(PopularMovies.getAppContext(), 2);
        } else if (!MoviesActivity.mTwoPane && getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // check to see if the phone is in landscape for phone
            mLayoutManager = new GridLayoutManager(PopularMovies.getAppContext(), 4);
        } else if (MoviesActivity.mTwoPane && getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // check to see if the phone is in landscape for tablet
            mLayoutManager = new GridLayoutManager(PopularMovies.getAppContext(), 2);
        } else {
            // (MoviesActivity.mTwoPane && getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            // check to see if the phone is in portrait for tablet
            mLayoutManager = new GridLayoutManager(PopularMovies.getAppContext(), 2);
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (sort_type.equals(res.getString(R.string.favorites))) { // for favorites
            getLoaderManager().initLoader(CURSOR_LOADER_ID, null, MoviesFragment.this);
        }
        mAdapter = new MovieAdapter(mMovies);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Timber.v(TAG + " inside onCreateLoader(int id, Bundle args)");
        return new CursorLoader(getActivity(),
                FavoriteContract.FavoritesList.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Timber.v(TAG + " onLoadFinished(Loader<Cursor> loader, Cursor data)");
        mFavoriteCursor = data;
        mFavoriteCursor.moveToFirst();
        DatabaseUtils.dumpCursor(data);

        // Create Movie objects array
        Movie[] movies = new Movie[data.getCount()];
        // Timber.v(TAG + " moviesArray.length()" + " " + moviesArray.length());

        for (int i = 0; i < mFavoriteCursor.getCount(); i++) {
            // Timber.v(TAG + " i = " + i);

            movies[i] = new Movie();

            // get id
            // Timber.v(TAG + " id :" + " " + mFavoriteCursor.getString(COL_ID));
            movies[i].setId(mFavoriteCursor.getString(COL_ID));

            // Get title
            // Timber.v(TAG + " original_title :" + " " + mFavoriteCursor.getString(COL_TITLE));
            movies[i].setTitle(mFavoriteCursor.getString(COL_TITLE));

            // Get poster path
            // Timber.v(TAG + " poster_path :" + " " + mFavoriteCursor.getString(COL_THUMBNAIL));
            movies[i].setThumbnail(mFavoriteCursor.getString(COL_THUMBNAIL));

            // Get synopsis
            // Timber.v(TAG + " overview :" + " " + mFavoriteCursor.getString(COL_SYNOPSIS));
            movies[i].setSynopsis(mFavoriteCursor.getString(COL_SYNOPSIS));

            // Get rating
            // Timber.v(TAG + " vote_average :" + " " + mFavoriteCursor.getString(COL_RATING));
            movies[i].setRating(mFavoriteCursor.getString(COL_RATING));

            // Get release date
            // Timber.v(TAG + " release_date :" + " " + mFavoriteCursor.getString(COL_DATE));
            movies[i].setDate(mFavoriteCursor.getString(COL_DATE));

            mFavoriteCursor.moveToNext();
        }

        mAdapter = new MovieAdapter(movies);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Timber.v(TAG + " onLoaderReset(Loader<Cursor> loader)");
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item selections.
     */
    public interface Callback {
        /**
         * MoviesActivity Callback for when an item has been selected.
         */
        void onItemSelected(int position, Movie[] movie);
    }
}
