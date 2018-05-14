package com.epipasha.cashflow.fragments;

import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.epipasha.cashflow.adapters.AccountAdapter;
import com.epipasha.cashflow.adapters.Adapter;
import com.epipasha.cashflow.data.CashFlowContract;

public class AccountListFragment extends ListFragment {
    @Override
    public Adapter createAdapter() {
        return new AccountAdapter(getActivity());
    }

    @Override
    public Loader<Cursor> createLoader() {
        return new CursorLoader(
                getActivity(),
                CashFlowContract.AccountEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

}
