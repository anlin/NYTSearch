package com.thunder.nytsearch.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thunder.nytsearch.R;
import com.thunder.nytsearch.activities.ArticleActiviy;
import com.thunder.nytsearch.models.Article;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.thunder.nytsearch.R.id.ivThumbnail;
import static com.thunder.nytsearch.R.id.tvHeadline;
import static java.security.AccessController.getContext;

/**
 * Created by anlinsquall on 17/3/17.
 */

public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumbnail;
        public TextView tvHeadline;

        public ViewHolder(View itemView) {
            super(itemView);

            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            tvHeadline = (TextView) itemView.findViewById(R.id.tvHeadline);
        }
    }

    private static ArrayList<Article> mArticles;
    private Context mContext;

    public ArticleArrayAdapter(Context context, ArrayList<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public ArticleArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Article article = mArticles.get(position);

        TextView textView = viewHolder.tvHeadline;
        textView.setText(article.getHeadline());

        ImageView imageView = viewHolder.ivThumbnail;
        if(!TextUtils.isEmpty(article.getThumbnail()))
            Glide.with(mContext).load(article.getThumbnail()).into(imageView);
    }

    @Override
    public int getItemCount() {
            return mArticles.size();
    }
}
