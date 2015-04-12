package com.mattkula.brownsnews.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.database.Article;
import com.mattkula.brownsnews.utils.SimpleAnimatorListener;
import com.mattkula.brownsnews.utils.ViewUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ArticleViewPagerFragment extends Fragment implements ArticleFragment.ArticleViewPagerDelegate {

    public static final String TAG_ARTICLES = "articles";
    public static final String TAG_HAS_OPTIONS = "hasOptions";

    @InjectView(R.id.view_pager_fragment) ViewPager viewPager;
    @InjectView(R.id.text_empty) TextView textEmpty;

    ArrayList<Article> articles;

    String mainText = "No Articles to Read";
    String savedText = "Double Tap Article Image to Save";
    String myText;

    private boolean isSwipeToRefreshEnabled = true;
    private int currentIndex = 0;

    public static ArticleViewPagerFragment newInstance(ArrayList<Article> articles, boolean hasOptionsMenu){
        ArticleViewPagerFragment fragment = new ArticleViewPagerFragment();

        Bundle args = new Bundle();
        args.putSerializable(TAG_ARTICLES, articles);
        args.putSerializable(TAG_HAS_OPTIONS, hasOptionsMenu);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_article_view_pager, container, false);
        ButterKnife.inject(this, v);

        viewPager.setPageTransformer(false, pageTransformer);

        ArrayList<Article> bundleArticles = (ArrayList<Article>)getArguments().getSerializable(TAG_ARTICLES);

        if(bundleArticles != null){
            loadArticles(bundleArticles);
        } else {
            showTextIfNeccessary();
            loadArticles(new ArrayList<Article>());
        }

        boolean hasOptionsMenu = getArguments().getBoolean(TAG_HAS_OPTIONS);
        setHasOptionsMenu(hasOptionsMenu);

        myText = hasOptionsMenu ? mainText : savedText;

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(viewPager != null) {
            viewPager.setCurrentItem(currentIndex);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viewPager != null) {
            currentIndex = viewPager.getCurrentItem();
        }
    }

    public void loadArticles(final ArrayList<Article> newArticles){
        this.articles = newArticles;

        if(viewPager != null){
            if (viewPager.getAdapter() == null) {
                viewPager.setAdapter(new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
                    @Override
                    public Fragment getItem(int i) {
                        ArticleFragment articleFragment = ArticleFragment.newInstance(articles.get(i));
                        articleFragment.setSwipeRefreshLayoutEnabled(isSwipeToRefreshEnabled);
                        articleFragment.setDelegate(ArticleViewPagerFragment.this);
                        return articleFragment;
                    }

                    @Override
                    public int getCount() {
                        return articles.size();
                    }

                    @Override
                    public int getItemPosition(Object object) {
                        return POSITION_NONE;
                    }
                });
                currentIndex = 0;
            } else {
                viewPager.getAdapter().notifyDataSetChanged();
                currentIndex = Math.min(currentIndex, newArticles.size() - 1);
                viewPager.setCurrentItem(currentIndex);
            }
        } else {
            Log.e("ASDF", "NULLLL");
        }

        fadeIn();
    }

    Animator.AnimatorListener animationListener = new SimpleAnimatorListener(){
        @Override
        public void onAnimationEnd(Animator animator) {
            showTextIfNeccessary();
            textEmpty.setAlpha(1);
        }
    };

    public void removeArticleAtPosition(Article article){
        showTextIfNeccessary();
        int index = this.articles.indexOf(article);
        this.articles.remove(index);
        this.viewPager.getAdapter().notifyDataSetChanged();
        showTextIfNeccessary();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    public void setSwipeRefreshLayoutEnabled(boolean enabled){
        this.isSwipeToRefreshEnabled = enabled;
    }

    public int getCurrentIndex(){
        return currentIndex;
    }
    public void setCurrentIndex(int i){
        currentIndex = i;
    }

    public void fadeOut(){
        if(viewPager != null){
            textEmpty.setAlpha(0);
            viewPager.animate().alpha(0).setListener(null);
        }
    }

    public void fadeIn() {
        if(viewPager != null){
            viewPager.animate().alpha(1).setListener(animationListener).start();
        }
    }

    private void showTextIfNeccessary(){
        if(this.articles != null && this.articles.size() == 0){
            textEmpty.setText(myText);
        } else {
            textEmpty.setText("");
        }
    }

    ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
        @Override
        public void transformPage(View view, float v) {
            View header = (View)view.getTag();
            View image = (View)header.getTag();
            header.setTranslationX(ViewUtils.dpToPixels(400, getActivity()) * v);
            image.setTranslationX(-ViewUtils.dpToPixels(200, getActivity()) * v);
        }
    };
}
