package com.example.assignment_4;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int FIRST_ITEM = 0;
    private static final int OTHER_ITEMS = 1;

    private List<NewsModal> newsList;
    private Context context;


    public NewsAdapter(List<NewsModal> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == FIRST_ITEM) {
            View firstLayoutView = inflater.inflate(R.layout.newsabove, parent, false);
            return new FirstViewHolder(firstLayoutView);
        } else {
            View otherLayoutView = inflater.inflate(R.layout.news_item, parent, false);
            return new NewsViewHolder(otherLayoutView);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NewsModal news = newsList.get(position);


        if (position == 0) {
            FirstViewHolder firstHolder = (FirstViewHolder) holder;
            firstHolder.aboveNewsSource.setText(news.getNewsSource());
            long timestamp = news.getNewsTime(); // Assuming getNewsTime() returns UNIX timestamp in seconds
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(timestamp * 1000L, System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
            firstHolder.aboveNewsTime.setText(timeAgo);
            firstHolder.aboveNewsHeading.setText(news.getNewsHeading());
            Picasso.get().load(news.getNewsImage()).into(firstHolder.aboveNewsImage);
            JSONObject obj = new JSONObject();
            try {
                obj.put("Source", news.getNewsSource());
                obj.put("headline", news.getNewsHeading());
                obj.put("time", news.UnixTimeStampToDate());
                obj.put("summary",news.getNewsSummary());
                obj.put("url",news.getUrl());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            firstHolder.aboveNewsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewsDialog dialog = new NewsDialog(context, obj);
                    Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });
        } else {
            NewsViewHolder newsSecond = (NewsViewHolder) holder;
            Log.d("NEWS SOURCE", ""+news.getNewsSource());
            newsSecond.newsSource.setText(news.getNewsSource());
            long timestamp = news.getNewsTime(); // Assuming getNewsTime() returns UNIX timestamp in seconds
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(timestamp * 1000L, System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
            newsSecond.newsTime.setText(timeAgo);
            newsSecond.newsHeading.setText(news.getNewsHeading());
            Picasso.get().load(news.getNewsImage()).into(newsSecond.newsImage);
//            newsHolder.belowNewsCard.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    NewsDialog dialog = new CustomNewsDialog(context, currItem.getSource(), currItem.formatDate(), currItem.getTitle(), currItem.getDescription(), currItem.getUrl());
//                    Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    dialog.show();
//                }
//            });
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? FIRST_ITEM : OTHER_ITEMS;
    }


    private static class FirstViewHolder extends RecyclerView.ViewHolder {
        private ImageView aboveNewsImage;
        private TextView aboveNewsSource;
        private TextView aboveNewsTime;
        private TextView aboveNewsHeading;
        private CardView aboveNewsCard;

        FirstViewHolder(View itemView) {
            super(itemView);
            aboveNewsImage = itemView.findViewById(R.id.imageViewNewsAbove);
            aboveNewsSource = itemView.findViewById(R.id.textView42);
            aboveNewsTime = itemView.findViewById(R.id.textView43);
            aboveNewsHeading = itemView.findViewById(R.id.textView44);
            aboveNewsCard = itemView.findViewById(R.id.newsCardAbove);
        }
    }

    private static class NewsViewHolder extends RecyclerView.ViewHolder {
        private ImageView newsImage;

        private TextView newsSource;
        private TextView newsTime;
        private TextView newsHeading;
        private CardView belowNewsCard;

        //        private CardView newsCard;
        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.newsImageItem);
            newsSource = itemView.findViewById(R.id.textView48);
            newsTime = itemView.findViewById(R.id.textView45);
            newsHeading = itemView.findViewById(R.id.textView47);
            belowNewsCard = itemView.findViewById(R.id.BelowNewsCard);

        }
    }
}
