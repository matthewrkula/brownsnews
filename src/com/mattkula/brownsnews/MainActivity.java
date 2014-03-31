package com.mattkula.brownsnews;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mattkula.brownsnews.data.Article;
import com.mattkula.brownsnews.fragments.ArticleViewPagerFragment;
import com.mattkula.brownsnews.fragments.ScheduleFragment;
import com.mattkula.brownsnews.sources.NewsSourceManager;
import com.mattkula.brownsnews.fragments.ArticleFragment;
import com.mattkula.brownsnews.views.LoadingView;

import java.util.ArrayList;
import java.util.zip.Inflater;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Sentinel-Bold.ttf"));

        viewPagerFragment = ArticleViewPagerFragment.newInstance(null);
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

        loadArticles();
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

    public void loadArticles() {
        viewPagerFragment.fadeOut();
        loadingView.show();
        new NewsSourceManager().getAllArticles(this, this);
    }

    @Override
    public void onArticlesDownloaded(ArrayList<Article> articles) {
        this.articles = articles;
        loadingView.dismiss();
        refreshViewPager();

        if(Prefs.isFirstTime(this)){
            tutorialView.setVisibility(View.VISIBLE);
            tutorialView.animate().alpha(1);
        }

        Prefs.setValueForKey(this, Prefs.TAG_IS_FIRST_TIME, false);
    }

    private void refreshViewPager(){
        viewPagerFragment.loadArticles(this.articles);
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
            case R.id.menu_settings:
                viewPagerFragment.fadeOut();
                Intent intent = new Intent(this, SelectSourcesActivity.class);
                startActivityForResult(intent, 1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                if(viewPagerFragment.isVisible()){
                    loadArticles();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        loadArticles();
    }

    private String[] menuItems = new String[]{
            "News",
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
