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
import timber.log.Timber;

public class MovieTrailersAdapter extends ArrayAdapter<MovieTrailers> {

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

        if (getCount() == 0) {
            Timber.v(TAG + " getCount() == 0 is true");
            holder.textView.setText("No Trailers!");
        } else {
            Timber.v(TAG + " getCount() == 0 is false");
            holder.textView.setText(mMovieTrailers[position].getName());
        }
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
