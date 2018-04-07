package com.epipasha.cashflow.fragments.operation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.data.CashFlowContract.AccountEntry;
import com.epipasha.cashflow.data.CashFlowContract.CategoryEntry;
import com.epipasha.cashflow.data.CashFlowContract.OperationEntry;
import com.epipasha.cashflow.fragments.Adapter;
import com.epipasha.cashflow.objects.OperationType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class OperationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACCOUNT_LOADER_ID = 1;
    private static final int CATEGORY_LOADER_ID = 2;
    private static final int OPERATION_LOADER_ID = 3;

    private Adapter mAdapter;
    private RecyclerView recyclerView;
    private ProgressBar loadingIndicator;
    private TextView tvEmptyList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);
        loadingIndicator = (ProgressBar)v.findViewById(R.id.pb_loading_indicator);
        tvEmptyList = (TextView)v.findViewById(R.id.tv_empty);
        tvEmptyList.setText(getString(R.string.empty_list_operations));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                // Retrieve the id of the task to delete
                int id = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = OperationEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // COMPLETED (2) Delete a single row of data using a ContentResolver
                getContext().getContentResolver().delete(uri, null, null);

                // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
                getLoaderManager().restartLoader(OPERATION_LOADER_ID, null, OperationFragment.this);

            }
        }).attachToRecyclerView(recyclerView);

        mAdapter = new OperationAdapter(getActivity());

        recyclerView.setAdapter(mAdapter);

        showLoading();

        getLoaderManager().restartLoader(OPERATION_LOADER_ID, null, this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // re-queries for all tasks
//        getLoaderManager().restartLoader(ACCOUNT_LOADER_ID, null, this);
//        getLoaderManager().restartLoader(CATEGORY_LOADER_ID, null, this);
//        getLoaderManager().restartLoader(OPERATION_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case ACCOUNT_LOADER_ID:
                return new CursorLoader(
                        getActivity(),
                        AccountEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            case CATEGORY_LOADER_ID:
                return new CursorLoader(
                        getActivity(),
                        CategoryEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            case OPERATION_LOADER_ID:
                return new CursorLoader(
                        getActivity(),
                        OperationEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        if(cursor.getCount() > 0) {
            showList();
        }else{
            showEmpty();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        showEmpty();
    }

    private void showLoading(){
        recyclerView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
        tvEmptyList.setVisibility(View.INVISIBLE);
    }

    private void showList(){
        recyclerView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.INVISIBLE);
        tvEmptyList.setVisibility(View.INVISIBLE);
    }

    private void showEmpty(){
        recyclerView.setVisibility(View.INVISIBLE);
        loadingIndicator.setVisibility(View.INVISIBLE);
        tvEmptyList.setVisibility(View.VISIBLE);
    }

}