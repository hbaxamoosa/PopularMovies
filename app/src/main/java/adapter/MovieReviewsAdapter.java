package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baxamoosa.popularmovies.R;

import model.MovieReviews;

public class MovieReviewsAdapter extends ArrayAdapter<MovieReviews> {

    private final String TAG = MovieReviewsAdapter.class.getSimpleName();
    private Context mContext;
    private int mLayoutResourceId;
    private MovieReviews[] mMovieReviews;

    public MovieReviewsAdapter(Context context, int layoutResourceId, MovieReviews[] movieReviews) {
        super(context, layoutResourceId, movieReviews);
        // Timber.v(TAG + " MovieReviewsAdapter constructor");
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mMovieReviews = movieReviews;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Timber.v(TAG + " getView(int position, View convertView, ViewGroup parent)" + " position: " + position);

        View row = convertView;
        MovieReviewsHolder holder = null;

        if (row == null) {
            // inflate the layout for each item of listView
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new MovieReviewsHolder(row);
            row.setTag(holder);
        } else {
            holder = (MovieReviewsHolder) row.getTag();
        }

        if (getCount() == 0) {
            // Timber.v(TAG + " getCount() == 0 is true");
            holder.textViewAuthor.setText("No reviews yet!");
            holder.textViewContent.setText("No reviews yet!");
        } else {
            // Timber.v(TAG + " getCount() == 0 is false");
            holder.textViewAuthor.setText(mMovieReviews[position].getAuthor());
            holder.textViewContent.setText(mMovieReviews[position].getContent());
        }
        return row;
    }

    static class MovieReviewsHolder {
        TextView textViewAuthor;
        TextView textViewContent;

        public MovieReviewsHolder(View view) {
            textViewAuthor = (TextView) view.findViewById(R.id.review_author);
            textViewContent = (TextView) view.findViewById(R.id.review_content);
        }
    }
}
