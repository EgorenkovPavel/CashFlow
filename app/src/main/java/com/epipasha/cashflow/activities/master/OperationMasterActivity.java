package com.epipasha.cashflow.activities.master;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.activities.BaseActivity;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.ViewModelFactory;
import com.epipasha.cashflow.data.complex.AccountWithBalance;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.databinding.ActivityMasterBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OperationMasterActivity extends BaseActivity {

    private OperationMasterViewModel model;

    private AccountAdapter mAccountAdapter;
    private CategoryAdapter mCategoryAdapter;
    private CategoryAdapter mSubcategoryAdapter;
    private AccountAdapter mRecAccountAdapter;

    private RecyclerView rvAccounts;
    private RecyclerView rvCategories;
    private RecyclerView rvSubcategories;
    private RecyclerView rvRecAccounts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMasterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_master);

        findViews();

        model = ViewModelProviders.of(this,
                ViewModelFactory.getInstance(getApplication()))
                .get(OperationMasterViewModel.class);

        binding.setViewmodel(model);

        model.getAccounts().observe(this, accounts -> {
            mAccountAdapter.setItems(accounts);
            mRecAccountAdapter.setItems(accounts);
        });

        model.getCategories().observe(this, categories -> mCategoryAdapter.setItems(categories));

        model.getSubcategories().observe(this, categories -> mSubcategoryAdapter.setItems(categories));

        model.getSelectedAccount().observe(this, id -> mAccountAdapter.setSelectedId(id));

        model.getSelectedCategory().observe(this, id -> mCategoryAdapter.setSelectedId(id));

        model.getSelectedSubcategory().observe(this, id -> mSubcategoryAdapter.setSelectedId(id));

        model.getSelectedRepAccount().observe(this, id -> mRecAccountAdapter.setSelectedId(id));

        model.getStatus().observe(this, status -> {
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
                    snackbar.setAction(R.string.undo, view -> AppExecutors.getInstance().discIO().execute(() -> model.deleteOperation()));
                    snackbar.show();
                    break;
                }
                case OPERATION_DELETED:{
                    Snackbar.make(rvAccounts, R.string.operation_deleted, Snackbar.LENGTH_LONG).show();
                    break;
                }
                case CLOSE:{
                    setResult(RESULT_OK);
                    finish();
                    break;
                }
            }
        });
    }

    private void findViews() {

        rvAccounts = findViewById(R.id.rvAccounts);
        rvCategories = findViewById(R.id.rvCategories);
        rvSubcategories = findViewById(R.id.rvSubcategories);
        rvRecAccounts = findViewById(R.id.rvRecAccounts);

        rvAccounts.setHasFixedSize(true);
        rvCategories.setHasFixedSize(true);
        rvSubcategories.setHasFixedSize(true);
        rvRecAccounts.setHasFixedSize(true);

        rvAccounts.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvRecAccounts.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        rvCategories.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvSubcategories.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        DividerItemDecoration mAccountDividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL);
        rvAccounts.addItemDecoration(mAccountDividerItemDecoration);
        rvRecAccounts.addItemDecoration(mAccountDividerItemDecoration);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        rvCategories.addItemDecoration(mDividerItemDecoration);
        rvSubcategories.addItemDecoration(mDividerItemDecoration);

        mAccountAdapter = new AccountAdapter();
        mAccountAdapter.setListener(item -> model.selectAccount(item));
        rvAccounts.setAdapter(mAccountAdapter);

        mCategoryAdapter = new CategoryAdapter();
        mCategoryAdapter.setListener(item -> model.selectCategory(item));
        rvCategories.setAdapter(mCategoryAdapter);

        mSubcategoryAdapter = new CategoryAdapter();
        mSubcategoryAdapter.setListener(item -> model.selectSubcategory(item));
        rvSubcategories.setAdapter(mSubcategoryAdapter);

        mRecAccountAdapter = new AccountAdapter();
        mRecAccountAdapter.setListener(item -> model.selectRepAccount(item));
        rvRecAccounts.setAdapter(mRecAccountAdapter);
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

                itemView.setOnClickListener(view -> {
                    if (listener != null) listener.onItemClick(items.get(getLayoutPosition()));
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

                itemView.setOnClickListener(view -> {
                    if (listener != null) listener.onItemClick(items.get(getLayoutPosition()));
                });
            }
        }
    }

    private interface ItemClickListener<T>{
        void onItemClick(T item);
    }
}
