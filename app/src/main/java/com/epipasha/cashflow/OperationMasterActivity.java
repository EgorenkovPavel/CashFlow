package com.epipasha.cashflow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.objects.Account;
import com.epipasha.cashflow.objects.Category;
import com.epipasha.cashflow.objects.Operation;
import com.epipasha.cashflow.objects.OperationType;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class OperationMasterActivity extends AppCompatActivity{

    private int sum = 0;

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

        initAccountSpinner();
        initAnalyticSpinner();
    }

    @Override
    protected void onStart() {
        super.onStart();
        restoreFromPrefs();
    }

    private void restoreFromPrefs(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        String operationTypePos = sharedPref.getString(getString(R.string.pref_operation_master_operation_type_pos), "");
        OperationType type = OperationType.toEnum(operationTypePos);

        if (type != null) {
            setCheckedOperationType(type);

            int accountPos = sharedPref.getInt(getString(R.string.pref_operation_master_account_pos), 0);
            int analyticPos = sharedPref.getInt(getAnalyticPref(type), 0);

            spinAccount.setSelection(accountPos);
            spinAnalytic.setSelection(analyticPos);
        }
    }

    private void saveToPrefs(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        OperationType type =  getCheckedOperationType();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.pref_operation_master_account_pos), spinAccount.getSelectedItemPosition());
        editor.putString(getString(R.string.pref_operation_master_operation_type_pos), type.toString());
        editor.putInt(getAnalyticPref(type), spinAnalytic.getSelectedItemPosition());
        editor.apply();

    }

    private String getAnalyticPref(OperationType type){

        switch (type){
            case IN:
                return getString(R.string.pref_operation_master_analytic_in);
            case OUT:
                return getString(R.string.pref_operation_master_analytic_out);
            case TRANSFER:
                return getString(R.string.pref_operation_master_analytic_transfer);
        }
        return "";
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

    private void initAccountSpinner() {

        Account selectedAccount = (Account) spinAccount.getSelectedItem();

        ArrayList<Account> accountList = CashFlowDbManager.getInstance(this).getAccounts();

        AccountAdapter accountArrayAdapter = new AccountAdapter(this, accountList);
        accountArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinAccount.setAdapter(accountArrayAdapter);
        spinAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initAnalyticSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(selectedAccount!=null){
            spinAccount.setSelection(accountList.indexOf(selectedAccount));
        }

    }

    private void initAnalyticSpinner() {
        Object o = spinAnalytic.getSelectedItem();

        switch (groupType.getCheckedRadioButtonId()){
            case R.id.btnIn :
                ArrayList<Category> categories = CashFlowDbManager.getInstance(this).getCategories(OperationType.IN);

                ArrayAdapter<Category> categoryArrayAdapter = new CategoryAdapter(this, categories);
                categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinAnalytic.setAdapter(categoryArrayAdapter);

                if ((o instanceof Category)&&(((Category) o).getType().equals(OperationType.IN))){
                    spinAnalytic.setSelection(categories.indexOf(o));
                }
                break;
            case R.id.btnOut:

                ArrayList<Category> categoriesOut = CashFlowDbManager.getInstance(this).getCategories(OperationType.OUT);

                ArrayAdapter<Category> categoryArrayAdapterOut = new CategoryAdapter(this, categoriesOut);
                categoryArrayAdapterOut.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinAnalytic.setAdapter(categoryArrayAdapterOut);

                if ((o instanceof Category)&&(((Category) o).getType().equals(OperationType.OUT))){
                    spinAnalytic.setSelection(categoriesOut.indexOf(o));
                }

                break;
            case R.id.btnTransfer:

                ArrayList<Account> accountList = CashFlowDbManager.getInstance(this).getAccounts();
                accountList.remove(spinAccount.getSelectedItem());

                AccountAdapter accountArrayAdapter = new AccountAdapter(this, accountList);
                accountArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinAnalytic.setAdapter(accountArrayAdapter);

                if ((o instanceof Account)&&(!spinAccount.getSelectedItem().equals(o))){
                    spinAnalytic.setSelection(accountList.indexOf(o));
                }

                break;
        }
    }

    private void findViews() {

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
        lblSum.setText(String.format(Locale.getDefault(),"%,d",sum));

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

                sum = sum* 10 + digit;
                lblSum.setText(String.format(Locale.getDefault(),"%,d",sum));
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
                sum = sum/10;
                lblSum.setText(String.format(Locale.getDefault(),"%,d",sum));
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

                Operation operation = new Operation();

                Account account = (Account) spinAccount.getSelectedItem();
                if(account==null){
                    Toast.makeText(getApplicationContext(), "No account selected!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                operation.setAccount((Account) spinAccount.getSelectedItem());

                Object analytic = spinAnalytic.getSelectedItem();
                if (analytic==null) {
                    Toast.makeText(getApplicationContext(), "No analytic selected!!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (groupType.getCheckedRadioButtonId()){
                    case R.id.btnIn :
                        operation.setType(OperationType.IN);
                        operation.setCategory((Category) analytic);
                        break;
                    case R.id.btnOut:
                        operation.setType(OperationType.OUT);
                        operation.setCategory((Category) analytic);
                        break;
                    case R.id.btnTransfer:
                        operation.setType(OperationType.TRANSFER);
                        operation.setRecipientAccount((Account) analytic);
                        break;
                }

                if(sum==0){
                    Toast.makeText(getApplicationContext(), "Type the sum", Toast.LENGTH_SHORT).show();
                    return;
                }
                operation.setSum(sum);

                CashFlowDbManager.getInstance(view.getContext()).addOperation(operation);

                Toast t = Toast.makeText(getApplicationContext(), "Operation created!!!", Toast.LENGTH_SHORT);
                t.show();

                saveToPrefs();

                initAccountSpinner();
                initAnalyticSpinner();

                sum = 0;
                lblSum.setText(String.format(Locale.getDefault(),"%,d",sum));

            }
        });

    }

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
}
