package com.enterprises.wayne.moviesapp.events;

import com.enterprises.wayne.moviesapp.model.Movie;

/**
 * Created by ahmed on 6/17/2016.
 * used when the user favorites a movie in the details screen and we want the main screen to know
 */
public class MovieFavoritedEvent
{
    public Movie getMovie()
    {
        return movie;
    }

    public void setMovie(Movie movie)
    {
        this.movie = movie;
    }

    private Movie movie;

    public MovieFavoritedEvent(Movie movie)
    {
        this.movie = movie;
    }
}
