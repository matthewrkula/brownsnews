package com.mattkula.brownsnews.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mattkula.brownsnews.R;

public class EmptyFragment extends Fragment {

    public static EmptyFragment newInstance(String param1, String param2) {
        EmptyFragment fragment = new EmptyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

}
