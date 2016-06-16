package com.enterprises.wayne.moviesapp.fragment;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.enterprises.wayne.moviesapp.R;
import com.enterprises.wayne.moviesapp.database.DatabaseHelper;
import com.enterprises.wayne.moviesapp.events.MovieFavoritedEvent;
import com.enterprises.wayne.moviesapp.model.Movie;
import com.enterprises.wayne.moviesapp.model.Review;
import com.enterprises.wayne.moviesapp.model.Video;
import com.enterprises.wayne.moviesapp.util.Constants;
import com.enterprises.wayne.moviesapp.util.URLUtils;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.DebouncingOnClickListener;

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
    @Bind(R.id.layoutReviews)
    LinearLayout layoutReviews;
    @Bind(R.id.layoutVideos)
    LinearLayout layoutVideos;
    @Bind(R.id.progressBarReviews)
    ProgressBar progressBarReviews;
    @Bind(R.id.progressBarVideos)
    ProgressBar progressBarVideos;
    @Bind(R.id.expandableLinearLayout)
    LinearLayout expandableLayout;
    @Bind(R.id.buttonReadPlot)
    Button buttonReadPlot;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.scrollView)
    ScrollView scrollView;

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
        String titleText = movie.getTitle() + "(" + movie.getYear() + ")";
        textViewTitle.setText(titleText);
        textViewPlot.setText(movie.getPlot());
        textViewRating.setText(String.format("%.1f/10", movie.getVote()));
        ratingBar.setRating((float) (movie.getVote() / 2.0));

        // color rating bar
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        // load the image
        if (movie.getPosterPath() != null)
            Picasso.with(getContext()).
                    load(URLUtils.getPosterUrl(movie.getPosterPath()))
                    .into(imageViewPoster);

        // load reviews and videos
        loadReviews();
        loadVideos();

        // hide the favorite fab if it's already in favorites
        if (movie.isFavorite())
            fab.hide();

        return rootView;
    }

    /**
     * makes a get request to get the movie's videos
     */
    private void loadVideos()
    {
        progressBarVideos.setVisibility(View.VISIBLE);

        String url = URLUtils.getVideosUrl(movie.getId());
        Ion.with(getContext())
                .load("GET", url)
                .asString()
                .setCallback(new FutureCallback<String>()
                {
                    @Override
                    public void onCompleted(Exception e, String result)
                    {
                        if (!isAdded())
                            return;
                        progressBarVideos.setVisibility(View.INVISIBLE);

                        // check error
                        if (e != null)
                            return;

                        // parse videos
                        try
                        {
                            JSONObject resultJson = new JSONObject(result);
                            JSONArray videosJson = resultJson.getJSONArray("results");
                            for (int i = 0; i < videosJson.length(); i++)
                            {
                                Video video = Video.fromJson(videosJson.getJSONObject(i));
                                addVideoRow(video);
                            }
                        } catch (JSONException e1)
                        {
                        }


                    }
                });
    }


    /**
     * makes a get request to get the movie's reviews
     */
    private void loadReviews()
    {
        progressBarReviews.setVisibility(View.VISIBLE);

        String url = URLUtils.getReviewsUtil(movie.getId());
        Ion.with(getContext())
                .load("GET", url)
                .asString()
                .setCallback(new FutureCallback<String>()
                {
                    @Override
                    public void onCompleted(Exception e, String result)
                    {
                        if (!isAdded())
                            return;
                        progressBarReviews.setVisibility(View.INVISIBLE);

                        // check error
                        if (e != null)
                            return;

                        // parse reviews
                        try
                        {
                            JSONObject resultJson = new JSONObject(result);
                            JSONArray reviewsJson = resultJson.getJSONArray("results");
                            for (int i = 0; i < reviewsJson.length(); i++)
                            {
                                Review review = Review.fromJson(reviewsJson.getJSONObject(i));
                                addReviewRow(review);
                            }
                        } catch (JSONException e1)
                        {
                        }

                    }
                });
    }

    /**
     * inflates a row for a single review and add it to the layout
     */
    private void addVideoRow(final Video video)
    {
        // inflate row
        View view = LayoutInflater.from(getContext()).inflate(R.layout.row_video, null);

        // reference view
        TextView textViewType = (TextView) view.findViewById(R.id.textViewType);
        TextView textViewName = (TextView) view.findViewById(R.id.textViewName);
        ImageView imageViewIcon = (ImageView) view.findViewById(R.id.imageViewIcon);

        // populate data
        textViewType.setText(video.getType());
        textViewName.setText(video.getName());
        int iconResource = video.getType().equals("Trailer") ?
                R.drawable.ic_local_movies_white_48dp : R.drawable.ic_movie_white_48dp;
        imageViewIcon.setImageResource(iconResource);

        // add listener to open something that plays the video
        view.findViewById(R.id.cardViewVideo).setOnClickListener(new DebouncingOnClickListener()
        {
            @Override
            public void doClick(View v)
            {
                Log.e("Game", "clicked");
                String videoUrl = URLUtils.getViewVideoUrl(video.getKey());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                Log.e("Game", "url " + videoUrl);
                startActivity(intent);
            }
        });

        // add to layout
        layoutVideos.addView(view);
    }


    /**
     * inflates a row for a single review and add it to the layout
     */
    private void addReviewRow(Review review)
    {
        // inflate row
        View view = LayoutInflater.from(getContext()).inflate(R.layout.row_review, null);

        // reference view
        TextView textViewAuthor = (TextView) view.findViewById(R.id.textViewAuthor);
        TextView textViewContent = (TextView) view.findViewById(R.id.textViewContent);

        // populate data
        textViewAuthor.setText(review.getAuthor());
        textViewContent.setText(review.getContent());

        // add to layout
        layoutReviews.addView(view);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.buttonReadPlot)
    void expandPlot()
    {
        // set layout transiation of the whole expandableview
        LayoutTransition l = new LayoutTransition();
        l.enableTransitionType(LayoutTransition.CHANGING);
        expandableLayout.setLayoutTransition(l);

        // hide the image view
        ViewGroup.LayoutParams imagViewParams = imageViewPoster.getLayoutParams();
        imagViewParams.width = 0;
        imageViewPoster.setLayoutParams(imagViewParams);

        // fire an animation after the first finishes
        expandableLayout.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

                // hide the read plot button
                buttonReadPlot.setVisibility(View.GONE);

                // expand the layout
                textViewPlot.setMaxLines(Integer.MAX_VALUE);

            }
        }, l.getDuration(LayoutTransition.CHANGING));
    }

    @OnClick(R.id.fab)
    void favoriteMovie()
    {
        // hide the fab
        fab.hide();

        // add to database
        movie.setFavorite(true);
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.addMovie(movie);

        // notify any one waiting for a movie to be favorited
        EventBus.getDefault().post(new MovieFavoritedEvent(movie));
    }


}
