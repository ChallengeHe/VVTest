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

public class ColorWheelLayout extends BaseWheelLayout {
    private static final String TAG = "ColorWheelLayout";
    private TextView layoutLabelView;
    private TextView layoutValueView;
    private WheelView plusOrMinusWheelView;
    private NumberWheelView hundredsWheelView, tensWheelView, onesWheelView, pOneWheelView, pTwoWheelView, pThreeWheelView;
    private String selectedPlusOrMinus;
    private Integer selectedHundreds;
    private Integer selectedTens;
    private Integer selectedOnes;
    private Integer selectedPOne;
    private Integer selectedPTwo;
    private Integer selectedPThree;

    public ColorWheelLayout(Context context) {
        super(context);
    }

    public ColorWheelLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorWheelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ColorWheelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int provideLayoutRes() {
        return R.layout.wheel_picker_color;
    }

    @Override
    protected List<WheelView> provideWheelViews() {
        return Arrays.asList(plusOrMinusWheelView, hundredsWheelView, tensWheelView, onesWheelView, pOneWheelView, pTwoWheelView, pThreeWheelView);
    }

    @Override
    protected void onInit(@NonNull Context context) {
        layoutLabelView = findViewById(R.id.wheel_picker_color_lable);
        layoutValueView = findViewById(R.id.wheel_picker_color_value);
        plusOrMinusWheelView = findViewById(R.id.wheel_picker_color_plus_or_minus);
        hundredsWheelView = findViewById(R.id.wheel_picker_color_hundreds);
        tensWheelView = findViewById(R.id.wheel_picker_color_tens);
        onesWheelView = findViewById(R.id.wheel_picker_color_ones);
        pOneWheelView = findViewById(R.id.wheel_picker_color_point_one);
        pTwoWheelView = findViewById(R.id.wheel_picker_color_point_two);
        pThreeWheelView = findViewById(R.id.wheel_picker_color_point_three);

        plusOrMinusWheelView.setData(Arrays.asList("+", "-"));
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
        int layoutLabelColor = typedArray.getColor(R.styleable.BaseWheelLayout_wheel_layoutLabelColor, 0x000);
        typedArray.recycle();
        layoutLabelView.setText(layoutLabel);
        layoutLabelView.setTextColor(layoutLabelColor);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            selectedPlusOrMinus = plusOrMinusWheelView.getCurrentItem();
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
        if (id == R.id.wheel_picker_color_plus_or_minus) {
            selectedPlusOrMinus = plusOrMinusWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_color_hundreds) {
            selectedHundreds = hundredsWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_color_tens) {
            selectedTens = tensWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_color_ones) {
            selectedOnes = onesWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_color_point_one) {
            selectedPOne = pOneWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_color_point_two) {
            selectedPTwo = pTwoWheelView.getItem(position);
        } else if (id == R.id.wheel_picker_color_point_three) {
            selectedPThree = pThreeWheelView.getItem(position);
        }
        if (onColorSelectedListener != null) {
            onColorSelectedListener.onColorSelected(getColorValue());
        }
        layoutValueView.setText("" + getColorValue());
    }

    public void setColorValue(float color) {
        DecimalFormat df = new DecimalFormat("000.000");
        DecimalFormat decimalFormat = new DecimalFormat("0.0##");
        if (color > 0) {
            decimalFormat.setRoundingMode(RoundingMode.CEILING);
            df.setRoundingMode(RoundingMode.CEILING);
        } else {
            decimalFormat.setRoundingMode(RoundingMode.FLOOR);
            df.setRoundingMode(RoundingMode.FLOOR);
        }
        String colorStr = df.format(color);
        if (colorStr == null || TextUtils.isEmpty(colorStr)) return;
        layoutValueView.setText(decimalFormat.format(color));
        String plusOrMinusStr = "+";
        int index = 0;
        if (colorStr.startsWith("-")) {
            plusOrMinusStr = String.valueOf(colorStr.charAt(index++));
        }
        try {
            int hundreds = Integer.valueOf(String.valueOf(colorStr.charAt(index++)));
            int tens = Integer.valueOf(String.valueOf(colorStr.charAt(index++)));
            int ones = Integer.valueOf(String.valueOf(colorStr.charAt(index++)));
            index++;
            int pOne = Integer.valueOf(String.valueOf(colorStr.charAt(index++)));
            int pTwo = Integer.valueOf(String.valueOf(colorStr.charAt(index++)));
            int pThree = Integer.valueOf(String.valueOf(colorStr.charAt(index++)));
            plusOrMinusWheelView.setDefaultValue(plusOrMinusStr);
            hundredsWheelView.setDefaultValue(hundreds);
            tensWheelView.setDefaultValue(tens);
            onesWheelView.setDefaultValue(ones);
            pOneWheelView.setDefaultValue(pOne);
            pTwoWheelView.setDefaultValue(pTwo);
            pThreeWheelView.setDefaultValue(pThree);

            selectedPlusOrMinus = plusOrMinusWheelView.getCurrentItem();
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

    public float getColorValue() {
        String colorStr = "" + selectedPlusOrMinus + selectedHundreds + selectedTens + selectedOnes + "." + selectedPOne + selectedPTwo + selectedPThree;
        Log.e(TAG, "getColorValue: colorStr=" + colorStr);
        return Float.parseFloat(colorStr);
    }

    @Override
    public void onWheelScrollStateChanged(WheelView view, int state) {
        super.onWheelScrollStateChanged(view, state);
    }

    private OnColorSelectedListener onColorSelectedListener;

    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        this.onColorSelectedListener = onColorSelectedListener;
    }

    public interface OnColorSelectedListener {
        void onColorSelected(float color);
    }
}
