package com.frm.safe_pin_pad_library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.ColorInt;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.*;

/**
 * PinPadView
 * <p>
 *
 *     XML Sample
 * <pre>
 * </pre>
 */
public class PinPadView extends FrameLayout {
    private static final int DEFAULT_INDICATOR_SIZE = 24;
    private static final int DEFAULT_PIN_LENGTH = 4;
    private static final float DEFAULT_TEXT_SIZE_NUMERIC = 18f;
    private static final float DEFAULT_TEXT_SIZE_ALPHA = 12f;
    private static final float DEFAULT_TEXT_SIZE_PROMPT = 18f;
    private static final int DEFAULT_INDICATOR_SPACING = 8;
    private static final boolean DEFAULT_PLACE_DIGITS_RANDOMLY = true;
    private static final boolean DEFAULT_VIBRATE_ON_INCOMPLETE_SUBMIT = true;
    private static final boolean DEFAULT_AUTO_SUBMIT = true;

    @ColorInt
    private int indicatorFilledColor_ = Color.WHITE;
    private int indicatorEmptyColor_ = Color.WHITE;
    private int indicatorSize_;
    private int indicatorSpacing_;

    @ColorInt
    private int buttonTextColor_ = Color.WHITE;
    private int promptTextColor_ = Color.WHITE;
    private String promptText_;
    private int pinLength_ = DEFAULT_PIN_LENGTH;
    private float textSizeNumeric_;
    private float textSizeAlpha_;
    private float textSizePrompt_;
    private int iconSize_;
    private int figureSize_;

    public static final int BASIC_ORDER = 0;
    public static final int SHIFT_ORDER = 1;
    public static final int RANDOM_ORDER = 2;

    private int symbolsOrder_ = RANDOM_ORDER;

    private boolean autoSubmit_ = DEFAULT_AUTO_SUBMIT;
    private boolean vibrateOnIncompleteSubmit_ = DEFAULT_VIBRATE_ON_INCOMPLETE_SUBMIT;

    private PinPadButton button0_;
    private PinPadButton button1_;
    private PinPadButton button2_;
    private PinPadButton button3_;
    private PinPadButton button4_;
    private PinPadButton button5_;
    private PinPadButton button6_;
    private PinPadButton button7_;
    private PinPadButton button8_;
    private PinPadButton button9_;
    private PinPadButton buttonBack_;
    private PinPadButton buttonDone_;
    private TextView textViewPrompt_;
    private LinearLayout layoutIndicator_;

    private List<PinPadButton> buttons_;

    private String[] digits_;
    private String[] words_;
    private String[] colors_;
    private String[] figures_;
    private String[] textures_;
    private String[] currentDigits_;
    private String[] currentWords_;
    private String[] currentColors_;
    private String[] currentFigures_;
    private String[] currentTextures_;
    private HashMap<String, Integer> colorsRes_;
    private HashMap<String, Integer> figuresRes_;
    private HashMap<String, Integer> texturesRes_;

    private OnPinChangedListener pinChangeListener_;
    private OnSubmitListener submitListener_;

    /**
     * StringBuilder for the pin text
     */
    private StringBuilder digitPinBuilder_;
    private StringBuilder wordPinBuilder_;
    private List<Integer> enteredPinIds_;
    private int promptPadding_;
    private int promptPaddingTop_;
    private int promptPaddingBottom_;

    public interface OnPinChangedListener {
        /**
         * Listener method invoked when the pin changed (either a new digit added or an old one removed)
         *
         * @param oldPin - old pin
         * @param newPin - new pin
         */
        void onPinChanged(List<Integer> oldPin, List<Integer> newPin);

    }

    public interface OnSubmitListener {
        /**
         * this will be called only when "enter/done" key is
         * pressed and the PIN is complete
         *
         * @param pin - pin
         */
        void onCompleted(List<Integer> pin);

        /**
         * This will be called anytime "enter/done" key is pressed
         * and the PIN is not yet complete
         *
         * @param pin - pin
         */
        void onIncompleteSubmit(List<Integer> pin);
    }

    public PinPadView(Context context) {
        super(context);
    }

    public PinPadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinPadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PinPadView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private AttributeSet attrs_;

