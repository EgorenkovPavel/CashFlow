package com.epipasha.cashflow.fragments.listAdapter;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.db.CashFlowDbHelper;


public abstract class RecyclerListFragment extends Fragment {

    private SQLiteDatabase db;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        CashFlowDbHelper dbHelper = new CashFlowDbHelper(getActivity());
        db = dbHelper.getReadableDatabase();

        adapter = getAdapter();

        recyclerView = (RecyclerView) v.findViewById(R.id.main_activity_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(getDividerItemDecoration());
        recyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {

            int position = data.getIntExtra("Position", -1);

            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(position);
        }
    }

    @NonNull
    private DividerItemDecoration getDividerItemDecoration() {
        DividerItemDecoration div = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        div.setDrawable(dividerDrawable);
        return div;
    }

    public Cursor getCursor(){
        return Cursor(db);
    }

    public abstract RecyclerView.Adapter getAdapter();

    public abstract Cursor Cursor(SQLiteDatabase db);
}
