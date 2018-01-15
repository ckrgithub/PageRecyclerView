package com.ckr.pagesnaphelper.view;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ckr.pagesnaphelper.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OneFragment extends Fragment {
    public static final String TEXT = "text";
    public static final String COLOR = "color";
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    private Unbinder unbinder;
    private String text;
    private int color;
    private View view;

    public static OneFragment newInstance(String text,int color) {

        Bundle args = new Bundle();
        args.putString(TEXT, text);
        args.putInt(COLOR, color);
        OneFragment fragment = new OneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        if (arguments != null) {
            text = arguments.getString(TEXT, "");
            color = arguments.getInt(COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_one, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView.setText(text);
        relativeLayout.setBackgroundColor(color);
    }
}
