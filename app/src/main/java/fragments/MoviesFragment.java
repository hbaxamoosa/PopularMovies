package fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baxamoosa.popularmovies.MoviesActivity;
import com.baxamoosa.popularmovies.PopularMovies;
import com.baxamoosa.popularmovies.R;

import java.util.ArrayList;

import adapter.MovieAdapter;
import model.Movie;
import network.FetchMoviesTask;

public class MoviesFragment extends android.support.v4.app.Fragment {

    public static Movie[] mMovies;
    public static RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private final String TAG = MoviesFragment.class.getSimpleName();
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Movie> listOfMovies;
    private String mSortBy;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
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
        mAdapter = new MovieAdapter(mMovies);
        mRecyclerView.setAdapter(mAdapter);
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
