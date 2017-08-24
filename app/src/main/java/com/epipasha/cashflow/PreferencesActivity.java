package com.epipasha.cashflow;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.epipasha.cashflow.db.CashFlowDbManager;
import com.epipasha.cashflow.objects.Currency;

import java.util.ArrayList;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        getSupportActionBar().setTitle(getResources().getString(R.string.preferences));

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction tr = fm.beginTransaction();
        tr.add(R.id.container, new PrefFragment());
        tr.commit();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public static class PrefFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            ArrayList<Currency> list = CashFlowDbManager.getInstance(getActivity()).getCurrencies();
            CharSequence[] curs = new CharSequence[list.size()];
            for (int i=0;i<list.size();i++) {
                curs[i]=list.get(i).getName();
            }

            ListPreference listPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_main_currency));
            listPreference.setEntries(curs);
            listPreference.setEntryValues(curs);
        }

    }
}
