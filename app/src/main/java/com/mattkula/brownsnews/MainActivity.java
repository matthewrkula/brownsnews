package com.mattkula.brownsnews;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mattkula.brownsnews.background.UpdateManager;
import com.mattkula.brownsnews.database.Article;
import com.mattkula.brownsnews.database.ArticleDataSource;
import com.mattkula.brownsnews.fragments.ArticleViewPagerFragment;
import com.mattkula.brownsnews.fragments.ScheduleFragment;
import com.mattkula.brownsnews.sources.NewsSourceManager;
import com.mattkula.brownsnews.utils.SimpleAnimatorListener;
import com.mattkula.brownsnews.views.LoadingView;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements NewsSourceManager.OnArticlesDownloadedListener, SwipeRefreshLayout.OnRefreshListener{

    RequestQueue requestQueue;
    ArrayList<Article> articles = new ArrayList<Article>();

    LoadingView loadingView;
    ImageView tutorialArrow;
    ImageView tutorialArrowDown;
    View tutorialView;
    ListView menu;

    ArticleViewPagerFragment viewPagerFragment;
    ArticleViewPagerFragment savedArticleFragment;
    ScheduleFragment scheduleFragment;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    int currentFragmentIndex = 0;
    boolean showNewArticles = true;
    ArticleDataSource dataSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Sentinel-Bold.ttf"));

        if(!Prefs.getValueForKey(this, Prefs.TAG_LATEST_UPDATE, "0").equals(Prefs.VERSION)){
            showUpdateDialog();
            Prefs.setValueForKey(this, Prefs.TAG_LATEST_UPDATE, Prefs.VERSION);
        }

        dataSource = new ArticleDataSource(this);
        dataSource.open();

        // FOR TESTING
//        dataSource.markAllUnread();
        //

        viewPagerFragment = ArticleViewPagerFragment.newInstance(null, true);
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
                view.animate().alpha(0).setListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        view.setVisibility(View.GONE);
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
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        requestQueue = Volley.newRequestQueue(this);
        UpdateManager.rescheduleUpdates(this);

        loadArticles(false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadArticlesIntoFragment();
    }

    private void showUpdateDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("What's new?")
                .setMessage("You can now view the team schedule from the sliding menu!")
                .setPositiveButton("OK", null)
                .create();

        dialog.show();
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
        this.showNewArticles = showLoading;
        if(showLoading){
            viewPagerFragment.fadeOut();
            loadingView.show();
        }
        new NewsSourceManager().getAllArticles(this, this);
    }

    @Override
    public void onArticlesDownloaded() {
        loadArticlesIntoFragment();
        loadingView.dismiss();

        if(Prefs.isFirstTime(this)){
            tutorialView.setVisibility(View.VISIBLE);
            tutorialView.animate().alpha(1);
        }

        Prefs.setValueForKey(this, Prefs.TAG_IS_FIRST_TIME, false);
        if(articles.size() > 0){
            Prefs.setValueForKey(this, Prefs.TAG_UPDATE_LAST_LINK, articles.get(0).link);
        }
    }

    private void loadArticlesIntoFragment(){
        ArrayList<Article> newArticles = dataSource.getAllArticles(0, Prefs.getValueForKey(this, Prefs.TAG_READ_SHOWN, false));

        if (shouldRefreshViewPager(this.articles, newArticles)) {
            this.articles = newArticles;
            if (this.currentFragmentIndex == 0) {// && this.showNewArticles){
                viewPagerFragment.loadArticles(this.articles);
            }
        } else {
            viewPagerFragment.fadeIn();
        }
    }

    // Refresh view pager if the array lists have at least one different article
    private boolean shouldRefreshViewPager(ArrayList<Article> oldArticles, ArrayList<Article> newArticles) {
        if (oldArticles.size() != newArticles.size()) {
            return true;
        }

        for (int i=0, n=newArticles.size(); i < n; i++) {
            if (!oldArticles.get(i).equals(newArticles.get(i))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_refresh:
                loadArticles(true);
                return true;
            case R.id.menu_sources:
                intent = new Intent(this, SelectSourcesActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.menu_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == RESULT_OK){
                if(viewPagerFragment.isVisible()){
                    loadArticlesIntoFragment();
                }
            }
        } else if(requestCode == 2){
            if(resultCode == RESULT_OK){
                if(viewPagerFragment.isVisible()){
                    onArticlesDownloaded();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        loadArticles(true);
    }

    private String[] menuItems = {
            "News",
            "Saved Articles",
            "Schedule",
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
                    viewPagerFragment = ArticleViewPagerFragment.newInstance(MainActivity.this.articles, true);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, viewPagerFragment)
                            .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                            .commit();
                    break;
                case 1:
                    ArrayList<Article> savedArticles = dataSource.getSavedArticles();
                    savedArticleFragment = ArticleViewPagerFragment.newInstance(savedArticles, false);
                    savedArticleFragment.setSwipeRefreshLayoutEnabled(false);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, savedArticleFragment)
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
