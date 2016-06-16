package com.enterprises.wayne.moviesapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.enterprises.wayne.moviesapp.R;
import com.enterprises.wayne.moviesapp.model.Movie;
import com.enterprises.wayne.moviesapp.util.URLUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ahmed on 6/16/2016.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder>
{

    /* fields */
    Context context;
    List<Movie> data;
    Listener listener;

    /* Constructor */
    public MoviesAdapter(Context context)
    {
        this.context = context;
        this.data = new ArrayList<>();
    }

    /* methods */

    /**
     * replaces data and updates UI
     */
    public void setData(List<Movie> newData)
    {
        this.data.clear();
        this.data.addAll(newData);
        notifyDataSetChanged();
    }

    /**
     * deletes all the data and updates UI
     */
    public void clear()
    {
        data.clear();
        notifyDataSetChanged();
    }


    /**
     * finds this movie and the data and set it to favorited
     */
    public void setMovieFavorited(int movieId)
    {
        for (Movie movie : data)
            if (movie.getId() == movieId)
                movie.setFavorite(true);
    }


    /**
     * registers to be invoked for callbacks
     */
    public void setListener(Listener listener)
    {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Movie movie = data.get(position);

        // lazy load the image
        if (movie.getPosterPath() != null)
            Picasso.with(context).
                    load(URLUtils.getPosterUrl(movie.getPosterPath()))
                    .placeholder(R.drawable.ic_photo_black_48dp)
                    .into(holder.imageViewPoster);

    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {

        @Bind(R.id.imageViewPoster)
        ImageView imageViewPoster;

        public ViewHolder(View view)
        {
            super(view);

            // reference views
            ButterKnife.bind(this, view);

            // setup listener
            imageViewPoster.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (listener != null)
                        listener.onClick(data.get(getAdapterPosition()));
                }
            });
        }
    }

    /**
     * for callbacks to the parent who made the adapter
     */
    public interface Listener
    {
        public void onClick(Movie movie);
    }
}
