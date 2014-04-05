package com.mattkula.brownsnews.fragments;

import android.animation.Animator;
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
import com.mattkula.brownsnews.database.ArticleDataSource;
import com.mattkula.brownsnews.views.NotifyingScrollView;
import com.squareup.picasso.Picasso;

import javax.sql.DataSource;


/**
 * Created by matt on 3/27/14.
 */
public class ArticleFragment extends Fragment {

    Article article;
    ImageView articleImage;
    TextView textTitle;
    TextView textAuthor;
    TextView textSource;
    TextView textSaved;
    WebView textContent;
    NotifyingScrollView scrollView;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isSwipeToRefreshEnabled = true;

    ArticleDataSource dataSource;

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

        dataSource = new ArticleDataSource(getActivity());
        dataSource.open();

        this.article = (Article)getArguments().getSerializable("article");

        articleImage = (ImageView)v.findViewById(R.id.article_image);
        textTitle = (TextView)v.findViewById(R.id.article_title);
        textAuthor = (TextView)v.findViewById(R.id.article_author);
        textSource = (TextView)v.findViewById(R.id.article_source);
        textContent = (WebView)v.findViewById(R.id.article_content);
        textSaved = (TextView)v.findViewById(R.id.saved_text);
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_container);

        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener)getActivity());
        swipeRefreshLayout.setColorScheme(R.color.browns_orange, R.color.browns_orange, R.color.browns_brown, R.color.browns_orange);
        swipeRefreshLayout.setEnabled(isSwipeToRefreshEnabled);


        textTitle.setText(article.title);
        textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(article.link));
                startActivity(i);
            }

        });

        final GestureDetector detector = new GestureDetector(gestureListener);
        articleImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                detector.onTouchEvent(motionEvent);
                return true;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

    public void setSwipeRefreshLayoutEnabled(boolean enabled){
        isSwipeToRefreshEnabled = enabled;
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

    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.e("ASDF", "Double tapped on " + article.title);
            dataSource.saveArticle(article);
            textSaved.setText(article.isSaved ? "Saved" : "Removed");
            animateStatusText();
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            dataSource.readArticle(article);
            textSaved.setText(article.isRead ? "Read" : "Unread");
            animateStatusText();
            super.onLongPress(e);
        }
    };

    private void animateStatusText(){
        textSaved.setScaleX(3);
        textSaved.setScaleY(3);
        textSaved.animate().alpha(1).scaleX(1).scaleY(1).setListener(savedTextListener);
    }

    Animator.AnimatorListener savedTextListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            textSaved.animate().alpha(0).setDuration(200).setStartDelay(200);
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

}
