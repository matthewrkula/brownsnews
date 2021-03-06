package com.mattkula.brownsnews.fragments;

import android.animation.Animator;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.database.Article;
import com.mattkula.brownsnews.database.ArticleDataSource;
import com.mattkula.brownsnews.utils.SimpleAnimatorListener;
import com.mattkula.brownsnews.utils.ViewUtils;
import com.mattkula.brownsnews.views.NotifyingScrollView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ArticleFragment extends Fragment {

    public static final String TAG_ARTICLE = "article";

    Article article;
    ArticleDataSource dataSource;
    ArticleViewPagerDelegate delegate;

    @InjectView(R.id.article_image) ImageView articleImage;
    @InjectView(R.id.article_header) RelativeLayout articleHeader;
    @InjectView(R.id.article_title) TextView textTitle;
    @InjectView(R.id.article_author) TextView textAuthor;
    @InjectView(R.id.article_source) TextView textSource;
    @InjectView(R.id.saved_text) TextView textSaved;
    @InjectView(R.id.article_content) TextView textContent;
    @InjectView(R.id.image_read) ImageView imageRead;
    @InjectView(R.id.scrollview) NotifyingScrollView scrollView;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.article_continue) TextView continueButton;

    boolean isSwipeToRefreshEnabled = true;
    int shownCharacters = 2000;

    public static ArticleFragment newInstance(Article article) {
        ArticleFragment myFragment = new ArticleFragment();

        Bundle args = new Bundle();
        args.putSerializable(TAG_ARTICLE, article);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_article, container, false);
        ButterKnife.inject(this, v);
        shownCharacters = getActivity().getResources().getInteger(R.integer.article_character_length);

        dataSource = new ArticleDataSource(getActivity()).open();

        this.article = (Article)getArguments().getSerializable(TAG_ARTICLE);

        // Tags are set for ViewPager Transformer caching (mock ViewHolder pattern)
        v.setTag(articleHeader);
        articleHeader.setTag(articleImage);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((SwipeRefreshLayout.OnRefreshListener) getActivity()).onRefresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeColors(R.color.primary_light, R.color.primary_light, R.color.primary_dark, R.color.primary_light);
        swipeRefreshLayout.setEnabled(isSwipeToRefreshEnabled);

        textTitle.setText(article.title);
        textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(article.link));
                startActivity(i);
            }
        });

        imageRead.setAlpha(article.isRead ? 1f : 0f);

        textAuthor.setText("By: " + article.author + " on " + article.publishedDate.toLocaleString());
        textSource.setText("Via: " + article.newsSource);

        textContent.setAlpha(1);
        textContent.setBackgroundColor(Color.argb(1, 0, 0, 0));
        textContent.setBackgroundResource(R.color.primary_dark);
        textContent.setMovementMethod(LinkMovementMethod.getInstance());

        reloadContent();

        if(!article.imageUrl.equals("none")){
            Glide.with(this)
                    .load(article.imageUrl)
                    .asBitmap()
                    .placeholder(R.drawable.browns_dog)
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            v.findViewById(R.id.article_image_shadow).animate().alpha(1).start();
                            return false;
                        }
                    })
                    .animate(android.R.anim.fade_in)
                    .into(articleImage);
        } else {
            double d = Math.random();
            articleImage.setImageDrawable((getResources().getDrawable(d > 0.5 ? R.drawable.browns_dog : R.drawable.brownie)));
            articleImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        scrollView.setOnScrollChangedListener(new NotifyingScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
                articleHeader.setTranslationY(-t * .2f);
                articleImage.setTranslationY(t * .1f);

                ActionBar ab = getActivity().getActionBar();
                if (ab != null && t - oldt > 10) {
                    ab.hide();
                } else if (ab != null && t == 0) {
                    ab.show();
                }
            }
        });
        scrollView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                scrollView.getViewTreeObserver().removeOnPreDrawListener(this);
                scrollView.setPadding(0, (int)(articleImage.getHeight() - ViewUtils.dpToPixels(40, getActivity())), 0, 20);
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

    private void reloadContent() {
        final Handler handler = new Handler();

        new Thread() {
            @Override
            public void run() {
                String content = article.content;
                boolean visible = false;
                if (content.length() >= shownCharacters) {
                    content = content.substring(0, shownCharacters);
                    visible = true;
                }

                final Spanned spanned = Html.fromHtml(content);
                final boolean finalVisible = visible;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textContent.setText(spanned);
                        continueButton.setVisibility(finalVisible ? View.VISIBLE : View.GONE);
                    }
                });
            }
        }.start();
    }

    @OnClick(R.id.article_continue)
    public void loadMoreOfArticle() {
        shownCharacters *= 2;
        reloadContent();
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
        void removeArticleAtPosition(Article article);
    }
}
