package com.mattkula.brownsnews.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.data.Article;
import com.mattkula.brownsnews.views.NotifyingScrollView;
import com.squareup.picasso.Picasso;


/**
 * Created by matt on 3/27/14.
 */
public class ArticleFragment extends Fragment {

    Article article;
    ImageView articleImage;
    TextView textTitle;
    TextView textAuthor;
    WebView textContent;
    NotifyingScrollView scrollView;

    public static ArticleFragment newInstance(Article article) {
        ArticleFragment myFragment = new ArticleFragment();

        Bundle args = new Bundle();
        args.putSerializable("article", article);
        myFragment.setArguments(args);

        return myFragment;
    }

    int top = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_article, container, false);

        this.article = (Article)getArguments().getSerializable("article");

        articleImage = (ImageView)v.findViewById(R.id.article_image);
        textTitle = (TextView)v.findViewById(R.id.article_title);
        textAuthor = (TextView)v.findViewById(R.id.article_author);
        textContent = (WebView)v.findViewById(R.id.article_content);

        textTitle.setText(article.title);
        textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(article.link));
                startActivity(i);
            }
        });
        textAuthor.setText("By: " + article.author);

        textContent.setBackgroundColor(Color.argb(1, 0, 0, 0));
        textContent.setBackgroundResource(R.color.browns_brown);
        textContent.loadData(getStyle(article.content), "text/html", "utf-8");
        textContent.animate().alpha(1).setDuration(300);

//        new AsyncTask<Void, Void, Void>() {
//            SpannableString str;
//            @Override
//            protected Void doInBackground(Void... voids) {
//                str = new SpannableString(Html.fromHtml(article.content));
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                textContent.setText(str);
//            }
//        }.execute();

        if(!article.imageUrl.equals("none")){
            Picasso.with(getActivity())
                    .load(article.imageUrl)
                    .fit()
                    .centerCrop()
                    .into(articleImage);
        } else {
            articleImage.setImageDrawable((getResources().getDrawable(R.drawable.browns_dog)));
            articleImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        scrollView = (NotifyingScrollView)v.findViewById(R.id.scrollview);

        return v;
    }


    private String getStyle(String content){
        String text = "<html><head>"
                + "<style type=\"text/css\">body{color: #fff; font-family: 'Serif'}"
                + "</style></head>"
                + "<body>"
                + content.replaceAll("'", "&#39;")
                    .replaceAll("-", "&#8211;")
                    .replaceAll("—", "&#8212")
                    .replaceAll("–", "&#8212")
                    .replaceAll("’", "&#8217")
                    .replaceAll("‘", "&#8216")
                    .replaceAll("…", "&#133")
                + "</body></html>";
        Log.e("ASDF", text);
        return text;
    }
}
