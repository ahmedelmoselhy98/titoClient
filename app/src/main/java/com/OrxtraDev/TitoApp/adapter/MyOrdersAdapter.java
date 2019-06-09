package com.OrxtraDev.TitoApp.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.OrxtraDev.TitoApp.R;
import com.OrxtraDev.TitoApp.activity.OrderDetailActivity;
import com.OrxtraDev.TitoApp.model.OrderModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {


    Context context;
    ArrayList<OrderModel> list;

    public MyOrdersAdapter(Context context) {
        this.context = context;
    }

    public MyOrdersAdapter(Context context, ArrayList<OrderModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_my_orders, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {

        if (!list.get(i).getTripPrice().isEmpty())
            holder.priceTV.setText("SAR "+list.get(i).getTripPrice());
        else
            holder.priceTV.setText("SAR 0.0");

        holder.dateTV.setText(holder.handleDateFormat(Long.parseLong(list.get(i).getTime())));
//        holder.fromTV.setText("(lat: " + list.get(i).getLat() + ", lon: " + list.get(i).getLang() + ")");
//        holder.toTV.setText("(lat: " + list.get(i).getToLat() + ", lon: " + list.get(i).getToLang() + ")");
        holder.fromTV.setText(list.get(i).getcAddress());
        holder.toTV.setText(list.get(i).getToAddress());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("model", list.get(i));
                context.startActivity(intent);



            }
        });


    }

    @Override
    public int getItemCount() {
        if (list.size() > 0)
            return list.size();
        else
            return 30;
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.fromTV)
        TextView fromTV;
        @BindView(R.id.toTV)
        TextView toTV;
        @BindView(R.id.priceTV)
        TextView priceTV;
        @BindView(R.id.dateTV)
        TextView dateTV;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        String handleDateFormat(long currentDate) {
            String myDate = "";
            Date date = new Date(currentDate);
            SimpleDateFormat targetFormat = new SimpleDateFormat("MMM d, h:mm a");
            myDate = targetFormat.format(date);

            return myDate;
        }

    }

}
