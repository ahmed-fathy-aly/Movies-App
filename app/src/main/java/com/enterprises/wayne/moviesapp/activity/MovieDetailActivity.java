package com.enterprises.wayne.moviesapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.enterprises.wayne.moviesapp.fragment.MovieDetailFragment;
import com.enterprises.wayne.moviesapp.R;
import com.enterprises.wayne.moviesapp.model.Movie;
import com.enterprises.wayne.moviesapp.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * An activity representing a single Movie detail screen
 */
public class MovieDetailActivity extends AppCompatActivity
{

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // reference views
        ButterKnife.bind(this);

        // get intent extras
        Movie movie = (Movie) getIntent().getSerializableExtra(Constants.KEY_MOVIE);

        // setup tool bar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the detail fragment and add it to the activity
        if (savedInstanceState == null)
        {
            Bundle arguments = new Bundle();
            arguments.putSerializable(Constants.KEY_MOVIE, movie);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            navigateUpTo(new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
