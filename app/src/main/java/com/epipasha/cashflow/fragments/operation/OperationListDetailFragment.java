package com.epipasha.cashflow.fragments.operation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.NumberTextWatcherForThousand;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.fragments.ListDetailFragment;
import com.epipasha.cashflow.objects.Account;
import com.epipasha.cashflow.objects.Category;
import com.epipasha.cashflow.objects.Operation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.epipasha.cashflow.objects.OperationType.*;


public class OperationListDetailFragment extends ListDetailFragment<Operation> implements RadioGroup.OnCheckedChangeListener{

    private Operation operation;

    private ArrayList<Account> accountList;
    private List<Category> categoryList;
    private ArrayList<Account> recipientAccountList;
    private ArrayAdapter<Category> categoryArrayAdapter;
    private ArrayAdapter<Account> recipientAccountArrayAdapter;

    private NumberTextWatcherForThousand sumWatcher;

    private Spinner categorySpinner;
    private Spinner accountSpinner;
    private Spinner recipientAccountSpinner;
    private TextView edtDate;
    private EditText edtSum;
    private TextView lblAccount,lblCategory, lblRecipientAccount;

    @Override
    public void setInstance(Operation instance) {
        this.operation = instance;
    }

    @Override
    public Operation getInstance() {

        //operation.setSum(Integer.valueOf(edtSum.getText().toString()));
        operation.setSum((int) sumWatcher.getLong(edtSum.getText().toString()));

        if(operation.getID()==0){
            int id = CashFlowDbManager.getInstance(getActivity()).addOperation(operation);
            operation.setID(id);
        }else{
            CashFlowDbManager.getInstance(getActivity()).updateOperation(operation);
        }

        return operation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_detail_operation, container, false);

        RadioGroup typeGroup = (RadioGroup) v.findViewById(R.id.operation_detail_type_group);
        typeGroup.setOnCheckedChangeListener(this);

        RadioButton btnIn = (RadioButton) v.findViewById(R.id.operation_detail_btnIn);
        RadioButton btnOut = (RadioButton) v.findViewById(R.id.operation_detail_btnOut);
        RadioButton btnTransfer = (RadioButton) v.findViewById(R.id.operation_detail_btnTransfer);

        lblAccount = (TextView)v.findViewById(R.id.operation_detail_label_account);
        lblCategory = (TextView)v.findViewById(R.id.operation_detail_label_category);
        lblRecipientAccount = (TextView)v.findViewById(R.id.operation_detail_label_recipient_account);

        accountSpinner = (Spinner) v.findViewById(R.id.operation_detail_account);
        categorySpinner = (Spinner) v.findViewById(R.id.operation_detail_category);
        recipientAccountSpinner = (Spinner)v.findViewById(R.id.operation_detail_recipient_account);

        edtSum = (EditText)v.findViewById(R.id.operation_detail_sum);
        sumWatcher = new NumberTextWatcherForThousand(edtSum);
        edtSum.addTextChangedListener(sumWatcher);

