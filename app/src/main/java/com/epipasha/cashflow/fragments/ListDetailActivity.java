package com.epipasha.cashflow.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.epipasha.cashflow.MainActivity;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.fragments.account.AccountListDetailFragment;
import com.epipasha.cashflow.fragments.category.CategoryListDetailFragment;
import com.epipasha.cashflow.fragments.operation.OperationListDetailFragment;
import com.epipasha.cashflow.objects.Account;
import com.epipasha.cashflow.objects.Category;
import com.epipasha.cashflow.objects.Operation;

import java.io.Serializable;

public class ListDetailActivity<T> extends AppCompatActivity {

    private ListDetailFragment<T> frag;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Button btnOk = (Button)findViewById(R.id.detail_btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInstance();
                finish();
            }
        });
        Button btnCancel = (Button)findViewById(R.id.detail_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        T instance = (T) getIntent().getSerializableExtra("Instance");
        position = getIntent().getIntExtra("Position", -1);

        if(instance instanceof Account) {
            frag = (ListDetailFragment<T>) new AccountListDetailFragment();
            getSupportActionBar().setTitle(getResources().getString(R.string.account));
        }else if(instance instanceof Category){
            frag = (ListDetailFragment<T>) new CategoryListDetailFragment();
            getSupportActionBar().setTitle(getResources().getString(R.string.category));
        }else if(instance instanceof Operation) {
            frag = (ListDetailFragment<T>) new OperationListDetailFragment();
            getSupportActionBar().setTitle(getResources().getString(R.string.operation));
        }

        FragmentManager fm = getFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        tr.add(R.id.detail_container, frag);
        tr.commit();

        frag.setInstance(instance);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void saveInstance(){

        T instance = frag.getInstance();

        Intent i = new Intent();
        i.putExtra("Instance", (Serializable) instance);
        i.putExtra("Position", position);
        setResult(MainActivity.RESULT_OK, i);
    }
}
