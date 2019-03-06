package com.epipasha.cashflow.categories;

import android.app.Application;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryViewModel extends AndroidViewModel{

    private DataSource mRepository;

    private ObservableField<Category> mCategory = new ObservableField<>();
    private ObservableInt mParentCategoryPosition = new ObservableInt(0);
    private ObservableField<List<Category>> mParentCategories = new ObservableField<>();

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
            if(category == null) return;
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
        mCategory.set(new Category("", OperationType.IN, 0, null));
        mParentCategories.set(new ArrayList<>());
    }

    public void start(int categoryId){
        mRepository.getCategoryById(categoryId, new DataSource.GetCategoryCallback() {
            @Override
            public void onCategoryLoaded(Category category) {
                mCategory.set(category);
                isNew.set(false);
                loadParentCategories();
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
        mMonthCashflow = mRepository.loadMonthCashflow(categoryId);


    }

    public void start(){
        loadParentCategories();
    }

    private void loadParentCategories(){
        mRepository.getParentCategories(mCategory.get().getType(), new DataSource.GetCategoriesCallback(){

            @Override
            public void onCategoriesLoaded(List<Category> categories) {
                if(!isNew.get()){
                    int id = mCategory.get().getId();
                    for (Category cat:categories) {
                        if (cat.getId() == id){
                            categories.remove(cat);
                            break;
                        }
                    }
                }
                categories.add(0, null);
                mParentCategories.set(categories);
                setParentCategoryPosition();
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }
    //TODO rewrite loading to rx. wait loading and then set parent id

    public ObservableInt getParentCategoryPosition() {
        return mParentCategoryPosition;
    }

    public ObservableField<List<Category>> getParentCategories() {
        return mParentCategories;
    }

    private void setParentCategoryPosition(){
        //if(!isNew.get())
            mParentCategoryPosition.set(getPositionById(mParentCategories.get().toArray(), mCategory.get().getParentId()));
    }

    private int getPositionById(Object[] list, Integer id){

        for (int i=0; i<list.length;i++){
            Object category = list[i];

            if(category == null)
                if(id == null) return i;
                else continue;
            if(((Category)category).getId() == id) {
                return i;
            }
        }
        return 0;
    }
    public ObservableBoolean getIsNew() {
        return isNew;
    }

    public TextWatcher getWatcher() {
        return mWatcher;
    }

    public void setOperationType(OperationType type){
        Category category = mCategory.get();
        if(category == null) return;
        category.setType(type);
        category.setParentId(null);
        loadParentCategories();
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

        category.setParentId(mParentCategories.get().get(mParentCategoryPosition.get()).getId());

        mRepository.insertCategory(category);
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
