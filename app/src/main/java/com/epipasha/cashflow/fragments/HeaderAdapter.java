package com.epipasha.cashflow.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.epipasha.cashflow.fragments.Adapter;

public abstract class HeaderAdapter<HeaderHolder extends RecyclerView.ViewHolder, ItemHolder extends RecyclerView.ViewHolder> extends Adapter {
    protected static final int HEADER_ITEM = 234;
    protected static final int LIST_ITEM = 897;

    public HeaderAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {

            case HEADER_ITEM: {
                return onCreateHeaderViewHolder(parent, viewType);
            }

            case LIST_ITEM: {
                 return onCreateItemViewHolder(parent, viewType);
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

         if (position == 0) {

             onBindHeaderViewHolder((HeaderHolder) holder);

        } else {
             onBindItemViewHolder((ItemHolder) holder, position - 1);
         }
    }

    protected abstract void onBindItemViewHolder(ItemHolder holder, int position);

    protected abstract void onBindHeaderViewHolder(HeaderHolder holder);

    protected abstract ItemHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    protected abstract HeaderHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType);

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return HEADER_ITEM;
        } else {
            return LIST_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor == null || mCursor.getCount() == 0) {
            return 0;
        }
        return mCursor.getCount() + 1;
    }

}
