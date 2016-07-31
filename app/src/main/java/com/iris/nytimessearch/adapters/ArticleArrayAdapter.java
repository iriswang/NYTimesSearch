package com.iris.nytimessearch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.iris.nytimessearch.R;
import com.iris.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by iris on 7/30/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article>{

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadline());

        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);
        String thumbnail = article.getThumbNail();

        if(!thumbnail.isEmpty()) {
            Picasso.with(getContext()).load(thumbnail).into(imageView);
        }
        return convertView;
    }

}
