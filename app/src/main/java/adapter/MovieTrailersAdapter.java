package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baxamoosa.popularmovies.R;

import model.MovieTrailers;

public class MovieTrailersAdapter extends ArrayAdapter<MovieTrailers> {

    private static final String YOUTUBE_IMAGE_URL_PREFIX = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_IMAGE_URL_SUFFIX = "/0.jpg";
    private final String TAG = MovieTrailersAdapter.class.getSimpleName();
    private Context mContext;
    private int mLayoutResourceId;
    private MovieTrailers[] mMovieTrailers;

    public MovieTrailersAdapter(Context context, int layoutResourceId, MovieTrailers[] movieTrailers) {
        super(context, layoutResourceId, movieTrailers);
        // Timber.v(TAG + " MovieTrailersAdapter constructor");
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mMovieTrailers = movieTrailers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Timber.v(TAG + " getView(int position, View convertView, ViewGroup parent)" + " position: " + position);

        View row = convertView;
        MovieTrailersHolder holder = null;

        if (row == null) {
            // inflate the layout for each item of listView
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new MovieTrailersHolder(row);
            row.setTag(holder);
        } else {
            holder = (MovieTrailersHolder) row.getTag();
        }

        holder.textView.setText(mMovieTrailers[position].getName());
        // Timber.v(TAG + " mMovieTrailers[position].getKey(): " + mMovieTrailers[position].getKey());
        // String thumbnail = YOUTUBE_IMAGE_URL_PREFIX + mMovieTrailers[position].getKey() + YOUTUBE_IMAGE_URL_SUFFIX;
        // holder.imageView.setImageURI(Uri.parse(thumbnail));
        return row;
    }

    static class MovieTrailersHolder {
        TextView textView;
        ImageView imageView;

        public MovieTrailersHolder(View view) {
            textView = (TextView) view.findViewById(R.id.trailer_name);
            imageView = (ImageView) view.findViewById(R.id.trailer_image);
        }
    }
}
