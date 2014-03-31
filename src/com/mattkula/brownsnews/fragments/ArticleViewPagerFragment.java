package com.mattkula.brownsnews.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.data.Article;

import java.util.ArrayList;

/**
 * Created by matt on 3/30/14.
 */
public class ArticleViewPagerFragment extends Fragment {

    ViewPager viewPager;
    ArrayList<Article> articles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_article_view_pager, container, false);

        viewPager = (ViewPager)v.findViewById(R.id.view_pager_fragment);
        viewPager.setPageTransformer(false, pageTransformer);

        return v;
    }

    public void loadArticles(final ArrayList<Article> articles){
        this.articles = articles;

        viewPager.setAdapter(new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                ArticleFragment articleFragment = ArticleFragment.newInstance(articles.get(i));
                return articleFragment;
            }

            @Override
            public int getCount() {
                return articles.size();
            }
        });

        viewPager.animate().alpha(1);
    }

    public void fadeOut(){
        viewPager.animate().alpha(0);
    }

    ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
        @Override
        public void transformPage(View view, float v) {
            LinearLayout layout = (LinearLayout)view.findViewById(R.id.content);
            layout.setTranslationX(600*v);
        }
    };
}
