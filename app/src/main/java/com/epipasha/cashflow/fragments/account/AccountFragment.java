package com.epipasha.cashflow.fragments.account;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.fragments.ListDetailActivity;
import com.epipasha.cashflow.fragments.ListFragment;
import com.epipasha.cashflow.objects.Account;

import java.util.ArrayList;

/**
 * Created by Pavel on 08.11.2016.
 */

public class AccountFragment extends ListFragment<Account> {

    @Override
    public void addInstance() {
        Intent i = new Intent();
        i.setClass(getActivity(), ListDetailActivity.class);
        i.putExtra("Instance", new Account());
        i.putExtra("Position", -1);
        startActivityForResult(i, getActivity().RESULT_CANCELED);
    }

    @Override
    protected RecyclerView.Adapter getAdapter(ListFragment fragment, ArrayList<Account> list) {
        return new AccountListAdapter(fragment, list);
    }

    @Override
    protected ArrayList<Account> getList() {
        return CashFlowDbManager.getInstance(getActivity()).getAccounts();
    }

}
