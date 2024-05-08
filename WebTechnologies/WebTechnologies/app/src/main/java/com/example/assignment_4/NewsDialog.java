package com.example.assignment_4;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class NewsDialog extends Dialog {
    private JSONObject data;
    private Context context;

    public NewsDialog(Context context, JSONObject data) {
        super(context);
        this.context = context;
        this.data = data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_dialog_layout);

        TextView source = findViewById(R.id.newsTitle);
        TextView date = findViewById(R.id.newsDate);
        TextView headline = findViewById(R.id.newsHeadline);
        TextView summary = findViewById(R.id.newsSummary);
        try {
            source.setText(data.getString("Source"));
            date.setText(data.getString("time"));
            headline.setText(data.getString("headline"));
            summary.setText(data.getString("summary"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // Set custom dialog layout

//        Here write the code for setting up the layout
        String url = null;
        String headlineurl = null;
        try {
            url = data.getString("url");
            headlineurl = data.getString("headline");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView chrome = findViewById(R.id.chrome);
        String finalUrl = url;
        chrome.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(finalUrl)); // Assuming `url` is defined somewhere
            context.startActivity(intent);
        });

        String twitterUrl = "https://twitter.com/intent/tweet?text=" + headlineurl + " " + url;
        String facebookUrl = "https://www.facebook.com/sharer/sharer.php?u=" + url + "&amp;src=sdkpreparse";

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView twitter = findViewById(R.id.twitter);
        twitter.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(twitterUrl)); // Assuming `url` is defined somewhere
            context.startActivity(intent);
        });
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView facebook = findViewById(R.id.facebook);
        facebook.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(facebookUrl)); // Assuming `url` is defined somewhere
            context.startActivity(intent);
        });
    }


}
