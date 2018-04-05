package com.epipasha.cashflow.fragments.category;

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
import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Calendar;
import java.util.Locale;

public class CategoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TASK_LOADER_ID = 0;

    private CategoryAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        recyclerView.setHasFixedSize(true);

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
                getLoaderManager().restartLoader(TASK_LOADER_ID, null, CategoryFragment.this);

            }
        }).attachToRecyclerView(recyclerView);

        mAdapter = new CategoryAdapter(getActivity());

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
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        return new CursorLoader(
                getActivity(),
                CashFlowContract.CategoryEntry.buildCategoryCostUri(year, month),
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

    class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int HEADER_ITEM = 234;
        private static final int LIST_ITEM = 897;
        private Cursor mCursor;
        private Context mContext;

        public CategoryAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            switch (viewType) {

                case HEADER_ITEM: {
                    View view = LayoutInflater.from(mContext)
                            .inflate(R.layout.list_item_category_header, parent, false);

                    return new HeaderHolder(view);
                }

                case LIST_ITEM: {
                    View view = LayoutInflater.from(mContext)
                            .inflate(R.layout.list_item_category, parent, false);

                    return new CategoryHolder(view);
                }

                default:
                    throw new IllegalArgumentException("Invalid view type, value of " + viewType);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            int idIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry._ID);
            int titleIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_TITLE);
            int typeIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_TYPE);
            int budgetIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_BUDGET);
            int factIndex = mCursor.getColumnIndex(CashFlowContract.CategoryCostEntry.COLUMN_SUM);

            if (holder instanceof HeaderHolder){

                int inBudget = 0;
                int inFact = 0;
                int inDelta = 0;
                int outBudget = 0;
                int outFact = 0;
                int outDelta = 0;

                mCursor.moveToFirst();
                while (!mCursor.isAfterLast()){
                    OperationType type = OperationType.toEnum(mCursor.getInt(typeIndex));
                    switch (type){
                        case IN:{
                            inBudget += mCursor.getInt(budgetIndex);
                            inFact += mCursor.getInt(factIndex);
                            break;
                        }
                        case OUT:{
                            outBudget += mCursor.getInt(budgetIndex);
                            outFact += mCursor.getInt(factIndex);
                            break;
                        }
                    }
                    mCursor.moveToNext();
                }

                inDelta = inFact - inBudget;
                outDelta = outBudget - outFact;

                ((HeaderHolder)holder).tvInBudget.setText(String.format(Locale.getDefault(), "%,d", inBudget));
                ((HeaderHolder)holder).tvInFact.setText(String.format(Locale.getDefault(), "%,d", inFact));
                ((HeaderHolder)holder).tvInDelta.setText(String.format(Locale.getDefault(), "%,d", inDelta));

                ((HeaderHolder)holder).tvOutBudget.setText(String.format(Locale.getDefault(), "%,d", outBudget));
                ((HeaderHolder)holder).tvOutFact.setText(String.format(Locale.getDefault(), "%,d", outFact));
                ((HeaderHolder)holder).tvOutDelta.setText(String.format(Locale.getDefault(), "%,d", outDelta));

                ((HeaderHolder)holder).tvCashflow.setText(String.format(Locale.getDefault(), "%,d", inFact - outFact));

            }else if (holder instanceof CategoryHolder) {

                mCursor.moveToPosition(position-1); // get to the right location in the cursor

                // Determine the values of the wanted data
                final int id = mCursor.getInt(idIndex);
                String title = mCursor.getString(titleIndex);
                OperationType type = OperationType.toEnum(mCursor.getInt(typeIndex));
                int budget = mCursor.getInt(budgetIndex);
                int fact = mCursor.getInt(factIndex);

                int delta = 0;
                if (type.equals(OperationType.IN)) {
                    delta = fact - budget;
                } else if (type.equals(OperationType.OUT)) {
                    delta = budget - fact;
                }

                //Set values
                holder.itemView.setTag(id);
                ((CategoryHolder)holder).categoryTitleView.setText(title);
                ((CategoryHolder)holder).categoryBudgetView.setText(String.format(Locale.getDefault(), "%,d", budget));
                ((CategoryHolder)holder).categoryFactView.setText(String.format(Locale.getDefault(), "%,d", fact));
                ((CategoryHolder)holder).categoryDeltaView.setText(String.format(Locale.getDefault(), "%,d", delta));

                int deltaColor = R.color.primaryTextColor;
                if (type.equals(OperationType.IN)) {
                    deltaColor = delta >= 0 ? R.color.colorPrimaryDark : R.color.colorAccentDark;
                } else if (type.equals(OperationType.OUT)) {
                    deltaColor = delta >= 0 ? R.color.colorPrimaryDark : R.color.colorAccentDark;
                }

                ((CategoryHolder)holder).categoryDeltaView.setTextColor(getResources().getColor(deltaColor));
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
            if (mCursor == null || mCursor.getCount() == 0) {
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

        class HeaderHolder extends RecyclerView.ViewHolder{

            TextView tvInBudget, tvInFact, tvInDelta;
            TextView tvOutBudget, tvOutFact, tvOutDelta;
            TextView tvCashflow;

            public HeaderHolder(View itemView) {
                super(itemView);

                tvInBudget = (TextView) itemView.findViewById(R.id.tvInBudget);
                tvInFact = (TextView) itemView.findViewById(R.id.tvInFact);
                tvInDelta = (TextView) itemView.findViewById(R.id.tvInDelta);

                tvOutBudget = (TextView) itemView.findViewById(R.id.tvOutBudget);
                tvOutFact = (TextView) itemView.findViewById(R.id.tvOutFact);
                tvOutDelta = (TextView) itemView.findViewById(R.id.tvOutDelta);

                tvCashflow = (TextView) itemView.findViewById(R.id.tvCashflow);
            }

        }

        class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView categoryTitleView;
            TextView categoryBudgetView;
            TextView categoryFactView;
            TextView categoryDeltaView;

            public CategoryHolder(View itemView) {
                super(itemView);
                categoryTitleView = (TextView) itemView.findViewById(R.id.lblIn);
                categoryBudgetView = (TextView) itemView.findViewById(R.id.tvInBudget);
                categoryFactView = (TextView) itemView.findViewById(R.id.tvFact);
                categoryDeltaView = (TextView) itemView.findViewById(R.id.tvInDelta);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);

                int idIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry._ID);
                int id = mCursor.getInt(idIndex);

                Intent i = new Intent(mContext, DetailCategoryActivity.class);

                Uri uri = CashFlowContract.CategoryEntry.buildCategoryUriWithId(id);
                i.setData(uri);
                startActivity(i);
            }
        }
    }
}
