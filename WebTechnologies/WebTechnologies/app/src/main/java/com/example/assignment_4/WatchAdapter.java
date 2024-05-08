package com.example.assignment_4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WatchAdapter extends RecyclerView.Adapter<WatchAdapter.ViewHolder>{

    private List<WatchlistModal> watchList;
    private Context context;
    private OnItemClickListener listener;


    public WatchAdapter(List<WatchlistModal> watchList, Context context, OnItemClickListener listener) {
        this.watchList = watchList;
        this.context = context;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ticker;
        private TextView name;
        private TextView currentPrice;
        private TextView change;
        private ImageView arrow;

        public ViewHolder(View itemView) {
            super(itemView);
            ticker = itemView.findViewById(R.id.textTicker);
            currentPrice = itemView.findViewById(R.id.textStockPrice);
            name = itemView.findViewById(R.id.textShares);
            change = itemView.findViewById(R.id.textChange);
            arrow = itemView.findViewById(R.id.nextArrow);
        }
    }

    @NonNull
    @Override
    public WatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new WatchAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull WatchAdapter.ViewHolder holder, int position) {
        WatchlistModal watchlist = watchList.get(position);
        watchQuote quote = watchlist.getQuote();
        watchProfile profile = watchlist.getProfile();

//        Data being posted
        holder.ticker.setText(profile.getTicker());
        holder.currentPrice.setText("$ "+Double.toString(watchlist.formatDouble(quote.getC())));
        holder.name.setText(profile.getName());
        holder.change.setText("$"+Double.toString(watchlist.formatDouble(quote.getD()))+"("+Double.toString(watchlist.formatDouble(quote.getDp()))+"%)");
        int success = ContextCompat.getColor(holder.itemView.getContext(), R.color.buttonColor);
        int error = ContextCompat.getColor(holder.itemView.getContext(), R.color.watchList);
        holder.change.setTextColor(success);
        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClickFav(profile.getTicker());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return watchList.size();
    }

    public interface OnItemClickListener {
        void onItemClickFav(String symbol);
    }
}
