package com.epipasha.cashflow.fragments;

import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.epipasha.cashflow.adapters.Adapter;
import com.epipasha.cashflow.adapters.CategoryAdapter;
import com.epipasha.cashflow.data.CashFlowContract;

import java.util.Calendar;

public class CategoryListFragment extends ListFragment {
    @Override
    public Adapter createAdapter() {
        return new CategoryAdapter(getActivity());
    }

    @Override
    public Loader<Cursor> createLoader() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        return new CursorLoader(
                getActivity(),
                CashFlowContract.CategoryEntry.buildCategoryCostUri(year, month),
                null,
                null,
                null,
                CashFlowContract.CategoryEntry.TABLE_NAME +"."+ CashFlowContract.CategoryEntry.COLUMN_TYPE + "," + CashFlowContract.CategoryEntry.TABLE_NAME +"."+ CashFlowContract.CategoryEntry.COLUMN_TITLE);
    }

}
