package com.enterprises.wayne.moviesapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ahmed on 6/16/2016.
 */
public class PosterImageView extends ImageView
{
    public PosterImageView(Context context)
    {
        super(context);
    }

    public PosterImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PosterImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int height = (int) (widthMeasureSpec * 1.5);
        super.onMeasure(widthMeasureSpec, height);
    }
}
