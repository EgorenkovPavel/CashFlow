package com.epipasha.cashflow;

import static com.epipasha.cashflow.data.CashFlowContract.AccountEntry;
import static com.epipasha.cashflow.data.CashFlowContract.CategoryEntry;
import static com.epipasha.cashflow.data.CashFlowContract.OperationEntry;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.epipasha.cashflow.Prefs.OperationMasterPrefs;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Date;
import java.util.Locale;

public class OperationMasterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private  static final int ACCOUNT_LOADER_ID = 432;
    private  static final int CATEGORY_LOADER_ID = 879;
    private  static final int REP_ACCOUNT_LOADER_ID = 654;

    private int sum = 0;

    private CoordinatorLayout parentContainer;
    private RadioGroup groupType;
    private Spinner spinAccount, spinAnalytic;
    private TextView lblSum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_master);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        findViews();

        setCheckedOperationType(OperationMasterPrefs.getOperationType(this));
        setSum(0);

        getSupportLoaderManager().initLoader(ACCOUNT_LOADER_ID, null, this);
        initAnalyticSpinner();
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

    private void initAnalyticSpinner() {
        switch (groupType.getCheckedRadioButtonId()){
            case R.id.btnIn: case R.id.btnOut:{
                getSupportLoaderManager().restartLoader(CATEGORY_LOADER_ID, null, OperationMasterActivity.this);
                break;
            }
            case R.id.btnTransfer:{
                getSupportLoaderManager().restartLoader(REP_ACCOUNT_LOADER_ID, null, OperationMasterActivity.this);
                break;
            }
        }
    }

    private void setSum(int s){
        sum = s;
        lblSum.setText(String.format(Locale.getDefault(),"%,d",sum));
    }

    private void findViews() {

        parentContainer = (CoordinatorLayout) findViewById(R.id.master_container);

        groupType = (RadioGroup)findViewById(R.id.type_group);
        groupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                initAnalyticSpinner();
            }
        });

        spinAccount = (Spinner)findViewById(R.id.spinner_account);
        spinAnalytic = (Spinner)findViewById(R.id.spinner_analytic);

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

                int accountId = Utils.getSelectedId(spinAccount);
                if(accountId == 0){
                    Snackbar.make(view, "No account selected!!!", Snackbar.LENGTH_LONG).show();
                    return;
                }

                int analyticId = Utils.getSelectedId(spinAnalytic);
                if (analyticId == 0) {
                    Snackbar.make(view, "No analytic selected!!!", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(sum == 0){
                    Snackbar.make(parentContainer, "Type the sum", Snackbar.LENGTH_LONG).show();
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(OperationEntry.COLUMN_DATE, (new Date()).getTime());
                values.put(OperationEntry.COLUMN_ACCOUNT_ID, accountId);
                values.put(OperationEntry.COLUMN_SUM, sum);

                switch (groupType.getCheckedRadioButtonId()){
                    case R.id.btnIn :{
                        values.put(OperationEntry.COLUMN_TYPE, OperationType.IN.toDbValue());
                        values.put(OperationEntry.COLUMN_CATEGORY_ID, analyticId);
                        break;
                    }
                    case R.id.btnOut: {
                        values.put(OperationEntry.COLUMN_TYPE, OperationType.OUT.toDbValue());
                        values.put(OperationEntry.COLUMN_CATEGORY_ID, analyticId);
                        break;
                    }
                    case R.id.btnTransfer: {
                        values.put(OperationEntry.COLUMN_TYPE, OperationType.TRANSFER.toDbValue());
                        values.put(OperationEntry.COLUMN_RECIPIENT_ACCOUNT_ID, analyticId);
                        break;
                    }
                }

                OperationMasterPrefs.saveOperationType(OperationMasterActivity.this, getCheckedOperationType());
                OperationMasterPrefs.saveAccountId(OperationMasterActivity.this, accountId);
                OperationMasterPrefs.saveAnalyticId(OperationMasterActivity.this, analyticId, getCheckedOperationType());

                final Uri uri = getContentResolver().insert(OperationEntry.CONTENT_URI, values);
                if (uri == null){
                    Snackbar.make(view, "ERROR", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar snackbar = Snackbar.make(view, "Operation created!!!", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int numRowsDeleted = getContentResolver().delete(uri, null, null);
                            if (numRowsDeleted > 0){
                                Snackbar.make(view, "Operation deleted", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(parentContainer, "ERROR", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                    snackbar.show();
                    setSum(0);
                }
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id){
            case ACCOUNT_LOADER_ID:{
                return new CursorLoader(
                        this,
                        AccountEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            }
            case CATEGORY_LOADER_ID:{
                return new CursorLoader(
                        this,
                        CategoryEntry.CONTENT_URI,
                        null,
                        CategoryEntry.COLUMN_TYPE + " = " + getCheckedOperationType().toDbValue(),
                        null,
                        null);
            }
            case REP_ACCOUNT_LOADER_ID:{
                return new CursorLoader(
                        this,
                        AccountEntry.CONTENT_URI,
                        null,
                        AccountEntry._ID + " != " + OperationMasterPrefs.getAccountId(this),
                        null,
                        null);
            }
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()){
            case ACCOUNT_LOADER_ID:{
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        cursor,
                        new String[]{AccountEntry.COLUMN_TITLE},
                        new int[]{android.R.id.text1}
                        ,0);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinAccount.setAdapter(adapter);

                Utils.setPositionById(spinAccount, OperationMasterPrefs.getAccountId(this));
                break;
            }
            case CATEGORY_LOADER_ID:{
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        cursor,
                        new String[]{CategoryEntry.COLUMN_TITLE},
                        new int[]{android.R.id.text1}
                        ,0);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinAnalytic.setAdapter(adapter);

                Utils.setPositionById(spinAnalytic, OperationMasterPrefs.getAnalyticId(this, getCheckedOperationType()));
                break;
            }
            case REP_ACCOUNT_LOADER_ID:{
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        cursor,
                        new String[]{AccountEntry.COLUMN_TITLE},
                        new int[]{android.R.id.text1}
                        ,0);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinAnalytic.setAdapter(adapter);

                Utils.setPositionById(spinAnalytic, OperationMasterPrefs.getAnalyticId(this, getCheckedOperationType()));
                break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


/*
    private class AccountAdapter extends ArrayAdapter<Account> {

        private final ArrayList<Account> accounts;

        AccountAdapter(Context context, ArrayList<Account> accounts) {
            super(context, R.layout.account_spinner_adapter, accounts);
            this.accounts = accounts;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Account account = accounts.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.account_spinner_adapter, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.account_spinner_adapter_name))
                    .setText(account.getName());
            ((TextView) convertView.findViewById(R.id.account_spinner_adapter_balance))
                    .setText(String.format(Locale.getDefault(),"%,d",account.getBalance()));
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            Account account = accounts.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.account_spinner_adapter_dropdown, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.account_spinner_adapter_dropdown_name))
                    .setText(account.getName());
            ((TextView) convertView.findViewById(R.id.account_spinner_adapter_dropdown_balance))
                    .setText(String.format(Locale.getDefault(),"%,d",account.getBalance()));
            return convertView;
        }
    }

    private class CategoryAdapter extends ArrayAdapter<Category> {

        private final ArrayList<Category> categories;

        CategoryAdapter(Context context, ArrayList<Category> categories) {
            super(context, R.layout.category_spinner_adapter, categories);
            this.categories = categories;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
             Category category = categories.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.category_spinner_adapter, parent, false);
            }

            GregorianCalendar c = new GregorianCalendar();
            Date end = new Date(c.getTimeInMillis());

            c.set(GregorianCalendar.DAY_OF_MONTH, c.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
            c.set(GregorianCalendar.HOUR_OF_DAY, 0);
            c.set(GregorianCalendar.MINUTE, 0);
            c.set(GregorianCalendar.SECOND, 0);
            c.set(GregorianCalendar.MILLISECOND, 0);
            Date start = new Date(c.getTimeInMillis());

            int fact = CashFlowDbManager.getInstance(getContext()).getCategorySum(category, start, end);

            ((TextView) convertView.findViewById(R.id.name)).setText(category.getName());
            ((TextView) convertView.findViewById(R.id.budget))
                    .setText(String.format(Locale.getDefault(),"%,d",category.getBudget()));
            ((TextView) convertView.findViewById(R.id.fact))
                    .setText(String.format(Locale.getDefault(),"%,d",fact));

            return convertView;
        }

    }
*/
}
