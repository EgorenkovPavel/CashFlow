package com.epipasha.cashflow.fragments.listAdapter;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.epipasha.cashflow.fragments.ListDetailActivity;


public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder{

    public RecyclerViewHolder(View v) {
        super(v);
    }

    public abstract void setData(Cursor c);

}
