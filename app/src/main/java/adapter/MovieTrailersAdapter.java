package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baxamoosa.popularmovies.R;

import model.MovieTrailers;
import timber.log.Timber;

public class MovieTrailersAdapter<T> extends BaseAdapter {

    private static final String YOUTUBE_IMAGE_URL_PREFIX = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_IMAGE_URL_SUFFIX = "/0.jpg";
    private final String TAG = MovieTrailersAdapter.class.getSimpleName();
    private Context mContext;
    private MovieTrailers[] mMovieTrailers;

    public MovieTrailersAdapter(Context context, MovieTrailers[] movieTrailers) {
        Timber.v(TAG + " MovieTrailersAdapter constructor");
        mContext = context;
        mMovieTrailers = movieTrailers;
    }

    @Override
    public int getCount() {
        Timber.v(TAG + " inside getCount(): " + mMovieTrailers.length);
        return mMovieTrailers.length;
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

        Timber.v(TAG + " getView(int position, View convertView, ViewGroup parent)");

        // inflate the layout for each item of listView
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listview_trailers, null);

        TextView trailer = (TextView) convertView.findViewById(R.id.trailer_name);
        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.trailer_image);

        //Timber.v(TAG + " mMovieTrailers[position].getName(): " + mMovieTrailers[position].getName());
        //trailer.setText(mMovieTrailers[position].getName());
        //thumbnail.setImageBitmap("http://img.youtube.com/vi/Ut3Hvbvs1bs/0.jpg");

        //thumbnail.setImageURI(Uri.parse("http://img.youtube.com/vi/Ut3Hvbvs1bs/0.jpg"));
        trailer.setText("trailer name");
        return convertView;
    }
}
