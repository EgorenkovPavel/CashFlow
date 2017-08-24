package com.epipasha.cashflow.fragments.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.fragments.ListDetailFragment;
import com.epipasha.cashflow.objects.Account;
import com.epipasha.cashflow.objects.Category;
import com.epipasha.cashflow.objects.Currency;

import java.util.ArrayList;


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
        Spinner curSpinner = (Spinner) v.findViewById(R.id.account_detail_currency);

        if (account != null){
            name.setText(account.getName());

            ArrayList<Currency> list = CashFlowDbManager.getInstance(getActivity()).getCurrencies();

            ArrayAdapter<Currency> currencyArrayAdapter = new ArrayAdapter<Currency>(getActivity(), android.R.layout.simple_spinner_item, list);
            currencyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            curSpinner.setAdapter(currencyArrayAdapter);
            curSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    account.setCurrency((Currency) parent.getAdapter().getItem(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            Currency cur = account.getCurrency();
            if(cur!=null){
                for (int i=0; i<list.size();i++) {
                    if (((Currency)list.get(i)).getId()==cur.getId()){
                        curSpinner.setSelection(i);
                    }
                }
            }
        }
        return v;
    }
}
