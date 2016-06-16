package com.enterprises.wayne.moviesapp.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Created by ahmed on 6/16/2016.
 */
public class Movie implements Serializable
{
    /* fields */
    private String id;
    private String title;
    private String plot;
    private String releaseDate;
    private String posterPath;
    private double vote;
    private boolean hasVideo;

    /* getters and setters */

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getPlot()
    {
        return plot;
    }

    public void setPlot(String plot)
    {
        this.plot = plot;
    }

    public String getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath()
    {
        return posterPath;
    }

    public void setPosterPath(String posterPath)
    {
        this.posterPath = posterPath;
    }

    public double getVote()
    {
        return vote;
    }

    public void setVote(double vote)
    {
        this.vote = vote;
    }

    public boolean isHasVideo()
    {
        return hasVideo;
    }

    public void setHasVideo(boolean hasVideo)
    {
        this.hasVideo = hasVideo;
    }

    /* methods */

    /**
     * parses a movie from a json
     */
    public static Movie fromJson(JSONObject json)
    {
        Movie movie = new Movie();

        try
        {
            System.out.println("json = " + json.toString());
            movie.setId(json.getString("id"));
            movie.setTitle(json.getString("title"));
            movie.setPlot(json.getString("overview"));
            movie.setReleaseDate(json.getString("release_date"));
            movie.setPosterPath(json.getString("poster_path"));
            movie.setVote(json.getDouble("vote_average"));
            movie.setHasVideo(json.getBoolean("video"));
        } catch (Exception e)
        {
            System.out.println("error " + e.getMessage());
        }

        return movie;
    }

    /**
     * seperates the year field from the release date
     */
    public String getYear()
    {
        StringTokenizer tok = new StringTokenizer(releaseDate, "-");
        return tok.nextToken();
    }
}
