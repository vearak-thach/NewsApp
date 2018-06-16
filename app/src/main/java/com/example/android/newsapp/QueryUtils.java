package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.newsapp.MainActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving news stories data from Guardian
 */
public final class QueryUtils {
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils(){

    }

    /**
     *Query the USGS dataaset an return a list of {@link Story} objects
     */
    public static List<Story> fetchStoryData(String requestUrl){
        URL url = createURL(requestUrl);

        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        //Extract relevant fields from the JSON response and create a list of {@link Story}s
        List<Story> stories = extractFeatureFromJSON(jsonResponse);

        return stories;
    }

    private static URL createURL (String stringURL){
        URL url = null;

        try{
            url = new URL(stringURL);
        }catch (MalformedURLException exception){
            Log.e(LOG_TAG, "Problem building the URL", exception);
        }

        return url;
    }

    /**
     * Make a HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        //If the URL is null, then return early.
        if(url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(1000 /*milliseconds*/);
            urlConnection.setConnectTimeout(1500);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If the request was successful (response code 200),
            //then read the input stream and parse the response.
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the news story JSON results.", e);
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    /**
     * Return a list of {@link Story} objects that has been built up from
     * parsing the given JSON response
     */
    private static List<Story> extractFeatureFromJSON(String storyJSON){
        //If the JSON string is empty or null, then return early
        if(TextUtils.isEmpty(storyJSON)){
            return null;
        }

        //Create an empty ArrayList that we can start adding stories to
        List<Story> stories = new ArrayList<>();

        //Try to parse the JSON response string. If there's a problem with the way the JSON
        //is formatted, a JSONException object will be thrown.
        //Catch the exception so the app doesn't crash, and print the error message to the log
        try {

            //Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(storyJSON);

            //Extract the JSONObject associated with the key "response"
            JSONObject jsonResponse = baseJsonResponse.getJSONObject("response");

            //Extract the JSONArray associated with the key called "results"
            JSONArray storyArray = jsonResponse.getJSONArray("results");

            //For each story in the storyArray, create an {@link Story} object
            for (int i = 0; i < storyArray.length(); i++){

                //Get a single story at position i within the list of stories
                JSONObject currentStory = storyArray.getJSONObject(i);

                String webTitle = currentStory.getString("webTitle");
                String sectionName = currentStory.getString("sectionName");
                String webPublicationDate = currentStory.getString("webPublicationDate");
                String webUrl = currentStory.getString("webUrl");

                String author = "";

                Story story = new Story(webTitle, sectionName, author, webPublicationDate, webUrl);

                stories.add(story);

            }
        }catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the story JSON results.", e);
        }

        return stories;
    }
}
