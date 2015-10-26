package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baxamoosa.popularmovies.R;

import model.MovieReviews;
import timber.log.Timber;

public class MovieReviewsAdapter<T> extends BaseAdapter {

    private final String TAG = MovieReviewsAdapter.class.getSimpleName();
    private Context mContext;
    private MovieReviews[] mMovieReviews;

    public MovieReviewsAdapter(Context context, MovieReviews[] movieReviews) {
        Timber.v(TAG + " MovieReviewsAdapter constructor");
        mContext = context;
        mMovieReviews = movieReviews;
    }

    @Override
    public int getCount() {
        Timber.v(TAG + " getCount(): " + mMovieReviews.length);
        return mMovieReviews.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listview_reviews, null);

        TextView author = (TextView) convertView.findViewById(R.id.review_author);
        TextView content = (TextView) convertView.findViewById(R.id.review_content);

        if (getCount() == 0) {
            Timber.v(TAG + " getCount() == 0 is true");
            author.setText("No reviews yet!");
            content.setText("No reviews yet!");
        } else {
            Timber.v(TAG + " getCount() == 0 is false");
            author.setText(mMovieReviews[position].getAuthor());
            content.setText(mMovieReviews[position].getContent());
        }
        return convertView;
    }
}
