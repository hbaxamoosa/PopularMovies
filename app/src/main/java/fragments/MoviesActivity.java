package fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baxamoosa.popularmovies.PopularMovies;
import com.baxamoosa.popularmovies.R;

import java.util.ArrayList;

import adapter.MovieAdapter;
import model.Movie;
import network.FetchMoviesTask;
import timber.log.Timber;

public class MoviesActivity extends android.support.v4.app.Fragment {

    public static Movie[] mMovies;
    public static RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;
    private static OnItemClickedListener sDummyCallbacks = new OnItemClickedListener() {
        @Override
        public void OnItemClicked(String id) {
        }
    };
    private final String TAG = MoviesActivity.class.getSimpleName();
    OnItemClickedListener mListener = sDummyCallbacks;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Movie> listOfMovies;
    private String mSortBy;

    public MoviesActivity() {
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

        Timber.v(TAG + "popularmovies.MoviesActivity.mTwoPane is " + com.baxamoosa.popularmovies.MoviesActivity.mTwoPane);
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new GridLayoutManager(PopularMovies.getAppContext(), 2);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnItemClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnItemClickedListener");
        }
    }

    // Put this interface on the Fragment or the View
    public interface OnItemClickedListener {
        void OnItemClicked(String id);
    }
}
