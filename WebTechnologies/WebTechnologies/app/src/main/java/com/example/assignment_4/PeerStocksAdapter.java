package com.example.assignment_4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PeerStocksAdapter extends RecyclerView.Adapter<PeerStocksAdapter.ViewHolder>{

        private List<String> tickerList;
        private Context context;


        public PeerStocksAdapter(List<String> tickerList, Context context) {
            this.tickerList = tickerList;
            this.context = context;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView peerTicker;

            public ViewHolder(View itemView) {
                super(itemView);
                peerTicker = itemView.findViewById(R.id.PeerTicker);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.peer_layout, parent, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String ticker = (String) tickerList.get(position);

            holder.peerTicker.setText(ticker);
            holder.peerTicker.setPaintFlags(holder.peerTicker.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.peerTicker.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StockInfo.class);
                    intent.putExtra("ticker", ticker);
                    context.startActivity(intent);
                }
            }));

        }

        @Override
        public int getItemCount() {
            return tickerList.size();
        }
    }

    