    private AttributeSet getAttrs() {
        return attrs_;
    }

    private void init(Context context, AttributeSet attrs) {
        if (context != null && attrs != null) {
            attrs_ = attrs;
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PinPadView);

            pinLength_ = a.getInteger(R.styleable.PinPadView_pin_length, DEFAULT_PIN_LENGTH);
            textSizeNumeric_ = a.getDimension(R.styleable.PinPadView_button_numeric_text_size,
                    DEFAULT_TEXT_SIZE_NUMERIC);
            textSizeAlpha_ = a.getDimension(R.styleable.PinPadView_button_alpha_text_size,
                    DEFAULT_TEXT_SIZE_ALPHA);
            textSizePrompt_ = a.getDimension(R.styleable.PinPadView_prompt_text_size,
                    DEFAULT_TEXT_SIZE_PROMPT);
            iconSize_ = a.getDimensionPixelSize(R.styleable.PinPadView_button_icon_size,
                    24);
            figureSize_ = a.getDimensionPixelSize(R.styleable.PinPadView_button_figure_size,
                    48);
            indicatorSize_ = a.getDimensionPixelSize(R.styleable.PinPadView_pin_indicator_size,
                    DEFAULT_INDICATOR_SIZE);
            indicatorSpacing_ = a.getDimensionPixelSize(R.styleable.PinPadView_pin_indicator_spacing,
                    DEFAULT_INDICATOR_SPACING);
            promptPadding_ = a.getDimensionPixelSize(R.styleable.PinPadView_prompt_text_padding,
                    getResources().getDimensionPixelSize(R.dimen.safe_pin_pad_default_prompt_padding));
            promptPaddingTop_ = a.getDimensionPixelSize(R.styleable.PinPadView_prompt_text_paddingTop,
                    getResources().getDimensionPixelSize(R.dimen.safe_pin_pad_default_prompt_padding_top));
            promptPaddingBottom_ = a.getDimensionPixelSize(R.styleable.PinPadView_prompt_text_paddingBottom,
                    getResources().getDimensionPixelSize(R.dimen.safe_pin_pad_default_prompt_padding_bottom));
            autoSubmit_ = a.getBoolean(R.styleable.PinPadView_auto_submit,
                    DEFAULT_AUTO_SUBMIT);
            vibrateOnIncompleteSubmit_ = a.getBoolean(R.styleable.PinPadView_vibrate_on_incomplete_submit,
                    DEFAULT_VIBRATE_ON_INCOMPLETE_SUBMIT);

            indicatorFilledColor_ = a.getColor(R.styleable.PinPadView_pin_indicator_filled_color,
                    ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_default_pin_indicator_filled_color, null));

