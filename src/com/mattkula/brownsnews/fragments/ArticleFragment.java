package com.mattkula.brownsnews.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    TextView textContent;
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
        textContent = (TextView)v.findViewById(R.id.article_content);

        textTitle.setText(article.title);
        textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(article.link));
                startActivity(i);
            }
        });
        textAuthor.setText("By: " + article.author);

        new AsyncTask<Void, Void, Void>() {
            SpannableString str;
            @Override
            protected Void doInBackground(Void... voids) {
                str = new SpannableString(Html.fromHtml(article.content));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                textContent.setText(str);
            }
        }.execute();

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
}
