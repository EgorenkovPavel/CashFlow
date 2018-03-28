package com.epipasha.cashflow;

import static com.epipasha.cashflow.data.CashFlowContract.*;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.epipasha.cashflow.Prefs.OperationMasterPrefs;
import com.epipasha.cashflow.objects.OperationType;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OperationMasterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ACCOUNT_LOADER_ID = 432;
    private static final int CATEGORY_LOADER_ID = 879;
    private static final int REP_ACCOUNT_LOADER_ID = 654;
    private static final int FACT = 587;

    private int sum = 0;

    private ViewGroup parentContainer;
    private RadioGroup groupType;
    private Spinner spinAccount, spinAnalytic;
    private TextView lblSum;
    private ProgressBar pbBudget;
    private TextView tvBudgetSum, tvFactSum, tvOver;
    private View budgetBlock;

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

        budgetBlock = findViewById(R.id.progress);
        pbBudget = (ProgressBar)findViewById(R.id.pbBudget);
        tvBudgetSum = (TextView)findViewById(R.id.tvBudgetSum);
        tvFactSum = (TextView)findViewById(R.id.tvFactSum);
        tvOver = (TextView)findViewById(R.id.tvOver);

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
        spinAnalytic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onAnalyticChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                    onAnalyticChanged();
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
                        AccountEntry._ID + " != " + Utils.getSelectedId(spinAccount),
                        null,
                        null);
            }
            case FACT:{

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                Date start = new Date(cal.getTimeInMillis());

                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date end = new Date(cal.getTimeInMillis());

                return new CursorLoader(
                        this,
                        CategoryCostEntry.CONTENT_URI,
                        new String[]{"SUM(" + CategoryCostEntry.COLUMN_SUM + ") AS " + CategoryCostEntry.COLUMN_SUM},
                        CategoryCostEntry.COLUMN_CATEGORY_ID + " = " + args.getInt("id") + " " +
                        " AND " + CategoryCostEntry.COLUMN_DATE + " between "+start.getTime()+" AND " + end.getTime(),
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
                        R.layout.item_operation_master_account,
                        cursor,
                        new String[]{AccountEntry.COLUMN_TITLE, AccountEntry.SERVICE_COLUMN_SUM},
                        new int[]{R.id.tvTitle, R.id.tvSum}
                        ,0);

                adapter.setDropDownViewResource(R.layout.item_operation_master_account);
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
                onAnalyticChanged();
                break;
            }
            case REP_ACCOUNT_LOADER_ID:{
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        this,
                        R.layout.item_operation_master_account,
                        cursor,
                        new String[]{AccountEntry.COLUMN_TITLE, AccountEntry.SERVICE_COLUMN_SUM},
                        new int[]{R.id.tvTitle, R.id.tvSum}
                        ,0);

                adapter.setDropDownViewResource(R.layout.item_operation_master_account);
                spinAnalytic.setAdapter(adapter);

                Utils.setPositionById(spinAnalytic, OperationMasterPrefs.getAnalyticId(this, getCheckedOperationType()));
                onAnalyticChanged();
                break;
            }
            case FACT:{

                Cursor catCursor = (Cursor)spinAnalytic.getSelectedItem();

                int budget = catCursor.getInt(catCursor.getColumnIndex(CategoryEntry.COLUMN_BUDGET));
                tvBudgetSum.setText(String.valueOf(budget));

                pbBudget.setMax(budget);

                int fact = 0;
                if(cursor != null && cursor.moveToFirst()){
                    fact = cursor.getInt(cursor.getColumnIndex(CategoryCostEntry.COLUMN_SUM));
                }
                tvFactSum.setText(String.valueOf(fact));
                pbBudget.setProgress(fact);

                if (budget >= fact){
                    tvOver.setVisibility(View.INVISIBLE);
                }else{
                    tvOver.setVisibility(View.VISIBLE);
                    tvOver.setText(String.valueOf(fact-budget));
                }

                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onAnalyticChanged(){

         switch (getCheckedOperationType()){
            case IN: case OUT:{
                budgetBlock.setVisibility(View.VISIBLE);

                Bundle bundle = new Bundle();
                bundle.putInt("id", Utils.getSelectedId(spinAnalytic));

                getSupportLoaderManager().restartLoader(FACT,bundle,this);
                break;
            }
            case TRANSFER:{
                budgetBlock.setVisibility(View.INVISIBLE);
                break;
            }
        }

    }

}
