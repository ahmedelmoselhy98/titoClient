package com.OrxtraDev.TitoApp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.OrxtraDev.TitoApp.R;
import com.OrxtraDev.TitoApp.model.CategLocal;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by OrxtraDev on 12/8/2016.
 */

public class CategAdapter extends RecyclerView.Adapter<CategAdapter.ViewHolder> {
    List<CategLocal> contents;
    Context mContext;
    String  selectCateg;

    public CategAdapter(List<CategLocal> contents, Context mContext, String selectCateg) {
        this.contents = contents;
        this.mContext = mContext;
        this.selectCateg=selectCateg;
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categ, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {



        holder.image.setImageResource(contents.get(position).getImage());
        holder.title.setText(contents.get(position).getTitle());
        holder.subTitle.setText(contents.get(position).getSubTitle());




        if (contents.get(position).getId().equals(selectCateg)){
           holder.item_categ_parent.setBackground(mContext.getResources().getDrawable(R.drawable.back_order));
        }else {
            holder.item_categ_parent.setBackground(mContext.getResources().getDrawable(R.drawable.back_categ));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(contents.get(position));
            }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {



        @BindView(R.id.item_categ_parent)LinearLayout item_categ_parent;
        @BindView(R.id.cateIV)ImageView image;
        @BindView(R.id.titleTV)TextView title;
        @BindView(R.id.subTitleTV)TextView subTitle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

}