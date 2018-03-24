package com.epipasha.cashflow.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.fragments.account.DetailAccountActivity;
import com.epipasha.cashflow.fragments.category.DetailCategoryActivity;
import com.epipasha.cashflow.objects.OperationType;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SummaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACCOUNT_LOADER_ID = 432;
    private static final int CATEGORY_COST_LOADER_ID = 343;

    private AccountAdapter mAccountAdapter;
    private CategoryAdapter mCategoryAdapter;

    private RecyclerView rvAccounts, rvCategories;
    private HorizontalBarChart mChart;
    private TextView tvSum;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_summary, container, false);

        mChart = (HorizontalBarChart) v.findViewById(R.id.chart);
        tvSum = (TextView)v.findViewById(R.id.tvSum);

        rvAccounts = (RecyclerView)v.findViewById(R.id.rvAccounts);
        rvCategories = (RecyclerView)v.findViewById(R.id.rvCategories);

        rvAccounts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        //rvCategories.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvCategories.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        rvAccounts.setHasFixedSize(true);
        rvCategories.setHasFixedSize(true);

        mAccountAdapter = new AccountAdapter(getActivity());
        rvAccounts.setAdapter(mAccountAdapter);

        mCategoryAdapter = new CategoryAdapter(getActivity());
        rvCategories.setAdapter(mCategoryAdapter);

        getLoaderManager().initLoader(ACCOUNT_LOADER_ID, null, this);
        getLoaderManager().initLoader(CATEGORY_COST_LOADER_ID, null, this);

        initChart();

        return v;
    }

    private void initChart() {

        mChart.setTouchEnabled(false);
        mChart.getDescription().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(daysInMonth);
        leftAxis.setDrawLabels(true); // no axis labels
        leftAxis.setDrawAxisLine(true); // no axis line
        leftAxis.setDrawGridLines(true); // no grid lines
        leftAxis.setDrawZeroLine(true); // draw a zero line
        leftAxis.setGranularity(1f);

        mChart.getAxisRight().setEnabled(false); // no right axis

        LimitLine ll = new LimitLine(day);
        ll.setLineColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        ll.setLineWidth(1f);
        leftAxis.addLimitLine(ll);

        LimitLine l2 = new LimitLine(100f, "");
        l2.setLineColor(Color.BLACK);
        l2.setLineWidth(1f);
        leftAxis.addLimitLine(l2);

        Legend legend = mChart.getLegend();
        legend.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // re-queries for all tasks
        getLoaderManager().restartLoader(ACCOUNT_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case ACCOUNT_LOADER_ID:
                return new CursorLoader(
                        getActivity(),
                        CashFlowContract.AccountEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            case CATEGORY_COST_LOADER_ID: {
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
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()){
            case ACCOUNT_LOADER_ID:{
                mAccountAdapter.swapCursor(cursor);
                if(cursor!=null){
                    int sum = 0;
                    while (cursor.moveToNext()){
                        sum += cursor.getInt(cursor.getColumnIndex(CashFlowContract.AccountEntry.SERVICE_COLUMN_SUM));
                    }
                    tvSum.setText(String.format(Locale.getDefault(),"%,d",sum));
                }

                break;
            }
            case CATEGORY_COST_LOADER_ID:{
                mCategoryAdapter.swapCursor(cursor);
                loadChart(cursor);
                break;
            }
        }
    }

    private void loadChart(Cursor cursor) {

        if(cursor == null ){
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int i = 0;
        while (cursor.moveToNext()){
            String label = cursor.getString(cursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_TITLE));
            int budget = cursor.getInt(cursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_BUDGET));
            int cost = cursor.getInt(cursor.getColumnIndex(CashFlowContract.CategoryCostEntry.COLUMN_SUM));

            long dayCost = budget == 0 ? daysInMonth : cost/(budget/daysInMonth);

            labels.add(label);
            entries.add(new BarEntry(i, dayCost));
            i++;
        }

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColors(new int[]{R.color.colorPrimary}, getActivity());

        BarData data = new BarData(set);
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return "";
            }
        });

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        data.setBarWidth(0.9f); // set custom bar width
        mChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, entries.size() * 100));
        mChart.setData(data);
        mChart.setFitBars(true); // make the x-axis fit exactly all bars
        mChart.invalidate(); // refresh

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()){
            case ACCOUNT_LOADER_ID:{
                mAccountAdapter.swapCursor(null);
                break;
            }
            case CATEGORY_COST_LOADER_ID:{
                mCategoryAdapter.swapCursor(null);
                break;
            }
        }
    }

    class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountHolder>{

        private Cursor mCursor;
        private Context mContext;

        public AccountAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public AccountAdapter.AccountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.fragment_summary_account_item, parent, false);

            return new AccountAdapter.AccountHolder(view);
        }

        @Override
        public void onBindViewHolder(AccountAdapter.AccountHolder holder, int position) {

            int idIndex = mCursor.getColumnIndex(CashFlowContract.AccountEntry._ID);
            int titleIndex = mCursor.getColumnIndex(CashFlowContract.AccountEntry.COLUMN_TITLE);
            int sumIndex = mCursor.getColumnIndex(CashFlowContract.AccountEntry.SERVICE_COLUMN_SUM);

            mCursor.moveToPosition(position); // get to the right location in the cursor

            // Determine the values of the wanted data
            final int id = mCursor.getInt(idIndex);
            String title = mCursor.getString(titleIndex);
            int sum = mCursor.getInt(sumIndex);

            //Set values
            holder.itemView.setTag(id);
            holder.accountTitleView.setText(title);
            holder.accountSumView.setText(String.format(Locale.getDefault(),"%,d",sum));
        }

        @Override
        public int getItemCount() {
            if (mCursor == null) {
                return 0;
            }
            return mCursor.getCount();
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
                accountTitleView = (TextView) itemView.findViewById(R.id.tvAccountName);
                accountSumView = (TextView) itemView.findViewById(R.id.tvAccountBalance);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int adapterPosition = getAdapterPosition();
                mCursor.moveToPosition(adapterPosition);

                int idIndex = mCursor.getColumnIndex(CashFlowContract.AccountEntry._ID);
                int id = mCursor.getInt(idIndex);

                Intent i = new Intent(mContext, DetailAccountActivity.class);

                Uri uri = CashFlowContract.AccountEntry.buildAccountUriWithId(id);
                i.setData(uri);
                startActivity(i);
            }
        }
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder>{

        private Cursor mCursor;
        private Context mContext;

        public CategoryAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public CategoryAdapter.CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.fragment_budget, parent, false);

            return new CategoryAdapter.CategoryHolder(view);
        }

        @Override
        public void onBindViewHolder(CategoryAdapter.CategoryHolder holder, int position) {

            int idIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry._ID);
            int titleIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_TITLE);
            int typeIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_TYPE);
            int budgetIndex = mCursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_BUDGET);
            int factIndex = mCursor.getColumnIndex(CashFlowContract.CategoryCostEntry.COLUMN_SUM);

            mCursor.moveToPosition(position); // get to the right location in the cursor

            // Determine the values of the wanted data
            final int id = mCursor.getInt(idIndex);
            String title = mCursor.getString(titleIndex);
            OperationType type = OperationType.toEnum(mCursor.getInt(typeIndex));
            int budget = mCursor.getInt(budgetIndex);
            int fact = mCursor.getInt(factIndex);

            int delta = 0;
            if(type.equals(OperationType.IN)) {
                delta = fact - budget;
            }else if (type.equals(OperationType.OUT)) {
                delta = budget - fact;
            }

                //Set values
            holder.itemView.setTag(id);
            holder.categoryTitleView.setText(title);
            holder.categoryBudgetView.setText(String.format(Locale.getDefault(),"%,d",budget));
            holder.categoryFactView.setText(String.format(Locale.getDefault(),"%,d",fact));
            holder.categoryDeltaView.setText(String.format(Locale.getDefault(),"%,d",delta));

            holder.progressView.setMax(budget);
            holder.progressView.setProgress(fact);
            holder.progressView.setIndeterminate(false);

            int titleColor = R.color.primaryTextColor;
            int deltaColor = R.color.primaryTextColor;
            if(type.equals(OperationType.IN)){
                titleColor = R.color.colorPrimaryDark;
                deltaColor = delta >=0 ? R.color.colorPrimaryDark : R.color.colorAccentDark;
            }else if (type.equals(OperationType.OUT)){
                titleColor = R.color.colorAccentDark;
                deltaColor = delta >=0 ? R.color.colorPrimaryDark : R.color.colorAccentDark;
            }

            //holder.categoryTitleView.setTextColor(getResources().getColor(titleColor));
            holder.categoryDeltaView.setTextColor(getResources().getColor(deltaColor));
        }

        @Override
        public int getItemCount() {
            if (mCursor == null) {
                return 0;
            }
            return mCursor.getCount();
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

        class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView categoryTitleView;
            TextView categoryBudgetView;
            TextView categoryFactView;
            TextView categoryDeltaView;
            ProgressBar progressView;

            public CategoryHolder(View itemView) {
                super(itemView);
                categoryTitleView = (TextView) itemView.findViewById(R.id.tvCategory);
                categoryBudgetView = (TextView) itemView.findViewById(R.id.tvBudget);
                categoryFactView = (TextView) itemView.findViewById(R.id.tvFact);
                categoryDeltaView = (TextView) itemView.findViewById(R.id.tvDelta);
                progressView = (ProgressBar) itemView.findViewById(R.id.progress);

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
