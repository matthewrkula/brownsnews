package com.mattkula.brownsnews;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
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

import com.mattkula.brownsnews.background.UpdateManager;
import com.mattkula.brownsnews.database.Article;
import com.mattkula.brownsnews.database.ArticleDataSource;
import com.mattkula.brownsnews.fragments.ArticleViewPagerFragment;
import com.mattkula.brownsnews.fragments.EmptyFragment;
import com.mattkula.brownsnews.fragments.ScheduleFragment;
import com.mattkula.brownsnews.sources.NewsSourceManager;
import com.mattkula.brownsnews.utils.SimpleAnimatorListener;
import com.mattkula.brownsnews.utils.ViewUtils;
import com.mattkula.brownsnews.views.LoadingView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity implements NewsSourceManager.OnArticlesDownloadedListener, SwipeRefreshLayout.OnRefreshListener{

    ArrayList<Article> articles = new ArrayList<>();

    @InjectView(R.id.loading_view) LoadingView loadingView;
    @InjectView(R.id.tutorial_arrow) ImageView tutorialArrow;
    @InjectView(R.id.tutorial_arrow_pull) ImageView tutorialArrowDown;
    @InjectView(R.id.tutorial_view) View tutorialView;
    @InjectView(R.id.sliding_menu) ListView menu;
    @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    ArticleViewPagerFragment viewPagerFragment;
    ArticleViewPagerFragment savedArticleFragment;
    ScheduleFragment scheduleFragment;
    EmptyFragment emptyFragment;

    int currentFragmentIndex = 0;
    boolean showNewArticles = true;
    boolean isCreated = false;
    ArticleDataSource dataSource;

    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCreated = true;
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        ViewUtils.updateActionBarFont(this);

        showUpdateDialogIfNeeded();

        dataSource = new ArticleDataSource(this);
        dataSource.open();

        // FOR TESTING
//        dataSource.markAllUnread();
        //

        viewPagerFragment = ArticleViewPagerFragment.newInstance(null, true);
        scheduleFragment = new ScheduleFragment();
        emptyFragment = new EmptyFragment();

        showFragment(viewPagerFragment);
        setUpArrowAnimations();

        menu.setAdapter(menuAdapter);
        menu.setOnItemClickListener(menuItemClickListener);

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

        UpdateManager.rescheduleUpdates(this);

        if (Prefs.isFirstTime(this)) {
            dialog = ProgressDialog.show(this, null, "Finding Browns articles for you...");
        }

        loadArticles(false);
    }

    @OnClick(R.id.tutorial_view)
    public void onTutorialViewClick(final View view) {
        view.animate().alpha(0).setListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCreated = false;
        dataSource.close();
    }

    public void showUpdateDialogIfNeeded() {
        if(!Prefs.getValueForKey(this, Prefs.TAG_LATEST_UPDATE, "0").equals(BuildConfig.VERSION_NAME)){
//            AlertDialog dialog = new AlertDialog.Builder(this)
//                    .setTitle("What's new?")
//                    .setMessage("You can now view the team schedule from the sliding menu!")
//                    .setPositiveButton("OK", null)
//                    .create();
//
//            dialog.show();
            Prefs.setValueForKey(this, Prefs.TAG_LATEST_UPDATE, BuildConfig.VERSION_NAME);
        }
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
        if (!isCreated) {
            return;
        }

        loadArticlesIntoFragment();
        loadingView.dismiss();
        if (dialog != null) {
            dialog.hide();
        }

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

    private void showFragment(Fragment fragment) {
        showFragment(fragment, FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    }

    private void showFragment(Fragment fragment, int transition) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setTransition(transition)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private AdapterView.OnItemClickListener menuItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            drawerLayout.closeDrawer(Gravity.LEFT);

            if(currentFragmentIndex == i) return;
            currentFragmentIndex = i;

            switch (i){
                case 0:
                    viewPagerFragment = ArticleViewPagerFragment.newInstance(MainActivity.this.articles, true);
                    showFragment(viewPagerFragment, FragmentTransaction.TRANSIT_ENTER_MASK);
                    break;
                case 1:
                    ArrayList<Article> savedArticles = dataSource.getSavedArticles();
                    savedArticleFragment = ArticleViewPagerFragment.newInstance(savedArticles, false);
                    savedArticleFragment.setSwipeRefreshLayoutEnabled(false);
                    showFragment(savedArticleFragment, FragmentTransaction.TRANSIT_ENTER_MASK);
                    break;
                case 2:
//                    scheduleFragment = new ScheduleFragment();
                    showFragment(emptyFragment, FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    break;
            }

        }
    };
}
