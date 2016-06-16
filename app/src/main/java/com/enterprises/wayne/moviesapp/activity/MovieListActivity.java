package com.enterprises.wayne.moviesapp.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.enterprises.wayne.moviesapp.R;
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

/**
 * An activity representing a list of Movies.
 */
public class MovieListActivity extends AppCompatActivity
{

    /* UI */
    @Bind(R.id.parent)
    LinearLayout parent;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.recyclerViewMovies)
    RecyclerView recyclerViewMovies;

    /* fields */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // reference views
        ButterKnife.bind(this);

        // setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // check if tablet or phone
        if (findViewById(R.id.movie_detail_container) != null)
            mTwoPane = true;

        // download movies
        getMovies();
    }

    /**
     * makes a GET request to fetch the list of movies and put them in the recycler-view
     */
    private void getMovies()
    {
        progressBar.setVisibility(View.VISIBLE);

        String url = URLUtils.getMoviesUrl(Constants.POPULAR);
        Ion.with(this)
                .load("GET", url)
                .asString()
                .setCallback(new FutureCallback<String>()
                {
                    @Override
                    public void onCompleted(Exception e, String result)
                    {
                        progressBar.setVisibility(View.INVISIBLE);

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

                    }
                });
    }


}
