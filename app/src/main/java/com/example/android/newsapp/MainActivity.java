package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    private NewsAdapter mAdapter;

    /**
     *Constant value for the story loader ID. We can choose any integer.
     */
    private static final int STORY_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView storiesListView = (ListView) findViewById(R.id.stories_list);

        //Create a new adapter that takes an empty list of stories as input
        mAdapter = new NewsAdapter(this, new ArrayList<Story>());

        storiesListView.setAdapter(mAdapter);

        //Get a a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //If there is a network connection, fetch data
        if(networkInfo != null && networkInfo.isConnected()){
            //Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(STORY_LOADER_ID, null, this);
        }

        //Set an item click listener on the ListView, which sends an intent to a web browser
        //to open a website with more information about the selected story.
        storiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Find the current story that was clicked on
                Story currentStory = mAdapter.getItem(position);

                //Convert the String Url into a URI object (to pass into the Intent constructor)
                Uri storyUri = Uri.parse(currentStory.getWebUrl());

                //Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, storyUri);

                //Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    // Get build URI from url "http://content.guardianapis.com/search"
    private static String getGuardianRequestUrl(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .encodedAuthority("content.guardianapis.com")
                .appendPath("search")
                .appendQueryParameter("order-date", "published")
                .appendQueryParameter("show-references", "author")
                .appendQueryParameter("api-key", "test");
        String guardianUrl = builder.build().toString();
        return guardianUrl;
    }


    @Override
    public Loader<List<Story>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(this, getGuardianRequestUrl());
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> stories) {

        mAdapter.clear();

        //If there is a valid list of [@link Story]s, then add them to the adapter's
        //data set. This will trigger the ListView to update.
        if(stories != null && !stories.isEmpty()){
            mAdapter.addAll(stories);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        mAdapter.clear();
    }
}
