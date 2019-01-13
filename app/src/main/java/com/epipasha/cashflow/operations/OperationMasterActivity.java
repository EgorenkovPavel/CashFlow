package com.epipasha.cashflow.operations;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.activities.BaseActivity;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.entites.AccountWithBalance;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.data.ViewModelFactory;
import com.epipasha.cashflow.databinding.ActivityMasterBinding;
import com.epipasha.cashflow.objects.OperationType;

import java.util.List;
import java.util.Locale;

public class OperationMasterActivity extends BaseActivity {

    private OperationMasterViewModel model;

    private AccountAdapter mAccountAdapter;
    private CategoryAdapter mCategoryInAdapter;
    private CategoryAdapter mCategoryOutAdapter;
    private AccountAdapter mRecAccountAdapter;

    private RecyclerView rvAccounts;
    private RecyclerView rvAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMasterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_master);

        findViews();

        initAdapters();

        rvAccounts.setAdapter(mAccountAdapter);

        model = ViewModelProviders.of(this,
                ViewModelFactory.getInstance(getApplication()))
                .get(OperationMasterViewModel.class);

        binding.setViewmodel(model);

        model.getAccounts().observe(this, new Observer<List<AccountWithBalance>>() {
            @Override
            public void onChanged(@Nullable List<AccountWithBalance> accounts) {
                mAccountAdapter.setItems(accounts);
                mRecAccountAdapter.setItems(accounts);
            }
        });

        model.getCategoriesIn().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                mCategoryInAdapter.setItems(categories);
            }
        });

        model.getCategoriesOut().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable List<Category> categories) {
                mCategoryOutAdapter.setItems(categories);
            }
        });

        model.getOperationType().observe(this, new Observer<OperationType>() {
            @Override
            public void onChanged(@Nullable OperationType type) {
                if(type == null) return;

                 switch (type){
                    case IN:{
                        rvAnalytics.setAdapter(mCategoryInAdapter);
                        break;
                    }
                    case OUT:{
                        rvAnalytics.setAdapter(mCategoryOutAdapter);
                        break;
                    }
                    case TRANSFER: {
                        rvAnalytics.setAdapter(mRecAccountAdapter);
                        break;
                    }
                }
            }
        });

        model.getSelectedAccount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer id) {
                mAccountAdapter.setSelectedId(id);
            }
        });

        model.getSelectedInCategory().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer id) {
                mCategoryInAdapter.setSelectedId(id);
            }
        });

        model.getSelectedOutCategory().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer id) {
                mCategoryOutAdapter.setSelectedId(id);
            }
        });

        model.getSelectedRepAccount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer id) {
                mRecAccountAdapter.setSelectedId(id);
            }
        });

        model.getStatus().observe(this, new Observer<OperationMasterViewModel.Status>() {
            @Override
            public void onChanged(@Nullable OperationMasterViewModel.Status status) {
                if(status == null) return;
                switch (status){
                    case EMPTY_SUM:{
                        Snackbar.make(rvAccounts, R.string.no_sum, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    case EMPTY_TYPE:{
                        Snackbar.make(rvAccounts, R.string.no_type, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    case EMPTY_ANALYTIC:{
                        Snackbar.make(rvAccounts, R.string.no_analytic_selected, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    case EMPTY_ACCOUNT:{
                        Snackbar.make(rvAccounts, R.string.no_account_selected, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                    case OPERATION_SAVED: {
                        Snackbar snackbar = Snackbar.make(rvAccounts, R.string.operation_created, Snackbar.LENGTH_LONG);
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
                        Snackbar.make(rvAccounts, R.string.operation_deleted, Snackbar.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        });

    }

    private void initAdapters(){
        mAccountAdapter = new AccountAdapter();
        mAccountAdapter.setListener(new ItemClickListener<AccountWithBalance>() {
            @Override
            public void onItemClick(AccountWithBalance item) {
                model.selectAccount(item);
            }
        });

        mCategoryInAdapter = new CategoryAdapter();
        mCategoryInAdapter.setListener(new ItemClickListener<Category>() {
            @Override
            public void onItemClick(Category item) {
                model.selectInCategory(item);
            }
        });

        mCategoryOutAdapter = new CategoryAdapter();
        mCategoryOutAdapter.setListener(new ItemClickListener<Category>() {
            @Override
            public void onItemClick(Category item) {
                model.selectOutCategory(item);
            }
        });

        mRecAccountAdapter = new AccountAdapter();
        mRecAccountAdapter.setListener(new ItemClickListener<AccountWithBalance>() {
            @Override
            public void onItemClick(AccountWithBalance item) {
                model.selectRepAccount(item);
            }
        });
    }

    private void findViews() {

        rvAccounts = findViewById(R.id.rvAccounts);
        rvAnalytics = findViewById(R.id.rvAnalytics);

        rvAccounts.setHasFixedSize(true);
        rvAnalytics.setHasFixedSize(true);

        rvAccounts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvAnalytics.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        rvAccounts.addItemDecoration(mDividerItemDecoration);
        rvAnalytics.addItemDecoration(mDividerItemDecoration);

        Button btnMore = findViewById(R.id.btnMore);
        Button btnNext = findViewById(R.id.btnNext);

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

    private class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder>{

        private List<AccountWithBalance> items;
        private Integer selectedId;
        private ItemClickListener<AccountWithBalance> listener;

        @NonNull
        @Override
        public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_master_account, parent, false);
            return new AccountViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
            AccountWithBalance account = items.get(position);

            holder.name.setText(account.getTitle());
            holder.sum.setText(String.format(Locale.getDefault(), "%,d", account.getSum()));

            holder.itemView.setSelected(selectedId != null && selectedId == account.getId());
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        public void setItems(List<AccountWithBalance> items){
            this.items = items;
            notifyDataSetChanged();
        }

        public void setListener(ItemClickListener<AccountWithBalance> listener) {
            this.listener = listener;
        }

        public void setSelectedId(Integer id){
            this.selectedId = id;
            notifyDataSetChanged();
        }

        class AccountViewHolder extends RecyclerView.ViewHolder{

            TextView name;
            TextView sum;

            public AccountViewHolder(View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.account_list_item_name);
                sum = itemView.findViewById(R.id.account_list_item_sum);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) listener.onItemClick(items.get(getLayoutPosition()));
                    }
                });
            }
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{

        private List<Category> items;
        private Integer selectedId;
        private ItemClickListener<Category> listener;

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_master_category, parent, false);
            return new CategoryViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            Category category = items.get(position);

            holder.name.setText(category.getTitle());

            holder.itemView.setSelected(selectedId != null && selectedId == category.getId());
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }

        public void setItems(List<Category> items){
            this.items = items;
            notifyDataSetChanged();
        }

        public void setListener(ItemClickListener<Category> listener) {
            this.listener = listener;
        }

        public void setSelectedId(Integer id){
            this.selectedId = id;
            notifyDataSetChanged();
        }

        class CategoryViewHolder extends RecyclerView.ViewHolder{

            TextView name;

            public CategoryViewHolder(View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.lbl_in);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) listener.onItemClick(items.get(getLayoutPosition()));
                    }
                });
             }
        }
    }

    private interface ItemClickListener<T>{
        void onItemClick(T item);
    }
}
