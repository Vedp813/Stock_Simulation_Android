package com.example.assignment_4;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsModal {
    private String newsImage;
    private String newsSource;
    private long newsTime;
    private String newsHeading;
    private String newsSummary;
    private String url;

    public String getUrl() {
        return url;
    }

    public String getNewsSummary() {
        return newsSummary;
    }

    public String getNewsImage() {
        return newsImage;
    }

    public String getNewsSource() {
        return newsSource;
    }

    public long getNewsTime() {
        return newsTime;
    }

    public String getNewsHeading() {
        return newsHeading;
    }

    public NewsModal(String newsImage, String newsSource, long newsTime, String newsHeading,String newsSummary,String url) {
        this.newsImage = newsImage;
        this.newsSource = newsSource;
        this.newsTime = newsTime;
        this.newsHeading = newsHeading;
        this.newsSummary = newsSummary;
        this.url = url;
    }

    public String UnixTimeStampToDate() {
            long unixTimeStamp = this.newsTime; // Unix timestamp in seconds

            // Convert Unix timestamp to milliseconds
            long unixTimeMillis = unixTimeStamp * 1000;

            // Create a Date object using the milliseconds

            Date date = new Date(unixTimeMillis);

            // Format the date as "March 22, 2024"
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
            String formattedDate = dateFormat.format(date);

            // Print the formatted date
            return formattedDate;
        }

}
