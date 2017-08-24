package com.epipasha.cashflow.fragments.category;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import com.epipasha.cashflow.NumberTextWatcherForThousand;
import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.fragments.ListDetailFragment;
import com.epipasha.cashflow.objects.Category;
import com.epipasha.cashflow.objects.OperationType;


public class CategoryListDetailFragment extends ListDetailFragment<Category> {

    private Category category;

    private EditText name, budjet;
    private RadioButton btnIn;
    private RadioButton btnOut;

    private NumberTextWatcherForThousand budjetWatcher;

    @Override
    public void setInstance(Category category) {
        this.category = category;
    }

    @Override
    public Category getInstance() {
        category.setName(name.getText().toString());
        category.setBudjet((int) budjetWatcher.getLong(budjet.getText().toString()));

        OperationType type = null;
        if (btnIn.isChecked()){
            type = OperationType.IN;
        }else if(btnOut.isChecked()){
            type = OperationType.OUT;
        }

        category.setType(type);

        if(category.getID()==0){
            int id = CashFlowDbManager.getInstance(getActivity()).addCategory(category);
            category.setID(id);
        }else{
            CashFlowDbManager.getInstance(getActivity()).updateCategory(category);
        }

        return category;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_detail_category, container, false);

        name = (EditText)v.findViewById(R.id.category_detail_name);
        btnIn = (RadioButton)v.findViewById(R.id.category_detail_in);
        btnOut = (RadioButton)v.findViewById(R.id.category_detail_out);
        budjet = (EditText)v.findViewById(R.id.category_detail_budjet);
        budjetWatcher = new NumberTextWatcherForThousand(budjet);
        budjet.addTextChangedListener(budjetWatcher);

        if(category!=null) {
            name.setText(category.getName());
            budjet.setText(String.valueOf(category.getBudjet()));

            OperationType type = category.getType();

            btnIn.setChecked(type == OperationType.IN||type==null);
            btnOut.setChecked(type == OperationType.OUT);
        }

        return v;
    }
}
