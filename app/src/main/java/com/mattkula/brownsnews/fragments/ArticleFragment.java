package com.mattkula.brownsnews.fragments;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.mattkula.brownsnews.Prefs;
import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.database.Article;
import com.mattkula.brownsnews.database.ArticleDataSource;
import com.mattkula.brownsnews.utils.SimpleAnimatorListener;
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
    TextView textSaved;
    WebView textContent;
    ImageView imageRead;
    NotifyingScrollView scrollView;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isSwipeToRefreshEnabled = true;

    ArticleDataSource dataSource;

    ArticleViewPagerDelegate delegate;

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
        imageRead = (ImageView)v.findViewById(R.id.image_read);
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_container);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((SwipeRefreshLayout.OnRefreshListener)getActivity()).onRefresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorScheme(R.color.primary_light, R.color.primary_light, R.color.primary_dark, R.color.primary_light);
        swipeRefreshLayout.setEnabled(isSwipeToRefreshEnabled);

        textTitle.setText(article.title);
        textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(article.link));
                startActivity(i);
            }

        });

        imageRead.setAlpha(article.isRead ? 1f : 0f);

        textAuthor.setText("By: " + article.author + " on " + article.publishedDate.toLocaleString());
        textSource.setText("Via: " + article.newsSource);

        textContent.getSettings().setUseWideViewPort(false);
        textContent.setBackgroundColor(Color.argb(1, 0, 0, 0));
        textContent.setBackgroundResource(R.color.primary_dark);
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
        scrollView.setOnScrollChangedListener(new NotifyingScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
                articleImage.setTranslationY(-t * .3f);
            }
        });
        scrollView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                scrollView.getViewTreeObserver().removeOnPreDrawListener(this);
                scrollView.setPadding(0, articleImage.getHeight() - 80, 0, 0);
                return false;
            }
        });
        final GestureDetector detector = new GestureDetector(getActivity(), gestureListener);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, @NonNull MotionEvent motionEvent) {
                return detector.onTouchEvent(motionEvent);
            }
        });

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
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            Log.e("ASDF", "Swiped down on " + article.title);
//            if(velocityY > 0 && Math.abs(velocityX) < Math.abs(velocityY)){
//                dataSource.readArticle(article);
//                if(article.isRead){
//                    if(Prefs.getValueForKey(getActivity(), Prefs.TAG_READ_SHOWN, false)){
//                        textSaved.setText("Read");
//                        animateStatusText();
//                        imageRead.setAlpha(1f);
//                    } else {
//                        animateFlyDown();
//                    }
//                } else {
//                    textSaved.setText("Unread");
//                    animateStatusText();
//                    imageRead.setAlpha(0f);
//                }
//                return true;
//            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

    private void animateFlyDown(){
        WindowManager manager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        Point p = new Point();
        manager.getDefaultDisplay().getSize(p);

        getView().animate().translationY(p.y).rotationX(-35).scaleY(.5f).scaleX(.5f).alpha(0).setInterpolator(new AccelerateInterpolator()).setListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (delegate != null) {
                    delegate.removeArticleAtPosition(ArticleFragment.this.article);
                }
            }
        });
    }

    private void animateStatusText(){
        textSaved.setScaleX(3);
        textSaved.setScaleY(3);
        textSaved.animate().alpha(1).scaleX(1).scaleY(1).setListener(savedTextListener);
    }

    Animator.AnimatorListener savedTextListener = new SimpleAnimatorListener(){
        @Override
        public void onAnimationEnd(Animator animator) {
            textSaved.animate().alpha(0).setDuration(200).setStartDelay(200);
        }
    };

    public void setDelegate(ArticleViewPagerDelegate delegate){
        this.delegate = delegate;
    }

    public interface ArticleViewPagerDelegate {
        public void removeArticleAtPosition(Article article);
    }
}
