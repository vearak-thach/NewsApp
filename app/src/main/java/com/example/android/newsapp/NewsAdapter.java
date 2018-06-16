package com.example.android.newsapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class NewsAdapter extends ArrayAdapter<Story> {

    public NewsAdapter(Activity context, ArrayList<Story> stories){
        super(context, 0, stories);
    }

    /**
     *@return the formatted date string (i.e. "June 11, 2017") from a Date object.
     */
    private String formatDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try{
            Date dateObject = dateFormat.parse(date);
            String finalDateFormat = "LLL dd, yyyy";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDateFormat);
            return finalDateFormatter.format(dateObject);
        }catch (ParseException e){
            Log.e("QueryUtils", "Error parsing JSON date: ", e);
            return " ";
        }
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //Check if there is an existing list item view (called convertView) that we can reuse,
        //otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        //Find the story at the given position in the list of stories
        Story currentStory = getItem(position);

        //Create a new Date object from the time in milliseconds
        String dateObject = new String(currentStory.getPublishedDate());

        //Find the section at the given position in the list of stories
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_name);
        sectionTextView.setText(currentStory.getSectionName());

        //Find the title at the given position in the list of stories
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.web_title);
        titleTextView.setText(currentStory.getTitle());

        //Find the web publication date in the list of stories
        TextView publishedDateTextView = (TextView) listItemView.findViewById(R.id.web_publication_date);
        String formattedDate = formatDate(dateObject);
        publishedDateTextView.setText((CharSequence) formattedDate);

        //Find the author in the list of stories
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        authorTextView.setText(currentStory.getAuthor());

        //return the list item view that is now showing the appropriate data
        return listItemView;
    }

}
