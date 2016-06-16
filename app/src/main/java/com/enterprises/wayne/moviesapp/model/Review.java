package com.enterprises.wayne.moviesapp.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahmed on 6/16/2016.
 */
public class Review
{
    /* fields */
    private String id;
    private String author;
    private String content;

    /* getters and setters */

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    /* methods */

    /**
     * parses a review object from json
     */
    public static Review fromJson(JSONObject json)
    {
       Review review = new Review();

        try
        {
            review.setId(json.getString("id"));
            review.setAuthor(json.getString("author"));
            review.setContent(json.getString("content"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return review;
    }
}
