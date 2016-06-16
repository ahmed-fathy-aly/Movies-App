package com.enterprises.wayne.moviesapp.util;

import android.util.Log;

/**
 * Created by ahmed on 6/16/2016.
 */
public class URLUtils
{
    /* constants */
    public static final String HOST = "http://api.themoviedb.org/3/";

    /* methods */

    /**
     * GET request of a list of movies
     */
    public static String getMoviesUrl(String critrea)
    {
        return HOST + "movie/" + critrea + "?api_key=" + Keys.API_KEY;
    }

    /**
     * image url of a poster
     */
    public static String getPosterUrl(String posterPath)
    {
        return "http://image.tmdb.org/t/p/" + "w185" + posterPath;
    }

    /**
     * GET request of a list of reviews of that movies
     */
    public static String getReviewsUtil(String movieId)
    {
        return HOST + "movie/" + movieId + "/reviews" + "?api_key=" + Keys.API_KEY;
    }
}
