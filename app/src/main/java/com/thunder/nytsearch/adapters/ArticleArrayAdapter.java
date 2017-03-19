package com.thunder.nytsearch.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thunder.nytsearch.R;
import com.thunder.nytsearch.models.Article;

import java.util.ArrayList;

/**
 * Created by anlinsquall on 17/3/17.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, ArrayList<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the data item for position
        Article article = getItem(position);

        // Check if the exising view
        if(convertView == null){
            convertView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.item_article_result, parent,false);
        }
        //find the views
        ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
        TextView tvHeadline = (TextView) convertView.findViewById(R.id.tvHeadline);

        tvHeadline.setText(article.getHeadline());

        // populate thumbnail
        if(!TextUtils.isEmpty(article.getThumbnail()))
            Picasso.with(getContext()).load(article.getThumbnail()).into(ivThumbnail);

        return convertView;
    }
}
