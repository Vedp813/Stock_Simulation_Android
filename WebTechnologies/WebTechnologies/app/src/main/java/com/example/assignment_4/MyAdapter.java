package com.example.assignment_4;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<PortfolioModal> portList;
    private Context context;
    private OnItemClickListener listener;


    public MyAdapter(List<PortfolioModal> portList, Context context, OnItemClickListener listener) {
        this.portList = portList;
        this.context = context;
        this.listener = listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ticker;
        private TextView shares;
        private TextView totalcost;
        private TextView change;
        private ImageView arrow;

        public ViewHolder(View itemView) {
            super(itemView);
            ticker = itemView.findViewById(R.id.textTicker);
            totalcost = itemView.findViewById(R.id.textStockPrice);
            shares = itemView.findViewById(R.id.textShares);
            change = itemView.findViewById(R.id.textChange);
            arrow = itemView.findViewById(R.id.nextArrow);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PortfolioModal portfolio = portList.get(position);
        portQuote quote = portfolio.getQuote();
        holder.ticker.setText(portfolio.getTicker());
        holder.totalcost.setText("$ "+Double.toString(portfolio.formatDouble(portfolio.getTotalCost())));
        holder.shares.setText(portfolio.getQuantity()+" shares");
        int success = ContextCompat.getColor(holder.itemView.getContext(), R.color.buttonColor);
        int error = ContextCompat.getColor(holder.itemView.getContext(), R.color.watchList);
        holder.change.setTextColor(success);
        holder.change.setText("$"+Double.toString(portfolio.formatDouble(quote.getD()))+"("+Double.toString(portfolio.formatDouble(quote.getDp()))+"%)");
        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClickPort(portfolio.getTicker());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
       return portList.size();
    }

    public interface OnItemClickListener {
        void onItemClickPort(String symbol);
    }
}
