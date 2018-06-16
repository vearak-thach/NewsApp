package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<Story>> {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private String mURL;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsLoader(Context context, String url){
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading(){
        forceLoad();
    }

    /**
     * This is on a background thraad.
     */
    @Override
    public List<Story> loadInBackground(){
        if(mURL == null){
            return null;
        }

        //Perform the network request, parse the response, and extract a list of earthquakes.
        List<Story> stories = QueryUtils.fetchStoryData(mURL);
        return stories;
    }
}