            indicatorEmptyColor_ = a.getColor(R.styleable.PinPadView_pin_indicator_empty_color,
                    ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_default_pin_indicator_empty_color, null));

            buttonTextColor_ = a.getColor(R.styleable.PinPadView_button_text_color,
                    ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_default_button_text_color, null));
            promptTextColor_ = a.getColor(R.styleable.PinPadView_prompt_text_color,
                    ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_default_prompt_text_color, null));


            if (a.hasValue(R.styleable.PinPadView_prompt_text)) {
                promptText_ = a.getString(R.styleable.PinPadView_prompt_text);
            }

            a.recycle();

            // inflate compound view;
            View parent = inflate(context, R.layout.pinpad_layout, this);

            button0_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_0);
            button1_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_1);
            button2_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_2);
            button3_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_3);
            button4_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_4);
            button5_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_5);
            button6_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_6);
            button7_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_7);
            button8_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_8);
            button9_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_9);
            buttonBack_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_back);
            buttonDone_ = (PinPadButton) parent.findViewById(R.id.safe_pin_pad_btn_done);
            textViewPrompt_ = (TextView) parent.findViewById(R.id.safe_pin_pad_prompt);
            layoutIndicator_ = (LinearLayout) parent.findViewById(R.id.safe_pin_pad_indicator_layout);

            buttons_ = Arrays.asList(
                    button0_, button1_, button2_, button3_, button4_,
                    button5_, button6_, button7_, button8_, button9_);

            digits_ = new String[]{
                    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
            };

            words_ = new String[]{
                    "cat",
                    "dog",
                    "bull",
                    "bug",
                    "leon",
                    "wolf",
                    "elk",
                    "fly",
                    "duck",
                    "owl"
            };

            colors_ = new String[]{
                    "aqua",
                    "blue",
                    "fuchsia",
                    "green",
                    "maroon",
                    "orange",
                    "purple",
                    "red",
                    "teal",
                    "yellow"
            };

            figures_ = new String[]{
                    "circle",
                    "quad",
                    "triangle_right",
                    "triangle_left",
                    "triangle_up",
                    "triangle_down",
                    "rumb",
                    "pentangle",
                    "hexangle",
                    "empty"
            };

            textures_ = new String[]{
                    "horizontal_straight",
                    "vertical_straight",
                    "oblique_straight",
                    "double_straight",
                    "double_oblique",
                    "horizontal_curves",
                    "vertical_curves",
                    "oblique_curves",
                    "double_curves",
                    "double_oblique_curves"
            };

            colorsRes_ = new HashMap<>();
            colorsRes_.put("aqua", ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_aqua, null));
            colorsRes_.put("blue", ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_blue, null));
            colorsRes_.put("fuchsia", ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_fuchsia, null));
            colorsRes_.put("green", ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_green, null));
            colorsRes_.put("maroon", ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_maroon, null));
            colorsRes_.put("orange", ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_orange, null));
            colorsRes_.put("purple", ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_purple, null));
            colorsRes_.put("red", ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_red, null));
            colorsRes_.put("teal", ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_teal, null));
            colorsRes_.put("yellow", ResourcesCompat.getColor(getResources(), R.color.safe_pin_pad_yellow, null));

            figuresRes_ = new HashMap<>();
            figuresRes_.put("circle", R.drawable.circle_figure);
            figuresRes_.put("quad", R.drawable.quad_figure);
            figuresRes_.put("triangle_right", R.drawable.triangle_right_figure);
            figuresRes_.put("triangle_left",  R.drawable.triangle_left_figure);
            figuresRes_.put("triangle_up", R.drawable.triangle_up_figure);
            figuresRes_.put("triangle_down", R.drawable.triangle_down_figure);
            figuresRes_.put("rumb", R.drawable.rumb_figure);
            figuresRes_.put("pentangle", R.drawable.pentangle_figure);
            figuresRes_.put("hexangle", R.drawable.hexangle_figure);
            figuresRes_.put("empty", 0);

            texturesRes_ = new HashMap<>();
            texturesRes_.put("horizontal_straight", R.drawable.horizontal_straight_back);
            texturesRes_.put("vertical_straight", R.drawable.vertical_straight_back);
            texturesRes_.put("oblique_straight", R.drawable.oblique_straight_back);
            texturesRes_.put("double_straight", R.drawable.double_straight_back);
            texturesRes_.put("double_oblique", R.drawable.double_oblique_straight_back);
            texturesRes_.put("horizontal_curves", R.drawable.horizontal_curves_back);
            texturesRes_.put("vertical_curves", R.drawable.vertical_curves_back);
            texturesRes_.put("oblique_curves", R.drawable.oblique_curves_back);
            texturesRes_.put("double_curves", R.drawable.double_curves_back);
            texturesRes_.put("double_oblique_curves", R.drawable.double_oblique_curves_back);

            digitPinBuilder_ = new StringBuilder();
            wordPinBuilder_ = new StringBuilder();
            enteredPinIds_ = new ArrayList<>();

            createIndicators(context, attrs);
            assignButtonNumbers();

            //set properties;
            setNumericTextSize(textSizeNumeric_, false);
            setAlphabetTextSize(textSizeAlpha_, false);
            setImageButtonSize(iconSize_, false);
            setImagePinButtonSize(figureSize_, false);
            setDoneButtonTextColor(buttonTextColor_, false);
            setPromptTextColor(promptTextColor_, false);
            setPromptTextSize(textSizePrompt_, false);
            setPromptText(promptText_);
            setPromptPadding(promptPadding_, false);
            setPromptPaddingTop(promptPaddingTop_, false);
            setPromptPaddingBottom(promptPaddingBottom_, false);
            setPinLength(pinLength_);
            setButtonClickListeners();
            updateIndicators();
        }
    }

    private void assignDigit(PinPadButton p, Integer i) {
        buttonDigits_.put(p, i);
        p.setDigit(digits_[i]);
    }

    private void assignWord(PinPadButton p, Integer i) {
        buttonWords_.put(p, i);
        p.setWord(words_[i]);
    }

    private void assignFigure(PinPadButton p, Integer i) {
        buttonFigures_.put(p, i);
        p.setFigure(figuresRes_.get(figures_[i]));
    }

    private void assignTexture(PinPadButton p, Integer i) {
        buttonTextures_.put(p, i);
        p.setTexture(texturesRes_.get(textures_[i]));
    }

    private void assignColors(PinPadButton p, Integer i) {
        buttonTextures_.put(p, i);
        p.setColor(colorsRes_.get(colors_[i]));
    }

    public void setPlaceOrder(int placeOrder) {
        symbolsOrder_ = placeOrder;
        assignButtonNumbers();
    }

    public int getPlaceOrder() {
        return symbolsOrder_;
    }

    public void setAutoSubmit(boolean autoSubmit) {
        autoSubmit_ = autoSubmit;
    }

    public void setVibrateOnIncompleteSubmit(boolean vibrateOnIncompleteSubmit) {
        vibrateOnIncompleteSubmit_ = vibrateOnIncompleteSubmit;
    }

    public boolean getVibrateOnIncompleteSubmit() {
        return vibrateOnIncompleteSubmit_;
    }

    public boolean getAutoSubmit() {
        return autoSubmit_;
    }

    private void assignButtonNumbers() {
        buttonDigits_ = new HashMap<>();
        buttonWords_ = new HashMap<>();
        buttonColors_ = new HashMap<>();
        buttonFigures_ = new HashMap<>();
        buttonTextures_ = new HashMap<>();

        int[] symbolIds = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        buttonIds_ = new HashMap<>();
        for (int i = 0; i < symbolIds.length; i++) {
            buttonIds_.put(buttons_.get(i), i);
        }

        currentDigits_ = new String[symbolIds.length];
        currentWords_ = new String[symbolIds.length];
        currentColors_ = new String[symbolIds.length];
        currentFigures_ = new String[symbolIds.length];
        currentTextures_ = new String[symbolIds.length];

        // digits
        orderSymbols(symbolIds);
        for (int i = 0; i < symbolIds.length; i++) {
            assignDigit(buttons_.get(i), symbolIds[i]);
            currentDigits_[i] = digits_[symbolIds[i]];
        }

        // words
        orderSymbols(symbolIds);
        for (int i = 0; i < symbolIds.length; i++) {
            assignWord(buttons_.get(i), symbolIds[i]);
            currentWords_[i] = words_[symbolIds[i]];
        }

        // figures
        orderSymbols(symbolIds);
        for (int i = 0; i < symbolIds.length; i++) {
            assignFigure(buttons_.get(i), symbolIds[i]);
            currentFigures_[i] = figures_[symbolIds[i]];
        }

        // textures
        orderSymbols(symbolIds);
        for (int i = 0; i < symbolIds.length; i++) {
            assignTexture(buttons_.get(i), symbolIds[i]);
            currentTextures_[i] = textures_[symbolIds[i]];
        }

        // colors
        orderSymbols(symbolIds);
        for (int i = 0; i < symbolIds.length; i++) {
            assignColors(buttons_.get(i), symbolIds[i]);
            currentColors_[i] = colors_[symbolIds[i]];
        }
    }

    private HashMap<PinPadButton, Integer> buttonIds_ = new HashMap<>();
    private HashMap<PinPadButton, Integer> buttonDigits_ = new HashMap<>();
    private HashMap<PinPadButton, Integer> buttonWords_ = new HashMap<>();
    private HashMap<PinPadButton, Integer> buttonFigures_ = new HashMap<>();
    private HashMap<PinPadButton, Integer> buttonTextures_ = new HashMap<>();
    private HashMap<PinPadButton, Integer> buttonColors_ = new HashMap<>();

    private void orderSymbols(int[] ar) {

        switch (symbolsOrder_) {
            case BASIC_ORDER:
                basicOrder(ar);
                break;

            case SHIFT_ORDER:
                shiftOrder(ar);
                break;

            case RANDOM_ORDER:
            default:
                shuffleArray(ar);
        }
    }

    private void basicOrder(int[] ar) {
        // do nothing
    }

    private void shiftOrder(int[] ar) {
        int prev = ar[0];
        for (int i = 1; i < ar.length; i++) {
            int newPrev = ar[i];
            ar[i] = prev;
            prev = newPrev;
        }
        ar[0] = prev;
    }

    private void shuffleArray(int[] ar) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    /**
     * Sets the listener to receive changes to the entered pin
     *
     * @param listener - {@link OnPinChangedListener} listener
     */
    public void setOnPinChangedListener(OnPinChangedListener listener) {
        pinChangeListener_ = listener;
    }

    public void vibratePhone(){
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(300);
    }

    /**
     * Sets the listener to handle clicking done
     *
     * @param listener - {@link OnSubmitListener} listener
     */
    public void setOnSubmitListener(OnSubmitListener listener) {
        submitListener_ = listener;
    }

    /**
     * Sets the pinpad prompt text
     *
     * @param promptText - text to display on the prompt field
     */
    public void setPromptText(String promptText) {
        promptText_ = promptText;
        textViewPrompt_.setVisibility(TextUtils.isEmpty(promptText) ? GONE : VISIBLE);
        textViewPrompt_.setText(promptText_);
        requestLayout();
    }

    /**
     * Sets the pinpad promptPadding
     *
     * @param padding - padding for prompt in pixels
     */
    public void setPromptPadding(int padding) {
        promptPadding_ = padding;
        setPromptPadding(padding, true);
    }

    /**
     * Sets the pinpad promptPadding
     *
     * @param paddingTop - padding for prompt top in pixels
     */
    public void setPromptPaddingTop(int paddingTop) {
        promptPaddingTop_ = paddingTop;
        setPromptPaddingTop(paddingTop, true);
    }

    public int getPromptPaddingTop() {
        return promptPaddingTop_;
    }

    public int getPromptPadding() {
        return promptPadding_;
    }

    public int getPromptPaddingBottom() {
        return promptPaddingBottom_;
    }

    /**
     * Sets the pinpad promptPadding
     *
     * @param paddingBottom - padding for prompt bottom in pixels
     */
    public void setPromptPaddingBottom(int paddingBottom) {
        promptPaddingBottom_ = paddingBottom;
        setPromptPaddingBottom(paddingBottom, true);
    }

    /**
     * Sets the textsize for the prompt text
     *
     * @param textSize - textsize for the prompt text in pixels
     */
    public void setPromptTextSize(float textSize) {
        setPromptTextSize(textSize, true);
    }

    /**
     * Sets the textSize for the numeric text
     *
     * @param textSize - textisze for the numeric text in pixles
     */
    public void setNumericTextSize(float textSize) {
        setNumericTextSize(textSize, true);
    }

    public void setAlphabetTextSize(float textSize) {
        setAlphabetTextSize(textSize, true);
    }

    public void setButtonTextColor(@ColorInt int color) {
        setButtonTextColor(color, true);
    }

    public void setPromptTextColor(@ColorInt int color) {
        setPromptTextColor(color, true);
    }

    public void setImageButtonSize(int size) {
        setImageButtonSize(size, true);
    }

    /**
     * Sets the pin length for the
     *
     * @param length - length for the pin
     */
    public void setPinLength(int length) {
        if (length < 0) return;
        pinLength_ = length;
        createIndicators(getContext(), getAttrs());
        updateIndicators();
        requestLayout();
    }

    /**
     * Gets the length for the pin
     *
     * @return int
     */
    public int getPinLength() {
        return pinLength_;
    }

    public String[] getDigits() {
        return currentDigits_;
    }

    public String[] getWords() {
        return currentWords_;
    }

    public String[] getColors() {
        return currentColors_;
    }

    public String[] getFigures() {
        return currentFigures_;
    }

    public String[] getTextures() {
        return currentTextures_;
    }

    /**
     * Sets necessary click listeners
     */
    private void setButtonClickListeners() {
        for (PinPadButton button : buttons_) {
            button.setButtonClickListener(symbolClickListener_);
        }

        buttonDone_.setButtonClickListener(doneButtonClickListener_);
        buttonBack_.setButtonClickListener(backButtonClickListener_);
    }

    /**
     * Updates the pin by updating the indicators as well as updating the listener
     *
     * @param oldPin - old pin
     * @param newPin - new pin
     */
    private void updatePin(List<Integer> oldPin, List<Integer> newPin) {

        enteredPinIds_ = newPin;

        // update indicators
        updateIndicators();

        // update listener
        if (pinChangeListener_ != null) {
            pinChangeListener_.onPinChanged(oldPin, newPin);
        }

        if (autoSubmit_ && pinLength_ == enteredPinIds_.size() && (submitListener_ != null)) {
            submitListener_.onCompleted(newPin);
        }
    }

    private void createIndicators(Context context, AttributeSet attrs) {
        layoutIndicator_.removeAllViews();
        for (int i = 0; i < pinLength_; i++) {
            Indicator indicator = new Indicator(context, attrs);
            indicator.setChecked(false);
            indicator.setIndicatorSize(indicatorSize_);
            indicator.setEmptyColor(indicatorEmptyColor_);
            indicator.setFilledColor(indicatorFilledColor_);

            int left, right;
            if (i == 0) {
                left = 0;
                right = indicatorSpacing_;
            } else if (i == pinLength_ - 1) {
                left = indicatorSpacing_;
                right = 0;
            } else {
                left = indicatorSpacing_;
                right = indicatorSpacing_;
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(indicatorSize_, indicatorSize_);
            params.gravity = Gravity.CENTER;
            params.setMargins(left, 0, right, 0);
            indicator.setLayoutParams(params);

            layoutIndicator_.addView(indicator);
        }
    }

    private void updateIndicators() {
        if (enteredPinIds_.size() <= layoutIndicator_.getChildCount()) {
            for (int i = 0; i < enteredPinIds_.size(); i++) {
                Indicator indicator = (Indicator) layoutIndicator_.getChildAt(i);
                indicator.setChecked(true);
            }
            for (int i = enteredPinIds_.size(); i < layoutIndicator_.getChildCount(); i++) {
                Indicator indicator = (Indicator) layoutIndicator_.getChildAt(i);
                indicator.setChecked(false);
            }
        }
        requestLayout();
    }

    /**
     * Click listener for the buttons
     */
    private PinPadButton.OnButtonClickListener symbolClickListener_ = new PinPadButton.OnButtonClickListener() {
        @Override
        public void onButtonClick(PinPadButton button) {
            if (enteredPinIds_.size() < getPinLength()) {
                List<Integer> oldPin = new ArrayList<>(enteredPinIds_);
                enteredPinIds_.add(buttonIds_.get(button));

                updatePin(oldPin, enteredPinIds_);
            } else {
                vibratePhone();
            }
        }
    };

    public void clear(){
        List<Integer> oldPin = new ArrayList<>(enteredPinIds_);
        enteredPinIds_.clear();

        updatePin(oldPin, enteredPinIds_);
    }

    /**
     * Click listener for the back button
     */
    private PinPadButton.OnButtonClickListener backButtonClickListener_ = new PinPadButton.OnButtonClickListener() {
        @Override
        public void onButtonClick(PinPadButton button) {
            if (enteredPinIds_.size() > 0) {
                List<Integer> oldPin = new ArrayList<>(enteredPinIds_);
                enteredPinIds_.remove(enteredPinIds_.size() - 1);
                updatePin(oldPin, enteredPinIds_);
            } else {
                vibratePhone();
            }
        }
    };

    /**
     * Click listener for the done button
     */
    private PinPadButton.OnButtonClickListener doneButtonClickListener_ = new PinPadButton.OnButtonClickListener() {
        @Override
        public void onButtonClick(PinPadButton button) {
            if (enteredPinIds_.size() == pinLength_) {
                if (submitListener_ != null) {
                    submitListener_.onCompleted(enteredPinIds_);
                }
            } else {
                if(vibrateOnIncompleteSubmit_){
                    vibratePhone();
                }
                if (submitListener_ != null) {
                    submitListener_.onIncompleteSubmit(enteredPinIds_);
                }
            }
        }
    };

    private String getDigitForButton(PinPadButton button) {
        if (button != null) {
            return digits_[buttonDigits_.get(button)];
        }
        return "";
    }

    private String getWordForButton(PinPadButton button) {
        if (button != null) {
            return words_[buttonWords_.get(button)];
        }
        return "";
    }

//    private String getColorForButton(PinPadButton button) {
//        if (button != null) {
//            return colors_[buttonColors_.get(button)];
//        }
//        return "";
//    }
//
//    private String getFigureForButton(PinPadButton button) {
//        if (button != null) {
//            return figures_[buttonFigures_.get(button)];
//        }
//        return "";
//    }
//
//    private String getTextureForButton(PinPadButton button) {
//        if (button != null) {
//            return textures_[buttonTextures_.get(button)];
//        }
//        return "";
//    }

    /***************************
     * private overloaded methods
     ***************************/
    private void setButtonTextColor(@ColorInt int color, boolean requestLayout) {
        for (PinPadButton button : buttons_) {
            if (button != null) {
                button.setTextColor(color);
            }
        }
        if (requestLayout) {
            requestLayout();
        }
    }

    private void setDoneButtonTextColor(@ColorInt int color, boolean requestLayout) {
        buttonDone_.setTextColor(color);
        if (requestLayout) {
            requestLayout();
        }
    }

    private void setPromptTextColor(@ColorInt int color, boolean requestLayout) {
        textViewPrompt_.setTextColor(color);
        if (requestLayout) {
            requestLayout();
        }
    }

    private void setNumericTextSize(float textSize, boolean requestLayout) {
        for (PinPadButton button : buttons_) {
            if (button != null) {
                button.setNumericTextSize(textSize);
            }
        }
        buttonDone_.setNumericTextSize(textSize);
        if (requestLayout) {
            requestLayout();
        }
    }

    private void setPromptTextSize(float textSize, boolean requestLayout) {
        textViewPrompt_.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        if (requestLayout) {
            requestLayout();
        }
    }

    private void setPromptPadding(int padding, boolean requestLayout) {
        textViewPrompt_.setPadding(padding, padding, padding, padding);
        if (requestLayout) {
            requestLayout();
        }
    }

    private void setPromptPaddingTop(int paddingTop, boolean requestLayout) {
        textViewPrompt_.setPadding(textViewPrompt_.getPaddingLeft(), paddingTop, textViewPrompt_.getPaddingRight(), textViewPrompt_.getPaddingBottom());
        if (requestLayout) {
            requestLayout();
        }
    }

    private void setPromptPaddingBottom(int paddingBottom, boolean requestLayout) {
        textViewPrompt_.setPadding(textViewPrompt_.getPaddingLeft(), textViewPrompt_.getPaddingTop(), textViewPrompt_.getPaddingRight(), paddingBottom);
        if (requestLayout) {
            requestLayout();
        }
    }

    private void setImagePinButtonSize(int size, boolean requestLayout) {
        for (PinPadButton button : buttons_) {
            button.setImageFigureSize(size);
        }
        if (requestLayout) {
            requestLayout();
        }
    }

    private void setImageButtonSize(int size, boolean requestLayout) {
        buttonBack_.setImageFigureSize(size);
        buttonDone_.setImageFigureSize(size);
        if (requestLayout) {
            requestLayout();
        }
    }

    /**
     * Sets the alphabet textsize with an option to request layout afterwards
     *
     * @param textSize      - textSize for the alphabet text on the pinpad
     * @param requestLayout - flag whether or not to call {@link #requestLayout()}
     */
    private void setAlphabetTextSize(float textSize, boolean requestLayout) {
        for (PinPadButton button : buttons_) {
            if (button != null) {
                button.setAlphabetTextSize(textSize);
            }
        }
        if (requestLayout) {
            requestLayout();
        }
    }
}
