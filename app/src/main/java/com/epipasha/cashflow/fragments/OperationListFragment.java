package com.epipasha.cashflow.fragments;

import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.epipasha.cashflow.adapters.Adapter;
import com.epipasha.cashflow.adapters.CategoryAdapter;
import com.epipasha.cashflow.adapters.OperationAdapter;
import com.epipasha.cashflow.data.CashFlowContract;

import java.util.Calendar;

public class OperationListFragment extends ListFragment {
    @Override
    public Adapter createAdapter() {
        return new OperationAdapter(getActivity());
    }

    @Override
    public Loader<Cursor> createLoader() {
        return new CursorLoader(
                getActivity(),
                CashFlowContract.OperationEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

}
