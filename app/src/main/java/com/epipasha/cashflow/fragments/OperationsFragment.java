package com.epipasha.cashflow.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.activities.OperationActivity;
import com.epipasha.cashflow.adapters.OperationAdapter;
import com.epipasha.cashflow.data.AppDatabase;
import com.epipasha.cashflow.data.AppExecutors;
import com.epipasha.cashflow.data.entites.OperationWithData;
import com.epipasha.cashflow.viewmodel.OperationsViewModel;
import com.epipasha.cashflow.viewmodel.ViewModelFactory;

import java.util.List;

public class OperationsFragment extends Fragment implements OperationAdapter.ItemClickListener, OperationAdapter.ItemLongClickListener{

    private RecyclerView rvList;
    private OperationAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        rvList = v.findViewById(R.id.rvList);

        initRecycledView();

        retrieveItems();

        return v;
    }

    private void initRecycledView(){

        rvList.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvList.getContext(),
                layoutManager.getOrientation());
        rvList.addItemDecoration(mDividerItemDecoration);

        mAdapter = new OperationAdapter(getContext(), this, this);
        rvList.setAdapter(mAdapter);

    }

    private void retrieveItems() {

        OperationsViewModel viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(getActivity().getApplication())).get(OperationsViewModel.class);
        viewModel.getOperations().observe(this, new Observer<List<OperationWithData>>() {
            @Override
            public void onChanged(@Nullable List<OperationWithData> operations) {
                mAdapter.setOperations(operations);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(getActivity(), OperationActivity.class);
        intent.putExtra(OperationActivity.EXTRA_OPERATION_ID, itemId);
        startActivity(intent);
    }

    @Override
    public void onItemLongClickListener(final int operationId, View view) {

        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.popup_list_item_operation);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_delete_operation){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.dialog_delete_operation)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    deleteOperation(operationId);
                                 }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                    // Create the AlertDialog object and return it
                    builder.create().show();

                    return true;
                }

                return false;
            }
        });
        popupMenu.show();
    }

    private void deleteOperation(final int operationId){
        AppExecutors.getInstance().discIO().execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase mDb = AppDatabase.getInstance(getContext());
                mDb.operationDao().deleteOperationById(operationId);
             }
        });
    }

}
