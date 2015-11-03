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

import adapter.MovieAdapter;
import model.Movie;
import network.FetchMoviesTask;
import timber.log.Timber;

public class MoviesActivity extends android.support.v4.app.Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Object> {

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
        Timber.v(TAG + " inside onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");

        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new GridLayoutManager(PopularMovies.getAppContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /*Button button = (Button) rootView.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.OnItemClicked("1");
            }
        });*/
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new MovieAdapter(mMovies);
        mRecyclerView.setAdapter(mAdapter);

        // getLoaderManager().initLoader(0, null, this);
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

    @Override
    public android.support.v4.content.Loader<Object> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Object> loader, Object data) {

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Object> loader) {

    }

    // Put this interface on the Fragment or the View
    public interface OnItemClickedListener {
        void OnItemClicked(String id);
    }
}
