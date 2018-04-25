package com.epipasha.cashflow;

import static com.epipasha.cashflow.data.CashFlowContract.AccountEntry;
import static com.epipasha.cashflow.data.CashFlowContract.CategoryEntry;
import static com.epipasha.cashflow.data.CashFlowContract.OperationEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.epipasha.cashflow.Prefs.OperationMasterPrefs;
import com.epipasha.cashflow.activities.BaseActivity;
import com.epipasha.cashflow.data.CashFlowContract;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OperationMasterActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACCOUNT_LOADER_ID = 432;
    private static final int CATEGORY_LOADER_ID = 879;
    private static final int REP_ACCOUNT_LOADER_ID = 654;

    private int sum = 0;

    private ViewGroup parentContainer;
    private RadioGroup groupType;
    private Spinner spinAccount, spinAnalytic;
    private TextView lblSum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_master);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle(getString(R.string.operation_master));
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        parentContainer = (ViewGroup) findViewById(R.id.master_container);

        groupType = (RadioGroup)findViewById(R.id.type_group);
        groupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                initAnalyticSpinner();
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

    private void createOperation(){
        int accountId = Utils.getSelectedId(spinAccount);
        if(accountId <= 0){
            Snackbar.make(parentContainer, R.string.no_account_selected, Snackbar.LENGTH_LONG).show();
            return;
        }

        int analyticId = Utils.getSelectedId(spinAnalytic);
        if (analyticId <= 0) {
            Snackbar.make(parentContainer, R.string.no_analytic_selected, Snackbar.LENGTH_LONG).show();
            return;
        }

        if(sum == 0){
            Snackbar.make(parentContainer, R.string.no_sum, Snackbar.LENGTH_LONG).show();
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
            Snackbar.make(parentContainer, R.string.error, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar snackbar = Snackbar.make(parentContainer, R.string.operation_created, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int numRowsDeleted = getContentResolver().delete(uri, null, null);
                    if (numRowsDeleted > 0){
                        Snackbar.make(view, R.string.operation_deleted, Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(parentContainer, R.string.error, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
            snackbar.show();
            setSum(0);
        }
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
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);

                return new CursorLoader(
                        this,
                        CashFlowContract.CategoryEntry.buildCategoryCostUri(year, month),
                        null,
                        CategoryEntry.COLUMN_TYPE + " = " + getCheckedOperationType().toDbValue(),
                        null,
                        CategoryEntry.TABLE_NAME +"."+CategoryEntry.COLUMN_TITLE);

            }
            case REP_ACCOUNT_LOADER_ID:{
                return new CursorLoader(
                        this,
                        AccountEntry.CONTENT_URI,
                        null,
                        AccountEntry._ID + " != " + Utils.getSelectedId(spinAccount),
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
                AccountAdapter adapter = new AccountAdapter(this, cursor, CursorAdapter.NO_SELECTION);
                spinAccount.setAdapter(adapter);

                Utils.setPositionById(spinAccount, OperationMasterPrefs.getAccountId(this));
                break;
            }
            case CATEGORY_LOADER_ID:{
                CategoryAdapter adapter = new CategoryAdapter(this, cursor, CursorAdapter.NO_SELECTION);
                spinAnalytic.setAdapter(adapter);

                Utils.setPositionById(spinAnalytic, OperationMasterPrefs.getAnalyticId(this, getCheckedOperationType()));
                break;
            }
            case REP_ACCOUNT_LOADER_ID:{
                AccountAdapter adapter = new AccountAdapter(this, cursor, CursorAdapter.NO_SELECTION);
                spinAnalytic.setAdapter(adapter);

                Utils.setPositionById(spinAnalytic, OperationMasterPrefs.getAnalyticId(this, getCheckedOperationType()));
                break;
            }
         }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    class AccountAdapter extends CursorAdapter{

        public AccountAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context)
                    .inflate(R.layout.list_item_account, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            int idIndex = cursor.getColumnIndex(AccountEntry._ID);
            int titleIndex = cursor.getColumnIndex(AccountEntry.COLUMN_TITLE);
            int sumIndex = cursor.getColumnIndex(AccountEntry.SERVICE_COLUMN_SUM);

            // Determine the values of the wanted data
            final int id = cursor.getInt(idIndex);
            String title = cursor.getString(titleIndex);
            int sum = cursor.getInt(sumIndex);

            //Set values
            ((TextView)view.findViewById(R.id.account_list_item_name)).setText(title);
            ((TextView)view.findViewById(R.id.account_list_item_sum)).setText(String.format(Locale.getDefault(), "%,d", sum));

        }
    }

    class CategoryAdapter extends CursorAdapter{

        public CategoryAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context)
                    .inflate(R.layout.list_item_category, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            int idIndex = cursor.getColumnIndex(CashFlowContract.CategoryEntry._ID);
            int titleIndex = cursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_TITLE);
            int typeIndex = cursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_TYPE);
            int budgetIndex = cursor.getColumnIndex(CashFlowContract.CategoryEntry.COLUMN_BUDGET);
            int factIndex = cursor.getColumnIndex(CashFlowContract.CategoryCostEntry.COLUMN_SUM);

            // Determine the values of the wanted data
            final int id = cursor.getInt(idIndex);
            String title = cursor.getString(titleIndex);
            OperationType type = OperationType.toEnum(cursor.getInt(typeIndex));
            int budget = cursor.getInt(budgetIndex);
            int fact = cursor.getInt(factIndex);

            int delta = 0;
            if(type.equals(OperationType.IN)) {
                delta = fact - budget;
            }else if (type.equals(OperationType.OUT)) {
                delta = budget - fact;
            }

            ((TextView)view.findViewById(R.id.lblIn)).setText(title);
            ((TextView)view.findViewById(R.id.tvInBudget)).setText(String.format(Locale.getDefault(),"%,d",budget));
            ((TextView)view.findViewById(R.id.tvFact)).setText(String.format(Locale.getDefault(),"%,d",fact));
            ((TextView)view.findViewById(R.id.tvInDelta)).setText(String.format(Locale.getDefault(),"%,d",delta));

//            int deltaColor = R.color.primaryTextColor;
//            if(type.equals(OperationType.IN)){
//                deltaColor = delta >=0 ? R.color.colorPrimaryDark : R.color.colorAccentDark;
//            }else if (type.equals(OperationType.OUT)){
//                deltaColor = delta >=0 ? R.color.colorPrimaryDark : R.color.colorAccentDark;
//            }
//
//            ((TextView)view.findViewById(R.id.tvInDelta)).setTextColor(getResources().getColor(deltaColor));

        }
    }

}
