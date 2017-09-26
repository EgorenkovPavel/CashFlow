package com.epipasha.cashflow.fragments.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.fragments.ListDetailFragment;
import com.epipasha.cashflow.objects.Account;


public class AccountListDetailFragment extends ListDetailFragment<Account> {

    private Account account;

    private EditText name;


    @Override
    public void setInstance(Account instance) {
        this.account = instance;
    }

    @Override
    public Account getInstance() {
        account.setName(name.getText().toString());

        if(account.getID()==0){
            int id = CashFlowDbManager.getInstance(getActivity()).addAccount(account);
            account.setID(id);
        }else{
            CashFlowDbManager.getInstance(getActivity()).updateAccount(account);
        }

        return account;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_detail_account, container, false);

        name = (EditText)v.findViewById(R.id.account_detail_name);

        return v;
    }
}
