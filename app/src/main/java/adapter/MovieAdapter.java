package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.baxamoosa.popularmovies.DetailActivity;
import com.baxamoosa.popularmovies.MainActivity;
import com.baxamoosa.popularmovies.R;
import com.squareup.picasso.Picasso;

import model.Movie;
import timber.log.Timber;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final String TAG = MovieAdapter.class.getSimpleName();
    private Movie[] mMovies;

    public MovieAdapter(){}

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
        // Timber.v(TAG + " thumbnail = " + thumbnail);

        String poster_path = "http://image.tmdb.org/t/p/w500/" + thumbnail;
        // Timber.v(TAG + " poster_path = " + poster_path);
        Picasso.with(movieAdapterViewHolder.itemView.getContext()).load(poster_path).into(movieAdapterViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return (null != mMovies ? mMovies.length : 0);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView imageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);

            // Timber.v(TAG + " inside MovieAdapterViewHolder(View itemView)");
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Context context = itemView.getContext();
            Toast.makeText(context, "position: " + position, Toast.LENGTH_SHORT).show();

            if (MainActivity.mTwoPane == true) {
                // launch as fragment
            } else {
                // launch as activity
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("id", mMovies[position].getId());
                intent.putExtra("title", mMovies[position].getTitle());
                intent.putExtra("poster_thumbnail", mMovies[position].getThumbnail());
                intent.putExtra("release_date", mMovies[position].getDate());
                intent.putExtra("rating", mMovies[position].getRating());
                intent.putExtra("synopsis", mMovies[position].getSynopsis());

                context.startActivity(intent);
            }
        }
    }
}
