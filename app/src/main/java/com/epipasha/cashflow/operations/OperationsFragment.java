package com.epipasha.cashflow.operations;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.data.ViewModelFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OperationsFragment extends Fragment implements OperationAdapter.ItemClickListener, OperationAdapter.ItemLongClickListener{

    private RecyclerView rvList;
    private OperationAdapter mAdapter;
    private OperationsViewModel model;

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

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvList.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvList.getContext(),
                layoutManager.getOrientation());
        rvList.addItemDecoration(mDividerItemDecoration);

        mAdapter = new OperationAdapter(getContext(), this, this);
        rvList.setAdapter(mAdapter);

    }

    private void retrieveItems() {

        model = ViewModelProviders
                .of(this, ViewModelFactory.getInstance(getActivity().getApplication()))
                .get(OperationsViewModel.class);
        model.getOperations().observe(this, operations -> mAdapter.setOperations(operations));
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(getActivity(), OperationActivity.class);
        intent.putExtra(OperationActivity.EXTRA_OPERATION_ID, itemId);
        startActivity(intent);
    }

    @Override
    public void onItemLongClickListener(final int operationId, final View view) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.popup_list_item_operation);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getItemId() == R.id.action_delete_operation){

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage(R.string.dialog_delete_operation)
                        .setPositiveButton(R.string.ok, (dialog, id) -> model.deleteOperation(operationId))
                        .setNegativeButton(R.string.cancel, (dialog, id) -> mAdapter.notifyDataSetChanged());
                // Create the AlertDialog object and return it
                builder.create().show();

                return true;
            }

            return false;
        });
        popupMenu.show();
    }


}
