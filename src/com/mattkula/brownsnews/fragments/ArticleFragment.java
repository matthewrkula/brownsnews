package com.mattkula.brownsnews.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.database.Article;
import com.mattkula.brownsnews.views.NotifyingScrollView;
import com.squareup.picasso.Picasso;


/**
 * Created by matt on 3/27/14.
 */
public class ArticleFragment extends Fragment {

    Article article;
    ImageView articleImage;
    TextView textTitle;
    TextView textAuthor;
    TextView textSource;
    WebView textContent;
    NotifyingScrollView scrollView;
    SwipeRefreshLayout swipeRefreshLayout;

    public static ArticleFragment newInstance(Article article) {
        ArticleFragment myFragment = new ArticleFragment();

        Bundle args = new Bundle();
        args.putSerializable("article", article);
        myFragment.setArguments(args);

        return myFragment;
    }

    int top = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_article, container, false);

        this.article = (Article)getArguments().getSerializable("article");

        articleImage = (ImageView)v.findViewById(R.id.article_image);
        textTitle = (TextView)v.findViewById(R.id.article_title);
        textAuthor = (TextView)v.findViewById(R.id.article_author);
        textSource = (TextView)v.findViewById(R.id.article_source);
        textContent = (WebView)v.findViewById(R.id.article_content);
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_container);

        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener)getActivity());
        swipeRefreshLayout.setColorScheme(R.color.browns_orange, R.color.browns_orange, R.color.browns_brown, R.color.browns_orange);

        textTitle.setText(article.title);
        textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(article.link));
                startActivity(i);
            }

        });

        textAuthor.setText("By: " + article.author + " on " + article.publishedDate.toLocaleString());
        textSource.setText("Via: " + article.newsSource);

        textContent.getSettings().setUseWideViewPort(false);
        textContent.setBackgroundColor(Color.argb(1, 0, 0, 0));
        textContent.setBackgroundResource(R.color.browns_brown);
        textContent.loadData(getStyle(article.content), "text/html; charset=UTF-8", null);
        textContent.animate().alpha(1).setDuration(300);

        if(!article.imageUrl.equals("none")){
            Picasso.with(getActivity())
                    .load(article.imageUrl)
                    .fit()
                    .centerCrop()
                    .into(articleImage);
        } else {
            double d = Math.random();
            articleImage.setImageDrawable((getResources().getDrawable(d > 0.5 ? R.drawable.browns_dog : R.drawable.brownie)));
            articleImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        scrollView = (NotifyingScrollView)v.findViewById(R.id.scrollview);

        return v;
    }

    private String getStyle(String content){
        String text = "<html><head>"
                + "<style type=\"text/css\">body{color: #fff; font-family: 'Serif'; overflow: hidden; width: 90%;} "
                + " a:link {color:#E34912}"
                + "</style></head>"
                + "<body>"
                + content
                + "</body></html>";

        return text;
    }
}
