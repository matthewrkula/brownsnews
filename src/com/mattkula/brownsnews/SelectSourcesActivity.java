package com.mattkula.brownsnews;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.mattkula.brownsnews.sources.NewsSource;
import com.mattkula.brownsnews.sources.NewsSourceManager;

/**
 * Created by matt on 3/30/14.
 */
public class SelectSourcesActivity extends FragmentActivity {

    ListView listView;
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
        setContentView(R.layout.activity_settings);
        listView = (ListView)findViewById(R.id.settings_listview);
        listView.setOnItemClickListener(clickListener);
        listView.setAdapter(menuAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
            case R.id.select_done:
                setResult(RESULT_OK);
                finish();
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
            Prefs.setValueForKey(SelectSourcesActivity.this, source.getName(),
                    !Prefs.isNewsSourceSelected(SelectSourcesActivity.this, source));
            listView.setAdapter(menuAdapter);
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
                v = View.inflate(getApplicationContext(), R.layout.menu_item_sliding_menu, null);
            }
            NewsSource s = (NewsSource)getItem(i);

            ((TextView)v.findViewById(R.id.item_name)).setText(s.getName());

            ImageView checkbox = (ImageView)v.findViewById(R.id.item_check);
            if(Prefs.isNewsSourceSelected(getApplicationContext(), s)){
                checkbox.setVisibility(View.VISIBLE);
            }

            return v;
        }
    };
}
