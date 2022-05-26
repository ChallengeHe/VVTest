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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class RatioWheelLayout extends BaseWheelLayout {
    private static final String TAG = "RatioWheelLayout";
    private TextView layoutLabelView;
    private TextView layoutValueView;
    private NumberWheelView hundredsWheelView, tensWheelView, onesWheelView, pOneWheelView, pTwoWheelView, pThreeWheelView;
    private Integer selectedHundreds;
    private Integer selectedTens;
    private Integer selectedOnes;
    private Integer selectedPOne;
    private Integer selectedPTwo;
    private Integer selectedPThree;

    public RatioWheelLayout(Context context) {
        super(context);
    }

    public RatioWheelLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RatioWheelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RatioWheelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int provideLayoutRes() {
        return R.layout.wheel_picker_ratio;
    }

    @Override
    protected List<WheelView> provideWheelViews() {
        return Arrays.asList(hundredsWheelView, tensWheelView, onesWheelView, pOneWheelView, pTwoWheelView, pThreeWheelView);
    }

    @Override
    protected void onInit(@NonNull Context context) {
        layoutLabelView = findViewById(R.id.wheel_picker_ratio_lable);
        layoutValueView = findViewById(R.id.wheel_picker_ratio_value);
        hundredsWheelView = findViewById(R.id.wheel_picker_ratio_hundreds);
        tensWheelView = findViewById(R.id.wheel_picker_ratio_tens);
        onesWheelView = findViewById(R.id.wheel_picker_ratio_ones);
        pOneWheelView = findViewById(R.id.wheel_picker_ratio_point_one);
        pTwoWheelView = findViewById(R.id.wheel_picker_ratio_point_two);
        pThreeWheelView = findViewById(R.id.wheel_picker_ratio_point_three);

        hundredsWheelView.setRange(0, 9, 1);
        tensWheelView.setRange(0, 9, 1);
        onesWheelView.setRange(0, 9, 1);
        pOneWheelView.setRange(0, 9, 1);
        pTwoWheelView.setRange(0, 9, 1);
        pThreeWheelView.setRange(0, 9, 1);
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
        }
    }

    @Override
    public void onWheelSelected(WheelView view, int position) {
        int id = view.getId();
        if (id == R.id.wheel_picker_ratio_hundreds) {
            selectedHundreds = hundredsWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_ratio_tens) {
            selectedTens = tensWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_ratio_ones) {
            selectedOnes = onesWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_ratio_point_one) {
            selectedPOne = pOneWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_ratio_point_two) {
            selectedPTwo = pTwoWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_ratio_point_three) {
            selectedPThree = pThreeWheelView.getItem(position);
        }
        if (onRatioSelectedListener != null) {
            onRatioSelectedListener.onRatioSelected(getRatioValue());
        }
        layoutValueView.setText("" + getRatioValue());
    }

    public void setRatioValue(float ratio) {
        if (ratio <= 0) return;
        layoutValueView.setText("" + ratio);
        DecimalFormat df = new DecimalFormat("000.000");
        df.setRoundingMode(RoundingMode.FLOOR);
        String ratioStr = df.format(ratio);
        DecimalFormat decimalFormat = new DecimalFormat("0.0##");
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        layoutValueView.setText(decimalFormat.format(ratio));
        if (ratioStr == null || TextUtils.isEmpty(ratioStr) || ratioStr.startsWith("-")) return;
        int index = 0;
        try {
            int hundreds = Integer.valueOf(String.valueOf(ratioStr.charAt(index++)));
            int tens = Integer.valueOf(String.valueOf(ratioStr.charAt(index++)));
            int ones = Integer.valueOf(String.valueOf(ratioStr.charAt(index++)));
            index++;
            int pOne = Integer.valueOf(String.valueOf(ratioStr.charAt(index++)));
            int pTwo = Integer.valueOf(String.valueOf(ratioStr.charAt(index++)));
            int pThree = Integer.valueOf(String.valueOf(ratioStr.charAt(index++)));
            hundredsWheelView.setDefaultValue(hundreds);
            tensWheelView.setDefaultValue(tens);
            onesWheelView.setDefaultValue(ones);
            pOneWheelView.setDefaultValue(pOne);
            pTwoWheelView.setDefaultValue(pTwo);
            pThreeWheelView.setDefaultValue(pThree);

            selectedHundreds = hundredsWheelView.getCurrentItem();
            selectedTens = tensWheelView.getCurrentItem();
            selectedOnes = onesWheelView.getCurrentItem();
            selectedPOne = pOneWheelView.getCurrentItem();
            selectedPTwo = pTwoWheelView.getCurrentItem();
            selectedPThree = pThreeWheelView.getCurrentItem();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public float getRatioValue() {
        String ratioStr = "" + selectedHundreds + selectedTens + selectedOnes + "." + selectedPOne + selectedPTwo + selectedPThree;
        return Float.parseFloat(ratioStr);
    }

    @Override
    public void onWheelScrollStateChanged(WheelView view, int state) {
        super.onWheelScrollStateChanged(view, state);
    }

    private OnRatioSelectedListener onRatioSelectedListener;

    public void setOnRatioSelectedListener(OnRatioSelectedListener onRatioSelectedListener) {
        this.onRatioSelectedListener = onRatioSelectedListener;
    }

    public interface OnRatioSelectedListener {
        void onRatioSelected(float ratio);
    }
}
