package com.vv3d.vvtest.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vv3d.vvtest.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class WidthWheelLayout extends BaseWheelLayout {
    private static final String TAG = "WidthWheelLayout";
    private TextView layoutLabelView;
    private TextView layoutValueView;
    private NumberWheelView hundredsWheelView, tensWheelView, onesWheelView, pOneWheelView, pTwoWheelView, pThreeWheelView, pFourWheelView, pFiveWheelView, pSixWheelView, pSevenWheelView;
    private Integer selectedHundreds;
    private Integer selectedTens;
    private Integer selectedOnes;
    private Integer selectedPOne;
    private Integer selectedPTwo;
    private Integer selectedPThree;
    private Integer selectedPFour;
    private Integer selectedPFive;
    private Integer selectedPSix;
    private Integer selectedPSeven;

    public WidthWheelLayout(Context context) {
        super(context);
    }

    public WidthWheelLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WidthWheelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WidthWheelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int provideLayoutRes() {
        return R.layout.wheel_picker_width;
    }

    @Override
    protected List<WheelView> provideWheelViews() {
        return Arrays.asList(hundredsWheelView, tensWheelView, onesWheelView, pOneWheelView, pTwoWheelView, pThreeWheelView, pFourWheelView, pFiveWheelView, pSixWheelView, pSevenWheelView);
    }

    @Override
    protected void onInit(@NonNull Context context) {
        layoutLabelView = findViewById(R.id.wheel_picker_width_lable);
        layoutValueView = findViewById(R.id.wheel_picker_width_value);
        hundredsWheelView = findViewById(R.id.wheel_picker_width_hundreds);
        tensWheelView = findViewById(R.id.wheel_picker_width_tens);
        onesWheelView = findViewById(R.id.wheel_picker_width_ones);
        pOneWheelView = findViewById(R.id.wheel_picker_width_point_one);
        pTwoWheelView = findViewById(R.id.wheel_picker_width_point_two);
        pThreeWheelView = findViewById(R.id.wheel_picker_width_point_three);
        pFourWheelView = findViewById(R.id.wheel_picker_width_point_four);
        pFiveWheelView = findViewById(R.id.wheel_picker_width_point_five);
        pSixWheelView = findViewById(R.id.wheel_picker_width_point_six);
        pSevenWheelView = findViewById(R.id.wheel_picker_width_point_seven);

        hundredsWheelView.setRange(0, 9, 1);
        tensWheelView.setRange(0, 9, 1);
        onesWheelView.setRange(0, 9, 1);
        pOneWheelView.setRange(0, 9, 1);
        pTwoWheelView.setRange(0, 9, 1);
        pThreeWheelView.setRange(0, 9, 1);
        pFourWheelView.setRange(0, 9, 1);
        pFiveWheelView.setRange(0, 9, 1);
        pSixWheelView.setRange(0, 9, 1);
        pSevenWheelView.setRange(0, 9, 1);
    }

    @Override
    protected void onAttributeSet(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseWheelLayout);
        String layoutLabel = typedArray.getString(R.styleable.BaseWheelLayout_wheel_layoutLabel);
        typedArray.recycle();
        layoutLabelView.setText(layoutLabel);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            selectedHundreds = hundredsWheelView.getCurrentItem();
            selectedTens = tensWheelView.getCurrentItem();
            selectedOnes = onesWheelView.getCurrentItem();
            selectedPOne = pOneWheelView.getCurrentItem();
            selectedPTwo = pTwoWheelView.getCurrentItem();
            selectedPThree = pThreeWheelView.getCurrentItem();
            selectedPFour = pFourWheelView.getCurrentItem();
            selectedPFive = pFiveWheelView.getCurrentItem();
            selectedPSix = pSixWheelView.getCurrentItem();
            selectedPSeven = pSevenWheelView.getCurrentItem();
        }
    }

    @Override
    public void onWheelSelected(WheelView view, int position) {
        int id = view.getId();
        if (id == R.id.wheel_picker_width_hundreds) {
            selectedHundreds = hundredsWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_width_tens) {
            selectedTens = tensWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_width_ones) {
            selectedOnes = onesWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_width_point_one) {
            selectedPOne = pOneWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_width_point_two) {
            selectedPTwo = pTwoWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_width_point_three) {
            selectedPThree = pThreeWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_width_point_four) {
            selectedPFour = pFourWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_width_point_five) {
            selectedPFive = pFiveWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_width_point_six) {
            selectedPSix = pSixWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_width_point_seven) {
            selectedPSeven = pSevenWheelView.getItem(position);
        }
        if (onWidthSelectedListener != null) {
            onWidthSelectedListener.onWidthSelected(getWidthValue());
        }
        layoutValueView.setText("" + getWidthValue());
    }

    public void setWidthValue(float width) {
        if (width <= 0) return;
        layoutValueView.setText("" + width);
        DecimalFormat df = new DecimalFormat("000.0000000");
        df.setRoundingMode(RoundingMode.FLOOR);
        String widthStr = df.format(width);
        DecimalFormat decimalFormat = new DecimalFormat("0.0######");
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        layoutValueView.setText(decimalFormat.format(width));
        if (widthStr == null || TextUtils.isEmpty(widthStr) || widthStr.startsWith("-")) return;
        int index = 0;
        try {
            int hundreds = Integer.valueOf(String.valueOf(widthStr.charAt(index++)));
            int tens = Integer.valueOf(String.valueOf(widthStr.charAt(index++)));
            int ones = Integer.valueOf(String.valueOf(widthStr.charAt(index++)));
            index++;
            int pOne = Integer.valueOf(String.valueOf(widthStr.charAt(index++)));
            int pTwo = Integer.valueOf(String.valueOf(widthStr.charAt(index++)));
            int pThree = Integer.valueOf(String.valueOf(widthStr.charAt(index++)));
            int pFour = Integer.valueOf(String.valueOf(widthStr.charAt(index++)));
            int pFive = Integer.valueOf(String.valueOf(widthStr.charAt(index++)));
            int pSix = Integer.valueOf(String.valueOf(widthStr.charAt(index++)));
            int pSeven = Integer.valueOf(String.valueOf(widthStr.charAt(index++)));
            hundredsWheelView.setDefaultValue(hundreds);
            tensWheelView.setDefaultValue(tens);
            onesWheelView.setDefaultValue(ones);
            pOneWheelView.setDefaultValue(pOne);
            pTwoWheelView.setDefaultValue(pTwo);
            pThreeWheelView.setDefaultValue(pThree);
            pFourWheelView.setDefaultValue(pFour);
            pFiveWheelView.setDefaultValue(pFive);
            pSixWheelView.setDefaultValue(pSix);
            pSevenWheelView.setDefaultValue(pSeven);

            selectedHundreds = hundredsWheelView.getCurrentItem();
            selectedTens = tensWheelView.getCurrentItem();
            selectedOnes = onesWheelView.getCurrentItem();
            selectedPOne = pOneWheelView.getCurrentItem();
            selectedPTwo = pTwoWheelView.getCurrentItem();
            selectedPThree = pThreeWheelView.getCurrentItem();
            selectedPFour = pFourWheelView.getCurrentItem();
            selectedPFive = pFiveWheelView.getCurrentItem();
            selectedPSix = pSixWheelView.getCurrentItem();
            selectedPSeven = pSevenWheelView.getCurrentItem();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public float getWidthValue() {
        String widthStr = "" + selectedHundreds + selectedTens + selectedOnes + "." + selectedPOne + selectedPTwo + selectedPThree + selectedPFour + selectedPFive + selectedPSix + selectedPSeven;
        return Float.parseFloat(widthStr);
    }

    @Override
    public void onWheelScrollStateChanged(WheelView view, int state) {
        super.onWheelScrollStateChanged(view, state);
    }

    private OnWidthSelectedListener onWidthSelectedListener;

    public void setOnWidthSelectedListener(OnWidthSelectedListener onWidthSelectedListener) {
        this.onWidthSelectedListener = onWidthSelectedListener;
    }

    public interface OnWidthSelectedListener {
        void onWidthSelected(float width);
    }
}
