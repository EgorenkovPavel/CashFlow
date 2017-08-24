package com.epipasha.cashflow;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.fragments.operation.OperationListDetailFragment;
import com.epipasha.cashflow.objects.Account;
import com.epipasha.cashflow.objects.Category;
import com.epipasha.cashflow.objects.Operation;
import com.epipasha.cashflow.objects.OperationType;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class OperationMaster extends AppCompatActivity{

    private int sum = 0;
    private ArrayList<OperationType> operationTypes;
    private ArrayList<Account> accounts;
    private ArrayList<Category> categories;
    private ArrayList<Account> repAccounts;

    private RadioGroup groupType;
    private Spinner spinAccount, spinAnalitic;
    private TextView lblSum;
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnMore, btnNext;
    private ImageButton btnBack;
    private HorizontalBarChart chart;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operation_master);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        findViews();

        initAccountSpinner();
        initAnaliticSpinner();

        setChartData();
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
                initAnaliticSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(selectedAccount!=null){
            spinAccount.setSelection(accountList.indexOf(selectedAccount));
        }

    }

    private void initAnaliticSpinner() {
        Object o = spinAnalitic.getSelectedItem();

        switch (groupType.getCheckedRadioButtonId()){
            case R.id.btnIn :
                ArrayList<Category> categories = CashFlowDbManager.getInstance(this).getCategories(OperationType.IN);

                ArrayAdapter<Category> categoryArrayAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categories);
                categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinAnalitic.setAdapter(categoryArrayAdapter);

                if ((o instanceof Category)&&(((Category) o).getType().equals(OperationType.IN))){
                    spinAnalitic.setSelection(categories.indexOf(o));
                }
                break;
            case R.id.btnOut:

                ArrayList<Category> categoriesOut = CashFlowDbManager.getInstance(this).getCategories(OperationType.OUT);

                ArrayAdapter<Category> categoryArrayAdapterOut = new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_item, categoriesOut);
                categoryArrayAdapterOut.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinAnalitic.setAdapter(categoryArrayAdapterOut);

                if ((o instanceof Category)&&(((Category) o).getType().equals(OperationType.OUT))){
                    spinAnalitic.setSelection(categoriesOut.indexOf(o));
                }

                break;
            case R.id.btnTransfer:

                ArrayList<Account> accountList = CashFlowDbManager.getInstance(this).getAccounts();
                accountList.remove(spinAccount.getSelectedItem());

                AccountAdapter accountArrayAdapter = new AccountAdapter(this, accountList);
                accountArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinAnalitic.setAdapter(accountArrayAdapter);

                if ((o instanceof Account)&&(!spinAccount.getSelectedItem().equals((Account)o))){
                    spinAnalitic.setSelection(accountList.indexOf(o));
                }

                break;
        }
    }

    private void findViews() {

        chart = (HorizontalBarChart)findViewById(R.id.chart);

        groupType = (RadioGroup)findViewById(R.id.type_group);
        groupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                initAnaliticSpinner();
                setChartData();
            }
        });

        spinAccount = (Spinner)findViewById(R.id.spinner_account);
        spinAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setChartData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinAnalitic = (Spinner)findViewById(R.id.spinner_analitic);
        spinAnalitic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setChartData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lblSum = (TextView) findViewById(R.id.operation_master_sum);
        lblSum.setText(String.format("%,d",sum));

        btn0 = (Button)findViewById(R.id.digit_0);
        btn1 = (Button)findViewById(R.id.digit_1);
        btn2 = (Button)findViewById(R.id.digit_2);
        btn3 = (Button)findViewById(R.id.digit_3);
        btn4 = (Button)findViewById(R.id.digit_4);
        btn5 = (Button)findViewById(R.id.digit_5);
        btn6 = (Button)findViewById(R.id.digit_6);
        btn7 = (Button)findViewById(R.id.digit_7);
        btn8 = (Button)findViewById(R.id.digit_8);
        btn9 = (Button)findViewById(R.id.digit_9);
        btnBack = (ImageButton)findViewById(R.id.digit_back);
        btnMore = (Button)findViewById(R.id.operation_master_more);
        btnNext = (Button)findViewById(R.id.operation_master_next);

        View.OnClickListener onDigitClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int digit = 0;
                switch (view.getId()){
                    case R.id.digit_0:
                        digit = 0;
                        break;
                    case R.id.digit_1:
                        digit = 1;
                        break;
                    case R.id.digit_2:
                        digit = 2;
                        break;
                    case R.id.digit_3:
                        digit = 3;
                        break;
                    case R.id.digit_4:
                        digit = 4;
                        break;
                    case R.id.digit_5:
                        digit = 5;
                        break;
                    case R.id.digit_6:
                        digit = 6;
                        break;
                    case R.id.digit_7:
                        digit = 7;
                        break;
                    case R.id.digit_8:
                        digit = 8;
                        break;
                    case R.id.digit_9:
                        digit = 9;
                        break;
                }

                sum = sum* 10 + digit;
                lblSum.setText(String.format("%,d",sum));
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
                lblSum.setText(String.format("%,d",sum));
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

                Object analitic = spinAnalitic.getSelectedItem();
                if (analitic==null) {
                    Toast.makeText(getApplicationContext(), "No analitic selected!!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (groupType.getCheckedRadioButtonId()){
                    case R.id.btnIn :
                        operation.setType(OperationType.IN);
                        operation.setCategory((Category) analitic);
                        break;
                    case R.id.btnOut:
                        operation.setType(OperationType.OUT);
                        operation.setCategory((Category) analitic);
                        break;
                    case R.id.btnTransfer:
                        operation.setType(OperationType.TRANSFER);
                        operation.setRecipientAccount((Account) analitic);
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

                initAccountSpinner();
                initAnaliticSpinner();

                setChartData();

                sum = 0;
                lblSum.setText(String.format("%,d",sum));
            }
        });

    }

    private void setChartData(){

        GregorianCalendar c = new GregorianCalendar();
        Date end = new Date(c.getTimeInMillis());

        c.set(GregorianCalendar.DAY_OF_MONTH, c.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));
        c.set(GregorianCalendar.HOUR_OF_DAY, 0);
        c.set(GregorianCalendar.MINUTE, 0);
        c.set(GregorianCalendar.SECOND, 0);
        c.set(GregorianCalendar.MILLISECOND, 0);
        Date start = new Date(c.getTimeInMillis());

        Category cat;
        int sum = 0, budjet = 0;
        switch (groupType.getCheckedRadioButtonId()) {
            case R.id.btnIn:
                cat = (Category) spinAnalitic.getSelectedItem();
                sum = CashFlowDbManager.getInstance(this).getCategorySum(cat, start, end);
                budjet = CashFlowDbManager.getInstance(this).getCategoryBudjet(cat, start, end);
                break;
            case R.id.btnOut:
                cat = (Category) spinAnalitic.getSelectedItem();
                sum = CashFlowDbManager.getInstance(this).getCategorySum(cat, start, end);
                budjet = CashFlowDbManager.getInstance(this).getCategoryBudjet(cat, start, end);
                break;
            case R.id.btnTransfer:
                Account accountFrom = (Account) spinAccount.getSelectedItem();
                Account accountTo = (Account) spinAnalitic.getSelectedItem();
                sum = accountFrom.getBalance();
                budjet = accountTo.getBalance();
                break;
        }

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, budjet));
        BarDataSet set = new BarDataSet(entries, getString(R.string.budjet));
        set.setColor(ContextCompat.getColor(this, R.color.budjet));
        set.setValueTextSize(10);

        List<BarEntry> entries1 = new ArrayList<>();
        entries1.add(new BarEntry(1f, sum));
        BarDataSet set1 = new BarDataSet(entries1, getString(R.string.fact));
        set1.setColor(ContextCompat.getColor(this, R.color.fact));
        set1.setValueTextSize(10);

        BarData data = new BarData();
        data.addDataSet(set);
        data.addDataSet(set1);

        final String[] labels = {getString(R.string.budjet), getString(R.string.fact)};

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextSize(10);

        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0f);
        left.setEnabled(false);

        YAxis rigth = chart.getAxisRight();
        rigth.setAxisMinimum(0f);
        rigth.setEnabled(false);

        chart.getLegend().setEnabled(false);

        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);

        data.setBarWidth(0.9f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        chart.invalidate(); // refresh

    }

    private class AccountAdapter extends ArrayAdapter<Account> {

        private ArrayList<Account> accounts;

        public AccountAdapter(Context context, ArrayList<Account> accounts) {
            super(context, R.layout.account_spinner_adapter, accounts);
            this.accounts = accounts;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Account account = accounts.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.account_spinner_adapter, null);
            }
            ((TextView) convertView.findViewById(R.id.account_spinner_adapter_name))
                    .setText(account.getName());
            ((TextView) convertView.findViewById(R.id.account_spinner_adapter_balance))
                    .setText(String.format("%,d",account.getBalance()) + " " + account.getCurrency().toString());
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            Account account = accounts.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.account_spinner_adapter_dropdown, null);
            }
            ((TextView) convertView.findViewById(R.id.account_spinner_adapter_dropdown_name))
                    .setText(account.getName());
            ((TextView) convertView.findViewById(R.id.account_spinner_adapter_dropdown_balance))
                    .setText(String.format("%,d",account.getBalance()) + " " + account.getCurrency().toString());
            return convertView;
        }
    }

}
