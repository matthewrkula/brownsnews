package com.mattkula.brownsnews.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.database.Article;
import com.mattkula.brownsnews.utils.SimpleAnimatorListener;

import java.util.ArrayList;

/**
 * Created by matt on 3/30/14.
 */
public class ArticleViewPagerFragment extends Fragment implements ArticleFragment.ArticleViewPagerDelegate {

    public ViewPager viewPager;
    TextView textEmpty;
    ArrayList<Article> articles;

    String mainText = "No Articles to Read";
    String savedText = "Double Tap Article Image to Save";
    String myText;

    private boolean isSwipeToRefreshEnabled = true;
    private int currentIndex = 0;

    public static ArticleViewPagerFragment newInstance(ArrayList<Article> articles, boolean hasOptionsMenu){

        ArticleViewPagerFragment fragment = new ArticleViewPagerFragment();

        Bundle args = new Bundle();
        args.putSerializable("articles", articles);
        args.putSerializable("hasOptions", hasOptionsMenu);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_article_view_pager, container, false);

        viewPager = (ViewPager)v.findViewById(R.id.view_pager_fragment);
        viewPager.setPageTransformer(false, pageTransformer);
        textEmpty = (TextView)v.findViewById(R.id.text_empty);

        ArrayList<Article> bundleArticles = (ArrayList<Article>)getArguments().getSerializable("articles");

        if(bundleArticles != null){
            loadArticles(bundleArticles);
        } else {
            loadArticles(new ArrayList<Article>());
        }

        boolean hasOptionsMenu = getArguments().getBoolean("hasOptions");
        setHasOptionsMenu(hasOptionsMenu);

        myText = hasOptionsMenu ? mainText : savedText;
        showTextIfNeccessary();

        return v;
    }

    public void loadArticles(final ArrayList<Article> articles){
        this.articles = articles;

        if(viewPager != null){
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
        } else {
            Log.e("ASDF", "NULLLL");
        }

        if(viewPager != null){
            viewPager.animate().alpha(1).setListener(animationListener).start();
        }
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

    private void showTextIfNeccessary(){
        if(this.articles.size() == 0){
            textEmpty.setText(myText);
        } else {
            textEmpty.setText("");
        }
    }

    ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
        @Override
        public void transformPage(View view, float v) {
            LinearLayout layout = (LinearLayout)view.findViewById(R.id.content);
            layout.setTranslationX(600*v);
        }
    };
}
