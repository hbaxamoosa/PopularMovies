package fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import network.FetchMovieReviewsTask;
import network.FetchMovieTrailersTask;
import timber.log.Timber;

public class DetailFragment extends Fragment {

    private final String TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Timber.v(TAG + " inside onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle arguments = getArguments(); // getArguments() for fragment, i.e. tablet mode

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
                setFavorite(view);
            }
        });


        if (MoviesActivity.mTwoPane == true && arguments != null) { // tablet, with click
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
        } else if (MoviesActivity.mTwoPane == false) { // && arguments == null, so phone
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

    }
}
