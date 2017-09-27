package com.epipasha.cashflow;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberTextWatcherForThousand implements TextWatcher {

    private final EditText editText;


    public NumberTextWatcherForThousand(EditText editText) {
        this.editText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try
        {
            editText.removeTextChangedListener(this);
            String value = editText.getText().toString();

            if (!value.equals(""))
            {
                if(value.startsWith(".")){
                    editText.setText("0.");
                }
                if(value.startsWith("0") && !value.startsWith("0.")){
                    editText.setText("");
                }

                 if (!value.equals("") && !value.equals("0"))
                    //editText.setText(getDecimalFormattedString(str));
                    editText.setText(String.format(Locale.getDefault(),"%,d", getLong(editText.getText().toString())));

                editText.setSelection(editText.getText().toString().length());
            }
            editText.addTextChangedListener(this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            editText.addTextChangedListener(this);
        }

    }

    public long getLong(String in){
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        String str = in.replaceAll(String.valueOf(symbols.getGroupingSeparator()), "");

        if(str.equals("")){
            return 0L;
        }else{
            return Long.parseLong(str);
        }

    }

}
