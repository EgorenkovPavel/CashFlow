package com.epipasha.cashflow.fragments.goal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.epipasha.cashflow.NumberTextWatcherForThousand;
import com.epipasha.cashflow.R;
import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.fragments.ListDetailFragment;
import com.epipasha.cashflow.objects.Category;
import com.epipasha.cashflow.objects.Goal;
import com.epipasha.cashflow.objects.OperationType;


public class GoalListDetailFragment extends ListDetailFragment<Goal> {

    private Goal goal;

    private EditText name, sum;
    private CheckBox done;
    private NumberTextWatcherForThousand sumWatcher;

    @Override
    public void setInstance(Goal goal) {
        this.goal = goal;
    }

    @Override
    public Goal getInstance() {
        goal.setName(name.getText().toString());
        goal.setSum((int) sumWatcher.getLong(sum.getText().toString()));
        goal.setDone(done.isChecked());

        if(goal.getId()==0){
            int id = CashFlowDbManager.getInstance(getActivity()).addGoal(goal);
            goal.setId(id);
        }else{
            CashFlowDbManager.getInstance(getActivity()).updateGoal(goal);
        }

        return goal;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_detail_goal, container, false);

        name = (EditText)v.findViewById(R.id.name);
        sum = (EditText)v.findViewById(R.id.sum);
        sumWatcher = new NumberTextWatcherForThousand(sum);
        sum.addTextChangedListener(sumWatcher);
        done = (CheckBox)v.findViewById(R.id.done);

        if(goal!=null) {
            name.setText(goal.getName());
            sum.setText(String.valueOf(goal.getSum()));
            done.setChecked(goal.isDone());
        }

        return v;
    }
}
