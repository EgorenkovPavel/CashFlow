package com.epipasha.cashflow;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.epipasha.cashflow.activities.BaseActivity;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.AccountWithBalance;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.viewmodel.OperationMasterViewModel;
import com.epipasha.cashflow.objects.OperationType;

import java.util.List;
import java.util.Locale;

public class OperationMasterActivity extends BaseActivity{

    private OperationMasterViewModel model;

    private AccountAdapter mAccountAdapter;
    private ArrayAdapter<Category> mCategoryInAdapter;
    private ArrayAdapter<Category> mCategoryOutAdapter;
    private AccountAdapter mRecAccountAdapter;

    private ViewGroup parentContainer;
    private Spinner spinAccount, spinAnalytic;
    private TextView lblSum, lblAnalytic;
    private RadioButton rbIn, rbOut, rbTransfer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_master);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(getString(R.string.operation_master));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();

        initAdapters();

        spinAccount.setAdapter(mAccountAdapter);

        model = ViewModelProviders.of(this).get(OperationMasterViewModel.class);

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

                rbIn.setChecked(type == OperationType.IN);
                rbOut.setChecked(type == OperationType.OUT);
                rbTransfer.setChecked(type == OperationType.TRANSFER);

                switch (type){
                    case IN: case OUT:{
                        lblAnalytic.setText(getString(R.string.category));
                        break;
                    }
                    case TRANSFER:{
                        lblAnalytic.setText(getString(R.string.account));
                        break;
                    }
                }

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

        model.getOperationSum().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer sum) {
                lblSum.setText(String.format(Locale.getDefault(),"%,d",sum));
            }
        });

        model.getOperationAccount().observe(this, new Observer<AccountWithBalance>() {
            @Override
            public void onChanged(@Nullable AccountWithBalance account) {
                spinAccount.setSelection(mAccountAdapter.getPosition(account));
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
                R.layout.list_item_account,
                R.id.account_list_item_name);

        mCategoryOutAdapter = new ArrayAdapter<>(
                OperationMasterActivity.this,
                R.layout.list_item_account,
                R.id.account_list_item_name);

        mRecAccountAdapter = new AccountAdapter(this);
    }

    private void findViews() {

        parentContainer = findViewById(R.id.master_container);

        rbIn = findViewById(R.id.btnIn);
        rbOut = findViewById(R.id.btnOut);
        rbTransfer = findViewById(R.id.btnTransfer);

        RadioGroup groupType = findViewById(R.id.type_group);
        groupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.btnIn : {
                        model.onOperationTypeChanged(OperationType.IN);
                        break;
                    }
                    case R.id.btnOut: {
                        model.onOperationTypeChanged(OperationType.OUT);
                        break;
                    }
                    case R.id.btnTransfer: {
                        model.onOperationTypeChanged(OperationType.TRANSFER);
                        break;
                    }
                }
            }
        });

        spinAccount = findViewById(R.id.spinner_account);
        spinAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                AccountWithBalance account = mAccountAdapter.getItem(position);
                model.onOperationAccountChanged(account);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spinAnalytic = findViewById(R.id.spinner_analytic);
        spinAnalytic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinAnalytic.getAdapter().equals(mCategoryInAdapter)){
                    Category category = mCategoryInAdapter.getItem(position);
                    model.onOperationCategoryChanged(category);
                }else if(spinAnalytic.getAdapter().equals(mCategoryOutAdapter)){
                    Category category = mCategoryOutAdapter.getItem(position);
                    model.onOperationCategoryChanged(category);
                }else if(spinAnalytic.getAdapter().equals(mRecAccountAdapter)){
                    AccountWithBalance account = mRecAccountAdapter.getItem(position);
                    model.onOperationAccountRecChanged(account);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        lblAnalytic = findViewById(R.id.lblAnalytic);
        lblSum = findViewById(R.id.operation_master_sum);

        Button btn0 = findViewById(R.id.digit_0);
        Button btn1 = findViewById(R.id.digit_1);
        Button btn2 = findViewById(R.id.digit_2);
        Button btn3 = findViewById(R.id.digit_3);
        Button btn4 = findViewById(R.id.digit_4);
        Button btn5 = findViewById(R.id.digit_5);
        Button btn6 = findViewById(R.id.digit_6);
        Button btn7 = findViewById(R.id.digit_7);
        Button btn8 = findViewById(R.id.digit_8);
        Button btn9 = findViewById(R.id.digit_9);
        ImageButton btnBack = findViewById(R.id.digit_back);
        Button btnMore = findViewById(R.id.operation_master_more);
        Button btnNext = findViewById(R.id.operation_master_next);

        View.OnClickListener onDigitClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int digit = 0;
                switch (view.getId()){
                    case R.id.digit_0: digit = 0; break;
                    case R.id.digit_1: digit = 1; break;
                    case R.id.digit_2: digit = 2; break;
                    case R.id.digit_3: digit = 3; break;
                    case R.id.digit_4: digit = 4; break;
                    case R.id.digit_5: digit = 5; break;
                    case R.id.digit_6: digit = 6; break;
                    case R.id.digit_7: digit = 7; break;
                    case R.id.digit_8: digit = 8; break;
                    case R.id.digit_9: digit = 9; break;
                }
                model.onDigitPressed(digit);
            }
        };

        btn0.setOnClickListener(onDigitClick);
        btn1.setOnClickListener(onDigitClick);
        btn2.setOnClickListener(onDigitClick);
        btn3.setOnClickListener(onDigitClick);
        btn4.setOnClickListener(onDigitClick);
        btn5.setOnClickListener(onDigitClick);
        btn6.setOnClickListener(onDigitClick);
        btn7.setOnClickListener(onDigitClick);
        btn8.setOnClickListener(onDigitClick);
        btn9.setOnClickListener(onDigitClick);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model.onDeleteDigit();
            }
        });
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
            super(context, R.layout.list_item_account);
            setDropDownViewResource(R.layout.list_item_account);
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
                        .inflate(R.layout.list_item_account, parent, false);

            AccountWithBalance account = getItem(position);

            ((TextView)view.findViewById(R.id.account_list_item_name)).setText(account.getTitle());
            ((TextView)view.findViewById(R.id.account_list_item_sum)).setText(String.format(Locale.getDefault(), "%,d", account.getSum()));

            return view;
        }
    }

}
