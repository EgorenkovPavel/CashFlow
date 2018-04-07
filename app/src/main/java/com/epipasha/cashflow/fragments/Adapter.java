package com.epipasha.cashflow.fragments;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

public abstract class Adapter<Holder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<Holder> {
    protected Cursor mCursor;
    protected Context mContext;

    public Adapter(Context mContext) {
        this.mContext = mContext;
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }
}
