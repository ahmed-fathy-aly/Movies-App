package com.enterprises.wayne.moviesapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.enterprises.wayne.moviesapp.R;
import com.enterprises.wayne.moviesapp.adapter.MoviesAdapter;
import com.enterprises.wayne.moviesapp.database.DatabaseHelper;
import com.enterprises.wayne.moviesapp.events.MovieFavoritedEvent;
import com.enterprises.wayne.moviesapp.fragment.MovieDetailFragment;
import com.enterprises.wayne.moviesapp.model.Movie;
import com.enterprises.wayne.moviesapp.util.Constants;
import com.enterprises.wayne.moviesapp.util.URLUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Movies.
 */
public class MovieListActivity extends AppCompatActivity implements MoviesAdapter.Listener, SwipeRefreshLayout.OnRefreshListener
{

    /* UI */
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerViewMovies)
    RecyclerView recyclerViewMovies;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.spinnerDisplayChoices)
    Spinner spinnerDisplayChoices;

    /* fields */
    MoviesAdapter adapterMovies;
    @Bind(R.id.parent)
    LinearLayout parent;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // reference views
        ButterKnife.bind(this);

        // setup toolbar
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // check if tablet or phone
        if (findViewById(R.id.movie_detail_container) != null)
            mTwoPane = true;

        // setup recycler view
        adapterMovies = new MoviesAdapter(this);
        adapterMovies.setListener(this);
        int coloumnsCount = mTwoPane ? 3 : 2;
        recyclerViewMovies.setLayoutManager(new GridLayoutManager(this, coloumnsCount));
        recyclerViewMovies.setAdapter(adapterMovies);

        // setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this);

        // change the movies displayed once the user selects something from the spinner
        spinnerDisplayChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                onRefresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // download movies
        onRefresh();

        // register for events like a movie getting favorited
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh()
    {
        // either show the favorites in the db or download the movies
        String choiceStr = (String) spinnerDisplayChoices.getSelectedItem();
        if (choiceStr.equals(getString(R.string.favorites)))
            getFavorites();
        else
            getMovies();
    }

    /**
     * makes a GET request to fetch the list of movies and put them in the recycler-view
     */
    private void getMovies()
    {
        // start refreshing of swipe refresh
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.post(new Runnable()
            {
                @Override
                public void run()
                {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });

        // check we'll get the top rated or popular movies
        String choiceStr = (String) spinnerDisplayChoices.getSelectedItem();
        String criterea = choiceStr.equals(getString(R.string.popular)) ?
                Constants.POPULAR : Constants.TOP_RATED;

        // make the request
        String url = URLUtils.getMoviesUrl(criterea);
        Ion.with(this)
                .load("GET", url)
                .asString()
                .setCallback(new FutureCallback<String>()
                {
                    @Override
                    public void onCompleted(Exception e, String result)
                    {

                        // end refreshing of swipe refresh
                        swipeRefreshLayout.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                        // check error
                        if (e != null)
                        {
                            Snackbar.make(parent, getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        // parse movies
                        List<Movie> movieList = new ArrayList<>();
                        try
                        {
                            JSONObject resultJson = new JSONObject(result);
                            JSONArray moviesListJson = resultJson.getJSONArray("results");
                            for (int i = 0; i < moviesListJson.length(); i++)
                                movieList.add(Movie.fromJson(moviesListJson.getJSONObject(i)));
                        } catch (JSONException e1)
                        {
                            Snackbar.make(parent, getString(R.string.error), Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        // check which of those movies were favorited
                        DatabaseHelper databaseHelper = new DatabaseHelper(MovieListActivity.this);
                        HashSet<Integer> favoritedIds = databaseHelper.getFavoritedMoviesId();
                        for (Movie movie : movieList)
                            if (favoritedIds.contains(movie.getId()))
                                movie.setFavorite(true);

                        // add to recycler view
                        adapterMovies.setData(movieList);

                    }
                });
    }


    /**
     * retrieves all favorite movies stored in the database and add them to recycler-view
     */
    private void getFavorites()
    {
        // end refreshing (in case we got here from swipe refresh)
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        // get movies from db
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<Movie> movieList = databaseHelper.getMovies();

        // add to recycler view
        adapterMovies.setData(movieList);
    }

    @Override
    public void onClick(Movie movie)
    {
        if (mTwoPane)
        {
            Bundle arguments = new Bundle();
            arguments.putSerializable(Constants.KEY_MOVIE, movie);
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else
        {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(Constants.KEY_MOVIE, movie);
            startActivity(intent);
        }
    }

    @Subscribe
    public void onEvent(MovieFavoritedEvent movieFavoritedEvent)
    {
        // update the movie's isFavorite field so if the user clicks on that movie again, it will
        // show in the details screen as already favorited
        adapterMovies.setMovieFavorited(movieFavoritedEvent.getMovie().getId());
    }
}
