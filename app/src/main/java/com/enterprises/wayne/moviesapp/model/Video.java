package com.enterprises.wayne.moviesapp.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahmed on 6/16/2016.
 */
public class Video
{
    /* fields */
    private String id;
    private String name;
    private String type;
    private String key;

    /* getters and setters */

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    /* methods */

    /**
     * parses a video object from json
     */
    public static Video fromJson(JSONObject json)
    {
        Video video = new Video();

        try
        {
            video.setId(json.getString("id"));
            video.setType(json.getString("type"));
            video.setName(json.getString("name"));
            video.setKey(json.getString("key"));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return video;
    }
}
