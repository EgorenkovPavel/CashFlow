package com.epipasha.cashflow.fragments.operation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.epipasha.cashflow.objects.OperationType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class OperationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACCOUNT_LOADER_ID = 1;
    private static final int CATEGORY_LOADER_ID = 2;
    private static final int OPERATION_LOADER_ID = 3;

    private OperationAdapter mAdapter;
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

    class OperationAdapter extends RecyclerView.Adapter<OperationAdapter.OperationHolder>{

        private Cursor mCursor;
        private Context mContext;
        private int maxPosition = 0;

        public OperationAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public OperationAdapter.OperationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item_operation, parent, false);

            return new OperationAdapter.OperationHolder(view);
        }

        @Override
        public void onBindViewHolder(OperationAdapter.OperationHolder holder, int position) {

            int idIndex = mCursor.getColumnIndex(OperationEntry._ID);
            int dateIndex = mCursor.getColumnIndex(OperationEntry.COLUMN_DATE);
            int accountIndex = mCursor.getColumnIndex(OperationEntry.SERVICE_COLUMN_ACCOUNT_TITLE);
            int categoryIndex = mCursor.getColumnIndex(OperationEntry.SERVICE_COLUMN_CATEGORY_TITLE);
            int repAccountIndex = mCursor.getColumnIndex(OperationEntry.SERVICE_COLUMN_RECIPIENT_ACCOUNT_TITLE);
            int typeIndex = mCursor.getColumnIndex(OperationEntry.COLUMN_TYPE);
            int sumIndex = mCursor.getColumnIndex(OperationEntry.COLUMN_SUM);

            mCursor.moveToPosition(position); // get to the right location in the cursor

            // Determine the values of the wanted data
            final int id = mCursor.getInt(idIndex);
            Date date = new Date(mCursor.getLong(dateIndex));
            String account = mCursor.getString(accountIndex);
            String category = mCursor.getString(categoryIndex);
            String repAccount = mCursor.getString(repAccountIndex);
            OperationType type = OperationType.toEnum(mCursor.getInt(typeIndex));
            int sum = mCursor.getInt(sumIndex);

            //Set values
            holder.itemView.setTag(id);

            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            holder.operationDateView.setText(format.format(date));

            holder.operationAccountView.setText(account);
            holder.operationSumView.setText(String.format(Locale.getDefault(),"%,d",sum));

            switch (type){
                case IN:{
                    holder.operationCategoryView.setText(category);
                    holder.operationTypeImageView.setImageResource(R.mipmap.operation_type_in);
                    break;
                }
                case OUT:{
                    holder.operationCategoryView.setText(category);
                    holder.operationTypeImageView.setImageResource(R.mipmap.operation_type_out);
                    break;
                }
                case TRANSFER:{
                    holder.operationCategoryView.setText(repAccount);
                    holder.operationTypeImageView.setImageResource(R.mipmap.operation_type_transfer);
                    break;
                }
            }

            if (position+1 == maxPosition){
                incMaxPosition();
            }

        }

        private void incMaxPosition(){
            int delta = 50;
            if(mCursor == null){
                maxPosition = 0;
            }else{
                maxPosition = Math.min(maxPosition + delta, mCursor.getCount());
            }
        }

        @Override
        public int getItemCount() {
            if (mCursor == null) {
                return 0;
            }
            return maxPosition;//mCursor.getCount();
        }

        public Cursor swapCursor(Cursor c) {
            // check if this cursor is the same as the previous cursor (mCursor)
            if (mCursor == c) {
                return null; // bc nothing has changed
            }
            Cursor temp = mCursor;
            this.mCursor = c; // new cursor value assigned

            //check if this is a valid cursor, then update the cursor
            if (c != null) {
                this.notifyDataSetChanged();
            }

            incMaxPosition();

            return temp;
        }

        class OperationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView operationDateView;
            TextView operationAccountView;
            TextView operationCategoryView;
            TextView operationSumView;
            ImageView operationTypeImageView;

            public OperationHolder(View itemView) {
                super(itemView);
                operationDateView = (TextView) itemView.findViewById(R.id.operation_list_item_date);
                operationAccountView = (TextView) itemView.findViewById(R.id.operation_list_item_account);
                operationCategoryView = (TextView) itemView.findViewById(R.id.operation_list_item_category);
                operationSumView = (TextView) itemView.findViewById(R.id.operation_list_item_sum);
                operationTypeImageView = (ImageView) itemView.findViewById(R.id.operation_list_item_type);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);

                int idIndex = mCursor.getColumnIndex(CashFlowContract.OperationEntry._ID);
                int id = mCursor.getInt(idIndex);

                Intent i = new Intent(mContext, DetailOperationActivity.class);

                Uri uri = CashFlowContract.OperationEntry.buildOperationUriWithId(id);
                i.setData(uri);
                startActivity(i);
            }
        }
    }
}