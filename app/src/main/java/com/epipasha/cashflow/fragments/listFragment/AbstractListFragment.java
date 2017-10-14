package com.epipasha.cashflow.fragments.listFragment;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epipasha.cashflow.R;

import java.util.HashMap;

public abstract class AbstractListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private Adapter mAdapter;

    abstract int getLayout();

    abstract HashMap<Integer, View> findViewHolder(View v);

    abstract int getIdFromCursor(Cursor cursor);

    abstract void loadDataFromCursor(HashMap<Integer, View> views, Cursor cursor);

    abstract String getQuery();

    public abstract void openDialog(int id);

    public void refreshList(){
        getLoaderManager().restartLoader(LOADER_ID, null, this).forceLoad();
    }

    public AbstractListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration div = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        div.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(div);

        mAdapter = new Adapter();
        recyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new DbListLoader(getActivity(), getQuery());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private Cursor cursor;

        public void setCursor(Cursor cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(getLayout(), parent, false);
            // set the view's size, margins, paddings and layout parameters
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            cursor.moveToPosition(position);
            holder.loadData(cursor);
        }

        @Override
        public int getItemCount() {
            if (cursor == null) {
                return 0;
            } else {
                return cursor.getCount();
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        int id;
        final HashMap<Integer, View> views;

        public ViewHolder(View v) {
            super(v);
            views = findViewHolder(v);

            v.setOnClickListener(this);
        }

        public void loadData(Cursor cursor) {
            id = getIdFromCursor(cursor);
            loadDataFromCursor(views, cursor);
        }

        @Override
        public void onClick(View v) {
            openDialog(id);
        }
    }


}