        edtDate = (TextView) v.findViewById(R.id.operation_detail_date);
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        v.getContext(),
                        dataListener,
                        mYear,
                        mMonth,
                        mDay);
                dialog.show();
            }
        });

        initAccountSpinner();
        initCategorySpinner();
        initRecipientAccountSpinner();

        if (operation != null){
           if(operation.getType()==null){
                operation.setType(IN);
            }



            setViewVisibility();

            Account account = operation.getAccount();
            if(account!=null){
                for (int i=0; i<accountList.size();i++) {
                    if (accountList.get(i).getID()==account.getID()){
                        accountSpinner.setSelection(i);
                    }
                }
            }

            updateDateView();
            setCategoryList();
            setRecipientAccountList();

            switch (operation.getType()) {
                case IN:
                    btnIn.setChecked(true);
                    break;
                case OUT:
                    btnOut.setChecked(true);
                    break;
                case TRANSFER:
                    btnTransfer.setChecked(true);
                    break;
            }

            edtSum.setText(String.valueOf(operation.getSum()));

        }

        return v;
    }

    private void initRecipientAccountSpinner() {
        recipientAccountList = CashFlowDbManager.getInstance(getActivity()).getAccounts();

        recipientAccountArrayAdapter = new AccountAdapter(getActivity(), recipientAccountList);
        recipientAccountArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipientAccountSpinner.setAdapter(recipientAccountArrayAdapter);
        recipientAccountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operation.setRecipientAccount((Account) parent.getAdapter().getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initCategorySpinner() {
        categoryList = CashFlowDbManager.getInstance(getActivity()).getCategories();

        categoryArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryList);
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryArrayAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operation.setCategory((Category) parent.getAdapter().getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setViewVisibility(){

        int categoryVis = operation.getType()==TRANSFER ? View.GONE : View.VISIBLE;
        int repAccountVis = operation.getType()!=TRANSFER ? View.GONE : View.VISIBLE;

        lblCategory.setVisibility(categoryVis);
        categorySpinner.setVisibility(categoryVis);

        lblRecipientAccount.setVisibility(repAccountVis);
        recipientAccountSpinner.setVisibility(repAccountVis);

        lblAccount.setText(operation.getType()==TRANSFER ? getResources().getText(R.string.from) : getResources().getText(R.string.account));
    }

    private void initAccountSpinner() {
        accountList = CashFlowDbManager.getInstance(getActivity()).getAccounts();

        ArrayAdapter<Account> accountArrayAdapter = new AccountAdapter(getActivity(), accountList);
        accountArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(accountArrayAdapter);
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                operation.setAccount((Account)parent.getAdapter().getItem(position));
                setRecipientAccountList();
                if(recipientAccountList.isEmpty()){
                    operation.setRecipientAccount(null);
                }else {
                    operation.setRecipientAccount(recipientAccountList.get(0));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setRecipientAccountList() {
        ArrayList<Account> list = CashFlowDbManager.getInstance(getActivity()).getAccounts();
        recipientAccountList.clear();
        recipientAccountList.addAll(list);

        Account account = operation.getAccount();
        if(account!=null){
            for (int i=0; i<accountList.size();i++) {
                if (accountList.get(i).getID()==account.getID()){
                    recipientAccountList.remove(i);
                }
            }
        }

        recipientAccountArrayAdapter.notifyDataSetChanged();

        account = operation.getRecipientAccount();
        if(account!=null){
            for (int i=0; i<recipientAccountList.size();i++) {
                if (recipientAccountList.get(i).getID()==account.getID()){
                    recipientAccountSpinner.setSelection(i);
                }
            }
        }
    }

    private void setCategoryList() {
        ArrayList<Category> list = CashFlowDbManager.getInstance(getActivity()).getCategories(operation.getType());
        categoryList.clear();
        categoryList.addAll(list);

        categoryArrayAdapter.notifyDataSetChanged();

        Category category = operation.getCategory();
        if(category!=null){
            //categorySpinner.setSelection(categoryList.indexOf(category));
            for (int i=0; i<categoryList.size();i++) {
                if (categoryList.get(i).getID()==category.getID()){
                    categorySpinner.setSelection(i);
                }
            }
        }
    }

    private void updateDateView() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        edtDate.setText(format.format(operation.getDate()));
    }

    private final DatePickerDialog.OnDateSetListener dataListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            Date date = operation.getDate();
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            //GregorianCalendar gc = new GregorianCalendar();
            c.set(year, monthOfYear,dayOfMonth,mHour,mMinute);
            date = new Date(c.getTimeInMillis());
            operation.setDate(date);

            TimePickerDialog dialog = new TimePickerDialog(
                    view.getContext(),
                    timeListener,
                    mHour,
                    mMinute,
                    true);
            dialog.show();

            updateDateView();
        }
    };

    private final TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {

            Date date = operation.getDate();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY, i);
            c.set(Calendar.MINUTE, i1);

            date = new Date(c.getTimeInMillis());
            operation.setDate(date);

            updateDateView();
        }
    };

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case -1:
                break;
            case R.id.operation_detail_btnIn:
                operation.setType(IN);
                setCategoryList();
                if(categoryList.isEmpty()){
                    operation.setCategory(null);
                }else {
                    operation.setCategory(categoryList.get(0));
                }
                break;
            case R.id.operation_detail_btnOut:
                operation.setType(OUT);
                setCategoryList();
                if(categoryList.isEmpty()){
                    operation.setCategory(null);
                }else {
                    operation.setCategory(categoryList.get(0));
                }
                break;
            case R.id.operation_detail_btnTransfer:
                operation.setType(TRANSFER);
                setRecipientAccountList();
                if(recipientAccountList.isEmpty()){
                    operation.setRecipientAccount(null);
                }else {
                    operation.setRecipientAccount(recipientAccountList.get(0));
                }
                break;

            default:
                break;
        }
        setViewVisibility();
    }

    private class AccountAdapter extends ArrayAdapter<Account> {

        private final ArrayList<Account> accounts;

        public AccountAdapter(Context context, ArrayList<Account> accounts) {
            super(context, R.layout.account_spinner_adapter, accounts);
            this.accounts = accounts;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            Account account = accounts.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.account_spinner_adapter,parent,false);
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
}
