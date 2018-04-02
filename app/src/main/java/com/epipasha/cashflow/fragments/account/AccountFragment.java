package com.epipasha.cashflow.fragments.account;

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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.CashFlowContract.AccountEntry;

import java.util.Locale;

public class AccountFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TASK_LOADER_ID = 0;

    private AccountAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

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

                //TODO Сделать возможность удаления с проверкой целостности базы
                Toast.makeText(getContext(), "Don't work", Toast.LENGTH_LONG).show();

                /*
                // Retrieve the id of the task to delete
                int id = (int) viewHolder.itemView.getTag();

                // Build appropriate uri with String row id appended
                String stringId = Integer.toString(id);
                Uri uri = AccountEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // COMPLETED (2) Delete a single row of data using a ContentResolver
                getContext().getContentResolver().delete(uri, null, null);

*/
                // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
                getLoaderManager().restartLoader(TASK_LOADER_ID, null, AccountFragment.this);

            }
        }).attachToRecyclerView(recyclerView);

        mAdapter = new AccountAdapter(getActivity());

        recyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // re-queries for all tasks
        getLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                AccountEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    class AccountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int HEADER_ITEM = 234;
        private static final int LIST_ITEM = 897;

        private Cursor mCursor;
        private Context mContext;

        public AccountAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            switch (viewType) {

                case HEADER_ITEM: {
                    View view = LayoutInflater.from(mContext)
                            .inflate(R.layout.list_item_account_header, parent, false);

                    return new HeaderHolder(view);
                }

                case LIST_ITEM: {
                    View view = LayoutInflater.from(mContext)
                            .inflate(R.layout.list_item_account, parent, false);

                    return new AccountHolder(view);
                }

                default:
                    throw new IllegalArgumentException("Invalid view type, value of " + viewType);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            int idIndex = mCursor.getColumnIndex(AccountEntry._ID);
            int titleIndex = mCursor.getColumnIndex(AccountEntry.COLUMN_TITLE);
            int sumIndex = mCursor.getColumnIndex(AccountEntry.SERVICE_COLUMN_SUM);

            if (holder instanceof HeaderHolder) {

                mCursor.moveToFirst();
                int sum = 0;
                while (!mCursor.isAfterLast()){
                    sum += mCursor.getInt(sumIndex);
                    mCursor.moveToNext();
                }

                ((HeaderHolder)holder).accountTitleView.setText(getString(R.string.sum));
                ((HeaderHolder)holder).accountSumView.setText(String.format(Locale.getDefault(), "%,d", sum));

            } else if (holder instanceof AccountHolder) {

                mCursor.moveToPosition(position -1); // get to the right location in the cursor

                // Determine the values of the wanted data
                final int id = mCursor.getInt(idIndex);
                String title = mCursor.getString(titleIndex);
                int sum = mCursor.getInt(sumIndex);

                //Set values
                holder.itemView.setTag(id);
                ((AccountHolder)holder).accountTitleView.setText(title);
                ((AccountHolder)holder).accountSumView.setText(String.format(Locale.getDefault(), "%,d", sum));
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0){
                return HEADER_ITEM;
            } else {
                return LIST_ITEM;
            }
        }

        @Override
        public int getItemCount() {
            if (mCursor == null) {
                return 0;
            }
            return mCursor.getCount() + 1;
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
            return temp;
        }

        class AccountHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView accountTitleView;
            TextView accountSumView;

            public AccountHolder(View itemView) {
                super(itemView);
                accountTitleView = (TextView) itemView.findViewById(R.id.account_list_item_name);
                accountSumView = (TextView) itemView.findViewById(R.id.account_list_item_sum);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);

                int idIndex = mCursor.getColumnIndex(AccountEntry._ID);
                int id = mCursor.getInt(idIndex);

                Intent i = new Intent(mContext, DetailAccountActivity.class);

                Uri uri = AccountEntry.buildAccountUriWithId(id);
                i.setData(uri);
                startActivity(i);
            }
        }

        class HeaderHolder extends RecyclerView.ViewHolder{
            TextView accountTitleView;
            TextView accountSumView;

            public HeaderHolder(View itemView) {
                super(itemView);
                accountTitleView = (TextView) itemView.findViewById(R.id.account_list_item_name);
                accountSumView = (TextView) itemView.findViewById(R.id.account_list_item_sum);
            }

        }
    }
}

