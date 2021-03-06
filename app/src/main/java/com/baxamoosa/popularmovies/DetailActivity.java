package com.baxamoosa.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import fragments.DetailFragment;

public class DetailActivity extends AppCompatActivity {

    private final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) this.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            // Timber.v(TAG + " appBarLayout != null");
            appBarLayout.setTitle(this.getIntent().getExtras().getString("title"));
            ImageView backdrop = (ImageView) this.findViewById(R.id.backdrop);
            String backdrop_path = "http://image.tmdb.org/t/p/w500/" + this.getIntent().getExtras().getString("poster_thumbnail");
            Picasso.with(this).load(backdrop_path).into(backdrop);
        }
        // Timber.v(TAG + " Activity Created");

        if (MoviesActivity.mTwoPane && getIntent().getExtras() == null){
            // called from MovieAdapter with arguments
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, new DetailFragment())
                    .commit();
        } else if (MoviesActivity.mTwoPane && getIntent().getExtras() != null) {
            // called from MovieAdapter with intents
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail_container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MoviesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
