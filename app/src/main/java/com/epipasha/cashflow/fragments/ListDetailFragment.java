package com.epipasha.cashflow.fragments;

import android.app.Fragment;


public abstract class ListDetailFragment<T> extends Fragment {

    public abstract void setInstance(T instance);
    public abstract T getInstance();

}
