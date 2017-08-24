package com.epipasha.cashflow.fragments;

import android.app.Fragment;

/**
 * Created by Pavel on 13.11.2016.
 */

public abstract class ListDetailFragment<T> extends Fragment {

    public abstract void setInstance(T instance);
    public abstract T getInstance();

}
