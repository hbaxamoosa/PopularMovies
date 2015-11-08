package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baxamoosa.popularmovies.MoviesActivity;
import com.baxamoosa.popularmovies.R;
import com.squareup.picasso.Picasso;

import fragments.MoviesFragment;
import model.Movie;
import timber.log.Timber;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final String TAG = MovieAdapter.class.getSimpleName();
    private Movie[] mMovies;

    public MovieAdapter() {
    }

    public MovieAdapter(Movie[] movies) {
        mMovies = movies;
    }

    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Timber.v(TAG + " inside onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_movieadapter, viewGroup, false);
        if (MoviesActivity.mTwoPane == true && MoviesActivity.firstLoad == true) { // set the selected item to position 0 for tablet
            // Timber.v(TAG + " calling ((MoviesFragment.Callback) context).onItemSelected(0, mMovies)");
            ((MoviesFragment.Callback) viewGroup.getContext()).onItemSelected(0, mMovies);
            MoviesActivity.firstLoad = false;
        }
        return new MovieAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieAdapterViewHolder movieAdapterViewHolder, int i) {
        Timber.v(TAG + " inside onBindViewHolder");
        String thumbnail = mMovies[i].getThumbnail();
        Timber.v(TAG + " thumbnail = " + thumbnail);

        String poster_path = "http://image.tmdb.org/t/p/w500/" + thumbnail;
        Timber.v(TAG + " poster_path = " + poster_path);
        Picasso.with(movieAdapterViewHolder.itemView.getContext()).load(poster_path).into(movieAdapterViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        Timber.v(TAG + " getItemCount() is " + getItemCount());
        return (null != mMovies ? mMovies.length : 0);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView imageView;


        public MovieAdapterViewHolder(View itemView) {
            super(itemView);

            Timber.v(TAG + " inside MovieAdapterViewHolder(View itemView)");
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Context context = itemView.getContext();
            ((MoviesFragment.Callback) context).onItemSelected(position, mMovies);
        }
    }
}
