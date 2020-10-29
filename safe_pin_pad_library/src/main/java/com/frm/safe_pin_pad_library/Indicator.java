package com.frm.safe_pin_pad_library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

class Indicator extends LinearLayout implements Checkable {

    /**
     * Interface definition for a callback to be invoked when the checked state of this View is
     * changed.
     */
    public interface OnCheckedChangeListener {

        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param checkableView The view whose state has changed.
         * @param isChecked     The new checked state of checkableView.
         */
        void onCheckedChanged(View checkableView, boolean isChecked);
    }

    private static final int DEFAULT_INDICATOR_SIZE = 12;
    private static final int DEFAULT_INDICATOR_STROKE_WIDTH = 4;
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private boolean checked_ = false;

    @ColorInt
    private int indicatorFilledColor_ = Color.WHITE;
    private int indicatorEmptyColor_ = Color.WHITE;
    private int indicatorSize_;
    private int indicatorStrokeWidth_ = DEFAULT_INDICATOR_STROKE_WIDTH;

    private OnCheckedChangeListener onCheckedChangeListener_;

    public Indicator(Context context) {
        super(context);
        init(context, null);
    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Indicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Indicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (context != null) {
            if (attrs != null) {
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PinPadView);

                indicatorSize_ = a.getDimensionPixelSize(R.styleable.PinPadView_pin_indicator_size,
                        convertDpToPixel(DEFAULT_INDICATOR_SIZE));
                indicatorStrokeWidth_ = a.getDimensionPixelOffset(R.styleable.PinPadView_pin_indicator_stroke_width,
                        DEFAULT_INDICATOR_STROKE_WIDTH);
                if (a.hasValue(R.styleable.PinPadView_pin_indicator_filled_color)) {
                    indicatorFilledColor_ = a.getColor(R.styleable.PinPadView_pin_indicator_filled_color,
                            ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_default_pin_indicator_filled_color, null));
                }
                if (a.hasValue(R.styleable.PinPadView_pin_indicator_empty_color)) {
                    indicatorEmptyColor_ = a.getColor(R.styleable.PinPadView_pin_indicator_empty_color,
                            ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_default_pin_indicator_empty_color, null));
                }
                a.recycle();
            }

            setIndicatorSize(indicatorSize_);
            // create drawable
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(createDrawable());
            } else {
                setBackgroundDrawable(createDrawable());
            }
            setChecked(false);
        }

    }

    private StateListDrawable createDrawable() {
        StateListDrawable drawable = new StateListDrawable();

        drawable.addState(new int[] { android.R.attr.state_checked}, createFilledDrawable());
        drawable.addState(StateSet.WILD_CARD, createEmptyDrawable());
        return drawable;
    }

    private Drawable createEmptyDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(indicatorStrokeWidth_, indicatorEmptyColor_);
        drawable.setColor(Color.TRANSPARENT);
        return drawable;
    }

    private Drawable createFilledDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(indicatorFilledColor_);
        return drawable;
    }

    public void setIndicatorSize(int size) {
        indicatorSize_ = size;
        LayoutParams params = new LayoutParams(size, size);
        setLayoutParams(params);
    }

    public void setFilledColor(@ColorInt int color) {
        indicatorFilledColor_ = color;
        requestLayout();
    }

    public void setEmptyColor(@ColorInt int color) {
        indicatorEmptyColor_ = color;
        requestLayout();
    }

    /**
     * Register a callback to be invoked when the checked state of this view changes.
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        onCheckedChangeListener_ = listener;
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void setChecked(boolean b) {
        if (b != checked_) {
            checked_ = b;
            refreshDrawableState();

            if (onCheckedChangeListener_ != null) {
                onCheckedChangeListener_.onCheckedChanged(this, checked_);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return checked_;
    }

    @Override
    public void toggle() {
        setChecked(!checked_);
    }

    private int convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
}
