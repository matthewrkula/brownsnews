package com.mattkula.brownsnews;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mattkula.brownsnews.background.UpdateManager;
import com.mattkula.brownsnews.database.Article;
import com.mattkula.brownsnews.database.ArticleDataSource;
import com.mattkula.brownsnews.fragments.ArticleViewPagerFragment;
import com.mattkula.brownsnews.fragments.ScheduleFragment;
import com.mattkula.brownsnews.sources.NewsSourceManager;
import com.mattkula.brownsnews.views.LoadingView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends FragmentActivity implements NewsSourceManager.OnArticlesDownloadedListener, SwipeRefreshLayout.OnRefreshListener{

    RequestQueue requestQueue;
    ArrayList<Article> articles;

    LoadingView loadingView;
    ImageView tutorialArrow;
    ImageView tutorialArrowDown;
    View tutorialView;
    ListView menu;

    ArticleViewPagerFragment viewPagerFragment;
    ScheduleFragment scheduleFragment;
    DrawerLayout drawerLayout;

    int currentFragmentIndex = 0;
    ArticleDataSource dataSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Sentinel-Bold.ttf"));

        dataSource = new ArticleDataSource(this);
        dataSource.open();

        this.articles = dataSource.getAllArticles(0);
        viewPagerFragment = ArticleViewPagerFragment.newInstance(this.articles);
        scheduleFragment = new ScheduleFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, viewPagerFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();

        loadingView = (LoadingView)findViewById(R.id.loading_view);
        tutorialView = findViewById(R.id.tutorial_view);
        tutorialView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                view.animate().alpha(0).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
            }
        });

        tutorialArrow = (ImageView)findViewById(R.id.tutorial_arrow);
        tutorialArrowDown = (ImageView)findViewById(R.id.tutorial_arrow_pull);
        setUpArrowAnimations();

        menu = (ListView)findViewById(R.id.sliding_menu);
        menu.setAdapter(menuAdapter);
        menu.setOnItemClickListener(menuItemClickListener);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        requestQueue = Volley.newRequestQueue(this);

        UpdateManager.rescheduleUpdates(this);

        loadArticles(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

    private void setUpArrowAnimations(){
        ValueAnimator animator = ValueAnimator.ofInt(50, -50);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                tutorialArrow.setTranslationX((Integer)valueAnimator.getAnimatedValue());
                tutorialArrowDown.setTranslationY(-(Integer)valueAnimator.getAnimatedValue());
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(600);
        animator.start();
    }

    public void loadArticles(boolean showLoading) {
        if(showLoading){
            viewPagerFragment.fadeOut();
            loadingView.show();
        }
        new NewsSourceManager().getAllArticles(this, this);
    }

    @Override
    public void onArticlesDownloaded() {
        this.articles = dataSource.getAllArticles(0);
        loadingView.dismiss();
        viewPagerFragment.loadArticles(this.articles);

        if(Prefs.isFirstTime(this)){
            tutorialView.setVisibility(View.VISIBLE);
            tutorialView.animate().alpha(1);
        }

        Prefs.setValueForKey(this, Prefs.TAG_IS_FIRST_TIME, false);
        if(articles.size() > 0){
            Prefs.setValueForKey(this, Prefs.TAG_UPDATE_LAST_LINK, articles.get(0).link);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_refresh:
                loadArticles(true);
                return true;
            case R.id.menu_sources:
                viewPagerFragment.fadeOut();
                intent = new Intent(this, SelectSourcesActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.menu_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                if(viewPagerFragment.isVisible()){
                    loadArticles(true);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        loadArticles(true);
    }

    private String[] menuItems = new String[]{
            "News",
            "Saved Articles",
            "Schedule"
    };

    private BaseAdapter menuAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return menuItems.length;
        }

        @Override
        public Object getItem(int i) {
            return menuItems[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if(v == null){
                v = View.inflate(getApplicationContext(), R.layout.menu_item_sliding_menu, null);
            }
            String s = (String)getItem(i);

            ((TextView)v.findViewById(R.id.item_name)).setText(s);

            return v;
        }
    };

    private AdapterView.OnItemClickListener menuItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            drawerLayout.closeDrawer(Gravity.LEFT);

            if(currentFragmentIndex == i) return;
            currentFragmentIndex = i;

            switch (i){
                case 0:
                    viewPagerFragment = ArticleViewPagerFragment.newInstance(MainActivity.this.articles);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, viewPagerFragment)
                            .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                            .commit();
                    break;
                case 1:
                    ArrayList<Article> savedArticles = dataSource.getSavedArticles();
                    viewPagerFragment = ArticleViewPagerFragment.newInstance(savedArticles);
                    viewPagerFragment.setSwipeRefreshLayoutEnabled(false);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, viewPagerFragment)
                            .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                            .commit();
                    break;
                case 2:
                    scheduleFragment = new ScheduleFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, scheduleFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    getSupportFragmentManager().executePendingTransactions();
                    break;
            }
        }
    };
}
