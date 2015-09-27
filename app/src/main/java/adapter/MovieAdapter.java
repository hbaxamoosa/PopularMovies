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
import com.baxamoosa.popularmovies.R;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final String TAG = MovieAdapter.class.getSimpleName();

    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Timber.v(TAG + " inside onCreateViewHolder");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_movieadapter, viewGroup, false);
        return new MovieAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieAdapterViewHolder movieAdapterViewHolder, int i) {
        Timber.v(TAG + " inside onBindViewHolder");
        Picasso.with(movieAdapterViewHolder.itemView.getContext()).load("http://i.imgur.com/DvpvklR.png").into(movieAdapterViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView imageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);

            Timber.v(TAG + " inside MovieAdapterViewHolder(View itemView)");
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            //Picasso.with(itemView.getContext()).load("http://i.imgur.com/DvpvklR.png").into(imageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Context context = itemView.getContext();
            Timber.v(TAG + " inside onClick(View v)");
            Toast.makeText(v.getContext(), "position = " + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, DetailActivity.class);
            context.startActivity(intent);
        }
    }
}
