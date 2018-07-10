package com.team.project.wat_show.main_activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.team.project.wat_show.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Menu_myList extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";

    public Menu_myList() {
        // Required empty public constructor
    }

    // 프레그먼트 설정.
    public static Menu_myList newInstance(int columnCount) {

        Menu_myList fragment = new Menu_myList();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_menu_my_list, container, false);
    }

}
