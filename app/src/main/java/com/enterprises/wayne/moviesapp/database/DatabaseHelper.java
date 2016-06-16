package com.enterprises.wayne.moviesapp.database;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.enterprises.wayne.moviesapp.model.Movie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by ahmed on 6/16/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    /* constants */
    private static final String DATABASE_NAME = "moviesDB";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String createSQL =
                "CREATE TABLE MOVIES(" +
                        " ID            INT PRIMARY KEY" +
                        ",TITLE         TEXT" +
                        ",PLOT          TEXT" +
                        ",RELEASE_DATE  TEXT" +
                        ",POSTER_PATH   TEXT" +
                        ",VOTE          REAL" +
                        ",IS_FAVORITE   INT" +
                        ")";
        Log.e("Game", createSQL);
        db.execSQL(createSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE MOVIES");
        onCreate(db);
    }

    /**
     * adds a movie to the table
     */
    public void addMovie(Movie movie)
    {
        String valuesString = String.format("(%d, \"%s\", \"%s\", \"%s\", \"%s\", %.2f, %d)"
                , movie.getId()
                , movie.getTitle()
                , movie.getPlot()
                , movie.getReleaseDate()
                , movie.getPosterPath()
                , movie.getVote()
                , movie.isFavorite() ? 1 : 0);
        String insertSQL =
                "REPLACE INTO MOVIES(ID, TITLE, PLOT, RELEASE_DATE, POSTER_PATH, VOTE, IS_FAVORITE) " +
                        "VALUES " + valuesString;
        Log.e("Game", insertSQL);
        getWritableDatabase().execSQL(insertSQL);
    }

    /**
     * gets all the movies added as favorite
     *
     * @return
     */
    public List<Movie> getMovies()
    {
        // query the db
        String selectSQL = "SELECT * FROM MOVIES";
        Cursor curosr = getReadableDatabase().rawQuery(selectSQL, null);

        // parse the movies
        List<Movie> movieList = new ArrayList<>();
        if (curosr.moveToFirst())
            do
            {
                Movie movie = new Movie();
                movie.setId(curosr.getInt(curosr.getColumnIndex("ID")));
                movie.setTitle(curosr.getString(curosr.getColumnIndex("TITLE")));
                movie.setPlot(curosr.getString(curosr.getColumnIndex("PLOT")));
                movie.setReleaseDate(curosr.getString(curosr.getColumnIndex("RELEASE_DATE")));
                movie.setPosterPath(curosr.getString(curosr.getColumnIndex("POSTER_PATH")));
                movie.setVote(curosr.getDouble(curosr.getColumnIndex("VOTE")));
                movie.setFavorite(curosr.getInt(curosr.getColumnIndex("IS_FAVORITE")) == 1 ? true : false);

                movieList.add(movie);
            } while (curosr.moveToNext());

        return movieList;
    }

    /**
     * @return a set of the ids of all movies
     */
    public HashSet<Integer> getFavoritedMoviesId()
    {
        // query the db
        String selectSQL = "SELECT * FROM MOVIES WHERE MOVIES.IS_FAVORITE = 1";
        Cursor curosr = getReadableDatabase().rawQuery(selectSQL, null);

        // form the set
        HashSet<Integer> favoritedMovies = new HashSet<>();
        if (curosr.moveToFirst())
            do
            {
                favoritedMovies.add(curosr.getInt(curosr.getColumnIndex("ID")));
            } while (curosr.moveToNext());
        return favoritedMovies;

    }
}
