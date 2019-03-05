package com.epipasha.cashflow.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class Indicator extends View {

    private int width;
    private int heigth;

    private Paint paint;
    private int successColor = Color.GREEN;
    private int errorColor = Color.RED;

    private float dx;

    private int min;
    private int max;
    private int value;
    private float cashflow;
    private float budget;
    private List<Integer> dots;

    public Indicator(Context context) {
        super(context);
        init();
    }

    public Indicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        paint = new Paint();
        dots = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec);
        heigth = MeasureSpec.getSize(heightMeasureSpec);

        dx = heigth/30f;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setStrokeWidth(heigth/2f);

        float dayBudget = (value - min)*budget/(max-min);

        if(cashflow >= dayBudget)
            paint.setColor(errorColor);
        else
            paint.setColor(successColor);

        if(cashflow >= budget){

            canvas.drawLine(dx, heigth / 2f, width - dx, heigth / 2f, paint);
        }else if(cashflow < budget && budget != 0){
            float x = (cashflow/budget)*(width - 2*dx) + dx;
            canvas.drawLine(dx, heigth / 2f, x, heigth / 2f, paint);
        }

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(dx);

        canvas.drawLine(dx, heigth*5/6f, width - dx, heigth*5/6f, paint);

        canvas.drawLine(dx, heigth*4/6f, dx, heigth*5/6f, paint);
        canvas.drawLine(width - dx, heigth*4/6f, width - dx, heigth*5/6f, paint);

        for (Integer dot:dots){
            if(dot > min && dot < max) {
                float x = dotX(dot);
                canvas.drawLine(x, heigth * 4 / 6f, x, heigth * 5 / 6f, paint);
            }
        }

        paint.setStrokeWidth(dx*2f);
        float y = dotX(value);
        canvas.drawLine(y, 0, y, heigth, paint);

    }

    private float dotX(int dot){
        if(max == min){
            return 0;
        }
        return (dot - min)* (width - 2 * dx)/(max - min) + dx;
    }

    public void setSuccessColor(int successColor) {
        this.successColor = successColor;
        invalidate();
    }

    public void setErrorColor(int errorColor) {
        this.errorColor = errorColor;
        invalidate();
    }

    public void setMin(int min) {
        this.min = min;
        invalidate();
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public void setValue(int value) {
        this.value = value;
        invalidate();
    }

    public void setCashflow(float cashflow) {
        this.cashflow = cashflow;
        invalidate();
    }

    public void setBudget(float budget) {
        this.budget = budget;
        invalidate();
    }

    public void addDot(int dot){
        dots.add(dot);
        invalidate();
    }
}