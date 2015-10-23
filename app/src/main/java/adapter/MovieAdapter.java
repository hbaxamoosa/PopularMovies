package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baxamoosa.popularmovies.DetailActivity;
import com.baxamoosa.popularmovies.R;
import com.squareup.picasso.Picasso;

import model.Movie;
import network.FetchMovieReviewsTask;
import network.FetchMovieTrailersTask;
import timber.log.Timber;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final String TAG = MovieAdapter.class.getSimpleName();
    private Movie[] mMovies;

    public MovieAdapter(Movie[] movies) {
        mMovies = movies;
    }

    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Timber.v(TAG + " inside onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_movieadapter, viewGroup, false);
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
        //Log.v(TAG, "mMovies.length = " + mMovies.length);
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
            Timber.v(TAG + " inside onClick(View v)");
            // TODO: make a asynctask call to get trailers and reviews for the specific movie
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("id", mMovies[position].getId());
            intent.putExtra("title", mMovies[position].getTitle());
            intent.putExtra("poster_thumbnail", mMovies[position].getThumbnail());
            intent.putExtra("release_date", mMovies[position].getDate());
            intent.putExtra("rating", mMovies[position].getRating());
            intent.putExtra("synopsis", mMovies[position].getSynopsis());

            // Use the id to get the trailers
            FetchMovieTrailersTask trailersTask = new FetchMovieTrailersTask(mMovies[position].getId());
            trailersTask.execute();

            // Use the id to get the reviews
            FetchMovieReviewsTask reviewsTask = new FetchMovieReviewsTask(mMovies[position].getId());
            reviewsTask.execute();

            context.startActivity(intent);
        }
    }
}
