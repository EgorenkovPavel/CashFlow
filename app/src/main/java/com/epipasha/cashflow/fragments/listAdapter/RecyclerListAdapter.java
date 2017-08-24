package com.epipasha.cashflow.fragments.listAdapter;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

public abstract class RecyclerListAdapter<ViewHolder extends RecyclerViewHolder> extends RecyclerView.Adapter<ViewHolder>{

    private RecyclerListFragment frag;
    private Cursor cursor;

    public RecyclerListAdapter(RecyclerListFragment frag) {
        this.frag = frag;
        setCursor();
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                setCursor();
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setData(cursor);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void setCursor() {
        if (cursor != null) {
            cursor.close();
        }

        cursor = frag.getCursor();
    }

}
