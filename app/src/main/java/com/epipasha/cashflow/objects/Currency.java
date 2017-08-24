package com.epipasha.cashflow.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.DropBoxManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.epipasha.cashflow.R;
import com.epipasha.cashflow.db.CashFlowDbManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pavel on 17.12.2016.
 */

public class Currency implements Serializable{

    private int Id;
    private String name;
    private float rate;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public static void loadRate(Context context){
        new FeatchCurrency().execute(context);
    }

    @Override
    public String toString() {
        return name;
    }

    //-----------------------------------------------------------------------------------------

    private static byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer))>0){
                out.write(buffer, 0 , bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    private static String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private static final String TAG = "CashFlowLog";

    private static void fetchItems(ArrayList<Currency> list, String defCur){

        HashMap<String,Currency> map = new HashMap<>();
        StringBuilder strBuilder = new StringBuilder();
        for (Currency currency:list) {
            if (!currency.getName().equals(defCur)){
                strBuilder.append(strBuilder.length()==0?"":",").append("\"").append(currency.getName()).append(defCur).append("\"");
                map.put(currency.getName()+defCur, currency);
            }else{
                currency.setRate(1);
            }

        }

        try{
            String url = Uri.parse("http://query.yahooapis.com/v1/public/yql")
                    .buildUpon()
                    .appendQueryParameter("q", "select * from yahoo.finance.xchange where pair in ("+strBuilder.toString()+")") //(\"USDRUB\",\"USDJPY\")")
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("diagnostics", "false")
                    .appendQueryParameter("env", "store://datatables.org/alltableswithkeys")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Reseived JSON " + jsonString);

            JSONObject jsonBody = new JSONObject(jsonString);

            JSONObject query = jsonBody.getJSONObject("query");
            JSONArray results = query.getJSONObject("results").getJSONArray("rate");
            for (int i=0; i < results.length(); i++){
                JSONObject item = results.getJSONObject(i);

                Currency cur = map.get(item.getString("id"));
                cur.setRate(Float.parseFloat(item.getString("Rate")));

                Log.i(TAG, item.getString("Name"));
                Log.i(TAG, item.getString("Rate"));

            }
        } catch (IOException e) {
            Log.i(TAG, "Failed to connect", e);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static class FeatchCurrency extends AsyncTask<Context,Void,Void> {

        @Override
        protected Void doInBackground(Context... con) {
            ArrayList<Currency> list = CashFlowDbManager.getInstance(con[0]).getCurrencies();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con[0]);
            String defCur = prefs.getString(con[0].getResources().getString(R.string.pref_main_currency), con[0].getResources().getString(R.string.RUB));
            fetchItems(list, defCur);
            for (Currency cur:list) {
                CashFlowDbManager.getInstance(con[0]).updateCurrency(cur);
            }

            return null;
        }
    }

}
