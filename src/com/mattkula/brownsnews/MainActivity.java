package com.mattkula.brownsnews;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mattkula.brownsnews.data.Article;
import com.mattkula.brownsnews.data.NewsSourceManager;
import com.mattkula.brownsnews.fragments.ArticleFragment;
import com.mattkula.brownsnews.views.LoadingView;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements NewsSourceManager.OnArticlesDownloadedListener{

    RequestQueue requestQueue;
    ArrayList<Article> articles;

    ViewPager viewPager;

    LoadingView loadingView;

    ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
        @Override
        public void transformPage(View view, float v) {
            LinearLayout layout = (LinearLayout)view.findViewById(R.id.content);
            layout.setTranslationX(300*v);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Sentinel-Bold.ttf"));

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPager.setPageTransformer(false, pageTransformer);
        loadingView = (LoadingView)findViewById(R.id.loading_view);

        requestQueue = Volley.newRequestQueue(this);

        loadArticles();
    }

    public void loadArticles() {
        viewPager.animate().alpha(0);
        loadingView.show();
        new NewsSourceManager().getAllArticles(this);
    }

    @Override
    public void onArticlesDownloaded(ArrayList<Article> articles) {
        this.articles = articles;
        loadingView.dismiss();
        refreshViewPager();
    }

    private void refreshViewPager(){
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_refresh:
                loadArticles();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
