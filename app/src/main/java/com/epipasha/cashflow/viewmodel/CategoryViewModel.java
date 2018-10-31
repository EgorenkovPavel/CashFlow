package com.epipasha.cashflow.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.epipasha.cashflow.Utils;
import com.epipasha.cashflow.data.DataSource;
import com.epipasha.cashflow.data.Repository;
import com.epipasha.cashflow.data.dao.AnalyticDao;
import com.epipasha.cashflow.data.entites.Category;
import com.epipasha.cashflow.objects.OperationType;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;

import java.util.List;
import java.util.Locale;

public class CategoryViewModel extends AndroidViewModel{

    private DataSource mRepository;

    private ObservableField<Category> mCategory = new ObservableField<>();
    private LiveData<List<AnalyticDao.MonthCashflow>> mMonthCashflow;
    private ObservableBoolean isNew = new ObservableBoolean(true);
    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            Category category = mCategory.get();
            int budget = (int)Utils.getLong(s.toString());

            if(category.getBudget() != budget){
                category.setBudget(budget);
                mCategory.notifyChange();
            }
        }
    };

    public CategoryViewModel(@NonNull Application application, Repository repository) {
        super(application);

        mRepository = repository;
        mCategory.set(new Category("", OperationType.IN, 0));

    }

    public void start(int categoryId){
        mRepository.getCategoryById(categoryId, new DataSource.GetCategoryCallback() {
            @Override
            public void onCategoryLoaded(Category category) {
                mCategory.set(category);
                isNew.set(false);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
        mMonthCashflow = mRepository.loadMonthCashflow(categoryId);
    }

    public ObservableBoolean getIsNew() {
        return isNew;
    }

    public TextWatcher getWatcher() {
        return mWatcher;
    }

    public void setOperationType(OperationType type){
        mCategory.get().setType(type);
    }

    public ObservableField<Category> getCategory() {
        return mCategory;
    }

    public LiveData<List<AnalyticDao.MonthCashflow>> getMonthCashflow() {
        return mMonthCashflow;
    }

    public void saveObject(){
        Category category = mCategory.get();

        //TODO add check fields
        if(category == null){
            return;
        }

        if(isNew.get()){
            mRepository.insertCategory(category);
        }else{
            mRepository.updateCategory(category);
        }
    }

    @BindingAdapter({"app:budget"})
    public static void setBudget(EditText view, int budget) {
        view.setText(String.format(Locale.getDefault(),"%,d", budget));
        view.setSelection(view.getText().toString().length());
    }

    @BindingAdapter({"app:budgetLine"})
    public static void selBudgetLine(BarChart chart, int budget){
        chart.getAxisLeft().getLimitLines().clear();

        YAxis yAxis = chart.getAxisLeft();
        LimitLine line = new LimitLine(budget);
        line.setEnabled(true);
        line.setLineWidth(3);
//        line.setLineColor(getResources().getColor(R.color.colorAccent));
//        line.setTextColor(getResources().getColor(R.color.colorAccent));
//        line.setLabel(getString(R.string.budget));
        yAxis.addLimitLine(line);
    }

}
