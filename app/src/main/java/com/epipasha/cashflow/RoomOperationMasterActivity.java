package com.epipasha.cashflow;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.epipasha.cashflow.Prefs.OperationMasterPrefs;
import com.epipasha.cashflow.activities.BaseActivity;
import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.entites.Account;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.entites.Operation;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RoomOperationMasterActivity extends BaseActivity{

    private static final int OPERATION_SAVED = 23;
    private static final int OPERATION_DELETED = 54;

    private Handler mHandler;
    private int sum = 0;

    AppDatabase mDb;

    private ViewGroup parentContainer;
    private RadioGroup groupType;
    private Spinner spinAccount, spinAnalytic;
    private TextView lblSum, lblAnalytic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_master);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(getString(R.string.operation_master));
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        initAccountSpinner();

        setCheckedOperationType(OperationMasterPrefs.getOperationType(this));
        setSum(0);

    }

    private void initAccountSpinner(){
        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                final LiveData<List<Account>> accounts = mDb.accountDao().loadAllAccounts();

                accounts.observe(RoomOperationMasterActivity.this, new Observer<List<Account>>() {
                    @Override
                    public void onChanged(@Nullable List<Account> accounts) {
                        ArrayAdapter<Account> adapter = new ArrayAdapter<>(
                                RoomOperationMasterActivity.this,
                                R.layout.list_item_account,
                                R.id.account_list_item_name,
                                accounts);
                        spinAccount.setAdapter(adapter);

                        int accountId = OperationMasterPrefs.getAccountId(RoomOperationMasterActivity.this);
                        Utils.setPositionById(spinAccount, accountId);
                    }
                });
            }
        });
    }

    private void findViews() {

        parentContainer = (ViewGroup) findViewById(R.id.master_container);

        groupType = (RadioGroup)findViewById(R.id.type_group);
        groupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onOperationTypeChanged();
            }
        });

        spinAccount = (Spinner)findViewById(R.id.spinner_account);
        spinAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initAnalyticSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinAnalytic = (Spinner)findViewById(R.id.spinner_analytic);

        lblAnalytic = findViewById(R.id.lblAnalytic);
        lblSum = (TextView) findViewById(R.id.operation_master_sum);

        Button btn0 = (Button) findViewById(R.id.digit_0);
        Button btn1 = (Button) findViewById(R.id.digit_1);
        Button btn2 = (Button) findViewById(R.id.digit_2);
        Button btn3 = (Button) findViewById(R.id.digit_3);
        Button btn4 = (Button) findViewById(R.id.digit_4);
        Button btn5 = (Button) findViewById(R.id.digit_5);
        Button btn6 = (Button) findViewById(R.id.digit_6);
        Button btn7 = (Button) findViewById(R.id.digit_7);
        Button btn8 = (Button) findViewById(R.id.digit_8);
        Button btn9 = (Button) findViewById(R.id.digit_9);
        ImageButton btnBack = (ImageButton) findViewById(R.id.digit_back);
        Button btnMore = (Button) findViewById(R.id.operation_master_more);
        Button btnNext = (Button) findViewById(R.id.operation_master_next);

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
                setSum(sum* 10 + digit);
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
                setSum(sum/10);
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
                createOperation();
            }
        });

    }

    private OperationType getCheckedOperationType(){
        switch (groupType.getCheckedRadioButtonId()){
            case R.id.btnIn :
                return OperationType.IN;
            case R.id.btnOut:
                return OperationType.OUT;
            case R.id.btnTransfer:
                return OperationType.TRANSFER;
            default:
                return OperationType.IN;
        }
    }

    private void setCheckedOperationType(OperationType type){

        if (type == null){
            return;
        }

        switch (type){
            case IN:
                groupType.check(R.id.btnIn);
                break;
            case OUT:
                groupType.check(R.id.btnOut);
                break;
            case TRANSFER:
                groupType.check(R.id.btnTransfer);
                break;
            default:
                groupType.check(R.id.btnIn);
        }
    }

    private void onOperationTypeChanged(){
        setSpinnersLabels();
        initAnalyticSpinner();
    }

    private void setSpinnersLabels(){
        final OperationType type = getCheckedOperationType();

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

    }

    private void initAnalyticSpinner() {

        final OperationType type = getCheckedOperationType();

        switch (type){
            case IN: case OUT:{
                AppExecutors.getInstance().discIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        LiveData<List<Category>> categories = mDb.categoryDao().loadAllCategoriesByType(type);

                        categories.observe(RoomOperationMasterActivity.this, new Observer<List<Category>>() {
                            @Override
                            public void onChanged(@Nullable List<Category> categories) {
                                ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                                        RoomOperationMasterActivity.this,
                                        android.R.layout.simple_spinner_item,
                                        android.R.id.text1,
                                        categories);

                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinAnalytic.setAdapter(adapter);
                                Utils.setPositionById(spinAnalytic,
                                        OperationMasterPrefs.getAnalyticId(
                                                RoomOperationMasterActivity.this, type));
                            }
                        });

                    }
                });
                break;
            }
            case TRANSFER: {
                AppExecutors.getInstance().discIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        LiveData<List<Account>> accounts = null;
                        Account operationAccount = (Account) spinAccount.getSelectedItem();
                        if (operationAccount == null)
                            accounts = mDb.accountDao().loadAllAccounts();
                        else
                            accounts = mDb.accountDao().loadAllAccountsExceptId(operationAccount.getId());

                        accounts.observe(RoomOperationMasterActivity.this, new Observer<List<Account>>() {
                            @Override
                            public void onChanged(@Nullable List<Account> accounts) {

                                ArrayAdapter<Account> adapter = new ArrayAdapter<>(
                                        RoomOperationMasterActivity.this,
                                        android.R.layout.simple_spinner_item,
                                        android.R.id.text1,
                                        accounts);

                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinAnalytic.setAdapter(adapter);

                                Utils.setPositionById(spinAnalytic,
                                        OperationMasterPrefs.getAnalyticId(
                                                RoomOperationMasterActivity.this, type));
                            }
                        });
                    }
                });
                break;
            }
        }

    }

    private void setSum(int s){
        sum = s;
        lblSum.setText(String.format(Locale.getDefault(),"%,d",sum));
    }

    private void createOperation(){

        Account account = (Account) spinAccount.getSelectedItem();
        if(account == null){
            Snackbar.make(parentContainer, R.string.no_account_selected, Snackbar.LENGTH_LONG).show();
            return;
        }
        int accountId = account.getId();
        Integer categoryId = null;
        Integer repAccountId = null;

        OperationType type = getCheckedOperationType();

        switch (type){
            case IN: case OUT: {
                Category category = (Category) spinAnalytic.getSelectedItem();
                if (category == null) {
                    Snackbar.make(parentContainer, R.string.no_analytic_selected, Snackbar.LENGTH_LONG).show();
                    return;
                }
                categoryId = category.getId();
                break;
            }
            case TRANSFER:{
                Account repAccount = (Account)spinAnalytic.getSelectedItem();
                if (repAccount == null) {
                    Snackbar.make(parentContainer, R.string.no_analytic_selected, Snackbar.LENGTH_LONG).show();
                    return;
                }
                repAccountId = repAccount.getId();
                break;
            }
        }

        if(sum == 0){
            Snackbar.make(parentContainer, R.string.no_sum, Snackbar.LENGTH_LONG).show();
            return;
        }

        final Operation operation = new Operation(new Date(), type, accountId, categoryId, repAccountId, sum);

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case OPERATION_SAVED:{
                        Operation operation = (Operation)msg.obj;
                        onOperationSaved(operation);
                        break;
                    }
                    case OPERATION_DELETED:{
                        int numRowsDeleted = (int) msg.obj;
                        onOperationDeleted(numRowsDeleted);
                        break;
                    }
                }
            }
        };

       AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                int operationId = (int) mDb.operationDao().insertOperationWihtAnalytic(operation);
                operation.setId(operationId);
                mHandler.obtainMessage(OPERATION_SAVED, operation).sendToTarget();
            }
       });
    }

    private void onOperationSaved(final Operation operation){
        if (operation.getId() == -1) {
            Snackbar.make(parentContainer, R.string.error, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar snackbar = Snackbar.make(parentContainer, R.string.operation_created, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppExecutors.getInstance().discIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            int numRowsDeleted = mDb.operationDao().deleteOperation(operation);
                            mHandler.obtainMessage(OPERATION_DELETED, numRowsDeleted).sendToTarget();
                        }
                    });
                }
            });
            snackbar.show();
            setSum(0);
        }
    }

    private void onOperationDeleted(int numRowsDeleted){
        if (numRowsDeleted > 0) {
            Snackbar.make(parentContainer, R.string.operation_deleted, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(parentContainer, R.string.error, Snackbar.LENGTH_LONG).show();
        }
    }

    private Integer getAccountId(){
        Account account = (Account) spinAccount.getSelectedItem();
        if (account == null)
            return null;
        else
            return account.getId();
    }

    @Override
    protected void onStop() {
        super.onStop();

        OperationMasterPrefs.saveOperationType(RoomOperationMasterActivity.this, getCheckedOperationType());

        Integer accountId = getAccountId();
        if (accountId != null)
            OperationMasterPrefs.saveAccountId(RoomOperationMasterActivity.this, accountId);

        OperationType type = getCheckedOperationType();
        Integer analyticId = null;

        switch (type){
            case IN: case OUT: {
                Category category = (Category) spinAnalytic.getSelectedItem();
                if (category != null) {
                    analyticId = category.getId();
                }
                break;
            }
            case TRANSFER:{
                Account repAccount = (Account)spinAnalytic.getSelectedItem();
                if (repAccount != null) {
                    analyticId = repAccount.getId();
                }
                break;
            }
        }

        if (analyticId != null)
            OperationMasterPrefs.saveAnalyticId(RoomOperationMasterActivity.this, analyticId, getCheckedOperationType());
    }

//Adaptors
    //todo use adapters
//
//    class AccountAdapter extends CursorAdapter{
//
//        public AccountAdapter(Context context, Cursor c, int flags) {
//            super(context, c, flags);
//        }
//
//        @Override
//        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
//            return LayoutInflater.from(context)
//                    .inflate(R.layout.list_item_account, viewGroup, false);
//        }
//
//        @Override
//        public void bindView(View view, Context context, Cursor cursor) {
//
//            int idIndex = cursor.getColumnIndex(AccountEntry._ID);
//            int titleIndex = cursor.getColumnIndex(AccountEntry.COLUMN_TITLE);
//            int sumIndex = cursor.getColumnIndex(AccountEntry.SERVICE_COLUMN_SUM);
//
//            // Determine the values of the wanted data
//            final int id = cursor.getInt(idIndex);
//            String title = cursor.getString(titleIndex);
//            int sum = cursor.getInt(sumIndex);
//
//            //Set values
//            ((TextView)view.findViewById(R.id.account_list_item_name)).setText(title);
//            ((TextView)view.findViewById(R.id.account_list_item_sum)).setText(String.format(Locale.getDefault(), "%,d", sum));
//
//        }
//    }
//
//    class CategoryAdapter extends CursorAdapter{
//
//        public CategoryAdapter(Context context, Cursor c, int flags) {
//            super(context, c, flags);
//        }
//
//        @Override
//        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
//            return LayoutInflater.from(context)
//                    .inflate(R.layout.list_item_master_category, viewGroup, false);
//        }
//
//        @Override
//        public void bindView(View view, Context context, Cursor cursor) {
//
//            int idIndex = cursor.getColumnIndex(CategoryEntry._ID);
//            int titleIndex = cursor.getColumnIndex(CategoryEntry.COLUMN_TITLE);
//            int typeIndex = cursor.getColumnIndex(CategoryEntry.COLUMN_TYPE);
//            int budgetIndex = cursor.getColumnIndex(CategoryEntry.COLUMN_BUDGET);
//            int factIndex = cursor.getColumnIndex(CashFlowContract.CategoryCostEntry.COLUMN_SUM);
//
//            // Determine the values of the wanted data
//            final int id = cursor.getInt(idIndex);
//            String title = cursor.getString(titleIndex);
//            OperationType type = OperationType.toEnum(cursor.getInt(typeIndex));
//            int budget = cursor.getInt(budgetIndex);
//            int fact = cursor.getInt(factIndex);
//
//            int delta = 0;
//            if(type.equals(OperationType.IN)) {
//                delta = fact - budget;
//            }else if (type.equals(OperationType.OUT)) {
//                delta = budget - fact;
//            }
//
//            ((TextView)view.findViewById(R.id.lbl_in)).setText(title);
////            ((TextView)view.findViewById(R.id.tvInBudget)).setText(String.format(Locale.getDefault(),"%,d",budget));
////            ((TextView)view.findViewById(R.id.tvFact)).setText(String.format(Locale.getDefault(),"%,d",fact));
////            ((TextView)view.findViewById(R.id.tvInDelta)).setText(String.format(Locale.getDefault(),"%,d",delta));
//
////            int deltaColor = R.color.primaryTextColor;
////            if(type.equals(OperationType.IN)){
////                deltaColor = delta >=0 ? R.color.colorPrimaryDark : R.color.colorAccentDark;
////            }else if (type.equals(OperationType.OUT)){
////                deltaColor = delta >=0 ? R.color.colorPrimaryDark : R.color.colorAccentDark;
////            }
////
////            ((TextView)view.findViewById(R.id.tvInDelta)).setTextColor(getResources().getColor(deltaColor));
//
//        }
//    }

}
