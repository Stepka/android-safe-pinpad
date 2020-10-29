package com.frm.safe_pin_pad_library;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

class PinPadButton extends ForegroundRelativeLayout {
    private static final float DEFAULT_TEXT_SIZE_NUMERIC = 18f;
    private static final float DEFAULT_TEXT_SIZE_ALPHA = 12f;
    private static final int DEFAULT_DRAWABLE_SIZE = 15;
    @ColorInt
    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private float textSizeNumeric_;
    private float textSizeAlpha_;
    private int drawableSize_;
    private Drawable buttonFigure_;
    private Drawable buttonBackground_;
    private String digit_;
    private String word_;

    private TextView digitTextView_;
    private TextView wordTextView_;
    private ImageView figure_;
    private ImageView texture_;

    private ColorStateList defaultTextColor_ = ColorStateList.valueOf(DEFAULT_TEXT_COLOR);
    private OnButtonClickListener buttonClickListener_;

    public interface OnButtonClickListener {
        void onButtonClick(PinPadButton button);
    }

    public PinPadButton(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PinPadButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinPadButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(@NonNull Context context, AttributeSet attrs, int defStyle) {
        View view = inflate(context, R.layout.pinpad_button_layout, this);
        digitTextView_ = (TextView) view.findViewById(R.id.safe_digit_text);
        wordTextView_ = (TextView) view.findViewById(R.id.safe_word_text);
        figure_ = (ImageView) view.findViewById(R.id.safe_pin_button_figure);
        texture_ = (ImageView) view.findViewById(R.id.safe_pin_button_texture);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PinPadView, defStyle, 0);

            textSizeNumeric_ = a.getDimension(R.styleable.PinPadView_button_numeric_text_size,
                    DEFAULT_TEXT_SIZE_NUMERIC);
            textSizeAlpha_ = a.getDimension(R.styleable.PinPadView_button_alpha_text_size,
                    DEFAULT_TEXT_SIZE_ALPHA);
            drawableSize_ = a.getDimensionPixelSize(R.styleable.PinPadView_button_icon_size,
                    15);
            if (a.hasValue(R.styleable.PinPadView_button_figure)) {
                buttonFigure_ = a.getDrawable(R.styleable.PinPadView_button_figure);
            }
            if (a.hasValue(R.styleable.PinPadView_button_text_numeric)) {
                digit_ = a.getString(R.styleable.PinPadView_button_text_numeric);
            }
            if (a.hasValue(R.styleable.PinPadView_button_text_alpha)) {
                word_ = a.getString(R.styleable.PinPadView_button_text_alpha);
            }
            if (a.hasValue(R.styleable.PinPadView_button_text_color)) {
                defaultTextColor_ = a.getColorStateList(R.styleable.PinPadView_button_text_color);
            }

            a.recycle();
        }

        digitTextView_.setVisibility(VISIBLE);
        digitTextView_.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeNumeric_);
        if (digit_ != null && !digit_.isEmpty()) {
            // create numeric textview
            digitTextView_.setText(digit_);

        }
        wordTextView_.setVisibility(VISIBLE);
        wordTextView_.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeAlpha_);
        if (word_ != null && !word_.isEmpty()) {
            // create alphabet textview
            wordTextView_.setText(word_);
        }

        // create icon
        figure_.setVisibility(VISIBLE);
        texture_.setVisibility(VISIBLE);
        LayoutParams params = new LayoutParams(drawableSize_, drawableSize_);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        figure_.setLayoutParams(params);
        figure_.setImageDrawable(buttonFigure_);

        setTextColor(defaultTextColor_);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        setLayoutParams(lp);

        setClickable(true);
    }

    /**
     * Sets a button click listener for the button
     * @param listener - {@link OnButtonClickListener} listener
     */
    public void setButtonClickListener(OnButtonClickListener listener) {
        buttonClickListener_ = listener;
    }

    public void setTextColor(ColorStateList colorStateList) {
        if (colorStateList != null) {
            wordTextView_.setTextColor(colorStateList);
            digitTextView_.setTextColor(colorStateList);
        }
    }

    public void setFigure(int id) {
        if (id >= 0)
        {
            figure_.setImageResource(id);
        }
    }

    public void setTexture(int id) {
        if (id >= 0)
        {
            texture_.setImageResource(id);
        }
    }

    /**
     * Sets the text color to use for both the alphabet and numeric texts
     * @param color - @{@link ColorInt} representation of the color
     */
    public void setColor(@ColorInt int color) {
        wordTextView_.setTextColor(ColorStateList.valueOf(color));
        digitTextView_.setTextColor(ColorStateList.valueOf(color));
        figure_.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
        texture_.setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
        requestLayout();
    }

    public void setNumericTextSize(float textSize) {
        if (digitTextView_ != null) {
            textSizeNumeric_ = textSize;
            digitTextView_.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            requestLayout();
        }
    }

    public void setAlphabetTextSize(float textSize) {
        if (wordTextView_ != null) {
            textSizeAlpha_ = textSize;
            wordTextView_.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            requestLayout();
        }
    }

    /**
     * Gets the numeric text for the button
     */
    public String getDigit() {
        return digit_;
    }

    /**
     * Sets the numeric text on the button
     * @param text - numeric text to display on the button
     */
    public void setDigit(String text) {
        if (digitTextView_ != null) {
            digit_ = text;
            digitTextView_.setText(text);
            requestLayout();
        }
    }

    /**
     * Gets the alphabet text for the button
     */
    public String getWord() {
        return word_;
    }

    /**
     * Sets the alphabet text on the button
     * @param text - alphabet text to display on the button
     */
    public void setWord(String text) {
        if (wordTextView_ != null) {
            word_ = text;
            wordTextView_.setText(text);
            requestLayout();
        }
    }

    /**
     * @return true if the button is an image button, false otherwise
     */
    public boolean isImageButton() {
        return buttonFigure_ != null;
    }

    /**
     * Sets the image size to use for images set on the button
     * @param imageSize - required image size in pixels
     */
    public void setImageFigureSize(int imageSize) {
        if (figure_ != null) {
            drawableSize_ = imageSize;
            LayoutParams params = new LayoutParams(drawableSize_, drawableSize_);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            figure_.setLayoutParams(params);
            requestLayout();
        }
    }

    /**
     * Sets the text color to use for both the alphabet and numeric texts
     * @param color - @{@link ColorInt} representation of the color
     */
    public void setTextColor(@ColorInt int color) {
        setTextColor(ColorStateList.valueOf(color));
        requestLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP) {
            if(buttonClickListener_ != null) {
                buttonClickListener_.onButtonClick(this);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_UP && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            if(buttonClickListener_ != null) {
                buttonClickListener_.onButtonClick(this);
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
