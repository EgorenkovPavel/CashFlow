package com.epipasha.cashflow;


import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.epipasha.cashflow.db.CashFlowContract.AccountBalanceEntry;
import com.epipasha.cashflow.db.CashFlowContract.AccountEntry;
import com.epipasha.cashflow.db.CashFlowContract.CategoryCostEntry;
import com.epipasha.cashflow.db.CashFlowContract.CategoryEntry;
import com.epipasha.cashflow.db.CashFlowContract.OperationEntry;
import com.epipasha.cashflow.db.CashFlowDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;


public class BackupFragment extends Fragment {

    private static final String[] TABLES = new String[]{
            AccountEntry.TABLE_NAME,
            CategoryEntry.TABLE_NAME,
            OperationEntry.TABLE_NAME,
            AccountBalanceEntry.TABLE_NAME,
            CategoryCostEntry.TABLE_NAME};

    public BackupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_backup, container, false);



        Button share = (Button)v.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject db = new JSONObject();
                    for (String table: TABLES) {
                        try {
                            db.put(table, exportDb(table));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    File root = android.os.Environment.getExternalStorageDirectory();
                    File file = new File(root.getAbsolutePath(), "myData.txt");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(db.toString().getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button restore = (Button)v.findViewById(R.id.restore);
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File root = android.os.Environment.getExternalStorageDirectory();
                    File file = new File(root.getAbsolutePath(), "myData.txt");
                    FileInputStream is = new FileInputStream(file);
                    int size = is.available();

                    byte[] buffer = new byte[size];

                    is.close();

                    JSONObject obj = new JSONObject(new String(buffer, "UTF-8"));
                    importDb(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }

    private JSONArray exportDb(String myTable){

        CashFlowDbHelper helper = new CashFlowDbHelper(getActivity());
        SQLiteDatabase db = helper.getReadableDatabase();

        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor = db.rawQuery(searchQuery, null );

        JSONArray resultSet     = new JSONArray();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ ){
                if( cursor.getColumnName(i) != null ){
                    try{
                        if( cursor.getString(i) != null ){
                            Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else{
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }catch( Exception e ){
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString() );
        return resultSet;

    }

    private void importDb(JSONObject obj){

        CashFlowDbHelper helper = new CashFlowDbHelper(getActivity());
        SQLiteDatabase db = helper.getWritableDatabase();

        for (String table: TABLES) {
            try {
                JSONArray rows = obj.getJSONArray(table);
                for (int i=0; i<rows.length(); i++) {
                    JSONObject row = rows.getJSONObject(i);

                    ContentValues values = new ContentValues();

                    Iterator<String> iterator = row.keys();
                    while(iterator.hasNext()){
                        String key = iterator.next();
                        try {
                            values.put(key, Integer.parseInt((String) row.get(key)));
                        }catch (Exception e){
                            values.put(key, (String) row.get(key));
                        }
                    }

                    db.insert(table, null, values);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
