package com.epipasha.cashflow.fragments.accounts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.activities.accounts.AccountActivity;
import com.epipasha.cashflow.data.ViewModelFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AccountsFragment extends Fragment {

    private RecyclerView rvList;
    private AccountAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        rvList = v.findViewById(R.id.rvList);

        initRecycledView();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AccountsViewModel viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getActivity().getApplication())).get(AccountsViewModel.class);
        viewModel.getAccounts().observe(this, accounts -> mAdapter.setAccounts(accounts));
    }

    private void initRecycledView(){
        rvList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvList.getContext(),
                layoutManager.getOrientation());
        rvList.addItemDecoration(mDividerItemDecoration);

        mAdapter = new AccountAdapter(id -> AccountActivity.start(getActivity(), id));

        rvList.setAdapter(mAdapter);
    }
}
