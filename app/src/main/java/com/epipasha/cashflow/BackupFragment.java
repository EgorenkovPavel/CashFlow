package com.epipasha.cashflow;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.epipasha.cashflow.db.CashFlowDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class BackupFragment extends Fragment {

    EditText jsonText;
    JSONObject db;

    String[] tables = new String[]{CashFlowDbHelper.TABLE_ACCOUNT,
            CashFlowDbHelper.TABLE_CATEGORY,
            CashFlowDbHelper.TABLE_OPERATION,
            CashFlowDbHelper.TABLE_ACCOUNT_BALANCE,
            CashFlowDbHelper.TABLE_GOAL,
            CashFlowDbHelper.TABLE_CURRENCY,
            CashFlowDbHelper.TABLE_CATEGORY_COST};

    public BackupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_backup, container, false);

        db = new JSONObject();
        for (String table:tables) {
            try {
                db.put(table, exportDb(table));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        jsonText = (EditText)v.findViewById(R.id.json);
        jsonText.setText(db.toString());

        Button share = (Button)v.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, db.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                */


                try {
                    File root = android.os.Environment.getExternalStorageDirectory();
                    File dir = new File(root.getAbsolutePath(), "cashflow");
                    if (!dir.mkdirs()) {
                        Log.e(LOG_TAG, "Directory not created");
                    }
                    File file = new File(root.getAbsolutePath(), "myData.txt");



                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(db.toString().getBytes());
                    outputStream.close();
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
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                            Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
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

}
