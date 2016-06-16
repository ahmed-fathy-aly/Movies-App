package com.enterprises.wayne.moviesapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.enterprises.wayne.moviesapp.fragment.MovieDetailFragment;
import com.enterprises.wayne.moviesapp.R;
import com.enterprises.wayne.moviesapp.adapter.MoviesAdapter;
import com.enterprises.wayne.moviesapp.model.Movie;
import com.enterprises.wayne.moviesapp.util.Constants;
import com.enterprises.wayne.moviesapp.util.URLUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An activity representing a list of Movies.
 */
public class MovieListActivity extends AppCompatActivity implements MoviesAdapter.Listener, SwipeRefreshLayout.OnRefreshListener
{

    /* UI */
    @Bind(R.id.parent)
    LinearLayout parent;
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

        // add listener to spinner
        spinnerDisplayChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                adapterMovies.clear();
                getMovies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        // download movies
        getMovies();
    }


    @Override
    public void onRefresh()
    {
        getMovies();
    }


    /**
     * makes a GET request to fetch the list of movies and put them in the recycler-view
     */
    private void getMovies()
    {
        // check the display choice
        String choiceStr = (String) spinnerDisplayChoices.getSelectedItem();
        if (choiceStr.equals(getString(R.string.favorites)))
            return;

        // start refreshing of swipe refresh]
        swipeRefreshLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        // make the request
        String criterea = choiceStr.equals(getString(R.string.popular))? Constants.POPULAR : Constants.TOP_RATED;
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

                        // add to recycler view
                        adapterMovies.setData(movieList);

                    }
                });
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

}
