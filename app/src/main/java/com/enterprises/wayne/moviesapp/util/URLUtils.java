package com.enterprises.wayne.moviesapp.util;

/**
 * Created by ahmed on 6/16/2016.
 */
public class URLUtils
{
    /* constants */
    public static final String HOST = "http://api.themoviedb.org/3/";

    /* methods */
    public static String getMoviesUrl(String critrea)
    {
        return  HOST + "movie/" + critrea + "?api_key=" + Keys.API_KEY;
    }
}
