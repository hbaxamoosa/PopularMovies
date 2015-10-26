package com.baxamoosa.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import network.FetchMovieReviewsTask;
import network.FetchMovieTrailersTask;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity {

    // TODO: convert this activity into a Fragment

    private final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Timber.v(TAG + " " + "Activity Created");

        Timber.v(TAG + " id : " + getIntent().getExtras().getString("id"));

        ImageView poster_thumbnail = (ImageView) findViewById(R.id.poster_thumbnail);
        TextView title = (TextView) findViewById(R.id.title);
        TextView release_date = (TextView) findViewById(R.id.release_date);
        TextView rating = (TextView) findViewById(R.id.rating);
        TextView synopsis = (TextView) findViewById(R.id.synopsis);
        Button favorite = (Button) findViewById(R.id.btn_favorite);
        // Add a button here for movie trailers
        // use the key from the JSON response like this: https://www.youtube.com/watch?v=8hP9D6kZseM
        // where v is the key value

        // Use the id to get the trailers
        FetchMovieTrailersTask trailersTask = new FetchMovieTrailersTask(this, getIntent().getExtras().getString("id"), this.findViewById(android.R.id.content).getRootView());
        trailersTask.execute();

        // Use the id to get the reviews
        FetchMovieReviewsTask reviewsTask = new FetchMovieReviewsTask(this, getIntent().getExtras().getString("id"), this.findViewById(android.R.id.content).getRootView());
        reviewsTask.execute();

        String poster_path = "http://image.tmdb.org/t/p/w500/" + getIntent().getExtras().getString("poster_thumbnail");
        Picasso.with(this).load(poster_path).into(poster_thumbnail);
        title.setText(getIntent().getExtras().getString("title"));
        release_date.setText(getIntent().getExtras().getString("release_date"));
        rating.setText((getIntent().getExtras().getString("rating")));
        synopsis.setText((getIntent().getExtras().getString("synopsis")));
    }

    public void setFavorite (View v){
        Toast.makeText(this, "clicked favorite button", Toast.LENGTH_LONG).show();
        // TODO: implement DB actions to store favorites. If the movie is not in favorties, add.
        // If the movie is already in favorites, allow the user the option to remove from favorites

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.menu_detail, menu);*/
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
/*        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
}
