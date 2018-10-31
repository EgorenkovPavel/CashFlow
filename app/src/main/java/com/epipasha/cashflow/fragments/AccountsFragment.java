package com.epipasha.cashflow.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.activities.AccountActivity;
import com.epipasha.cashflow.adapters.AccountAdapter;
import com.epipasha.cashflow.data.entites.AccountWithBalance;
import com.epipasha.cashflow.viewmodel.AccountsViewModel;
import com.epipasha.cashflow.viewmodel.ViewModelFactory;

import java.util.List;

public class AccountsFragment extends Fragment implements AccountAdapter.ItemClickListener {

    private RecyclerView rvList;
    private AccountAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        rvList = v.findViewById(R.id.rvList);

        initRecycledView();

        retrieveItems();

        return v;
    }

    private void initRecycledView(){

        rvList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvList.getContext(),
                layoutManager.getOrientation());
        rvList.addItemDecoration(mDividerItemDecoration);

        mAdapter = new AccountAdapter(this);
        rvList.setAdapter(mAdapter);

    }

    private void retrieveItems() {

        AccountsViewModel viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getActivity().getApplication())).get(AccountsViewModel.class);
        viewModel.getAccounts().observe(this, new Observer<List<AccountWithBalance>>() {
            @Override
            public void onChanged(@Nullable List<AccountWithBalance> accounts) {
                mAdapter.setAccounts(accounts);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(getActivity(), AccountActivity.class);
        intent.putExtra(AccountActivity.EXTRA_ACCOUNT_ID, itemId);
        startActivity(intent);
    }
}
