package com.epipasha.cashflow.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epipasha.cashflow.R;

import java.util.ArrayList;

public abstract class ListFragment<T> extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<T> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.main_activity_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration div = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        div.setDrawable(dividerDrawable);
        mRecyclerView.addItemDecoration(div);

        list = getList();

        // specify an adapter (see also next example)
        mAdapter = getAdapter(this, list);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            T inst = (T) data.getSerializableExtra("Instance");
            int position = data.getIntExtra("Position",-1);

            if (position == -1) {
                list.add(inst);
                position = list.size() - 1;
            } else {
                list.set(position, inst);
            }

            mAdapter.notifyDataSetChanged();

            mRecyclerView.scrollToPosition(position);
        }
    }

    public abstract void addInstance();

    protected abstract RecyclerView.Adapter getAdapter(ListFragment fragment, ArrayList<T> list);

    protected abstract ArrayList<T> getList();
}
