package com.enterprises.wayne.moviesapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.enterprises.wayne.moviesapp.R;
import com.enterprises.wayne.moviesapp.model.Movie;
import com.enterprises.wayne.moviesapp.util.Constants;
import com.enterprises.wayne.moviesapp.util.URLUtils;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Movie detail screen.
 */
public class MovieDetailFragment extends Fragment
{

    /* UI */

    @Bind(R.id.textViewTitle)
    TextView textViewTitle;
    @Bind(R.id.ratingBar)
    RatingBar ratingBar;
    @Bind(R.id.textViewRating)
    TextView textViewRating;
    @Bind(R.id.imageViewPoster)
    ImageView imageViewPoster;
    @Bind(R.id.textViewPlot)
    TextView textViewPlot;


    /* fields */
    Movie movie;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // get the arguments
        if (getArguments().containsKey(Constants.KEY_MOVIE))
            movie = (Movie) getArguments().getSerializable(Constants.KEY_MOVIE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // infalte and reference layout
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        // populate data
        textViewTitle.setText(movie.getTitle() + "(" + movie.getYear() + ")");
        textViewPlot.setText(movie.getPlot() + movie.getPlot() + movie.getPlot() + movie.getPlot());
        textViewRating.setText(String.format("%.1f/10", movie.getVote()));
        ratingBar.setRating((float) (movie.getVote() / 2.0));

        // load the image
        if (movie.getPosterPath() != null)
            Picasso.with(getContext()).
                    load(URLUtils.getPosterUrl(movie.getPosterPath()))
                    .into(imageViewPoster);

        return rootView;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
