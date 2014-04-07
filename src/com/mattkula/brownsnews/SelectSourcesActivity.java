package com.mattkula.brownsnews;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.widget.*;
import com.mattkula.brownsnews.sources.NewsSource;
import com.mattkula.brownsnews.sources.NewsSourceManager;

/**
 * Created by matt on 3/30/14.
 */
public class SelectSourcesActivity extends FragmentActivity {

    GridView gridView;

    NewsSource[] sources = NewsSourceManager.sources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView yourTextView = (TextView) findViewById(titleId);
        yourTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Sentinel-Bold.ttf"));
        getActionBar().setTitle("Sources");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_sources);

        gridView = (GridView)findViewById(R.id.settings_gridlayout);
        gridView.setOnItemClickListener(clickListener);
        gridView.setNumColumns(3);
        gridView.setAdapter(menuAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
            case R.id.select_done:
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(0, 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.select_sources, menu);
        return true;
    }

    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            NewsSource source = sources[i];
            boolean isSelected = Prefs.isNewsSourceSelected(SelectSourcesActivity.this, source);
//            view.animate().alpha(isSelected ? .3f : 1f);
            view.setAlpha(isSelected ? .3f : 1f);
            Prefs.setValueForKey(SelectSourcesActivity.this, source.getName(), !isSelected);
        }
    };

    private BaseAdapter menuAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return sources.length;
        }

        @Override
        public Object getItem(int i) {
            return sources[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            if(v == null){
                v = View.inflate(getApplicationContext(), R.layout.grid_item_news_source, null);
            }
            NewsSource s = (NewsSource)getItem(i);

            ((TextView)v.findViewById(R.id.grid_text)).setText(s.getName());

            ImageView bg = (ImageView)v.findViewById(R.id.grid_image);
            bg.setImageDrawable(getResources().getDrawable(s.getImageId()));

            if(!Prefs.isNewsSourceSelected(getApplicationContext(), s)){
                v.setAlpha(0.3f);
            } else {
                v.setAlpha(1f);
            }

            return v;
        }
    };

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
        super.onBackPressed();
    }
}
