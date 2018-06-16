package com.example.android.newsapp;

public class Story {

    /**
     * Information about the story
     */
    private String title;
    private String sectionName;
    private String author;
    private String publishedDate;
    private String webUrl;

    /**
     * Constructs a new {@link} Story List Object
     * @param title
     * @param sectionName
     * @param author
     * @param publishedDate
     * @param webUrl
     */

    public Story(String title, String sectionName, String author, String publishedDate, String webUrl){
        this.title = title;
        this.sectionName = sectionName;
        this.author = author;
        this.publishedDate = publishedDate;
        this.webUrl = webUrl;
    }

    public String getTitle(){return title;}

    public String getSectionName(){return sectionName;}

    public String getAuthor(){return author;}

    public String getPublishedDate(){return publishedDate;}

    public String getWebUrl(){return webUrl;}

}
