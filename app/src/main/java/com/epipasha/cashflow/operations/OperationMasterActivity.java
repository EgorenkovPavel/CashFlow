package com.epipasha.cashflow.operations;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.activities.ActivityOperationMasterBinding;
import com.epipasha.cashflow.activities.BaseActivity;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.entites.AccountWithBalance;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.ViewModelFactory;
import com.epipasha.cashflow.objects.OperationType;

import java.util.List;
import java.util.Locale;

public class OperationMasterActivity extends BaseActivity {

    private OperationMasterViewModel model;

    private AccountAdapter mAccountAdapter;
    private ArrayAdapter<Category> mCategoryInAdapter;
    private ArrayAdapter<Category> mCategoryOutAdapter;
    private AccountAdapter mRecAccountAdapter;

    private ViewGroup parentContainer;
    private Spinner spinAccount, spinAnalytic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityOperationMasterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_operation_master);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();

        initAdapters();

        spinAccount.setAdapter(mAccountAdapter);

        model = ViewModelProviders.of(this, ViewModelFactory.getInstance(getApplication())).get(OperationMasterViewModel.class);

        binding.setViewmodel(model);

        model.getAccounts().observe(this, new Observer<List<AccountWithBalance>>() {
            @Override
            public void onChanged(@Nullable List<AccountWithBalance> accounts) {
                mAccountAdapter.clear();
                if (accounts != null) mAccountAdapter.addAll(accounts);
                mAccountAdapter.notifyDataSetChanged();
            }
        });

        model.getCategoriesIn().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                mCategoryInAdapter.clear();
                if (categories != null) mCategoryInAdapter.addAll(categories);
                mCategoryInAdapter.notifyDataSetChanged();
            }
        });

        model.getCategoriesOut().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                mCategoryOutAdapter.clear();
                if (categories != null) mCategoryOutAdapter.addAll(categories);
                mCategoryOutAdapter.notifyDataSetChanged();
            }
        });

        model.getRecAccounts().observe(this, new Observer<List<AccountWithBalance>>() {
            @Override
            public void onChanged(@Nullable List<AccountWithBalance> accounts) {
                mRecAccountAdapter.clear();
                if (accounts != null) mRecAccountAdapter.addAll(accounts);
                mRecAccountAdapter.notifyDataSetChanged();
            }
        });

        model.getOperationType().observe(this, new Observer<OperationType>() {
            @Override
            public void onChanged(@Nullable OperationType type) {
                if(type == null) return;

                 switch (type){
                    case IN:{
                        spinAnalytic.setAdapter(mCategoryInAdapter);
                        break;
                    }
                    case OUT:{
                        spinAnalytic.setAdapter(mCategoryOutAdapter);
                        break;
                    }
                    case TRANSFER: {
                        spinAnalytic.setAdapter(mRecAccountAdapter);
                        break;
                    }
                }
            }
        });

        model.getStatus().observe(this, new Observer<OperationMasterViewModel.Status>() {
            @Override
            public void onChanged(@Nullable OperationMasterViewModel.Status status) {
                if(status == null) return;
                switch (status){
                    case EMPTY_SUM:{
                        Snackbar.make(parentContainer, R.string.no_sum, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    case EMPTY_TYPE:{
                        Snackbar.make(parentContainer, R.string.no_type, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    case EMPTY_ANALYTIC:{
                        Snackbar.make(parentContainer, R.string.no_analytic_selected, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    case EMPTY_ACCOUNT:{
                        Snackbar.make(parentContainer, R.string.no_account_selected, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    case OPERATION_SAVED: {
                        Snackbar snackbar = Snackbar.make(parentContainer, R.string.operation_created, Snackbar.LENGTH_LONG);
                        snackbar.setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AppExecutors.getInstance().discIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        model.deleteOperation();
                                    }
                                });
                            }
                        });
                        snackbar.show();
                        break;
                    }
                    case OPERATION_DELETED:{
                        Snackbar.make(parentContainer, R.string.operation_deleted, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        model.loadPrefs();
    }

    private void initAdapters(){
        mAccountAdapter = new AccountAdapter(this);

        mCategoryInAdapter = new ArrayAdapter<>(
                OperationMasterActivity.this,
                R.layout.item_account,
                R.id.account_list_item_name);

        mCategoryOutAdapter = new ArrayAdapter<>(
                OperationMasterActivity.this,
                R.layout.item_account,
                R.id.account_list_item_name);

        mRecAccountAdapter = new AccountAdapter(this);
    }

    private void findViews() {

        parentContainer = findViewById(R.id.master_container);

        spinAccount = findViewById(R.id.spinner_account);
        spinAnalytic = findViewById(R.id.spinner_analytic);

        Button btnMore = findViewById(R.id.operation_master_more);
        Button btnNext = findViewById(R.id.operation_master_next);

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.saveOperation();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        model.savePrefs();
    }

    private class AccountAdapter extends ArrayAdapter<AccountWithBalance>{

        public AccountAdapter(@NonNull Context context) {
            super(context, R.layout.item_account);
            setDropDownViewResource(R.layout.item_account);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return adapterView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return adapterView(position, convertView, parent);
        }

        private View adapterView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View view = convertView;
            if (view == null)
                view = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_account, parent, false);

            AccountWithBalance account = getItem(position);

            ((TextView)view.findViewById(R.id.account_list_item_name)).setText(account.getTitle());
            ((TextView)view.findViewById(R.id.account_list_item_sum)).setText(String.format(Locale.getDefault(), "%,d", account.getSum()));

            return view;
        }
    }

}
